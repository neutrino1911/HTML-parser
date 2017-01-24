package ru.security59.parser;


import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.*;
import java.util.Iterator;

public class Test {

    private void writeXLS(HSSFWorkbook workbook, String filename) {
        FileOutputStream out;
        try {
            out = new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
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
        System.out.println(filename + " written successfully..");
    }

    void findMatches() throws IOException {
        FileWriter writer = new FileWriter("/home/neutrino/log1.txt");
        HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream("/home/neutrino/export.xls"));
        Sheet oldSheet = wb.getSheetAt(0);

        Row oldRow;
        Cell cell;
        String text1;
        String text2;
        Iterator<Row> iterator = oldSheet.rowIterator();
        java.util.regex.Pattern p1;
        java.util.regex.Pattern p2;
        java.util.regex.Matcher m1;
        java.util.regex.Matcher m2;
        int count = 0;
        while (iterator.hasNext()) {
            oldRow = iterator.next();
            cell = oldRow.getCell(3);
            if (cell == null) continue;
            text1 = cell.getStringCellValue().toLowerCase();
            p1 = java.util.regex.Pattern.compile(".{20}приобрет.+(security59|инн)[^.]+\\.");
            //p1 = java.util.regex.Shop.compile("приобрет[^.]+\\.");
            //p1 = java.util.regex.Shop.compile("<a[^<]+Приобрет[^.]+\\.");
            //p1 = java.util.regex.Shop.compile("<a[^>]+>[^<]+</a>");
            //p1 = java.util.regex.Shop.compile("<a[^>]*security59\\.ru[^>]*>.+</a>");
            //p1 = java.util.regex.Shop.compile("security59\\.ru");
            m1 = p1.matcher(text1);
            if (m1.find()) {
                text2 = m1.group();
                count++;
                writer.write(text2 + "\r\n");
                /*p2 = java.util.regex.Shop.compile("<");
                m2 = p2.matcher(text2);
                if (!m2.find()) {
                    count++;
                    writer.write(text2 + "\r\n");
                }*/
            }
        }
        System.out.println(count);
        writer.close();
    }

    void removeMatches() throws IOException {
        HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream("/home/neutrino/export.xls"));
        Sheet oldSheet = wb.getSheetAt(0);
        HSSFWorkbook wb1 = new HSSFWorkbook();
        HSSFWorkbook wb2 = new HSSFWorkbook();
        Sheet newSheet = wb1.createSheet("sheet");
        Sheet newSheet2 = wb2.createSheet("sheet");
        Row row = newSheet.createRow(0);
        row.createCell(0).setCellValue("Название_позиции");
        row.createCell(1).setCellValue("Описание");
        row.createCell(2).setCellValue("Уникальный_идентификатор");

        Row oldRow;
        Row newRow;
        Cell cell;
        String text1;
        String text2;
        String newText;
        Iterator<Row> iterator = oldSheet.rowIterator();
        java.util.regex.Pattern p1;
        java.util.regex.Pattern p2;
        java.util.regex.Matcher m1;
        java.util.regex.Matcher m2;
        int count = 0;
        while (iterator.hasNext()) {
            oldRow = iterator.next();
            cell = oldRow.getCell(3);
            if (cell == null) continue;
            text1 = cell.getStringCellValue();
            //p1 = java.util.regex.Shop.compile("Приобрет.+(Security59|ИНН|Инн)[^.]+\\.");
            p1 = java.util.regex.Pattern.compile("Приобрет[^.]+\\.");
            //p1 = java.util.regex.Shop.compile("<a[^<]+Приобрет[^.]+\\.");
            //p1 = java.util.regex.Shop.compile("<a[^>]+>[^<]+</a>");
            //p1 = java.util.regex.Shop.compile("<a[^>]*security59\\.ru[^>]*>.+</a>");
            //p1 = java.util.regex.Shop.compile("security59\\.ru");
            m1 = p1.matcher(text1);
            if (m1.find()) {
                text2 = m1.group();

                newText = text1;
                newRow = newSheet.createRow(newSheet.getLastRowNum() + 1);
                newRow.createCell(0).setCellValue(oldRow.getCell(1).getStringCellValue());
                newRow.createCell(1).setCellValue(newText);
                newRow.createCell(2).setCellValue(oldRow.getCell(20).getNumericCellValue());


                /*p2 = java.util.regex.Shop.compile("<");
                m2 = p2.matcher(text2);
                if (!m2.find()) {
                    count++;
                    //newText = text1.replaceAll("Приобрет.+(Security59|ИНН|Инн)[^.]+\\.", "");
                    newText = text1.replaceAll("Приобрет[^.]+\\.", "");
                    newRow = newSheet.createRow(newSheet.getLastRowNum() + 1);
                    newRow.createCell(0).setCellValue(oldRow.getCell(1).getStringCellValue());
                    newRow.createCell(1).setCellValue(newText);
                    newRow.createCell(2).setCellValue(oldRow.getCell(20).getNumericCellValue());
                } else {
                    newRow = newSheet2.createRow(newSheet2.getLastRowNum() + 1);
                    newRow.createCell(0).setCellValue(oldRow.getCell(1).getStringCellValue());
                    newRow.createCell(1).setCellValue(text1);
                    newRow.createCell(2).setCellValue(oldRow.getCell(20).getNumericCellValue());
                }*/
            }
        }
        System.out.println(count);
        writeXLS(wb1, "/home/neutrino/import3.xls");
        writeXLS(wb2, "/home/neutrino/import4.xls");
    }

    void rty() {
        try {
            FileWriter writer = new FileWriter("/home/neutrino/log1.txt");
            HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream("/home/neutrino/export.xls"));
            Sheet oldSheet = wb.getSheetAt(0);
            HSSFWorkbook wb1 = new HSSFWorkbook();
            HSSFWorkbook wb2 = new HSSFWorkbook();
            Sheet newSheet = wb1.createSheet("sheet");
            Sheet newSheet2 = wb2.createSheet("sheet");
            Row row = newSheet.createRow(0);
            row.createCell(0).setCellValue("Название_позиции");
            row.createCell(1).setCellValue("Описание");
            row.createCell(2).setCellValue("Уникальный_идентификатор");

            Row oldRow;
            Row newRow;
            Cell cell;
            String text1;
            String text2;
            String newText;
            Iterator<Row> iterator = oldSheet.rowIterator();
            java.util.regex.Pattern p1;
            java.util.regex.Pattern p2;
            java.util.regex.Matcher m1;
            java.util.regex.Matcher m2;
            int count = 0;
            while (iterator.hasNext()) {
                oldRow = iterator.next();
                cell = oldRow.getCell(3);
                if (cell == null) continue;
                text1 = cell.getStringCellValue();
                p1 = java.util.regex.Pattern.compile("Приобрет.+(Security59|ИНН)[^.]+\\.");
                //p1 = java.util.regex.Shop.compile("Приобрет[^.]+\\.");
                //p1 = java.util.regex.Shop.compile("<a[^<]+Приобрет[^.]+\\.");
                //p1 = java.util.regex.Shop.compile("<a[^>]+>[^<]+</a>");
                //p1 = java.util.regex.Shop.compile("<a[^>]*security59\\.ru[^>]*>.+</a>");
                //p1 = java.util.regex.Shop.compile("security59\\.ru");
                m1 = p1.matcher(text1);
                if (m1.find()) {
                    text2 = m1.group();

                    count++;
                    writer.write(oldRow.getCell(0) + " " + text2 + "\r\n");
                    //newText = text1.replaceAll("<a[^>]*security59\\.ru[^>]*>.+</p>", "");
                    newText = text1;
                    newRow = newSheet.createRow(newSheet.getLastRowNum() + 1);
                    newRow.createCell(0).setCellValue(oldRow.getCell(1).getStringCellValue());
                    newRow.createCell(1).setCellValue(newText);
                    newRow.createCell(2).setCellValue(oldRow.getCell(20).getNumericCellValue());



                    /*p2 = java.util.regex.Shop.compile("<");
                    m2 = p2.matcher(text2);
                    if (!m2.find()) {
                        count++;
                        writer.write(text2 + "\r\n");
                        newText = text1.replaceAll("<a[^>]*>.+</p>", "");
                        newRow = newSheet.createRow(newSheet.getLastRowNum() + 1);
                        newRow.createCell(0).setCellValue(oldRow.getCell(1).getStringCellValue());
                        newRow.createCell(1).setCellValue(newText);
                        newRow.createCell(2).setCellValue(oldRow.getCell(20).getNumericCellValue());
                    }
                    else {
                        newRow = newSheet2.createRow(newSheet2.getLastRowNum() + 1);
                        newRow.createCell(0).setCellValue(oldRow.getCell(1).getStringCellValue());
                        newRow.createCell(1).setCellValue(text1);
                        newRow.createCell(2).setCellValue(oldRow.getCell(20).getNumericCellValue());
                    }*/
                }
            }
            System.out.println(count);
            writer.close();
            writeXLS(wb1, "/home/neutrino/import3.xls");
            writeXLS(wb2, "/home/neutrino/import4.xls");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
