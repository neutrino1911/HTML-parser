package ru.security59.parser;

class Target {
    private int id;
    private int categoryId;
    private int lastId;
    private int nextId;
    private int vendorId;
    private String currency;
    private String unit;
    private String url;
    private String vendorName;

    Target(int id, int categoryId, int lastId, int vendorId, String currency, String unit, String url, String vendorName) {
        this.id = id;
        this.categoryId = categoryId;
        this.lastId = lastId;
        this.vendorId = vendorId;
        this.currency = currency;
        this.unit = unit;
        this.url = url;
        this.vendorName = vendorName;
        if (lastId > 0) nextId = lastId + 1;
        else nextId = vendorId * 1000000 + categoryId * 1000 + 1;
    }

    int getId() {
        return id;
    }

    int getCategoryId() {
        return categoryId;
    }

    int getLastId() {
        return lastId;
    }

    int getNextId() {
        return nextId++;
    }

    int getVendorId() {
        return vendorId;
    }

    String getCurrency() {
        return currency;
    }

    String getUnit() {
        return unit;
    }

    String getUrl() {
        return url;
    }

    String getVendorName() {
        return vendorName;
    }
}
