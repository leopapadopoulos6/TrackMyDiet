package edu.nyit.trackmydiet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import edu.nyit.trackmydiet.adapters.FoodSearchAdapter;
import edu.nyit.trackmydiet.enums.MealType;
import edu.nyit.trackmydiet.interfaces.SearchFoodListener;
import edu.nyit.trackmydiet.interfaces.SelectFoodListener;
import edu.nyit.trackmydiet.models.FoodItem;
import edu.nyit.trackmydiet.models.Meal;
import edu.nyit.trackmydiet.models.ServiceResult;
import edu.nyit.trackmydiet.tasks.SearchFoodTask;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;

import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//TODO add comments
public class SearchFoodActivity extends AppCompatActivity {
    private RecyclerView foodListRecyclerView;
    private Meal currentMeal;
    private Button searchButton;
    private EditText searchView;
    private Spinner mealTypeSpinner;
    private SearchFoodListener searchFoodListener;
    private String foodQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_food);
        currentMeal = (getIntent().hasExtra("current_meal")) ? (Meal) getIntent().getSerializableExtra("current_meal") : null;
        if(currentMeal != null) {
            Log.d("CURRENT MEAL", currentMeal.toMap().toString());
        }
        foodListRecyclerView = findViewById(R.id.foodListRecyclerView);
        foodListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mealTypeSpinner = findViewById(R.id.searchFoodMealTypeSpinner);
        searchButton = findViewById(R.id.searchFoodButton);
        initMealTypeSpinner();
        searchButton.setOnClickListener(v -> handleSearchButton());
        searchView = findViewById(R.id.searchView);
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().contains("\n")) {
                    foodQuery = s.toString().toLowerCase();
                    SearchFoodTask searchFoodTask = new SearchFoodTask(searchFoodListener);
                    searchFoodTask.execute(foodQuery);
                } else {
                    searchView.setText(s.toString().substring(0, s.toString().length() - 1));
                    searchView.setSelection(s.length() - 1);
                }
            }
        });

        searchFoodListener = new SearchFoodListener() {
            @Override
            public void onFoundFoods(ArrayList<FoodItem> foodNames) {
                SelectFoodListener selectFoodListener = new SelectFoodListener() {
                    @Override
                    public void onFoodSelected(FoodItem food) {
                        Intent intent = new Intent(getApplicationContext(), SearchResultsActivity.class);
                        intent.putExtra("foodItem", food);
                        intent.putExtra("meal_type", mealTypeSpinner.getSelectedItemPosition());
                        if(currentMeal != null) {
                            intent.putExtra("current_meal", currentMeal);
                        }
                        startActivity(intent);
                        //intent call page
                    }
                };
                FoodSearchAdapter foodSearchAdapter = new FoodSearchAdapter(foodNames, selectFoodListener);
                foodListRecyclerView.setAdapter(foodSearchAdapter);
            }

            @Override
            public void onErrorSearchFood(String message) {
                Log.d("NutritionX ERR", message);
            }
        };
    }

    private void handleSearchButton() {
            //Implement the SearchFoodListener onFoundFoods() methods
            SearchFoodListener searchButtonFoodListener = new SearchFoodListener() {
                //onFoundFoods() is called after the SearchFoodTask is executed on line 139
                @Override
                public void onFoundFoods(ArrayList<FoodItem> foodNames) {
                    String searchViewText = searchView.getText().toString().toLowerCase();
                    if(!searchViewText.isEmpty()) {
                        Intent intent = new Intent(getApplicationContext(), SearchResultsActivity.class);
                        FoodItem chosenFoodItem = getFoodItem(searchViewText, foodNames);
                        if(chosenFoodItem != null) {
                            intent.putExtra("foodItem", chosenFoodItem);
                            intent.putExtra("meal_type", mealTypeSpinner.getSelectedItemPosition());
                            if(currentMeal != null) {
                                intent.putExtra("current_meal", currentMeal);
                            }
                            startActivity(intent);
                        } else {
                            searchView.setError("Food not found");
                        }
                    } else  {
                        searchView.setError("Search cannot be empty");
                    }
                }

                @Override
                public void onErrorSearchFood(String message) {
                    Log.d("NutritionX Err", message);
                }

            };
            String foodQuery = searchView.getText().toString().trim().toLowerCase();
            SearchFoodTask searchFoodTask = new SearchFoodTask(searchButtonFoodListener);
            try {
                ServiceResult serviceResult = searchFoodTask.execute(foodQuery).get();
                serviceResult.setCode(255);
                searchFoodTask.onPostExecuteButton(serviceResult);
                searchButtonFoodListener.onFoundFoods(searchFoodTask.getAttributes());
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    private FoodItem getFoodItem(String foodName, ArrayList<FoodItem> foodItems) {
        FoodItem result = null;
        for(FoodItem item: foodItems) {
            if(item.getName().equals(foodName)) {
                result = item;
                break;
            }
        }
        return result;
    }

    private void initMealTypeSpinner() {
        List<String> dietTypeList = new ArrayList<>(Arrays.asList("Breakfast", "Lunch", "Dinner", "Snack"));
        ArrayAdapter<String> mealTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dietTypeList);
        mealTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealTypeSpinner.setAdapter(mealTypeAdapter);
        MealType chosenMealType = (MealType) getIntent().getSerializableExtra("meal_type");
        if(chosenMealType == MealType.BREAKFAST) {
            mealTypeSpinner.setSelection(0);
        } else if(chosenMealType == MealType.LUNCH) {
            mealTypeSpinner.setSelection(1);
        } else if(chosenMealType == MealType.DINNER) {
            mealTypeSpinner.setSelection(2);
        } else {
            mealTypeSpinner.setSelection(3);
        }
    }
}
