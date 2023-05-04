package edu.nyit.trackmydiet.models;

import java.io.Serializable;

public class FoodItem implements Serializable {
    private String name;
    private String servingUnit;
    private Integer amount;

    public FoodItem() {

    }

    public FoodItem(String name, String servingUnit, Integer amount) {
        this.name = name;
        this.servingUnit = servingUnit;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServingUnit() {
        return servingUnit;
    }

    public void setServingUnit(String servingUnit) {
        this.servingUnit = servingUnit;
    }

    public Integer getAmount() { return amount; }

    public void setAmount(Integer amount) { this.amount = amount; }

}
