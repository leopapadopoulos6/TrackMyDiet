package edu.nyit.trackmydiet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import edu.nyit.trackmydiet.enums.MealType;
import edu.nyit.trackmydiet.models.FoodEntry;
import edu.nyit.trackmydiet.models.FoodItem;
import edu.nyit.trackmydiet.models.FoodLog;
import edu.nyit.trackmydiet.models.Meal;
import edu.nyit.trackmydiet.models.MealEntry;

//TODO add comments
//TODO Enable users to delete foods
public class MealCreationActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{

    private Meal currentMeal;
    private View firstMealEntry;
    private TextView dateTextView;
    private TextView dayOfTheWeekTextView;
    private LinearLayout foodLogLinearLayout;
    private Toolbar mealCreationToolbar;
    private TextView mealCaloriesTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_creation);
        firstMealEntry = findViewById(R.id.foodLogFirstEntry);
        mealCreationToolbar = findViewById(R.id.mealCreationToolBar);
        TextView mealTypeTextView = findViewById(R.id.mealTypeCreateToolBarText);
        mealCaloriesTextView = findViewById(R.id.mealCreationCaloriesTextView);
        TextView foodLogTitleTextView = findViewById(R.id.mealEntryFoodLogTitle);
        dayOfTheWeekTextView = findViewById(R.id.mealCreationCurrDayTextView);
        dateTextView = findViewById(R.id.mealCreationCurrDateTextView);
        FloatingActionButton enterFoodLogFab = findViewById(R.id.enterFoodLogFab);
        foodLogLinearLayout = findViewById(R.id.mealCreatefoodEntryList);
        String chosenMealType = getMealType(getIntent());
        insertCustomToolBar();
        mealTypeTextView.setText(chosenMealType);
        foodLogTitleTextView.setText(getString(R.string.food_log_title, chosenMealType));
        dayOfTheWeekTextView.setText(getDayOfTheWeek());
        dateTextView.setText(getDate());
        updateCurrentMeal();
        enterFoodLogFab.setOnClickListener(this::initFabPopupMenu);
    }

     /*
    //Popup-Dialog
    @Override
    public void onBackPressed() {

    }
    */

    private void updateCurrentMeal() {
        if(getIntent().getSerializableExtra("current_meal") == null) {
            FoodEntry firstChosenFood = new FoodEntry((HashMap<String, Object>) getIntent().getSerializableExtra("chosen_food"));
            currentMeal = new Meal((MealType) getIntent().getSerializableExtra("meal_type"));
            Log.d("CHOSEN FOOD", firstChosenFood.toString());
            initFirstFoodEntry(firstChosenFood, firstMealEntry);
            Log.d("CURRENT MEAL FIRST FOOD", currentMeal.toString());
        } else {
            FoodEntry newFood = new FoodEntry((HashMap<String, Object>) getIntent().getSerializableExtra("chosen_food"));
            currentMeal = (Meal) getIntent().getSerializableExtra("current_meal");
            Log.d("CHOSEN FOOD", newFood.toString());
            insertNewFoodEntry(newFood);
            Log.d("CURRENT MEAL", currentMeal.toString());
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_food_menu_item:
                handleAddSomeFoodMenuItem();
                return true;
            case R.id.save_food_log_menu_item:
                Double consumedCalories = Double.parseDouble(mealCaloriesTextView.getText().toString());
                currentMeal.setCalories(consumedCalories);
                handleSaveFoodLogMenuItem(consumedCalories);
                return true;
            default:
                return false;
        }
    }

    private void handleAddSomeFoodMenuItem() {
        Intent backToFoodSearchPage = new Intent(getApplicationContext(), SearchFoodActivity.class);
        backToFoodSearchPage.putExtra("current_meal", currentMeal);
        backToFoodSearchPage.putExtra("meal_type", currentMeal.getMealType());
        startActivity(backToFoodSearchPage);
    }

    private void handleSaveFoodLogMenuItem(Double consumedCal) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUserEmail = mAuth.getCurrentUser().getEmail().replace(".", "_");
        String currentDate = getDate().replace("-", "_");
        DatabaseReference currentUser = FirebaseDatabase.getInstance().getReference().child("/users/" + currentUserEmail);
        DatabaseReference currentUserFoodLogs = FirebaseDatabase.getInstance().getReference().child("/food_logs/" + currentUserEmail);
        currentUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Object currentWeight = dataSnapshot.child("weight").getValue();
                currentUserFoodLogs.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //If Food log doesn't exist for the current day
                        if(dataSnapshot.getValue() instanceof Long || !dataSnapshot.child(currentDate).exists()) {
                            FoodLog foodLogToAdd = new FoodLog(consumedCal, currentWeight);
                            MealEntry currentMealEntry = new MealEntry(currentMeal);
                            foodLogToAdd.addMealEntry(currentMealEntry.getMealType().toString().toLowerCase(), currentMealEntry);
                            foodLogToAdd.setConsumedProtein(currentMealEntry.getProtein());
                            foodLogToAdd.setConsumedFat(currentMealEntry.getFat());
                            foodLogToAdd.setConsumedCarbs(currentMealEntry.getCarbs());
                            Log.d("saveFoodOnData", "Food log equals -1 or date doesn't exist");
                            currentUserFoodLogs.child(currentDate).setValue(foodLogToAdd).addOnCompleteListener(task -> {
                                if(task.isSuccessful()) {
                                    Intent backToHome = new Intent(getApplicationContext(), NavigationActivity.class);
                                    startActivity(backToHome);
                                } else {
                                    Toast.makeText(getApplicationContext(), "Food Log Save Failed", Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            Log.e("saveFoodOnData", "Food log key exists");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("saveFoodOnCancelled", databaseError.toString());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("saveFoodUserOnCancelled", databaseError.toString());
            }
        });
    }

    private void insertNewFoodEntry(FoodEntry newFood) {
        currentMeal.addFoodEntry(newFood.getName(), newFood);
        Log.d("NEW_FOOD", newFood.toMap().toString());
        Log.d("NEWLY_UPDATED_MEAL", currentMeal.toString());
        Double updatedCalories = Double.parseDouble(mealCaloriesTextView.getText().toString());

        for(Map.Entry<String, FoodEntry> food : currentMeal.getFoods().entrySet()) {
            //Creating a new View from xml file
            LinearLayout newFoodEntryParent = (LinearLayout) LayoutInflater.from(getApplicationContext()).inflate(R.layout.homepage_meal_entry, null);
            newFoodEntryParent.setMinimumHeight(350);
            //Gets Linear Layout containing TextViews and Pie Charts
            LinearLayout newFoodEntryView = (LinearLayout) newFoodEntryParent.getChildAt(1);
            addNewFoodEntryUI(food.getValue(), newFoodEntryView);
            foodLogLinearLayout.addView(newFoodEntryParent);
            updatedCalories += food.getValue().getCalories();
        }
        updatedCalories = Math.round(updatedCalories * 10.0) / 10.0;
        mealCaloriesTextView.setText(Double.toString(updatedCalories));
        firstMealEntry.setVisibility(View.GONE);
    }

    private void addNewFoodEntryUI(FoodEntry foodEntry, LinearLayout foodEntryView) {
        double carbs   = foodEntry.getCarbs();
        double fat     = foodEntry.getFat();
        double protein = foodEntry.getProtein();
        int colorBlack = Color.parseColor("#000000");

        createPieChart(carbs, fat, protein, colorBlack, foodEntryView);
        TextView foodNameTextView = foodEntryView.getChildAt(0).findViewById(R.id.mealEntryFoodNameTextView);
        TextView foodDescriptionTextView = foodEntryView.getChildAt(0).findViewById(R.id.mealEntryDescripTextView);
        foodNameTextView.setTypeface(Typeface.DEFAULT_BOLD);
        foodNameTextView.setText(foodEntry.getName());
        foodDescriptionTextView.setText(getString(R.string.food_entry_desc, foodEntry.getAmount().toString(), foodEntry.getCalories().toString()));
    }

    private void initFirstFoodEntry(FoodEntry firstChosenFood, View firstMealEntry) {
        double carbs   = firstChosenFood.getCarbs();
        double fat     = firstChosenFood.getFat();
        double protein = firstChosenFood.getProtein();
        int colorBlack = Color.parseColor("#000000");
        mealCaloriesTextView.setText(firstChosenFood.getCalories().toString());

        createPieChart(carbs, fat, protein, colorBlack, firstMealEntry);
        TextView foodNameTextView = firstMealEntry.findViewById(R.id.mealEntryFoodNameTextView);
        TextView foodDescriptionTextView = firstMealEntry.findViewById(R.id.mealEntryDescripTextView);
        foodNameTextView.setTypeface(Typeface.DEFAULT_BOLD);
        foodNameTextView.setText(firstChosenFood.getName());
        foodDescriptionTextView.setText(getString(R.string.food_entry_desc, firstChosenFood.getAmount().toString(), firstChosenFood.getCalories().toString()));
        if(currentMeal.getFoods().size() == 0) {
            currentMeal.addFoodEntry(firstChosenFood.getName(), firstChosenFood);
        }
    }

    private void createPieChart(double carbs, double fat, double protein, int color, View mealEntryView) {
        //Pie Chart entries
        List<PieEntry> macroNutrients = new ArrayList<>();
        macroNutrients.add(new PieEntry((float) carbs, "Carbs"));
        macroNutrients.add(new PieEntry((float) fat, "Fat"));
        macroNutrients.add(new PieEntry((float) protein, "Protein"));
        //Creating Pie Chart Object
        PieChart pieChart = mealEntryView.findViewById(R.id.pieChart);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        //Creating PieChart data
        PieDataSet dataSet = new PieDataSet(macroNutrients, "");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData pieStats = new PieData(dataSet);
        pieStats.setValueTextSize(9f);
        pieChart.setEntryLabelColor(color);
        pieChart.setEntryLabelTextSize(9f);
        pieChart.setData(pieStats);
        pieChart.setDrawEntryLabels(false);
        pieChart.animateXY(1100, 1100);
    }

    private void initFabPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(MealCreationActivity.this, v);
        popupMenu.setOnMenuItemClickListener(MealCreationActivity.this);
        popupMenu.setGravity(Gravity.END);
        popupMenu.inflate(R.menu.food_log_popup_menu);
        popupMenu.show();
    }

    private void insertCustomToolBar() {
        Objects.requireNonNull(getSupportActionBar()).setCustomView(mealCreationToolbar);
        getSupportActionBar().hide();
    }

    /*
     * Capitalizes the first letter of the meal_type sent from the previous activity
     * @params [Intent intent]
     * @return [String]
     */
    private String getMealType(Intent intent) {
        String currentMeal = intent.getSerializableExtra("meal_type").toString().toLowerCase();
        return currentMeal.replace(currentMeal.charAt(0), Character.toUpperCase(currentMeal.charAt(0)));
    }

    private String getDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
        return simpleDateFormat.format(new Date());
    }

    private String getDayOfTheWeek() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        switch(day) {
            case Calendar.SUNDAY:
                return "Sun,";
            case Calendar.MONDAY:
                return "Mon,";
            case Calendar.TUESDAY:
                return "Tues,";
            case Calendar.WEDNESDAY:
                return "Wed,";
            case Calendar.THURSDAY:
                return "Thurs,";
            case Calendar.FRIDAY:
                return "Fri,";
            case Calendar.SATURDAY:
                return "Sat,";
            default:
                return null;
        }
    }
}
