package edu.nyit.trackmydiet.interfaces;

import java.util.ArrayList;

import edu.nyit.trackmydiet.models.FoodItem;

public interface SearchFoodListener {
    void onFoundFoods(ArrayList<FoodItem> FoodItems);
    void onErrorSearchFood(String message);
}
