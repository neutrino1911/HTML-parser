package ru.security59.parser.entities;

import javax.persistence.*;

@Entity
@Table(name = "Categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id") private int id;
    @Column(name = "cat_name") private String name;
    @Column(name = "tiu_id") private int tiuId;
    @Column(name = "uv_id") private int warrant;
}
