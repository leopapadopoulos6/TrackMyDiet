package edu.nyit.trackmydiet.ui.home;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import edu.nyit.trackmydiet.R;

public class HomeFragment extends Fragment {

    //Firebase Database and Auth Instances
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    //View Instance Variables
    private LinearLayout foodDiaryLinearLayout;
    private TextView dateTextView;
    private TextView caloriesLeftTextView;
    private TextView caloriesConsumedTextView;
    private TextView waterCupsTextView;
    private TextView waterOuncesTextView;
    private TextView weightLbsTextView;
    private TextView weightKgTextView;
    private ProgressBar caloriesProgressBar;
    private ProgressBar proteinProgressBar;
    private ProgressBar fatProgressBar;
    private ProgressBar carbProgressBar;
    private TextView protProgTextView;
    private TextView carbProgTextView;
    private TextView fatProgTextView;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.nav_home);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        foodDiaryLinearLayout = root.findViewById(R.id.homeFoodDiaryLinearLayout);
        dateTextView = root.findViewById(R.id.homeDateTextView);
        caloriesLeftTextView = root.findViewById(R.id.homeCalLeftTextView);
        caloriesConsumedTextView = root.findViewById(R.id.homeCalConsTextView);
        waterCupsTextView = root.findViewById(R.id.homeWaterCupsTextView);
        waterOuncesTextView = root.findViewById(R.id.homeWaterOuncesTextView);
        weightLbsTextView = root.findViewById(R.id.homeWeightLbsTextView);
        weightKgTextView = root.findViewById(R.id.homeWeigthKgTextView);
        caloriesProgressBar = root.findViewById(R.id.homeCalProgressBar);
        proteinProgressBar = root.findViewById(R.id.homeProtProgBar);
        fatProgressBar = root.findViewById(R.id.homeFatProgBar);
        carbProgressBar = root.findViewById(R.id.homeCarbProgBar);
        protProgTextView = root.findViewById(R.id.homeProtProgTextView);
        carbProgTextView = root.findViewById(R.id.homeCarbProgTextView);
        fatProgTextView = root.findViewById(R.id.homeFatProgTextView);
        setCurrentDate();
        checkIfFirstSignIn();
        return root;
    }

    /*
     * Checks if it is the users first time signing in
     * Queries firebase to check if isFirstSignIn = true
     * If the firstSignIn field is true, then the nutrition calculator activity starts
     * If the firstSignIn field is false, then the homepage is brought up with user data
     * @params [no params]
     * @return [void]
     */
    private void checkIfFirstSignIn() {
      DatabaseReference dbUsers = mDatabase.child("/users");
      dbUsers.addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              if(mAuth.getCurrentUser() != null) {
                  String currentUserEmail = mAuth.getCurrentUser().getEmail().replace(".", "_");
                  HashMap<String, Object> currentUser = (HashMap<String, Object>) dataSnapshot.child(currentUserEmail).getValue();
                  if ((boolean) currentUser.get("firstSignIn")) {
                      initFoodLog();
                      startActivity(new Intent(HomeFragment.this.getActivity(), NutritionInfoActivity.class));
                  } else {
                      setHomePageValues(currentUserEmail, currentUser);
                  }
              }
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {
              Log.d("chkFirstSgnIn:onCancel", databaseError.toException().toString());
          }
      });
    }

    /*
     * Sets the daily nutrient values on the homepage
     * depending on the food logs that are saved in the database
     * @params [String currentEmail, HashMap<String, Object> currentUser]
     * @return void
     */
    private void setHomePageValues(String currentUserEmail, HashMap<String, Object> currentUser) {
        DatabaseReference dbFoodLogs = mDatabase.child("/food_logs").child(currentUserEmail);
        String currentDate = getDate();
        dbFoodLogs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    Object currentUserFoodLogs = dataSnapshot.getValue();
                    if(currentUserFoodLogs instanceof Long) {
                        setDefaultValues(currentUser);
                    } else if(dataSnapshot.child(currentDate).exists()) {
                        setUpdatedValues(currentUser, dataSnapshot.child(currentDate));
                        addMealsToHomePageUI(dataSnapshot.child(currentDate));
                    } else {
                        setDefaultValues(currentUser);
                    }
                } catch(NullPointerException e) {
                    Log.d("EXCEPTION THROWN", "Food log does not exist for " + currentUserEmail);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addMealsToHomePageUI(DataSnapshot dataSnapshot) {
        HashMap<String, Object> meals = (HashMap<String, Object>) dataSnapshot.child("mealEntries").getValue();
        assert meals != null;
        for(Map.Entry meal : meals.entrySet()) {
            String mealName = meal.getKey().toString();
            mealName = mealName.replace(mealName.charAt(0), Character.toUpperCase(mealName.charAt(0)));
            String mealCalories = ((HashMap) meal.getValue()).get("calories").toString();
            double carbs = Double.parseDouble(((HashMap) meal.getValue()).get("carbs").toString());
            double fat = Double.parseDouble(((HashMap) meal.getValue()).get("fat").toString());
            double protein = Double.parseDouble(((HashMap) meal.getValue()).get("protein").toString());
            int colorBlack = Color.parseColor("#000000");

            //Creating a new View from xml file
            LinearLayout newFoodEntryParent = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.homepage_meal_entry, null);
            //Gets Linear Layout containing TextViews and Pie Charts
            LinearLayout newFoodEntryView = (LinearLayout) newFoodEntryParent.getChildAt(1);
            newFoodEntryView.setMinimumHeight(350);
            TextView mealNameTextView = newFoodEntryView.getChildAt(0).findViewById(R.id.mealEntryFoodNameTextView);
            TextView mealCalTextView = newFoodEntryView.getChildAt(0).findViewById(R.id.mealEntryDescripTextView);
            mealNameTextView.setText(mealName);
            mealNameTextView.setTypeface(Typeface.DEFAULT_BOLD);
            mealNameTextView.setTextSize(20);
            mealCalTextView.setText(getString(R.string.home_meal_entry_cal, mealCalories));
            createPieChart(carbs, fat, protein, colorBlack, newFoodEntryView);
            foodDiaryLinearLayout.addView(newFoodEntryParent, foodDiaryLinearLayout.getChildCount() - 1);
        }

    }

    private void createPieChart(double carbs, double fat, double protein, int color, LinearLayout mealEntryView) {
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
        pieChart.setMinimumHeight(350);
        pieChart.animateXY(1100, 1100);
    }

    /*
     * Sets the default nutrient values on the homepage
     * @params [HashMap<String, Object> currentUser]
     * @return [void]
     */
    private void setDefaultValues(HashMap<String, Object> currentUser) {
        caloriesLeftTextView.setText(currentUser.get("dailyCalories").toString());
        caloriesProgressBar.setProgress(0);
        caloriesConsumedTextView.setText("0");
        proteinProgressBar.setProgress(0);
        fatProgressBar.setProgress(0);
        carbProgressBar.setProgress(0);
        protProgTextView.setText(getString(R.string.homepage_macros, "0", currentUser.get("dailyProtein")));
        fatProgTextView.setText(getString(R.string.homepage_macros, "0", currentUser.get("dailyFat")));
        carbProgTextView.setText(getString(R.string.homepage_macros, "0", currentUser.get("dailyCarbs")));
        waterCupsTextView.setText("0");
        waterOuncesTextView.setText("0.0");
        DecimalFormat df = new DecimalFormat("###.##");
        if(currentUser.get("weight") instanceof  Double) {
            weightLbsTextView.setText(df.format(currentUser.get("weight")));
            weightKgTextView.setText(df.format(Math.round(((Double)currentUser.get("weight") * 0.45) * 10.0) / 10.0));
        } else {
            weightLbsTextView.setText(df.format(currentUser.get("weight")));
            weightKgTextView.setText(df.format(Math.round(((Long)currentUser.get("weight") * 0.45) * 10.0) / 10.0));
        }
    }

    private void setUpdatedValues(HashMap<String, Object> currentUser, DataSnapshot foodLogData) {
      double dailyCalories = Double.parseDouble(currentUser.get("dailyCalories").toString());
      double dailyProtein = Double.parseDouble(currentUser.get("dailyProtein").toString());
      double dailyFat = Double.parseDouble(currentUser.get("dailyFat").toString());
      double dailyCarbs = Double.parseDouble(currentUser.get("dailyCarbs").toString());
      double consumedCal = Double.parseDouble(foodLogData.child("consumedCal").getValue().toString());
      double consumedProtein = Double.parseDouble(foodLogData.child("consumedProtein").getValue().toString());
      double consumedFat = Double.parseDouble(foodLogData.child("consumedFat").getValue().toString());
      double consumedCarbs = Double.parseDouble(foodLogData.child("consumedCarbs").getValue().toString());
      double caloriesLeft = dailyCalories - consumedCal;

      caloriesLeftTextView.setText(Double.toString(caloriesLeft));
      caloriesProgressBar.setProgress((int) ((consumedCal / dailyCalories) * 100));
      caloriesConsumedTextView.setText(Double.toString(consumedCal));
      protProgTextView.setText(getString(R.string.homepage_macros, Double.toString(consumedProtein), Double.toString(dailyProtein)));
      proteinProgressBar.setProgress((int) ((consumedProtein / dailyProtein) * 100));
      fatProgTextView.setText(getString(R.string.homepage_macros, Double.toString(consumedFat), Double.toString(dailyFat)));
      fatProgressBar.setProgress((int) ((consumedFat / dailyFat) * 100));
      carbProgTextView.setText(getString(R.string.homepage_macros, Double.toString(consumedCarbs), Double.toString(dailyCarbs)));
      carbProgressBar.setProgress((int) ((consumedCarbs / dailyCarbs) * 100));
      waterCupsTextView.setText("0");
      waterOuncesTextView.setText("0.0");
      DecimalFormat df = new DecimalFormat("###.##");
      if(currentUser.get("weight") instanceof  Double) {
          weightLbsTextView.setText(df.format(currentUser.get("weight")));
          weightKgTextView.setText(df.format(Math.round(((Double) currentUser.get("weight") * 0.45) * 10.0) / 10.0));
      } else {
          weightLbsTextView.setText(df.format(currentUser.get("weight")));
          weightKgTextView.setText(df.format(Math.round(((Long) currentUser.get("weight") * 0.45) * 10.0) / 10.0));
      }
    }

    /*
     * Will check if the user exists in the foodLog table then
     * initialize the food log entry to -1 for the user
     * if it is their first ever sign in
     * @params [no params]
     * @return [void]
     */
    private void initFoodLog() {
       String currentUserEmail = mAuth.getCurrentUser().getEmail().replace(".", "_");
       mDatabase.child("/food_logs").orderByKey().equalTo(currentUserEmail).addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
             try{
                Log.d("QUERY VALUE", dataSnapshot.getValue().toString()); //attempts to retrieve user from food_logs table
             } catch (NullPointerException e) {
                 Log.d("EXCEPTION", "Null Pointer Exception Caught");
                 //Sets default value of -1 after exception thrown
                 mDatabase.child("/food_logs").child(currentUserEmail).setValue(-1);
             }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {
               Log.d("initFoodLog:onCancelled", databaseError.toException().toString());
           }
       });

    }

    private String getDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM_dd_yyyy", Locale.US);
        return simpleDateFormat.format(new Date());
    }

    /*
     * Set the current data of the dateTextView
     * @params [no params]
     * @return [void]
     */
    private void setCurrentDate() {
        dateTextView.setText(DateFormat.getDateInstance(DateFormat.FULL)
                                       .format(Calendar.getInstance().getTime()));
    }

}