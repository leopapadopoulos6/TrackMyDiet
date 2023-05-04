package edu.nyit.trackmydiet.models;

import java.util.HashMap;
import java.util.Map;

import edu.nyit.trackmydiet.enums.MealType;

public class MealEntry {
    private MealType mealType;
    private Double  calories;
    private Double  carbs;
    private Double  fat;
    private Double  protein;
    private Double  cholesterol;
    private Double  potassium;
    private Double  saturatedFat;
    private Double  sodium;
    private Double  sugar;
    private Double  fiber;
    private HashMap<String, FoodItem> foods;

    public MealEntry(MealType mealType) {
        this.mealType = mealType;
        foods = new HashMap<>();
    }

    public MealEntry(Meal meal) {
        this.mealType = meal.getMealType();
        foods = new HashMap<>();
        this.calories = 0.0;
        this.carbs = 0.0;
        this.fat = 0.0;
        this.protein = 0.0;
        this.cholesterol = 0.0;
        this.potassium = 0.0;
        this.saturatedFat = 0.0;
        this.sodium = 0.0;
        this.sugar = 0.0;
        this.fiber = 0.0;
        setFoodItems(meal.getFoods());
        setNutrients(meal.getFoods());
    }

    private void setNutrients(HashMap<String, FoodEntry> foods) {
        for(Map.Entry<String, FoodEntry> food : foods.entrySet()) {
            FoodEntry currentFood = food.getValue();
            this.calories += currentFood.getCalories();
            this.carbs += currentFood.getCarbs();
            this.fat += currentFood.getFat();
            this.protein += currentFood.getProtein();
            this.cholesterol += currentFood.getCholesterol();
            this.potassium += currentFood.getPotassium();
            this.saturatedFat += currentFood.getSaturatedFat();
            this.sodium += currentFood.getSodium();
            this.sugar += currentFood.getSugar();
            this.fiber += currentFood.getFiber();
       }
        this.calories = Math.round(this.calories * 10) / 10.0;
        this.carbs = Math.round(this.carbs * 10) / 10.0;
        this.fat = Math.round(this.fat * 10) / 10.0;
        this.protein = Math.round(this.protein * 10) / 10.0;
        this.cholesterol = Math.round(this.cholesterol * 10) / 10.0;
        this.potassium = Math.round(this.potassium * 10) / 10.0;
        this.saturatedFat = Math.round(this.saturatedFat * 10) / 10.0;
        this.sodium = Math.round(this.sodium * 10) / 10.0;
        this.sugar = Math.round(this.sugar * 10) / 10.0;
        this.fiber = Math.round(this.fiber * 10) / 10.0;
    }

    private void setFoodItems(HashMap<String, FoodEntry> foodEntries) {
        for(Map.Entry<String, FoodEntry> food : foodEntries.entrySet()) {
            FoodEntry currentFood = food.getValue();
            String foodItemName = currentFood.getName();
            String foodItemServingUnit = currentFood.getServingUnit();
            Integer foodItemAmount = currentFood.getAmount();
            foods.put(foodItemName, new FoodItem(foodItemName, foodItemServingUnit, foodItemAmount));
        }
    }

    public MealType getMealType() {
        return mealType;
    }

    public void setMealType(MealType mealType) {
        this.mealType = mealType;
    }

    public Double getCalories() {
        return calories;
    }

    public void setCalories(Double calories) {
        this.calories = calories;
    }

    public Double getCarbs() {
        return carbs;
    }

    public void setCarbs(Double carbs) {
        this.carbs = carbs;
    }

    public Double getFat() {
        return fat;
    }

    public void setFat(Double fat) {
        this.fat = fat;
    }

    public Double getProtein() {
        return protein;
    }

    public void setProtein(Double protein) {
        this.protein = protein;
    }

    public Double getCholesterol() {
        return cholesterol;
    }

    public void setCholesterol(Double cholesterol) {
        this.cholesterol = cholesterol;
    }

    public Double getPotassium() {
        return potassium;
    }

    public void setPotassium(Double potassium) {
        this.potassium = potassium;
    }

    public Double getSaturatedFat() {
        return saturatedFat;
    }

    public void setSaturatedFat(Double saturatedFat) {
        this.saturatedFat = saturatedFat;
    }

    public Double getSodium() {
        return sodium;
    }

    public void setSodium(Double sodium) {
        this.sodium = sodium;
    }

    public Double getSugar() {
        return sugar;
    }

    public void setSugar(Double sugar) {
        this.sugar = sugar;
    }

    public Double getFiber() {
        return fiber;
    }

    public void setFiber(Double fiber) {
        this.fiber = fiber;
    }

    public HashMap<String, FoodItem> getFoods() {
        return foods;
    }

    public void setFoods(HashMap<String, FoodItem> foods) {
        this.foods = foods;
    }
}
