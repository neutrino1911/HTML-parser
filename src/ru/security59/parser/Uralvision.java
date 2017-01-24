package ru.security59.parser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

class Uralvision {
    /*private static final String PATH = "/home/neutrino/parser/";
    private static final String DOMAIN = "http://uralvision.com";
    private static final String PREFIX = "/d/512960/d/";
    private static String[] header;
    private static LinkedList<String[]> outHSSF = new LinkedList<>();

    static {
        header = new String[]{
                //"ProductID",
                "Art",
                "Name",
                "Price",
                "Amount",
                "Measure",
                //"Status",
                "New",
                "Special",
                "YML",
                //"1C",
                "FolderID",
                //"FolderName",
                //"VendorID",
                "Vendor",
                //"Anonce",
                "Description",
                //"Description (plain)",
                //"Alias",
                "Image",
                //"Thumbnail",
                "URL",
                "Seo keywords",
                "Seo description",
                "Seo title",
                //"Noindex"
                //"SEF URL"
        };
    }

    static void run(String name) {
        LinkedList<String[]> list = readHSSF(PATH + name + "-import.xls", 1);
        LinkedList<String[]> pairs = readHSSF(PATH + "uralvision/categories.xls", 0);
        File file = new File(PATH + "uralvision/" + name);
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Failed to create directory!");
            }
        }
        //result.add(header);
        String imageName;
        String[] images;
        String[] newLine;
        for (String[] line : list) {
            newLine = new String[13];
            newLine[0] = line[0];//артикул
            newLine[1] = line[1];//наименование
            newLine[2] = line[5];//цена
            newLine[3] = "1";//количество
            newLine[4] = line[7];//единица
            //id категории
            for (String[] pair : pairs) {
                if (pair[0].equals(line[14])) {
                    newLine[5] = pair[1];
                    break;
                }
            }
            newLine[6] = line[23];//производитель
            newLine[7] = line[3];//описание
            //изображения
            images = line[8].split(",");
            for (int i = 0; i < images.length; i++) {
                imageName = line[1].trim().replaceAll(" ", "-") + "-" + String.valueOf(i) + ".png";
                //getImage(images[i].trim(), name + "/" + imageName);
                if (newLine[8] == null) newLine[8] = PREFIX + imageName.toLowerCase();
                else newLine[8] += ", " + PREFIX + imageName.toLowerCase();
            }
            newLine[9] = "/" + newLine[1].replaceAll(" ", "-");//URL
            newLine[10] = line[2];//ключевые слова
            newLine[11] = "";//сео описание
            newLine[12] = "";//сео заголовок
            outHSSF.add(newLine);
        }
        writeData(PATH + "uralvision/" + name + ".xls");
    }

    private static void getImage(String URI, String name) {
        System.out.println(URI);
        BufferedImage image;
        URL url;
        try {
            url = new URL(URI);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        }
        try {
            image = ImageIO.read(url);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        try {
            ImageIO.write(image, "png", new File(PATH + "uralvision/" + name));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static LinkedList<String[]> readHSSF(String filename, int from) {
        LinkedList<String[]> HSSF = new LinkedList<>();
        HSSFWorkbook workbook;
        try (InputStream inputStream = new FileInputStream(filename)) {
            workbook = new HSSFWorkbook(inputStream);
            inputStream.close();
        } catch (IOException e) {
            return null;
        }
        Sheet sheet = workbook.getSheetAt(0);
        Row row;
        Cell cell;
        String[] rowData;
        int rowSize = sheet.getRow(0).getLastCellNum();
        for (int i = from; i <= sheet.getLastRowNum(); i++) {
            row = sheet.getRow(i);
            rowData = new String[rowSize];
            for (int j = 0; j < rowSize; j++) {
                cell = row.getCell(j);
                rowData[j] = getCellValue(cell);
            }
            HSSF.add(rowData);
        }
        return HSSF;
    }

    private static String getCellValue(Cell cell) {
        String string = "";
        if (cell == null) return string;
        switch (cell.getCellType()) {
            case 0:
                string = String.valueOf((int) cell.getNumericCellValue());
                break;
            case 1:
                string = cell.getStringCellValue();
                break;
        }
        return string;
    }

    private static void writeData(String filename) {
        FileOutputStream out;
        try {
            out = new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("sheet");
        int rownum = 0;
        for (String[] rowData : outHSSF) {
            Row row = sheet.createRow(rownum++);
            int cellnum = 0;
            for (String cellData : rowData) {
                Cell cell = row.createCell(cellnum++);
                try {
                    cell.setCellValue(Double.parseDouble(cellData));
                } catch (Exception e) {
                    cell.setCellValue(cellData);
                }
            }
        }
        try {
            workbook.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Excel written successfully..");
    }*/
}
