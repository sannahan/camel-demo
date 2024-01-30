package org.test;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class Measurement extends PanacheEntity {
    private Integer year;
    private Integer coalConsumption;

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getCoalConsumption() {
        return coalConsumption;
    }

    public void setCoalConsumption(Integer coalConsumption) {
        this.coalConsumption = coalConsumption;
    }
}
