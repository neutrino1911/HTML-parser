package ru.security59.parser.entities;

import javax.persistence.*;

@Entity
@Table(name = "Targets")
public class Target {

    @Id @Column(name = "id") private int id;
    @Column(name = "cat_id") private int categoryId;
    @Transient private int lastId;
    @Transient private int nextId;
    @Column(name = "vend_id") private int vendorId;
    @Column(name = "currency") private String currency;
    @Column(name = "unit") private String unit;
    @Column(name = "url")  private String url;
    @Transient private String vendorName;

    public Target() {

    }

    public Target(int id, int categoryId, int lastId, int vendorId, String currency, String unit, String url, String vendorName) {
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

    public int getNextId() {
        if (nextId == 0) {
            if (lastId > 0) nextId = lastId + 1;
            else nextId = vendorId * 1000000 + categoryId * 1000 + 1;
        }
        return nextId++;
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

    public int getLastId() {
        return lastId;
    }

    public void setLastId(int lastId) {
        this.lastId = lastId;
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
        this.currency = currency;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }
}
