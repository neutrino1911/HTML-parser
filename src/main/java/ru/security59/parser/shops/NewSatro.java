package ru.security59.parser.shops;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import ru.security59.parser.entities.Image;
import ru.security59.parser.entities.Product;
import ru.security59.parser.entities.Product_;
import ru.security59.parser.entities.Target;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import static ru.security59.parser.HTMLParser.SIMULATION;
import static ru.security59.parser.HTMLParser.criteriaBuilder;
import static ru.security59.parser.HTMLParser.entityManager;

public class NewSatro extends AbstractShop {

    @Override
    public void parseItems(Target target) {
        Document document;
        /*try(InputStream in = new URL(target.getUrl()).openStream()) {
            document = DocumentBuilderFactory
                    .newInstance()
                    .newDocumentBuilder()
                    .parse(in);

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }*/
        try {
            document = DocumentBuilderFactory
                    .newInstance()
                    .newDocumentBuilder()
                    .parse(new File("/home/neutrino/Downloads/partners.xml"));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        document.getDocumentElement().normalize();
        NodeList offers = document.getElementsByTagName("offer");

        int insertCount = 0;
        int updateCount = 0;
        int failedCount = 0;

        for (int i = 0; i < offers.getLength(); i++) {
            System.out.printf("%3d/%3d ", i + 1, offers.getLength());
            Product product = new Product();
            product.setCategory(target.getCategory());
            product.setVendor(target.getVendor());
            product.setCurrency(target.getCurrency());
            product.setOriginURL("");
            product.setUnit(target.getUnit());

            getItemData(product, offers.item(i));
            if (product.getVendor() == null) {
                System.out.println("skipped");
                continue;
            } else {
                System.out.printf("%s %s%n", product.getName(), product.getOriginId());
            }

            CriteriaQuery<Product> criteria = criteriaBuilder.createQuery(Product.class);
            Root<Product> root = criteria.from(Product.class);
            criteria.select(root);
            criteria.where(criteriaBuilder.equal(root.get(Product_.originId), product.getOriginId()));
            List<Product> list = entityManager.createQuery(criteria).getResultList();

            if (list.isEmpty()) {
                product.setId(target.getNextId());
                entityManager.getTransaction().begin();
                if (!SIMULATION) entityManager.persist(product);
                entityManager.getTransaction().commit();
                insertCount++;
            }
            else {
                product.setId(list.get(0).getId());
                entityManager.getTransaction().begin();
                if (!SIMULATION) entityManager.merge(product);
                entityManager.getTransaction().commit();
                updateCount++;
            }

            //Загружаем изображения
            loadImages(product);
            //try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        }
        System.out.printf("Inserted: %d%nUpdated: %d%nFailed: %d%nIgnored: %d%n",
                insertCount,
                updateCount,
                failedCount,
                offers.getLength() - insertCount - updateCount - failedCount
        );
    }

    private void getItemData(Product product, Node node) {
        NodeList nodes = node.getChildNodes();
        product.setOriginId(node.getAttributes().getNamedItem("id").getNodeValue());
        product.setAvailability("+");
        for (int i = 0; i < nodes.getLength(); i++) {
            switch (nodes.item(i).getNodeName()) {
                case "vendor":
                    if (!nodes.item(i).getTextContent().startsWith(product.getVendor().getName())) {
                        product.setVendor(null);
                        return;
                    }
                    break;
                case "model":
                    product.setName(nodes.item(i).getTextContent());
                    break;
                case "description":
                    product.setDescription(nodes.item(i).getTextContent());
                    break;
                case "price":
                    product.setPrice(nodes.item(i).getTextContent());
                    break;
            }
        }
        for (int i = 0; i < nodes.getLength(); i++) {
            switch (nodes.item(i).getNodeName()) {
                case "picture":
                    product.addImage(new Image(nodes.item(i).getTextContent(), product));
                    break;
            }

        }
    }

    @Override
    protected void getItemPrice(Product product) {}

    @Override
    protected void getItemData(Product product) {}

    @Override
    protected LinkedList<String> getItemsURI(String URI) {
        return null;
    }
}
