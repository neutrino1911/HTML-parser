package ru.security59.parser;

import java.util.LinkedList;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

class Item {
    private int id;
    private int categoryId;
    private int vendorId;
    private String availability;
    private String currency;
    private String description;
    private LinkedList<String> images;
    private String name;
    private String originURL;
    private String price;
    private String seoURL;
    private String unit;
    private String vendorName;

    Item(int categoryId, int vendorId, String currency, String originURL, String unit, String vendorName) {
        this.categoryId = categoryId;
        this.vendorId = vendorId;
        this.currency = currency;
        this.originURL = originURL;
        this.unit = unit;
        this.vendorName = vendorName;
        images = new LinkedList<>();
    }

    Item(int id, String price, String availability, String originUrl) {
        this.id = id;
        this.price = price;
        this.availability=availability;
        this.originURL = originUrl;
    }

    void addImage(String image) {
        images.add(image);
    }

    String getAvailability() {
        return availability;
    }

    void setAvailability(String availability) {
        this.availability = availability;
    }

    String getPrice() {
        return price;
    }

    void setPrice(String price) {
        this.price = price.replaceAll(",", ".").replaceAll("[^0-9.]", "");
    }

    int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }

    int getCategoryId() {
        return categoryId;
    }

    void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    int getVendorId() {
        return vendorId;
    }

    void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    String getCurrency() {
        return currency;
    }

    void setCurrency(String currency) {
        this.currency = currency;
    }

    String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = escapeHtml4(description.trim());
    }

    LinkedList<String> getImages() {
        return images;
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        setName(name, true);
    }

    void setName(String name, boolean addVendor) {
        this.name = escapeHtml4(name.trim());
        if (addVendor) addVendorToName();

        seoURL = Transliterator.cyr2lat(this.name.toLowerCase());
        seoURL = seoURL.replaceAll("\\W", "-").replaceAll("-+", "-").replaceAll("-$", "");
    }

    String getOriginURL() {
        return originURL;
    }

    void setOriginURL(String originURL) {
        this.originURL = originURL;
    }

    String getSeoURL() {
        return seoURL;
    }

    void setSeoURL(String seoURL) {
        this.seoURL = seoURL;
    }

    String getUnit() {
        return unit;
    }

    void setUnit(String unit) {
        this.unit = unit;
    }

    String getVendorName() {
        return vendorName;
    }

    void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    String getInsertQuery() {
        String queryTables = "prod_id, prod_name, price, currency, unit, cat_id, " +
                "vend_id, prod_desc, availability, seo_url, origin_url";

        String queryValues = String.format("%d, '%s', %s, '%s', '%s', %d, %d, '%s', '%s', '%s', '%s'",
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
                originURL);

        return String.format("INSERT INTO Products (%s) VALUES (%s);", queryTables, queryValues);
    }

    String getUpdateQuery() {
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

        query.append(" WHERE prod_id = ");
        query.append(id);
        query.append(";");

        return query.toString();
    }

    private void addVendorToName() {
        if (!name.contains(vendorName)) name = vendorName + " " + name;
    }
}
