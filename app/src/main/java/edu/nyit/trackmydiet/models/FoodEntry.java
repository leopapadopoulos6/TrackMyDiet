package edu.nyit.trackmydiet.models;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.HashMap;

//TODO add comments
public class FoodEntry extends FoodItem implements Serializable {

    //private String  name; //extended
    //private String  servingUnit; //extended
    //private Integer amount; //extended
    private Double  servingWeightOz;
    private Double  calories;
    private Double  cholesterol;
    private Double  carbs;
    private Double  protein;
    private Double  potassium;
    private Double  fat;
    private Double  saturatedFat;
    private Double  sodium;
    private Double  sugar;
    private Double  fiber;

    public FoodEntry(HashMap<String, Object> food) {
        super((String) food.get("name"), (String) food.get("servingUnit"), (Integer) food.get("servingNumber"));
        //this.name = (String) food.get("name");
        //this.servingUnit = (String) food.get("servingUnit");
        //this.amount = (Integer) food.get("servingNumber");
        this.servingWeightOz = (Double) food.get("servingWeightOz");
        this.calories = (Double) food.get("calories");
        this.cholesterol = (Double) food.get("cholesterol");
        this.carbs = (Double) food.get("carbs");
        this.protein = (Double) food.get("protein");
        this.potassium = (Double) food.get("potassium");
        this.fat = (Double) food.get("totalFats");
        this.fiber = (Double) food.get("fiber");
        this.saturatedFat = (Double) food.get("saturatedFats");
        this.sodium = (Double) food.get("sodium");
        this.sugar = (Double) food.get("sugar");
    }

    public FoodEntry(String name, String servingUnit, Integer amount, Double servingWeightOz, Double calories,
                     Double cholesterol, Double carbs, Double protein, Double potassium, Double fat,
                     Double saturatedFat, Double sodium, Double sugar, Double fiber) {
        //this.name = name;
        //this.servingUnit = servingUnit;
        //this.amount = amount;
        super(name, servingUnit, amount);
        this.servingWeightOz = servingWeightOz;
        this.calories = calories;
        this.cholesterol = cholesterol;
        this.carbs = carbs;
        this.protein = protein;
        this.potassium = potassium;
        this.fat = fat;
        this.saturatedFat = saturatedFat;
        this.sodium = sodium;
        this.sugar = sugar;
        this.fiber = fiber;
    }

    public String getName() {
        return super.getName();
    }

    public String getServingUnit() { return super.getServingUnit(); }

    public Integer getAmount() {
        return super.getAmount();
    }

    public Double getServingWeightOz() {
        return servingWeightOz;
    }

    public Double getCalories() {
        return calories;
    }

    public Double getCholesterol() { return cholesterol; }

    public Double getCarbs() { return carbs; }

    public Double getSaturatedFat() {
        return saturatedFat;
    }

    public Double getPotassium() {
        return potassium;
    }

    public Double getProtein() {
        return protein;
    }

    public Double getFat() {
        return fat;
    }

    public Double getSodium() {
        return sodium;
    }

    public Double getSugar() {
        return sugar;
    }

    public Double getFiber() {
        return fiber;
    }

    @NonNull
    @Override
    public String toString() {
        return this.toMap().toString();
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", super.getName());
        result.put("servingUnit", super.getServingUnit());
        result.put("amount", super.getAmount());
        result.put("servingWeightOz", this.servingWeightOz);
        result.put("calories", this.calories);
        result.put("cholesterol", this.cholesterol);
        result.put("carbs", this.carbs);
        result.put("protein", this.protein);
        result.put("potassium", this.potassium);
        result.put("fat", this.fat);
        result.put("saturatedFat", this.saturatedFat);
        result.put("sodium", this.sodium);
        result.put("sugar", this.sugar);
        result.put("fiber", this.fiber);
        return result;
    }
}
