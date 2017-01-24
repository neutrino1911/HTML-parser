package ru.security59.parser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;

class LiderSB extends Shop {
    private static final String DOMAIN = "http://sec-s.ru";

    protected LinkedList<String> getItemsURI(String uri) {
        LinkedList<String> links = new LinkedList<>();
        Document doc = getDocument(uri);
        String selector = "ul.links_tov > li > a";
        Elements elements = doc.select(selector);
        for (Element element : elements)
            if (element.attr("href").startsWith("http")) links.add(element.attr("href"));
            else links.add(DOMAIN + element.attr("href"));
        return links;
    }

    protected void getItemData(Item item) {
        Document doc = getDocument(item.getOriginURL());
        if (doc == null) return;
        Elements elements;

        //Название
        elements = doc.select("div.h1-wr.h1-shop > h1");
        if (elements.size() > 0)
            item.setName(elements.get(0).childNode(0).toString().trim());

        //Описание
        elements = doc.select("div.shop2-product-desc div.block-tov-body");
        elements.select("a").remove();
        elements.select("*").removeAttr("style");
        if (elements.size() > 0) {
            String description;
            description = elements.get(0).childNode(1).unwrap().toString().replaceAll("Приобретая.+\\.", "");
            if (elements.size() > 1) description += "<br>" + elements.get(1).childNode(1);
            item.setDescription(description);
        }

        //Цена
        elements = doc.select("div.price-product-fix > span");
        if (elements.size() > 0)
            item.setPrice(elements.get(0).childNode(0).toString());

        //Наличие
        if ("0".equals(item.getPrice())) item.setAvailability("0");
        else item.setAvailability("+");

        //Изображение
        elements = doc.select("div.product-image > a");
        for (Element element : elements) {
            String image = element.attr("href").replaceAll(",", "%2C");
            item.addImage(image.startsWith("http") ? image : DOMAIN + image);
        }
    }
}