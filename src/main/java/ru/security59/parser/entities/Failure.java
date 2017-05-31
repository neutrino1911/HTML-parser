package ru.security59.parser.entities;

import javax.persistence.*;

@Entity
@Table(name = "Failures")
public class Failure {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id") private int id;
    @Column(name = "target_id") private int targetId;
    @Column(name = "url") private String URL;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }
}
