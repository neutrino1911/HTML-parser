package ru.security59.parser.util;

import com.opencsv.CSVWriter;
import org.apache.commons.lang3.SystemUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import static org.apache.commons.lang3.StringEscapeUtils.unescapeHtml4;

public class Exporter {

    private static final String LINUX_PATH = "/home/neutrino/share/ru.security59.parser/";
    private static final String WIN_PATH = "C:/ru.security59.parser/";
    private static final String PATH = SystemUtils.IS_OS_LINUX ? LINUX_PATH : WIN_PATH;
    private static String[] tiuHeader;
    private LinkedList<String[]> list;
    private HSSFWorkbook workbook;
    private static String[] sites;

    static {
        tiuHeader = new String[]{
                "Код_товара",
                "Название_позиции",
                "Ключевые_слова",
                "Описание",
                "Тип_товара",
                "Цена",
                "Валюта",
                "Единица_измерения",
                "Ссылка_изображения",
                "Наличие",
                "Номер_группы",
                "Идентификатор_товара",
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
        for (int i = 0; i < tiuHeader.length; i++)
            row.createCell(i).setCellValue(tiuHeader[i]);
    }

    public void write(int site, int[] vendors) {
        for (int vendor : vendors) {
            try {
                switch (site) {
                    case 0:
                        getTiuExport(vendor);
                        break;
                    case 1:
                        getUvExport(vendor);
                        break;
                }
            } catch (SQLException e) {
                System.out.printf("Some trouble with vendorId: %d, site: %s\r\n", vendor, sites[site]);
                e.printStackTrace();
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

    public void write(int site, int vendor) {
        int[] vendors = new int[]{vendor};
        write(site, vendors);
    }

    public void write(int site, int firstVendor, int lastVendor) {
        int[] vendors = new int[lastVendor - firstVendor + 1];
        for (int i = 0; i < vendors.length; i++)
            vendors[i] = firstVendor + i;
        write(site, vendors);
    }

    private void getTiuExport(int vendorId) throws SQLException {
        /*HSSFSheet sheet = workbook.getSheet("sheet");
        Row row;
        //Запрашиваем данные из БД
        ResultSet resultSet = statement.executeQuery("CALL getTiuImport(" + vendorId + ");");
        int rowNum = sheet.getLastRowNum() + 1;
        //Заполняем лист данными
        while (resultSet.next()) {
            try {
                row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(resultSet.getInt("prod_id"));
                row.createCell(1).setCellValue(resultSet.getString("prod_name"));
                row.createCell(2).setCellValue(resultSet.getString("cat_keywords"));
                row.createCell(3).setCellValue(resultSet.getString("prod_desc").replaceAll("&apos;", "'"));
                row.createCell(4).setCellValue("r");
                row.createCell(5).setCellValue(resultSet.getDouble("price"));
                row.createCell(6).setCellValue(resultSet.getString("currency"));
                row.createCell(7).setCellValue(resultSet.getString("unit"));
                row.createCell(8).setCellValue(resultSet.getString("images"));
                row.createCell(9).setCellValue(resultSet.getString("availability"));
                row.createCell(10).setCellValue(resultSet.getInt("tiu_id"));
                row.createCell(11).setCellValue(resultSet.getInt("prod_id"));
                row.createCell(12).setCellValue(resultSet.getInt("tiu_cat"));
                row.createCell(13).setCellValue(resultSet.getString("vend_name"));
                row.createCell(14).setCellValue(resultSet.getInt("warranty"));
                row.createCell(15).setCellValue(resultSet.getString("country"));
            } catch (Exception e) {
                System.out.println(resultSet.getInt("prod_id"));
                e.printStackTrace();
            }
        }*/
    }

    private void getUvExport(int vendorId) throws SQLException {
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
        String filename = PATH + "tiuImport.xls";
        //String filename = "/home/neutrino/import.xls";
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
        String filename = PATH + "uvImport.csv";
        try (FileOutputStream out = new FileOutputStream(filename);
             CSVWriter writer = new CSVWriter(new OutputStreamWriter(out, "windows-1251"), '\t')) {
            writer.writeAll(list, false);
        } catch (IOException e) { e.printStackTrace(); }
        System.out.println(filename + " written successfully..");
    }

    private boolean checkDirectory() {
        File file = new File(PATH);
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
