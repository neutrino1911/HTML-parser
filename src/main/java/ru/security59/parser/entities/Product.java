package ru.security59.parser.entities;

import ru.security59.parser.util.Transliterator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;
import static org.apache.commons.lang3.StringEscapeUtils.unescapeHtml4;

@Entity
@Table(name = "Products")
public class Product {
    @Id @Column(name = "id")        private int id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cat_id")    private Category category;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vend_id")   private Vendor vendor;
    @Column(name = "availability")  private String availability;
    @Column(name = "currency")      private String currency;
    @Column(name = "description")   private String description;
    @OneToMany(mappedBy = "product")private Set<Image> images;
    @Column(name = "name")          private String name;
    @Column(name = "origin_id")     private String originId;
    @Column(name = "origin_url")    private String originURL;
    @Column(name = "price")         private String price;
    @Column(name = "seo_url")       private String seoURL;
    @Column(name = "unit")          private String unit;

    public void addImage(Image image) {
        if (images == null) images = new HashSet<>(10);
        if (images.size() < 10) images.add(image);
    }

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
        this.price = price
                .replaceAll(",", ".")
                .replaceAll("[^0-9.]", "");
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
        this.currency = currency.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = escapeHtml4(description.trim());
    }

    public Set<Image> getImages() {
        if (images == null) images = new HashSet<>(10);
        return images;
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
        seoURL = seoURL
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("-+$", "");
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        if (!category.equals(product.category)) return false;
        if (!vendor.equals(product.vendor)) return false;
        if (!availability.equals(product.availability)) return false;
        if (!currency.equals(product.currency)) return false;
        if (!description.equals(product.description)) return false;
        if (!name.equals(product.name)) return false;
        if (!originId.equals(product.originId)) return false;
        if (!originURL.equals(product.originURL)) return false;
        if (!price.equals(product.price)) return false;
        if (!seoURL.equals(product.seoURL)) return false;
        return unit.equals(product.unit);
    }

    @Override
    public int hashCode() {
        int result = category.hashCode();
        result = 31 * result + vendor.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + originId.hashCode();
        result = 31 * result + originURL.hashCode();
        result = 31 * result + price.hashCode();
        result = 31 * result + seoURL.hashCode();
        result = 31 * result + unit.hashCode();
        return result;
    }

    private void addVendorToName() {
        if (!name.contains(vendor.getName()))
            name = vendor.getName() + " " + name;
    }
}
