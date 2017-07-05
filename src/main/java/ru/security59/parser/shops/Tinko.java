package ru.security59.parser.shops;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.security59.parser.entities.Product;

import java.util.LinkedList;

public class Tinko extends AbstractShop {
    private static final String DOMAIN = "http://www.tinko.ru";

    @Override
    protected LinkedList<String> getItemsURI(String uri) {
        LinkedList<String> links = new LinkedList<>();
        Document doc;
        Elements elements;
        while (true) {
            doc = getDocument(uri);
            elements = doc.select("article.goods > a.goods_name");
            for (Element element : elements)
                if (element.attr("href").startsWith("http")) links.add(element.attr("href"));
                else links.add(DOMAIN + element.attr("href"));
            if (doc.select("a.next").size() == 0) break;
            uri = doc.select("a.next").get(0).attr("href");
            if (!uri.startsWith("http")) uri = DOMAIN + uri;
        }
        return links;
    }

    @Override
    protected void getItemData(Product product) {
        Document doc = getDocument(product.getOriginURL());
        if (doc == null) return;
        Elements elements;

        //Название
        elements = doc.select("div.breadcrumbs h1");
        if (elements.size() > 0)
            product.setName(elements.get(0).childNode(0).toString());

        //Описание
        elements = doc.select("div.product-shop__short-description > div");
        elements.select("a").remove();
        elements.select("img").remove();
        elements.select("*").removeAttr("style");
        if (elements.size() > 0)
            product.setDescription(elements.get(0).html());

        elements = doc.select("div#techdata > ul");
        elements.select("a").remove();
        elements.select("img").remove();
        elements.select("*").removeAttr("style");
        if (elements.size() > 0) {
            String description;
            description = "\n<br />\n<table class=\"product-desc-table\"><tbody>";
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
            product.setDescription(product.getDescription() + description);
        }

        //Цена
        elements = doc.select("div.price-box > div.min > p");
        if (elements.size() > 0)
            product.setPrice(elements.get(0).childNode(0).toString());

        //Наличие
        if ("0".equals(product.getPrice())) product.setAvailability("0");
        else product.setAvailability("+");

        //Изображение
        elements = doc.select("div.product-image > a");
        for (Element element : elements) {
            /*String image = element.attr("href").replaceAll(",", "%2C");
            product.addImage(image.startsWith("http") ? image : DOMAIN + image);*/
        }
    }

    @Override
    protected void getItemPrice(Product product) {
        Document doc = getDocument(product.getOriginURL());
        if (doc == null) return;
        Elements elements;

        //Цена
        elements = doc.select("div.price-box > div.min > p");
        if (elements.size() > 0)
            product.setPrice(elements.get(0).childNode(0).toString());

        //Наличие
        if ("0".equals(product.getPrice())) product.setAvailability("0");
        else product.setAvailability("+");
    }
}
