package ru.security59.parser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

class SatroPaladin extends Shop {
    private LinkedList<String[]> itemList = new LinkedList<>();

    LinkedList<String[]> getPriceList(LinkedList<String[]> config, LinkedList<String[]> export) {
        LinkedList<String[]> pages = getAllPages(config);
        for (String[] page : pages)
            itemList.addAll(getItemPrices(page[25]));
        for (String[] item : itemList) {
            for (String[] oldItem : export) {
                if (item[1].equals(oldItem[1])) {
                    //Код товара
                    item[0] = oldItem[0];
                    //Валюта
                    item[6] = oldItem[6];
                    //Единица измерения
                    item[7] = oldItem[7];
                    //Идентификатор
                    item[19] = oldItem[19];
                    break;
                }
            }
        }
        return itemList;
    }

    private LinkedList<String[]> getItemPrices(String uri) {
        LinkedList<String[]> data = new LinkedList<>();
        Document doc = getDocument(uri, 10000);
        if (doc == null) return data;
        String selector = "article.goods";
        Elements elements = doc.select(selector);
        Elements name;
        Elements price;
        Elements manuf;
        String manufacturer;
        String[] item;
        for (Element element : elements) {
            item = new String[27];
            //Название
            manuf = element.select("p.brand");
            manufacturer = manuf.get(0).childNode(0).toString().trim();
            name = element.select("a.goods_name");
            if (name.size() > 0)
                item[1] = name.get(0).childNode(0).toString().trim();
            if (!item[1].toLowerCase().startsWith(manufacturer.toLowerCase()))
                item[1] = manufacturer + " " + item[1];
            //Цена
            price = element.select("span.price-lbl");
            if (price.size() > 0)
                item[5] = price.get(0).childNode(0).toString().replace("&nbsp;", "").replace("руб.", "");
            //Наличие
            if (item[5] == null) item[12] = "0";
            else item[12] = "+";
            data.add(item);
        }
        return data;
    }

    private LinkedList<String[]> getAllPages(LinkedList<String[]> config) {
        //LinkedList<String[]> list = getPatterns(config);
        LinkedList<String[]> newList = new LinkedList<>();
        /*Document doc;
        String[] item;
        int count;
        int pages;
        for (String[] str : list) {
            doc = getDocument(str[26], 10000);
            count = Integer.parseInt(doc.select("p.count_search > span").get(0).childNode(0).toString());
            pages = (count % 25 > 0) ? count / 25 + 1 : count / 25;
            for (int i = 0; i < pages; i++) {
                item = str.clone();
                item[26] = str[26] + "&page=" + (i + 1);
                newList.add(item);
            }
        }*/
        return newList;
    }

    protected LinkedList<String> getItemsURI(String uri) {
        LinkedList<String> links = new LinkedList<>();
        Document doc = getDocument(uri, 10000);
        String selector = "div.old_content > a.goods_name";
        Elements elements = doc.select(selector);
        for (Element element : elements)
            if (element.attr("href").startsWith("http://"))
                links.add(element.attr("href"));
            else
                links.add("http://satro-paladin.com" + element.attr("href"));
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
        if (doc == null) return null;
        Elements elements;

        //Название
        elements = doc.select("div.rown > div.left > h1");
        if (elements.size() > 0)
            name = elements.get(0).childNode(0).toString().trim();

        //Описание
        elements = doc.select("div#good_desc.desc");
        if (elements.size() > 0)
            description = escapeHtml4(elements.get(0).childNode(0).toString().trim());

        //Цена
        elements = doc.select("div.right > p.price");
        if (elements.size() > 0)
            price = elements.get(0).childNode(1).toString().replace(" ", "").replace("&nbsp;", "");

        //Изображение
        elements = doc.select("img.goodImg");
        if (elements.size() > 0)
            images = "http://satro-paladin.com" + elements.get(0).attr("src").replaceAll(",", "%2C");

        //Наличие
        if ("".equals(price)) availability = "0";
        else availability = "+";

        data.put("prod_name", name);
        data.put("prod_desc", description);
        data.put("price", price);
        data.put("images", images);
        data.put("availability", availability);

        return data;
    }

    protected void getItemData(Item item) { }
}
