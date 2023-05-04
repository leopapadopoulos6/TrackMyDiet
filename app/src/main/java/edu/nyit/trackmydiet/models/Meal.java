package edu.nyit.trackmydiet.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import edu.nyit.trackmydiet.enums.MealType;

//TODO add comments
public class Meal implements Serializable {

    private MealType mealType;
    private Double calories;
    private HashMap<String, FoodEntry> foods;

    public Meal(MealType mealType) {
        this.mealType = mealType;
        foods = new HashMap<>();
    }

    public MealType getMealType() {
        return mealType;
    }

    public void setCalories(Double calories) {
        this.calories = calories;
    }

    public Double getCalories() {
      return calories;
    }

    public HashMap<String, FoodEntry> getFoods() {
        return foods;
    }

    public void addFoodEntry(String foodName, FoodEntry entry) {
        this.foods.put(foodName, entry);
    }

    public void removeFoodEntry(String foodName) { this.foods.remove(foodName); }

    @Override
    public String toString() {
        return this.toMap().toString();
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("mealType", this.mealType);
        result.put("foods", this.foods);
        return result;
    }
}
