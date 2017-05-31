package ru.security59.parser.entities;

import javax.persistence.*;

@Entity
@Table(name = "Targets")
public class Target {
    @Id @Column(name = "id") private int id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cat_id") private Category category;
    @Column(name = "last_id")  private int lastId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vend_id") private Vendor vendor;
    @Column(name = "currency") private String currency;
    @Column(name = "unit") private String unit;
    @Column(name = "url")  private String url;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Target target = (Target) o;

        if (!category.equals(target.category)) return false;
        if (!vendor.equals(target.vendor)) return false;
        if (!currency.equals(target.currency)) return false;
        if (!unit.equals(target.unit)) return false;
        return url.equals(target.url);
    }

    @Override
    public int hashCode() {
        int result = category.hashCode();
        result = 31 * result + vendor.hashCode();
        result = 31 * result + currency.hashCode();
        result = 31 * result + unit.hashCode();
        result = 31 * result + url.hashCode();
        return result;
    }

    public int getNextId() {
        if (lastId == 0)  lastId = vendor.getId() * 1000000 + category.getId() * 1000;
        return ++lastId;
    }
}
