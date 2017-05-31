package ru.security59.parser.shops;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import ru.security59.parser.HTMLParser;
import ru.security59.parser.entities.Failure;
import ru.security59.parser.entities.Image;
import ru.security59.parser.entities.Product;
import ru.security59.parser.entities.Target;

import javax.persistence.TypedQuery;
import java.io.*;
import java.util.LinkedList;
import java.util.List;

import static ru.security59.parser.HTMLParser.entityManager;

public abstract class Shop {
    private static final String DIRECTORY = HTMLParser.export_path;
    private final boolean LOAD_IMAGES = HTMLParser.loadImages;
    static final boolean SIMULATION = HTMLParser.simulation;

    public void parseItems(Target target) {
        //Получаем ссылки на все товары категории
        LinkedList<String> links = getItemsURI(target.getUrl());
        int currentItemIndex = 1;

        int insertCount = 0;
        int updateCount = 0;
        int failedCount = 0;

        //Проходим по всем ссылкам
        for (String link : links) {
            //String query;
            System.out.printf("%3d/%3d ", currentItemIndex++, links.size());
            Product product = new Product();
            product.setCategory(target.getCategory());
            product.setVendor(target.getVendor());
            product.setCurrency(target.getCurrency());
            product.setOriginURL(link);
            product.setUnit(target.getUnit());

            //Загружаем и парсим страницу
            getItemData(product);

            //Если страница не загрузилась
            if (product.getName() == null) {
                System.out.println("Empty product " + product.getOriginURL());
                Failure failure = new Failure();
                failure.setTargetId(target.getId());
                failure.setURL(link);
                entityManager.getTransaction().begin();
                entityManager.persist(failure);
                entityManager.getTransaction().commit();
                failedCount++;
                continue;
            }

            TypedQuery<Product> query = entityManager
                    .createQuery("SELECT P FROM Product P WHERE P.originId=:originId", Product.class);
            query.setParameter("originId", product.getOriginId());
            List<Product> list = query.getResultList();
            if (list.isEmpty()) {
                product.setId(target.getNextId());
                if (!SIMULATION) entityManager.persist(product);
                insertCount++;
            }
            else {
                product.setId(list.get(0).getId());
                if (!SIMULATION) entityManager.merge(product);
                updateCount++;
            }
            loadImages(product);
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        }
        System.out.printf("Inserted: %d\nUpdated: %d\nFailed: %d\nIgnored: %d\n",
                insertCount,
                updateCount,
                failedCount,
                links.size() - insertCount - updateCount - failedCount
        );
    }

    public void parsePricesByTarget(Target target) {}

    public void parsePricesByVendor(int vendorId) {}

    void updateLaunchTime(int targetId) {}

    protected abstract void getItemData(Product product);
    protected abstract void getItemPrice(Product product);

    protected abstract LinkedList<String> getItemsURI(String URI);

    void loadImages(Product product) {
        for (Image image : product.getImages()) {
            if (LOAD_IMAGES) getImage(image);
            TypedQuery<Image> query = entityManager.createQuery(
                    "SELECT I FROM Image I WHERE I.product=:product AND I.url=:url",
                    Image.class
            );
            query.setParameter("product", product);
            query.setParameter("url", image.getUrl());
            List<Image> list = query.getResultList();
            if (list.isEmpty() && !SIMULATION) {
                entityManager.getTransaction().begin();
                entityManager.persist(image);
                entityManager.getTransaction().commit();
            }
        }
    }

    private void getImage(Image image) {
        System.out.printf("        %s ", image.getUrl());
        File file = new File(DIRECTORY + "img/" + image.getName());
        if (file.exists()) {
            System.out.println("exists");
            return;
        }
        String fileName = image.getUrl().replaceAll("^.+/", "");
        file = new File(DIRECTORY);
        if (!file.exists())
            if (file.mkdir()) System.out.println("Directory " + file + " is created!");
            else System.out.println("Failed to create " + file + " directory!");
        file = new File(DIRECTORY + "new/");
        if (!file.exists())
            if (file.mkdir()) System.out.println("Directory " + file + " is created!");
            else System.out.println("Failed to create " + file + " directory!");
        file = new File(DIRECTORY + "new/" + fileName);
        if (!file.exists()) {
            try {//wget URI -O outfile
                Process wget = new ProcessBuilder(
                        "wget",
                        image.getUrl(),
                        "-O",
                        DIRECTORY + "new/" + fileName
                ).start();
                wget.waitFor();
                Thread.sleep(100);
            } catch (Exception e) {
                System.out.println("error");
                e.printStackTrace();
            }
        }

        try {//convert infile.png -resize '500x500>' -gravity Center -extent '500x500' outfile.jpg
            Process convert = new ProcessBuilder("convert",
                    DIRECTORY + "new/" + fileName,
                    "-resize",
                    "500x500>",
                    "-gravity",
                    "Center",
                    "-extent",
                    "500x500",
                    DIRECTORY + "img/" + image.getUrl()
            ).start();
            convert.waitFor();
            Thread.sleep(100);
        } catch (Exception e) {
            System.out.println("error");
            e.printStackTrace();
        }
        System.out.println("ok");
    }

    Document getDocument(String uri) {
        System.out.println(uri);
        Document doc = null;
        Connection connection = Jsoup.connect(uri);
        connection.timeout(30000);
        //connection.userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
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
}
