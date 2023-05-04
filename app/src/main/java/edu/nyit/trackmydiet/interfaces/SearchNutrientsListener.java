package edu.nyit.trackmydiet.interfaces;


import edu.nyit.trackmydiet.models.FoodAttributes;

public interface SearchNutrientsListener {
    void onFoundNutrients(FoodAttributes FoodItems) throws CloneNotSupportedException;
    void onErrorSearchNutrients(String message);
}
