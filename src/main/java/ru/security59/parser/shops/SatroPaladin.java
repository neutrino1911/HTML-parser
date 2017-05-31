package ru.security59.parser.shops;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.security59.parser.entities.Image;
import ru.security59.parser.entities.Product;

import java.util.LinkedList;

public class SatroPaladin extends Shop {
    private static final String DOMAIN = "https://www.satro-paladin.com";

    @Override
    protected LinkedList<String> getItemsURI(String uri) {
        LinkedList<String> links = new LinkedList<>();
        Document doc = getDocument(uri);
        Elements elements;
        int count = Integer.valueOf(doc.select("p.count_search > span").get(0).html().trim());
        for (int i = 0; i < Math.ceil(count / 25.0); i++) {
            if (i > 0) {
                doc = getDocument(uri + "&page=" + (i + 1));
            }
            elements = doc.select("div.old_content > a.goods_name");
            for (Element element : elements)
                if (element.attr("href").startsWith("http")) links.add(element.attr("href"));
                else links.add(DOMAIN + element.attr("href"));
        }
        /*while (true) {
            doc = getDocument(uri);
            if (count == 0) {
                count = Integer.valueOf(doc.select("p.count_search > span").get(0).html().trim());
            }
            elements = doc.select("div.old_content > a.goods_name");
            for (Element element : elements)
                if (element.attr("href").startsWith("http")) links.add(element.attr("href"));
                else links.add(DOMAIN + element.attr("href"));
            elements = doc.select("div.paginator > div > *:last-child");
            if (elements.size() == 0 || "b".equals(elements.get(0).tagName())) break;
            uri = doc.select("div.paginator > div > *:nth-last-child(2)").get(0).attr("href");
            //uri = uri.replaceAll("catalog_search\\.htm", "catalog_search.php");
            if (!uri.startsWith("https")) uri = DOMAIN + uri;
        }*/
        return links;
    }

    @Override
    protected void getItemData(Product product) {
        Document doc = getDocument(product.getOriginURL());
        if (doc == null) return;
        Elements elements;

        //Код
        elements = doc.select("div.goods-code-n > span");
        if (elements.size() > 0)
            product.setOriginId(elements.get(0).html());

        //Название
        elements = doc.select("h1");
        if (elements.size() > 0)
            product.setName(elements.get(0).html());

        //Описание
        elements = doc.select("div#longdecr");
        elements.select("section").remove();
        //elements.select("a").remove();
        //elements.select("img").remove();
        //elements.select("*").removeAttr("style");
        if (elements.size() > 0)
            product.setDescription(elements.get(0).html());

        //Цена
        elements = doc.select("p.price.wholesale-price > span:nth-child(2)");
        //elements.select("span").remove();
        if (elements.size() > 0)
            product.setPrice(elements.get(0).html());

        //Наличие
        if ("0".equals(product.getPrice())) product.setAvailability("0");
        else product.setAvailability("+");

        //Изображение
        /*elements = doc.select("a.colorbox");
        for (Element element : elements) {
            String imageURL = element.attr("href").replaceAll(",", "%2C");
            imageURL = imageURL.startsWith("http") ? imageURL : DOMAIN + imageURL;
            product.addImage(new Image(imageURL, product));
        }*/
    }

    @Override
    protected void getItemPrice(Product product) {
        Document doc = getDocument(product.getOriginURL());
        if (doc == null) return;
        Elements elements;

        //Цена
        elements = doc.select("p.price.retail-price");
        elements.select("span").remove();
        if (elements.size() > 0)
            product.setPrice(elements.get(0).childNode(0).toString());

        //Наличие
        if ("0".equals(product.getPrice())) product.setAvailability("0");
        else product.setAvailability("+");
    }
}
