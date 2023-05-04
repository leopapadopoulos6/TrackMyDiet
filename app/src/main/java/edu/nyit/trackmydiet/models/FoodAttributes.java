package edu.nyit.trackmydiet.models;

import java.util.HashMap;

//TODO add comments
public class FoodAttributes implements Cloneable{
    private static final Double GRAMS_TO_OUNCES = 0.035274;
    private String name;
    private String servingUnit;
    private int servingNumber;
    private Double servingWeightOz;
    private Double calories;
    private Double totalFats;
    private Double saturatedFats;
    private Double cholesterol;
    private Double sodium;
    private Double carbs;
    private Double fiber;
    private Double sugar;
    private Double protein;
    private Double potassium;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNameCaps(String name) {
        StringBuilder sb = new StringBuilder();
        String[] words = name.split(" ");
        for(String word : words){
            if(word.charAt(0) < 65 || word.charAt(0) > 90) {
                char[] wordCharArr = word.toCharArray();
                wordCharArr[0] = Character.toUpperCase(word.charAt(0));
                sb.append(wordCharArr);
                sb.append(" ");
            }
        }
        this.name = sb.toString().trim();
    }

    public String getServingUnit() {
        return servingUnit;
    }

    public void setServingUnit(String servingUnit) {
        this.servingUnit = servingUnit;
    }

    public int getServingNumber() {
        return servingNumber;
    }

    public void setServingNumber(int servingNumber) {
        this.servingNumber = servingNumber;
    }

    public Double getServingWeightOz() {
        return servingWeightOz;
    }

    public void setServingWeight(Double servingWeight) {
        this.servingWeightOz = servingWeight;
    }

    public void setServingWeightOz(Double servingWeightGrams) {
        this.servingWeightOz = (servingWeightGrams == null) ? 0 : Math.round((servingWeightGrams * GRAMS_TO_OUNCES) * 10) / 10.0;
    }

    public Double getCalories() {
        return calories;
    }

    public void setCalories(Double calories) {
        this.calories = (calories == null) ? 0.0 : Math.round(calories * 10) / 10.0 ;
    }

    public Double getTotalFats() {
        return totalFats;
    }

    public void setTotalFats(Double totalFats) {
        this.totalFats = (totalFats == null) ? 0.0 : Math.round(totalFats * 10) / 10.0;
    }

    public Double getSaturatedFats() {
        return saturatedFats;
    }

    public void setSaturatedFats(Double saturatedFats) {
        this.saturatedFats = (saturatedFats == null) ? 0.0 : Math.round(saturatedFats * 10) / 10.0;
    }

    public Double getCholesterol() {
        return cholesterol;
    }

    public void setCholesterol(Double cholesterol) {
        this.cholesterol = (cholesterol == null) ? 0.0 : Math.round(cholesterol * 10) / 10.0;
    }

    public Double getSodium() {
        return sodium;
    }

    public void setSodium(Double sodium) {
        this.sodium = (sodium == null) ? 0.0 : Math.round(sodium * 10) / 10.0;
    }

    public Double getCarbs() {
        return carbs;
    }

    public void setCarbs(Double carbs) {
        this.carbs = (carbs == null) ? 0.0 : Math.round(carbs * 10) / 10.0;
    }

    public Double getFiber() {
        return fiber;
    }

    public void setFiber(Double fiber) {
        this.fiber = (fiber == null) ? 0.0 : Math.round(fiber * 10) / 10.0;
    }

    public Double getSugar() {
        return sugar;
    }

    public void setSugar(Double sugar) {
        this.sugar = (sugar == null) ? 0.0 : Math.round(sugar * 10) / 10.0;
    }

    public Double getProtein() {
        return protein;
    }

    public void setProtein(Double protein) {
        this.protein = (protein == null) ? 0.0 : Math.round(protein * 10) / 10.0;
    }

    public Double getPotassium() {
        return potassium;
    }

    public void setPotassium(Double potassium) {
        this.potassium = (potassium == null) ? 0.0 : Math.round(potassium  * 10) / 10.0 ;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        FoodAttributes copy = (FoodAttributes) super.clone();
        copy.setName(this.name);
        copy.setServingUnit(this.servingUnit);
        copy.setServingNumber(this.servingNumber);
        copy.setServingWeight(this.servingWeightOz);
        copy.setCalories(this.calories);
        copy.setTotalFats(this.totalFats);
        copy.setSaturatedFats(this.saturatedFats);
        copy.setCholesterol(this.cholesterol);
        copy.setSodium(this.sodium);
        copy.setCarbs(this.carbs);
        copy.setFiber(this.fiber);
        copy.setSugar(this.sugar);
        copy.setProtein(this.protein);
        copy.setPotassium(this.potassium);
        return copy;
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", this.name);
        result.put("servingUnit", this.servingUnit);
        result.put("servingNumber", (Integer) this.servingNumber);
        result.put("servingWeightOz", this.servingWeightOz);
        result.put("calories", this.calories);
        result.put("totalFats", this.totalFats);
        result.put("saturatedFats", this.saturatedFats);
        result.put("cholesterol", this.cholesterol);
        result.put("sodium", this.sodium);
        result.put("carbs", this.carbs);
        result.put("fiber", this.fiber);
        result.put("sugar", this.sugar);
        result.put("protein", this.protein);
        result.put("potassium", this.potassium);
        return result;
    }
}
