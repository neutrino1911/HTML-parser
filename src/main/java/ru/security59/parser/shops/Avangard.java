package ru.security59.parser.shops;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import ru.security59.parser.entities.Product;
import ru.security59.parser.entities.Target;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;

import static org.apache.commons.lang3.StringEscapeUtils.unescapeHtml4;

public class Avangard extends Shop {

    @Override
    public void parseItems(Target target) {
        /*//Обновляем время запуска
        if (!SIMULATION) updateLaunchTime(target.getId());

        Document document = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbf.newDocumentBuilder();
            String url = target.getUrl();
            InputStream in = new URL(url).openStream();
            document = builder.parse(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (document == null) return;
        document.getDocumentElement().normalize();
        System.out.println(document.getElementsByTagName("picture").getLength());
        NodeList list = document.getElementsByTagName("offer");

        int insertCount = 0;
        int updateCount = 0;
        int failedCount = 0;

        HashSet<String> items = new HashSet<>();
        Product product = null;

        for (int index = 0; index < list.getLength(); index++) {
            Node node = list.item(index);
            System.out.printf(
                    "%3d/%3d %s\r\n",
                    index,
                    list.getLength(),
                    ((Element)node).getElementsByTagName("name").item(0).getTextContent()
            );
            if (node.getNodeType() != Node.ELEMENT_NODE) continue;
            Element element = (Element) node;
            String name = unescapeHtml4(element.getElementsByTagName("name").item(0).getTextContent());
            if (!items.contains(name)) {//Если новый элемент
                items.add(name);
                if (index > 0) loadImages(product);
                product = new Product();
                product.setCategory(null);
                product.setVendor(target.getVendor());
                product.setUnit(target.getUnit());

                product.setName(name, false);
                product.setPrice(element.getElementsByTagName("price").item(0).getTextContent());
                product.setAvailability("true".equals(element.getAttribute("available")) ? "+" : "0");
                product.setCurrency(element.getElementsByTagName("currencyId").item(0).getTextContent());
                product.setOriginId(element.getAttribute("id"));
                product.setOriginURL(element.getElementsByTagName("url").item(0).getTextContent());
                product.setDescription("");
                for (int i = 0; i < element.getElementsByTagName("picture").getLength(); i++)
                    product.addImage(element.getElementsByTagName("picture").item(i).getTextContent());
                executeQuery("SELECT prod_id FROM Products WHERE prod_name = '" + product.getName() + "';");
                if (!resultSet.next()) {
                    product.setId(target.getNextId());
                    //if (!SIMULATION) executeUpdate(product.getInsertQuery());
                    insertCount++;
                }
                else {
                    product.setId(resultSet.getInt("prod_id"));
                    //if (!SIMULATION) executeUpdate(product.getUpdateQuery());
                    updateCount++;
                }
            }
            else {
                for (int i = 0; i < element.getElementsByTagName("picture").getLength(); i++)
                    product.addImage(element.getElementsByTagName("picture").item(i).getTextContent());
            }
        }
        loadImages(product);
        System.out.printf("Inserted: %d\nUpdated: %d\nFailed: %d\nIgnored: %d\n",
                insertCount,
                updateCount,
                failedCount,
                list.getLength() - insertCount - updateCount - failedCount
        );*/
    }

    @Override
    protected void getItemData(Product product) {
    }

    @Override
    protected void getItemPrice(Product product) {
    }

    @Override
    protected LinkedList<String> getItemsURI(String URI) {
        return null;
    }
}
