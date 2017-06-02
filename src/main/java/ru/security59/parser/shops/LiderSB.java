package ru.security59.parser.shops;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.security59.parser.entities.Product;

import java.util.LinkedList;

public class LiderSB extends AbstractShop {
    private static final String DOMAIN = "http://sec-s.ru";

    @Override
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

    @Override
    protected void getItemData(Product product) {
        Document doc = getDocument(product.getOriginURL());
        if (doc == null) return;
        Elements elements;

        //Название
        elements = doc.select("div.h1-wr.h1-shop > h1");
        if (elements.size() > 0)
            product.setName(elements.get(0).childNode(0).toString());

        //Описание
        elements = doc.select("div.shop2-product-desc div.block-tov-body");
        elements.select("a").remove();
        elements.select("*").removeAttr("style");
        if (elements.size() > 0) {
            String description;
            description = elements.get(0).childNode(1).unwrap().toString().replaceAll("Приобретая.+\\.", "");
            if (elements.size() > 1) description += "<br>" + elements.get(1).childNode(1);
            product.setDescription(description);
        }

        //Цена
        elements = doc.select("div.price-product-fix > span");
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
        elements = doc.select("div.price-product-fix > span");
        if (elements.size() > 0)
            product.setPrice(elements.get(0).childNode(0).toString());

        //Наличие
        if ("0".equals(product.getPrice())) product.setAvailability("0");
        else product.setAvailability("+");
    }
}
