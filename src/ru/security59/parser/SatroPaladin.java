package ru.security59.parser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;

class SatroPaladin extends Shop {
    private static final String DOMAIN = "http://www.satro-paladin.com";

    protected LinkedList<String> getItemsURI(String uri) {
        LinkedList<String> links = new LinkedList<>();
        Document doc;
        Elements elements;
        while (true) {
            doc = getDocument(uri, 10000);
            elements = doc.select("div.old_content > a.goods_name");
            for (Element element : elements)
                if (element.attr("href").startsWith("http")) links.add(element.attr("href"));
                else links.add(DOMAIN + element.attr("href"));
            elements = doc.select("div.paginator > div > *:last-child");
            if (elements.size() == 0 || "b".equals(elements.get(0).tagName())) break;
            uri = doc.select("div.paginator > div > *:nth-last-child(2)").get(0).attr("href");
            if (!uri.startsWith("http")) uri = DOMAIN + uri;
        }
        return links;
    }

    protected void getItemData(Item item) {
        Document doc = getDocument(item.getOriginURL(), 10000);
        if (doc == null) return;
        Elements elements;

        //Название
        elements = doc.select("h1");
        if (elements.size() > 0)
            item.setName(elements.get(0).childNode(0).toString().trim());

        //Описание
        elements = doc.select("div#good_desc.desc");
        //elements.select("a").remove();
        //elements.select("img").remove();
        //elements.select("*").removeAttr("style");
        if (elements.size() > 0)
            item.setDescription(elements.get(0).childNode(0).toString().trim());

        //Цена
        elements = doc.select("p.price.retail-price");
        elements.select("span").remove();
        if (elements.size() > 0)
            item.setPrice(elements.get(0).childNode(0).toString().trim());

        //Наличие
        if ("0".equals(item.getPrice())) item.setAvailability("0");
        else item.setAvailability("+");

        //Изображение
        elements = doc.select("a.colorbox.cboxElement");
        for (Element element : elements) {
            String image = element.attr("href").replaceAll(",", "%2C");
            item.addImage(image.startsWith("http") ? image : DOMAIN + image);
        }
    }
}
