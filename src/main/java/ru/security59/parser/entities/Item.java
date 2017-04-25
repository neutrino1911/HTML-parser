package ru.security59.parser.entities;

import ru.security59.parser.util.Transliterator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;
import static org.apache.commons.lang3.StringEscapeUtils.unescapeHtml4;

@Entity
@Table(name = "Products")
public class Item {

    @Id @Column(name = "prod_id") private int id;
    @Column(name = "cat_id") private int categoryId;
    @Column(name = "vend_id") private int vendorId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vend_id") private Vendor vendor;
    @Column(name = "availability") private String availability;
    @Column(name = "currency") private String currency;
    @Column(name = "prod_desc") private String description;
    @OneToMany(mappedBy = "item") private Set<Image> images;
    @Column(name = "prod_name") private String name;
    @Column(name = "origin_id") private String originId;
    @Column(name = "origin_url") private String originURL;
    @Column(name = "price") private String price;
    @Column(name = "seo_url") private String seoURL;
    @Column(name = "unit") private String unit;
    @Transient private String vendorName;

    public Item() {}

    public Item(int categoryId, int vendorId, String currency, String originURL, String unit, String vendorName) {
        this.categoryId = categoryId;
        this.vendorId = vendorId;
        this.currency = currency;
        this.originURL = originURL;
        this.unit = unit;
        this.vendorName = vendorName;
        images = new HashSet<>();
    }

    public Item(int id, String price, String availability, String originUrl) {
        this.id = id;
        this.price = price;
        this.availability=availability;
        this.originURL = originUrl;
    }

    public void addImage(Image image) {
        if (images.size() < 10) images.add(image);
    }

    public void addImage(String image) {}

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price.replaceAll(",", ".").replaceAll("[^0-9.]", "");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = escapeHtml4(description.trim());
    }

    public Set<Image> getImages() {
        return images;
    }

    public Set<String> getImages2() {
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        setName(name, true);
    }

    public void setName(String name, boolean addVendor) {
        this.name = escapeHtml4(name.trim());
        if (addVendor) addVendorToName();

        seoURL = Transliterator.cyr2lat(unescapeHtml4(this.name.toLowerCase()));
        seoURL = seoURL.replaceAll("\\W", "-").replaceAll("-+", "-").replaceAll("-$", "");
    }

    public String getOriginId() {
        return originId;
    }

    public void setOriginId(String originId) {
        this.originId = originId;
    }

    public String getOriginURL() {
        return originURL;
    }

    public void setOriginURL(String originURL) {
        this.originURL = originURL;
    }

    public String getSeoURL() {
        return seoURL;
    }

    public void setSeoURL(String seoURL) {
        this.seoURL = seoURL;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getInsertQuery() {
        String queryTables = "prod_id, prod_name, price, currency, unit, cat_id, " +
                "vend_id, prod_desc, availability, seo_url, origin_url, origin_id";

        String queryValues = String.format("%d, '%s', %s, '%s', '%s', %d, %d, '%s', '%s', '%s', '%s', %s",
                id,
                name,
                price,
                currency,
                unit,
                categoryId,
                vendorId,
                description,
                availability,
                seoURL,
                originURL,
                originId
                );

        return String.format("INSERT INTO Products (%s) VALUES (%s);", queryTables, queryValues);
    }

    public String getUpdateQuery() {
        StringBuilder query = new StringBuilder();

        query.append("UPDATE Products SET ");

        query.append("prod_name = '");
        query.append(name);
        query.append("'");

        query.append(", price = ");
        query.append(price);

        query.append(", currency = '");
        query.append(currency);
        query.append("'");

        query.append(", unit = '");
        query.append(unit);
        query.append("'");

        query.append(", cat_id = ");
        query.append(categoryId);

        query.append(", vend_id = ");
        query.append(vendorId);

        query.append(", prod_desc = '");
        query.append(description);
        query.append("'");

        query.append(", availability = '");
        query.append(availability);
        query.append("'");

        query.append(", seo_url = '");
        query.append(seoURL);
        query.append("'");

        query.append(", origin_url = '");
        query.append(originURL);
        query.append("'");

        query.append(", origin_id = ");
        query.append(originId);

        query.append(" WHERE prod_id = ");
        query.append(id);
        query.append(";");

        return query.toString();
    }

    private void addVendorToName() {
        if (!name.contains(vendorName)) name = vendorName + " " + name;
    }
}
