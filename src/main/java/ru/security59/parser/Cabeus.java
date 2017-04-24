package ru.security59.parser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

class Cabeus/* extends Shop*/ {
    /*@Override
    LinkedList<String[]> getPriceList(LinkedList<String[]> config, LinkedList<String[]> export) {
        return null;
    }

    protected LinkedList<String> getItemsURI(String URI) {
        LinkedList<String> URIs = new LinkedList<>();
        Document doc = getDocument(URI);
        String selector = "td.cell_2 > a";
        Elements elements = doc.select(selector);
        for (Element element : elements) {
            if (element.attr("href").startsWith("http://"))
                URIs.add(element.attr("href"));
            else
                URIs.add("http://cabeus.ru" + element.attr("href"));
        }
        return URIs;
    }

    protected String[] getItemData(String URI) {
        String[] data = new String[27];
        Document doc = getDocument(URI);
        Elements elements;
        //Название
        elements = doc.select("h1.page_hl");
        if (elements.size() > 0) {
            String words[] = elements.get(0).childNode(0).toString().trim().split(" ");
            data[1] = words[0] + " " + words[1];
        }
        //Описание
        elements = doc.select("div.pd_b2");
        if (elements.select("div.tabs").size() > 0) {
            if (elements.select("div.tabs li.active").toString().contains("Описание")) {
                elements = elements.select("div.tabs_cont div.tab_item_active");
            } else {
                elements.clear();
            }
        }
        if (elements.size() > 0) {
            data[3] = parseNodes(elements.get(0).childNodes());
        }
        //Цена
        elements = doc.select("span.val > b");
        if (elements.size() > 0)
            if (elements.get(0).childNodes().size() > 0)
                data[5] = elements.get(0).childNode(0).toString().replaceAll("\\s|\\(.*", "").replace(',', '.');
        //Изображение
        elements = doc.select("a.highslide");
        if (elements.size() > 0) {
            data[11] = "";
            for (Element element : elements) {
                String href;
                href = element.attr("href");
                if (!href.equals("#")) {
                    if (!data[11].isEmpty()) data[11] += ", ";
                    if (href.startsWith("http://"))
                        data[11] += href.replaceAll(",", "%2C").replaceAll("\\?.*$", "").trim();
                    else
                        data[11] += "http:" + href.replaceAll(",", "%2C").replaceAll("\\?.*$", "").trim();
                }
            }
        }
        //Наличие
        if (data[5] == null)
            data[12] = "0";
        else
            data[12] = "+";
        return data;
    }

    private String parseNodes(List<Node> nodes) {
        String text = "";
        for (Node node : nodes) {
            switch (node.nodeName()) {
                case "#text":
                    text += node.toString().trim();
                    break;
                case "br":
                    if (!text.endsWith("<br>") && !text.equals("") && !text.isEmpty())
                        text += node.toString();
                    break;
                case "table":
                    break;
                case "img":
                    break;
                default:
                    if (node.childNodes().size() > 0)
                        text += " " + parseNodes(node.childNodes());
            }
            text = text.trim();
        }
        return text.trim();
    }*/
}
