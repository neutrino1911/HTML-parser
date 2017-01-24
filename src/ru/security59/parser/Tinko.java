package ru.security59.parser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;

class Tinko extends Shop {
    private static final String DOMAIN = "http://www.tinko.ru";

    protected LinkedList<String> getItemsURI(String uri) {
        LinkedList<String> links = new LinkedList<>();
        Document doc;
        Elements elements;
        while (true) {
            doc = getDocument(uri, 10000);
            elements = doc.select("div.product-name > a");
            for (Element element : elements)
                if (element.attr("href").startsWith(DOMAIN)) links.add(element.attr("href"));
                else links.add(DOMAIN + element.attr("href"));
            if (doc.select("a.next").size() == 0) break;
            uri = doc.select("a.next").get(0).attr("href");
            if (!uri.startsWith("http")) uri = DOMAIN + uri;
        }
        return links;
    }

    protected void getItemData(Item item) {
        Document doc = getDocument(item.getOriginURL(), 10000);
        if (doc == null) return;
        Elements elements;

        //Название
        elements = doc.select("div.breadcrumbs h1");
        if (elements.size() > 0)
            item.setName(elements.get(0).childNode(0).toString().trim());

        //Описание
        elements = doc.select("div.product-shop__short-description > div");
        elements.select("a").remove();
        elements.select("img").remove();
        elements.select("*").removeAttr("style");
        if (elements.size() > 0)
            item.setDescription(elements.get(0).html());

        elements = doc.select("div#techdata > ul");
        elements.select("a").remove();
        elements.select("img").remove();
        elements.select("*").removeAttr("style");
        if (elements.size() > 0) {
            String description;
            description = "\n<br />\n<table class=\"item-desc-table\"><tbody>";
            for (Element element : elements.select("li")) {
                description += "<tr><td>";
                if (element.childNodes().size() > 0)
                    description += element.childNode(0);
                description += "</td><td>";
                if (element.childNodes().size() > 1)
                    description += element.childNode(1);
                description += "</td></tr>";
            }
            description += "</tbody></table>";
            item.setDescription(item.getDescription() + description);
        }

        //Цена
        elements = doc.select("div.price-box > div.min > p");
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
