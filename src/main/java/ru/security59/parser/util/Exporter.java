package ru.security59.parser.util;

import com.opencsv.CSVWriter;
import org.apache.commons.lang3.SystemUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import ru.security59.parser.entities.Product;
import ru.security59.parser.entities.Product_;
import ru.security59.parser.entities.Vendor;
import ru.security59.parser.entities.Vendor_;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import static org.apache.commons.lang3.StringEscapeUtils.unescapeHtml4;
import static ru.security59.parser.HTMLParser.*;

public class Exporter {


    private static String[] tiuHeader;
    private LinkedList<String[]> list;
    private HSSFWorkbook workbook;
    private static String[] sites;

    static {
        tiuHeader = new String[]{
                "Код_товара",
                "Название_позиции",
                //"Ключевые_слова",
                "Описание",
                "Тип_товара",
                "Цена",
                "Валюта",
                "Единица_измерения",
                "Ссылка_изображения",
                "Наличие",
                "Номер_группы",
                //"Идентификатор_товара",
                "Идентификатор_подраздела",
                "Производитель",
                "Гарантийный_срок",
                "Страна_производитель"
        };
        sites = new String[] {"tiu", "uv"};
    }

    public Exporter() {
        list = new LinkedList<>();
        workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("sheet");
        Row row = sheet.createRow(0);
        //Заполняем шапку
        for (int i = 0; i < tiuHeader.length; i++) {
            row.createCell(i).setCellValue(tiuHeader[i]);
        }
    }

    public void write(int site, int firstVendor, int lastVendor) {
        int[] vendors = new int[lastVendor - firstVendor + 1];
        for (int i = 0; i < vendors.length; i++)
            vendors[i] = firstVendor + i;
        write(site, vendors);
    }

    private void write(int site, int[] vendors) {
        for (int vendor : vendors) {
            switch (site) {
                case 0:
                    getTiuExport(vendor);
                    break;
                case 1:
                    getUvExport(vendor);
                    break;
            }
        }
        switch (site) {
            case 0:
                writeXLS();
                break;
            case 1:
                writeCSV();
                break;
        }
    }

    private void getTiuExport(int vendorId) {
        HSSFSheet sheet = workbook.getSheet("sheet");
        Row row;
        //Запрашиваем данные из БД

        CriteriaQuery<Product> criteria = criteriaBuilder.createQuery(Product.class);
        Root<Product> productRoot = criteria.from(Product.class);
        criteria.select(productRoot);
        Join<Product, Vendor> vendorJoin = productRoot.join(Product_.vendor);
        criteria.where(criteriaBuilder.equal(vendorJoin.get(Vendor_.id), vendorId));
        List<Product> productList = entityManager.createQuery(criteria).getResultList();

        int rowNum = sheet.getLastRowNum() + 1;
        //Заполняем лист данными
        for (Product product : productList) {
            row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(product.getId());
            row.createCell(1).setCellValue(product.getName());
            row.createCell(2).setCellValue(product.getDescription());
            row.createCell(3).setCellValue("r");
            row.createCell(4).setCellValue(product.getPrice());
            row.createCell(5).setCellValue(product.getCurrency());
            row.createCell(6).setCellValue(product.getUnit());
            row.createCell(7).setCellValue(product.getImagesAsString());
            row.createCell(8).setCellValue(product.getAvailability());
            row.createCell(9).setCellValue(product.getCategory().getTiuId());
            row.createCell(10).setCellValue(product.getCategory().getTiuCatId());
            row.createCell(11).setCellValue(product.getVendor().getName());
            row.createCell(12).setCellValue(product.getVendor().getWarranty());
            row.createCell(13).setCellValue(product.getVendor().getCountry());
        }
    }

    private void getUvExport(int vendorId) {
        /*ResultSet resultSet = statement.executeQuery("CALL getUvImport(" + vendorId + ");");
        String[] line;
        while (resultSet.next()) {
            line = new String[11];
            line[0] = String.valueOf(resultSet.getInt("prod_id"));
            line[1] = resultSet.getString("prod_name");
            if ("USD".equals(resultSet.getString("currency")))
                line[2] = String.valueOf(resultSet.getFloat("price") * 65);
            else
                line[2] = String.valueOf(resultSet.getFloat("price"));
            line[3] = "1";
            line[4] = resultSet.getString("unit");
            line[5] = String.valueOf(resultSet.getInt("uv_id") > 0 ? resultSet.getInt("uv_id") : 187952606);
            line[6] = resultSet.getString("vend_name");
            line[7] = unescapeHtml4(resultSet.getString("prod_desc").replaceAll("&apos;", "'"));
            line[8] = resultSet.getString("images");
            line[9] = resultSet.getString("seo_url");
            line[10] = resultSet.getString("cat_keywords");
            list.add(line);
        }*/
    }

    private void writeXLS() {
        if (!checkDirectory()) return;
        String filename = export_path + "tiuExport.xls";
        FileOutputStream out;
        try {
            out = new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        try { workbook.write(out); } catch (IOException e) { e.printStackTrace(); }
        try { out.close(); } catch (IOException e) { e.printStackTrace(); }
        System.out.println(filename + " written successfully..");
    }

    private void writeCSV() {
        if (!checkDirectory()) return;
        String filename = export_path + "uvImport.csv";
        try (FileOutputStream out = new FileOutputStream(filename);
             CSVWriter writer = new CSVWriter(new OutputStreamWriter(out, "windows-1251"), '\t')) {
            writer.writeAll(list, false);
        } catch (IOException e) { e.printStackTrace(); }
        System.out.println(filename + " written successfully..");
    }

    private boolean checkDirectory() {
        File file = new File(export_path);
        if (!file.exists())
            if (file.mkdir()) {
                System.out.println("Directory" + file + " is created!");
                return true;
            }
            else {
                System.out.println("Failed to create " + file + " directory!");
                return false;
            }
        return true;
    }
}
