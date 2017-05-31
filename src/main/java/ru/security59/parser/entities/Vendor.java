package ru.security59.parser.entities;

import javax.persistence.*;

@Entity
@Table(name = "Vendors")
public class Vendor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id") private int id;
    @Column(name = "name") private String name;
    @Column(name = "country") private String country;
    @Column(name = "warranty") private int warranty;

    public Vendor() {}

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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getWarranty() {
        return warranty;
    }

    public void setWarranty(int warranty) {
        this.warranty = warranty;
    }
}
