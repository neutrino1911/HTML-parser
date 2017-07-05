package ru.security59.parser.entities;

import javax.persistence.*;

@Entity
@Table(name = "Failures")
public class Failure {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "target_id")
    private int targetId;

    @Column(name = "url", length = 1023)
    private String URL;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Failure failure = (Failure) o;

        if (targetId != failure.targetId) return false;
        return URL.equals(failure.URL);
    }

    @Override
    public int hashCode() {
        int result = targetId;
        result = 31 * result + URL.hashCode();
        return result;
    }
}
