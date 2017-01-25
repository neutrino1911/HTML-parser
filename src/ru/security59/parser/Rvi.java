package ru.security59.parser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

class Rvi extends Shop {
    private static final String DOMAIN = "http://rvi-cctv.ru";
    private LinkedList<String[]> itemList = new LinkedList<>();

    LinkedList<String[]> getPriceList(LinkedList<String[]> config, LinkedList<String[]> export) {
        return null;
    }

    private LinkedList<String[]> getItemPrices(String uri) {
        return null;
    }

    @Override
    protected LinkedList<String> getItemsURI(String uri) {
        LinkedList<String> links = new LinkedList<>();
        Document doc;
        Elements elements;
        doc = getDocument(uri, 10000);
        elements = doc.select(".cat-inner .cat-block a.cb-name");
        for (Element element : elements)
            if (element.attr("href").startsWith(DOMAIN)) links.add(element.attr("href"));
            else links.add(DOMAIN + element.attr("href"));
        return links;
    }

    protected Map<String, String> getItemData(String uri) {
        Map<String, String> data = new HashMap<>();
        String name = "";
        String description = "";
        String price = "";
        String images = "";
        String availability;

        Document doc = getDocument(uri, 10000);
        if (doc == null) return data;
        Elements elements;

        //Название
        elements = doc.select("h1");
        if (elements.size() > 0)
            name = elements.get(0).childNode(0).toString().trim();

        //Описание
        elements = doc.select("div.tab-pane > div");
        elements.select("a").remove();
        elements.select("img").remove();
        elements.select("meta").remove();
        if (elements.size() > 0)
            description += elements.get(0).html();

        if (elements.size() > 1)
            description += "<br>" + elements.get(1).html();

        //Цена
        elements = doc.select("span.ci-price");
        if (elements.size() > 0)
            price = elements.get(0).childNode(0).toString().replaceAll(",", ".").replaceAll("[^0-9.]", "");

        //Изображение
        elements = doc.select("ul.pagination img");
        for (int i = 0; i < elements.size(); i++) {
            String link = elements.get(i).attr("src");
            link = link.replace("resize_cache/", "");
            link = link.replace("75_70_1/", "");
            if (!"".equals(images)) images += ",";
            images += DOMAIN + link;
        }

        //Наличие
        if ("".equals(price)) availability = "0";
        else availability = "+";

        data.put("prod_name", name);
        data.put("prod_desc", description.replaceAll("″", "\""));
        data.put("price", price);
        data.put("images", images);
        data.put("availability", availability);

        return data;
    }

    @Override
    protected void getItemData(Item item) {}

    @Override
    protected void getItemPrice(Item item) {}
}
