package ru.security59.parser.entities;

import javax.persistence.*;

@Entity
@Table(name = "Categories")
public class Category {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id") private int id;
    @Column(name = "name") private String name;
    @Column(name = "tiu_id") private int tiuId;
    @Column(name = "uv_id") private int uvId;
    @Column(name = "tiu_cat_id") private int tiuCatId;

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

    public int getTiuId() {
        return tiuId;
    }

    public void setTiuId(int tiuId) {
        this.tiuId = tiuId;
    }

    public int getUvId() {
        return uvId;
    }

    public void setUvId(int uvId) {
        this.uvId = uvId;
    }

    public int getTiuCatId() {
        return tiuCatId;
    }

    public void setTiuCatId(int tiuCatId) {
        this.tiuCatId = tiuCatId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Category category = (Category) o;

        if (tiuId != category.tiuId) return false;
        if (uvId != category.uvId) return false;
        if (tiuCatId != category.tiuCatId) return false;
        return name.equals(category.name);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + tiuId;
        result = 31 * result + uvId;
        result = 31 * result + tiuCatId;
        return result;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", tiuId=" + tiuId +
                ", uvId=" + uvId +
                ", tiuCatId=" + tiuCatId +
                '}';
    }
}
