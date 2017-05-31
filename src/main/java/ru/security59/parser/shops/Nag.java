package ru.security59.parser.shops;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.security59.parser.entities.Product;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

public class Nag extends Shop {
    private static final String DOMAIN = "http://shop.nag.ru";
    private LinkedList<String[]> itemList = new LinkedList<>();

    LinkedList<String[]> getPriceList(LinkedList<String[]> config, LinkedList<String[]> export) {
        return null;
    }

    private LinkedList<String[]> getItemPrices(String uri) {
        LinkedList<String[]> data = new LinkedList<>();
        Document doc = getDocument(uri);
        if (doc == null) return data;
        String selector = "ul.links_tov > li > a";
        Elements elements = doc.select(selector);
        int count = elements.size();
        int pagesCount = (count % 10 > 0) ? (count / 10 + 1) : (count / 10);
        String[] pages = new String[pagesCount];
        for (int i = 0; i < pagesCount; i++)
            pages[i] = uri + "/p/" + i;
        Document page;
        Elements items;
        Elements name;
        Elements price;
        Elements manuf;
        String manufacturer = "";
        for (String link : pages) {
            page = getDocument(link);
            items = page.select("div.product-list > form.shop2-product-item");
            String[] item;
            for (Element element : items) {
                item = new String[27];
                //Название
                manuf = element.select("div.shop2-vendor-1");
                manufacturer = manuf.get(0).childNode(0).toString().trim();
                name = element.select("div.name1-product > a");
                if (name.size() > 0)
                    item[1] = name.get(0).childNode(0).toString().trim();
                if(!item[1].toLowerCase().startsWith(manufacturer.toLowerCase()))
                    item[1] = manufacturer + " " + item[1];
                //Цена
                price = element.select("ins.price-product-11-inner2 > span");
                if (price.size() > 0)
                    item[5] = price.get(0).childNode(0).toString().replace("&nbsp;", "");
                //Наличие
                if (item[5] == null) item[12] = "0";
                else item[12] = "+";
                data.add(item);
            }
        }
        return data;
    }

    @Override
    protected LinkedList<String> getItemsURI(String uri) {
        LinkedList<String> links = new LinkedList<>();
        if (!uri.endsWith("?count=0")) uri += "?count=0";
        Document doc = getDocument(uri);
        String selector = "div.homepage_cataloge > div.item_info > h2 > a";
        Elements elements = doc.select(selector);
        for (Element element : elements)
            if (element.attr("href").startsWith(DOMAIN)) links.add(element.attr("href"));
            else links.add(DOMAIN + element.attr("href"));
        return links;
    }

    protected LinkedList<String> getCategoriesURI(String uri) {
        return null;
    }

    protected Map<String, String> getItemData(String uri) {
        Map<String, String> data = new HashMap<>();
        String name = "";
        String description = "";
        String price = "";
        String images = "";
        String availability = "3";

        Document doc = getDocument(uri);
        if (doc == null) return data;
        Elements elements;

        //Название
        elements = doc.select("h1.item_name");
        if (elements.size() > 0)
            name = elements.get(0).childNode(2).toString().trim();

        //Описание
        elements = doc.select("div.description");
        elements.select("a").remove();
        elements.select("img").remove();
        elements.select("*").removeAttr("style");
        if (elements.size() > 0)
            description = escapeHtml4(elements.get(0).children().toString()).replaceAll("'", "&apos;");

        //Цена
        elements = doc.select("span.price.currency_select:eq(1)");
        if (elements.size() > 0) {
            if (elements.get(0).childNode(0).toString().indexOf("get_price") > 0) {
                price = "0";
                availability = "-";
            }
            else
                price = elements.get(0).childNode(0).toString().replaceAll("[^0-9\\.]+", "");
        }

        //Изображение
        elements = doc.select("div#titul-foto.item_corn img");
        if (elements.size() > 0)
            images = "http://shop.nag.ru" + elements.get(0).attr("src").replaceAll(",", "%2C");

        data.put("prod_name", name);
        data.put("prod_desc", description);
        data.put("price", price);
        data.put("images", images);
        data.put("availability", availability);

        return data;
    }

    @Override
    protected void getItemData(Product product) {}

    @Override
    protected void getItemPrice(Product product) {}
}
