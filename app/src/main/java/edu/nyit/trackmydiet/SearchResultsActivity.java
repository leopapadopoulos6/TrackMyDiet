package edu.nyit.trackmydiet;

import androidx.appcompat.app.AppCompatActivity;

import edu.nyit.trackmydiet.enums.MealType;
import edu.nyit.trackmydiet.interfaces.SearchNutrientsListener;
import edu.nyit.trackmydiet.models.FoodAttributes;
import edu.nyit.trackmydiet.models.FoodItem;
import edu.nyit.trackmydiet.models.Meal;
import edu.nyit.trackmydiet.tasks.SearchNutrientsTask;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//TODO add comments
public class SearchResultsActivity extends AppCompatActivity {
    private int quantity = 1;

    private Meal currentMeal;
    private Button addToMealButton;
    private ElegantNumberButton elegantNumberButton;
    private FoodAttributes chosenFood;
    private Spinner mealTypeSpinner;
    private TextView name;
    private TextView servingWeight;
    private TextView calories;
    private TextView totalFats;
    private TextView saturatedFats;
    private TextView cholesterol;
    private TextView sodium;
    private TextView carbs;
    private TextView fiber;
    private TextView sugar;
    private TextView protein;
    private TextView potassium;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        addToMealButton = findViewById(R.id.addToMealButton);
        mealTypeSpinner = findViewById(R.id.searchResultsMealTypeSpinner);
        elegantNumberButton = findViewById(R.id.searchResultsNumberButton);
        elegantNumberButton.setNumber("1");
        name = findViewById(R.id.foodNameTextView);
        servingWeight = findViewById(R.id.servingWeightTextView);
        calories = findViewById(R.id.caloriesTextView);
        totalFats = findViewById(R.id.totalFatsTextView);
        saturatedFats = findViewById(R.id.saturatedFatsTextView);
        cholesterol = findViewById(R.id.cholesterolTextView);
        sodium = findViewById(R.id.sodiumTextView);
        carbs = findViewById(R.id.carbsTextView);
        fiber = findViewById(R.id.fiberTextView);
        sugar = findViewById(R.id.sugarTextView);
        protein = findViewById(R.id.proteinTextView);
        potassium = findViewById(R.id.potassiumTextView);
        currentMeal = (getIntent().hasExtra("current_meal")) ? (Meal) getIntent().getSerializableExtra("current_meal") : null;
        if(currentMeal != null) {
            Log.d("CURRENT MEAL", currentMeal.toMap().toString());
        }
        initMealTypeSpinner();
        FoodItem foodItem = (FoodItem) getIntent().getSerializableExtra("foodItem");

        addToMealButton.setOnClickListener(v -> {
            MealType chosenMeal = getChosenMeal();
            Intent goToMealCreationPage = new Intent(getApplicationContext(), MealCreationActivity.class);
            goToMealCreationPage.putExtra("meal_type", chosenMeal);
            Log.d("beforeSetting", "onCreate: " + chosenFood.toMap().toString());
            setChosenFoodValues();
            Log.d("afterSetting", "onCreate: " + chosenFood.toMap().toString());
            goToMealCreationPage.putExtra("chosen_food", chosenFood.toMap());
            if(currentMeal != null) {
                goToMealCreationPage.putExtra("current_meal", currentMeal);
            }
            startActivity(goToMealCreationPage);
        });

        SearchNutrientsListener searchNutrientsListener = new SearchNutrientsListener() {
            @Override
            public void onFoundNutrients(FoodAttributes foodAttribute) throws CloneNotSupportedException {
                updateList(foodAttribute);
                foodAttribute.setServingNumber(quantity);
                chosenFood = (FoodAttributes) foodAttribute.clone(); //causing a problem
                Log.d("chosenFoodClone", chosenFood.toMap().toString());
                elegantNumberButton.setOnValueChangeListener((view, oldValue, newValue) -> {
                    Log.d("chosenFoodClone", chosenFood.toMap().toString());
                    if(oldValue < newValue) {
                       quantity++;
                       foodAttribute.setServingNumber(quantity);
                       updateList(foodAttribute);
                   } else {
                       if(newValue == 0) {
                           elegantNumberButton.setNumber("1");
                           quantity = 1;
                           foodAttribute.setServingNumber(quantity);
                           updateList(foodAttribute);
                       } else {
                           quantity--;
                           foodAttribute.setServingNumber(quantity);
                           updateList(foodAttribute);
                       }
                   }
               });
            }

            @Override
            public void onErrorSearchNutrients(String message) {
                Log.d("errSearchNutrient", message);
            }
        };

        SearchNutrientsTask searchNutrientTask = new SearchNutrientsTask(searchNutrientsListener);
        searchNutrientTask.execute(foodItem.getName());
    }

    private MealType getChosenMeal() {
        if(mealTypeSpinner.getSelectedItemPosition() == 0) {
            return MealType.BREAKFAST;
        } else if(mealTypeSpinner.getSelectedItemPosition() == 1) {
            return MealType.LUNCH;
        } else if(mealTypeSpinner.getSelectedItemPosition() == 2) {
            return MealType.DINNER;
        } else {
            return MealType.SNACK;
        }
    }

    private void setChosenFoodValues() {
      chosenFood.setServingNumber(quantity);
      chosenFood.setCalories(Double.parseDouble(calories.getText().toString()));
      chosenFood.setTotalFats(Double.parseDouble(totalFats.getText().toString()));
      chosenFood.setSaturatedFats(Double.parseDouble(saturatedFats.getText().toString()));
      chosenFood.setCholesterol(Double.parseDouble(cholesterol.getText().toString()));
      chosenFood.setSodium(Double.parseDouble(sodium.getText().toString()));
      chosenFood.setCarbs(Double.parseDouble(carbs.getText().toString()));
      chosenFood.setFiber(Double.parseDouble(fiber.getText().toString()));
      chosenFood.setSugar(Double.parseDouble(sugar.getText().toString()));
      chosenFood.setPotassium(Double.parseDouble(potassium.getText().toString()));
      chosenFood.setProtein(Double.parseDouble(protein.getText().toString()));
    }

    private void updateList(FoodAttributes foodAttribute) {
        name.setText(foodAttribute.getName());
        servingWeight.setText(Double.toString(foodAttribute.getServingWeightOz()));
        calories.setText(Double.toString(Math.round(foodAttribute.getCalories() * quantity * 10) / 10.0));
        totalFats.setText(Double.toString(Math.round(foodAttribute.getTotalFats() * quantity * 10) / 10.0));
        saturatedFats.setText(Double.toString(Math.round(foodAttribute.getSaturatedFats() * quantity * 10) / 10.0));
        cholesterol.setText(Double.toString(Math.round(foodAttribute.getCholesterol() * quantity * 10)/10.0));
        sodium.setText(Double.toString(Math.round(foodAttribute.getSodium() * quantity * 10) / 10.0));
        carbs.setText(Double.toString(Math.round(foodAttribute.getCarbs() * quantity * 10)/ 10.0));
        fiber.setText(Double.toString(Math.round(foodAttribute.getFiber() * quantity * 10) / 10.0));
        sugar.setText(Double.toString(Math.round(foodAttribute.getSugar() * quantity * 10) / 10.0));
        protein.setText(Double.toString(Math.round(foodAttribute.getProtein() * quantity * 10) / 10.0));
        potassium.setText(Double.toString(Math.round(foodAttribute.getPotassium() * quantity * 10) / 10.0));
    }

    private void initMealTypeSpinner() {
        List<String> dietTypeList = new ArrayList<>(Arrays.asList("Breakfast", "Lunch", "Dinner", "Snack"));
        ArrayAdapter<String> mealTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dietTypeList);
        mealTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealTypeSpinner.setAdapter(mealTypeAdapter);
        int chosenMealType = (int) getIntent().getSerializableExtra("meal_type");
        if(chosenMealType == 0) {
            mealTypeSpinner.setSelection(0);
        } else if(chosenMealType == 1) {
            mealTypeSpinner.setSelection(1);
        } else if(chosenMealType == 2) {
            mealTypeSpinner.setSelection(2);
        } else {
            mealTypeSpinner.setSelection(3);
        }
    }
}
