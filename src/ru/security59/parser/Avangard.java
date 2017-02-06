package ru.security59.parser;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.LinkedList;

public class Avangard extends Shop {

    @Override
    void parseItems(Target target, boolean loadImages, boolean simulation) throws SQLException {
        this.loadImages = loadImages;
        this.simulation = simulation;
        //Обновляем время запуска
        if (!simulation) updateLaunchTime(target.getId());

        Document document = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbf.newDocumentBuilder();
            String url = target.getUrl();
            InputStream in = new URL(url).openStream();
            document = builder.parse(in);
            //document = builder.parse("security59.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (document == null) return;
        document.getDocumentElement().normalize();
        NodeList list = document.getElementsByTagName("offer");

        int insertCount = 0;
        int updateCount = 0;
        int failedCount = 0;

        Item item = null;
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
            String name = element.getElementsByTagName("name").item(0).getTextContent();
            if (item == null || !name.equals(item.getName())) {//Если новый элемент
                item = new Item(
                        0,
                        target.getVendorId(),
                        null,
                        "",
                        target.getUnit(),
                        target.getVendorName()
                );
                item.setName(name, false);
                item.setPrice(element.getElementsByTagName("price").item(0).getTextContent());
                item.setAvailability("true".equals(element.getAttribute("available")) ? "+" : "0");
                item.setCurrency(element.getElementsByTagName("currencyId").item(0).getTextContent());
                item.setOriginId(element.getAttribute("id"));
                item.setOriginURL(element.getElementsByTagName("url").item(0).getTextContent());
                item.setDescription("");
                for (int i = 0; i < element.getElementsByTagName("picture").getLength(); i++)
                    item.addImage(element.getElementsByTagName("picture").item(i).getTextContent());
                executeQuery("SELECT prod_id FROM Products WHERE origin_id = " + item.getOriginId() + ";");
                if (!resultSet.next()) {
                    item.setId(target.getNextId());
                    if (!simulation) executeUpdate(item.getInsertQuery());
                    insertCount++;
                }
                else {
                    item.setId(resultSet.getInt("prod_id"));
                    if (!simulation) executeUpdate(item.getUpdateQuery());
                    updateCount++;
                }
            }
            else {
                for (int i = 0; i < element.getElementsByTagName("picture").getLength(); i++)
                    item.addImage(element.getElementsByTagName("picture").item(i).getTextContent());
            }
            addImages(item);
        }

        System.out.printf("Inserted: %d\nUpdated: %d\nFailed: %d\nIgnored: %d\n",
                insertCount,
                updateCount,
                failedCount,
                list.getLength() - insertCount - updateCount - failedCount
        );
    }

    @Override
    protected void getItemData(Item item) {
    }

    @Override
    protected void getItemPrice(Item item) {

    }

    @Override
    protected LinkedList<String> getItemsURI(String URI) {
        return null;
    }
}
