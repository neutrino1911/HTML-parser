package ru.security59.parser.entities;

import javax.persistence.*;

@Entity
@Table(name = "Images")
public class Image {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id") private int id;
    @Column(name = "name") private String name;
    @Column(name = "url") private String url;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prod_id") private Product product;

    public Image() {}

    public Image(String url, Product product) {
        this.url = url;
        this.product = product;
        this.name = String.format("%s-%d.jpg", product.getSeoURL(), product.getImages().size());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Image image = (Image) o;

        if (!name.equals(image.name)) return false;
        return url.equals(image.url);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + url.hashCode();
        return result;
    }
}
