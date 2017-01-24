package ru.security59.parser;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

class Picocell {
    private static LinkedList<String[]> itemList = new LinkedList<>();
    private static final int manufacturerId = 100000;

    static LinkedList<String[]> getItemList(LinkedList<String[]> export, ArrayList<String[]> config) throws InterruptedException {
        String[] group;
        ArrayList<String> items;
        String[] item;
        for (int i = 0; i < config.size(); i++) {
            group = config.get(i);
            items = getItemsPages(group[0]);
            System.out.println(group[0]);
            int index = 0;
            for (int j = 0; j < items.size(); j++) {
                item = getItem(items.get(j));
                System.out.println(items.get(j));
                for (int k = 2; k < 27; k++)
                    if (group[k] != null) item[k] = group[k];
                for (String[] line : export) {
                    if (line[1].equals(item[1])) {
                        //item[0] = line[0];
                        item[18] = line[2];
                        export.remove(line);
                        break;
                    }
                }
                if (item[0] == null)
                    item[0] = String.valueOf(manufacturerId + (i + 1) * 1000 + index++ + 1);
                itemList.add(item);
                Thread.sleep(100);
            }
        }
        return itemList;
    }

    private static ArrayList<String> getItemsPages(String uri) throws InterruptedException {
        ArrayList<String> links = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();
        Document doc = getDocument(uri);
        if (doc == null) return links;
        String selector = "div.catalog_item-content > a.title";
        if (uri.endsWith("/kabel/"))
            selector = "li.active:nth-child(8) > ul:nth-child(2) > li > div > a:nth-child(2)";
        Elements elements = doc.select(selector);
        String link;
        String name;
        for (Element element : elements) {
            name = element.childNode(0).toString().trim();
            boolean duplicate = false;
            for (String str : names) {
                if (str.equals(name)) duplicate = true;
            }
            if (duplicate) continue;
            names.add(name);
            link = element.attr("href").trim();
            if (!link.startsWith("http")) link = "http://picocell.com" + link;
            links.add(link);
        }
        return links;
    }

    private static String[] getItem(String uri) throws InterruptedException {
        String[] data = new String[27];
        Document doc = getDocument(uri);
        if (doc == null) return data;
        //Название
        try {
            String selector = "ul.breadcrumb > li.active";
            Element element = doc.select(selector).get(0);
            String text = element.childNode(0).toString().trim();
            data[1] = text;
        } catch (Exception ignored) {
            System.out.println("Name: li.active:last-child");
        }
        //Изображение
        try {
            String selector = ".fancy > img";
            Element element = doc.select(selector).get(0);
            String text = element.attr("src");
            if (!text.startsWith("http")) text = "http://picocell.com" + text;
            data[11] = text;
        } catch (Exception ignored) {
            System.out.println("Image: .fancy > img");
        }
        //Описание
        try {
            String selector = "div.descr > div";
            doc.select(selector + " a").remove();
            for (Element image : doc.select("div.descr img")) {
                if (!image.attr("src").startsWith("http"))
                    data[11] += ", http://picocell.com" + image.attr("src");
                else
                    data[11] += ", " + image.attr("src");
            }
            doc.select("div.descr img").remove();
            Element element = doc.select(selector).get(0);
            String text = "";
            text += element.children().toString();
            if (doc.select(selector).size() != 1) {
                element = doc.select(selector).get(1);
                text = element.children().toString() + text;
            }
            data[3] = text;
        } catch (Exception ignored) {
            System.out.println("Description: div.descr > div");
        }
        //Цена
        try {
            String selector = "span.big";
            Element element = doc.select(selector).get(0);
            String text = element.childNode(0).toString();
            if (!Character.isDigit(text.charAt(0))) text = "";
            data[5] = text;
        } catch (Exception ignored) {
            System.out.println("Price: span.big");
        }
        //Наличие
        if (data[5].isEmpty()) data[12] = "0";
        else data[12] = "+";
        return data;
    }

    static ArrayList<String[]> getItemsPrices(LinkedList<String[]> export, ArrayList<String[]> config) throws InterruptedException {
        ArrayList<String[]> items = new ArrayList<>();
        String[] item = new String[27];
        Document document;
        Elements elements;
        String selector = "div.catalog_item-content";
        for (String[] group : config) {
            document = getDocument(group[0]);
            if (document == null) continue;
            elements = document.select(selector);
            for (Element element : elements) {
                item[1] = element.select("a.title").get(0).childNode(0).toString();
                item[5] = element.select("span.price > span.big").get(0).childNode(0).toString();
                boolean duplicate = false;
                for (String[] str : items)
                    if (str[1].equals(item[1])) duplicate = true;
                if (duplicate) continue;
                items.add(item);
            }
        }
        return items;
    }

    private static Document getDocument(String uri) throws InterruptedException {
        Document doc = null;
        Connection connection = Jsoup.connect(uri);
        int attempt = 0;
        while (attempt < 3) {
            try {
                doc = connection.get();
                break;
            } catch (IOException ignored) {
                System.out.println("Retry connection...");
                Thread.sleep(1000);
                attempt++;
            }
            if (attempt == 3) {
                System.out.println("Can't get data: " + uri);
            }
        }
        return doc;
    }
}