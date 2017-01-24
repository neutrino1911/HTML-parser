package ru.security59.parser;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import static ru.security59.parser.Parser.statement;

abstract class Shop {
    private static final String LINUX_PATH = /*"c:/parser/";/*/"/home/neutrino/share/parser/";
    private ResultSet resultSet;
    private boolean loadImages;
    private boolean simulation;

    void parseItemByTarget(Target target, boolean loadImages, boolean simulation) throws SQLException {
        this.loadImages = loadImages;
        this.simulation = simulation;
        String query;
        //Обновляем время запуска
        if (!simulation) updateLaunchTime(target.getId());

        //Получаем ссылки на все товары категории
        System.out.printf("%d ", target.getId());
        LinkedList<String> links = getItemsURI(target.getUrl());
        int currentItemIndex = 1;

        int insertCount = 0;
        int updateCount = 0;
        int failedCount = 0;

        //Проходим по всем ссылкам
        for (String link : links) {
            System.out.printf("%3d/%3d ", currentItemIndex++, links.size());
            Item item = new Item(
                    target.getCategoryId(),
                    target.getVendorId(),
                    target.getCurrency(),
                    link,
                    target.getUnit(),
                    target.getVendorName());

            //Загружаем и парсим страницу
            getItemData(item);

            //Если страница не загрузилась
            if (item.getName() == null) {
                System.out.println("Empty item " + item.getOriginURL());
                query = String.format("INSERT INTO Failures (target_id, url) VALUES(%d, '%s');",
                        target.getId(),
                        link);
                statement.executeUpdate(query);
                failedCount++;
                continue;
            }

            query = "SELECT prod_id FROM Products WHERE prod_name = '" + item.getName() + "';";
            if (executeQuery(query) != 0) return;

            //Если товар уже есть в базе
            if (resultSet.next()) {
                item.setId(resultSet.getInt("prod_id"));
                if (!simulation) executeUpdate(item.getUpdateQuery());
                updateCount++;
            }
            else {
                item.setId(target.getNextId());
                if (!simulation) executeUpdate(item.getInsertQuery());
                insertCount++;
            }
            if (loadImages) addImages(item);
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        }

        try { resultSet.close(); }
        catch (SQLException ignored) {}

        System.out.printf("Inserted: %d\nUpdated: %d\nFailed: %d\nIgnored: %d\n",
                insertCount,
                updateCount,
                failedCount,
                links.size() - insertCount - updateCount - failedCount
        );
    }

    void parseItemByTarget(Target target) throws SQLException {
        parseItemByTarget(target, true, false);
    }

    void parsePriceByTarget(int targetId) throws SQLException {
        //Обновляем время запуска
        /*if (!simulation) updateLaunchTime(targetId);

        if (executeQuery("CALL getTarget(" + targetId + ");") != 0) return;*/
        /*String query = null;
        //Обновляем время запуска
        if (!simulation) updateLaunchTime(targetId);
        try {
            query = "CALL getPriceTarget(" + targetId +");";
            resultSet = statement.executeQuery(query);
        } catch (SQLException e) {
            System.out.println(query);
            e.printStackTrace();
            return;
        }

        resultSet.next();
        int vendId = resultSet.getInt("vend_id");
        String vendName = resultSet.getString("vend_name");

        LinkedList<String> links = getItemsURI(resultSet.getString("url"));*/
    }

    void parsePriceByVendor(int vendorId) throws SQLException {
        String query = String.format("SELECT prod_id, prod_name, origin_url FROM Products WHERE vend_id = %d;", vendorId);
        if (executeQuery(query) != 0) return;


    }

    private int executeQuery(String query) throws SQLException {
        int returnCode = 0;
        try {
            resultSet = statement.executeQuery(query);
        } catch (SQLException e) {
            returnCode = 1;
            System.out.println(query);
            e.printStackTrace();
        }
        return returnCode;
    }

    private int executeUpdate(String query) throws SQLException {
        int returnCode;
        try {
            returnCode = statement.executeUpdate(query);
        } catch (SQLException e) {
            returnCode = 1;
            System.out.println(query);
            e.printStackTrace();
        }
        return returnCode;
    }

    private void updateLaunchTime(int targetId) {
        String query = null;
        try {
            query = "SELECT last_use FROM Targets WHERE id=" + targetId + " AND first_use!=NULL;";
            resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                query = "CALL updateTimestamp(" + targetId + ");";
                statement.executeQuery(query);
            }
            else {
                query = "CALL setTimestamp(" + targetId + ");";
                statement.executeQuery(query);
            }
        } catch (SQLException e) {
            System.out.println(query);
            e.printStackTrace();
        }
    }

    protected abstract void getItemData(Item item);

    protected abstract LinkedList<String> getItemsURI(String URI);

    private void addImages(Item item) throws SQLException {
        for (String image : item.getImages()) {
            String imageName = item.getSeoURL() + "-" + item.getImages().indexOf(image) + ".png";
            if (loadImages && !isImageExists(imageName)) getImage(image, imageName);
            String query = String.format(
                    "SELECT item_id, image_name FROM Images WHERE item_id = %d AND image_name = '%s';",
                    item.getId(),
                    imageName.replace(".png", ".jpg"));
            executeQuery(query);
            if (!resultSet.next())
                if (!simulation) {
                    query = String.format(
                            "INSERT INTO Images (item_id, image_name, origin_url) VALUES (%d, '%s', '%s');",
                            item.getId(),
                            imageName.replace(".png", ".jpg"),
                            image);
                    executeUpdate(query);
                }
        }
    }

    private void getImage(String URI, String name) {
        System.out.printf("        %s\r\n", URI);
        File file = new File(LINUX_PATH);
        if (!file.exists())
            if (file.mkdir()) System.out.println("Directory " + file + " is created!");
            else System.out.println("Failed to create " + file + " directory!");
        file = new File(LINUX_PATH + "new/");
        if (!file.exists())
            if (file.mkdir()) System.out.println("Directory " + file + " is created!");
            else System.out.println("Failed to create " + file + " directory!");
        BufferedImage image;
        URL URL;
        try {
            URL = new URL(URI);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        }
        try {
            image = ImageIO.read(URL);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        try {
            ImageIO.write(image, "png", new File(LINUX_PATH + "new/" + name));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {//convert infile.png -resize '500x500>' -gravity Center -extent '500x500' outfile.jpg
            new ProcessBuilder("convert",
                    LINUX_PATH + "new/" + name,
                    "-resize",
                    "500x500>",
                    "-gravity",
                    "Center",
                    "-extent",
                    "500x500",
                    LINUX_PATH + "img/" + name.replace(".png", ".jpg")).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isImageExists(String imageName) {
        File file = new File(LINUX_PATH + "new/" + imageName);
        return file.exists();
    }

    Document getDocument(String uri, int timeout) {
        System.out.println(uri);
        Document doc = null;
        Connection connection = Jsoup.connect(uri);
        connection.timeout(timeout);
        //connection.userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.100 Safari/537.36");
        int attempt = 0;
        while (attempt < 3) {
            try {
                doc = connection.get();
                break;
            } catch (IOException ignored) {
                System.out.println("Retry connection...");
                attempt++;
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ignored) {
            }
            if (attempt == 3) {
                System.out.println("Can't get data: " + uri);
            }
        }
        return doc;
    }

    Document getDocument(String uri) {
        return getDocument(uri, 3000);
    }
}
