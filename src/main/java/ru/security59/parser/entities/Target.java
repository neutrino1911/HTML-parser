package ru.security59.parser.entities;

import javax.persistence.*;

@Entity
@Table(name = "Targets")
public class Target {
    @Id @Column(name = "id") private int id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cat_id") private Category category;
    @Transient private int lastId;
    @Transient private int nextId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vend_id") private Vendor vendor;
    @Column(name = "currency") private String currency;
    @Column(name = "unit") private String unit;
    @Column(name = "url")  private String url;

    public Target() {}

    public Target(int id, Category category, int lastId, Vendor vendor, String currency, String unit, String url) {
        this.id = id;
        this.category = category;
        this.lastId = lastId;
        this.vendor = vendor;
        this.currency = currency;
        this.unit = unit;
        this.url = url;
        if (lastId > 0) nextId = lastId + 1;
        else nextId = vendor.getId() * 1000000 + category.getId() * 1000 + 1;
    }

    public int getNextId() {
        if (nextId == 0) {
            if (lastId > 0) nextId = lastId + 1;
            else nextId = vendor.getId() * 1000000 + category.getId() * 1000 + 1;
        }
        return nextId++;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public int getLastId() {
        return lastId;
    }

    public void setLastId(int lastId) {
        this.lastId = lastId;
    }

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
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
}
