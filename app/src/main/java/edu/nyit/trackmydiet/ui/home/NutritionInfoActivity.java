package edu.nyit.trackmydiet.ui.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import edu.nyit.trackmydiet.NavigationActivity;
import edu.nyit.trackmydiet.R;

public class NutritionInfoActivity extends AppCompatActivity {

    //Database and Auth references
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    //Constants and static fields
    private static final Pattern WEIGHT_PATTERN_DEC = Pattern.compile("^[0-9]+\\.[0-9]{1,3}$"); //pattern for proper decimal number
    private final int MIN_FEET = 0;
    private final int MAX_FEET = 10;
    private final int MIN_INCHES = 0;
    private final int MAX_INCHES = 11;
    private final double INCHES_TO_CM = .393701;
    private final int STANDARD_DIET = 0;
    private final int LOW_CARB_DIET = 1;
    private final int HIGH_CARB_DIET = 2;
    private final int PERFECT_HEALTH_DIET = 3;

    //Android View Variables
    private EditText feetField;
    private EditText inchesField;
    private EditText weightField;
    private EditText ageField;
    private Spinner dietTypeSpinner;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition_info);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        feetField = findViewById(R.id.nutrientCalcFeet);
        inchesField = findViewById(R.id.nutrientCalcInches);
        weightField = findViewById(R.id.nutrientCalcWeight);
        ageField = findViewById(R.id.nutrientCalcAge);
        dietTypeSpinner = findViewById(R.id.nutrientCalcDietType);
        submitButton = findViewById(R.id.nutrientCalcSubmit);
        populateDietTypeSpinner();
        handleSubmitButton();
    }

    /*
     * Disables back button functionality to force user
     * to enter the desired information
     * @params [no params]
     * @return [void]
     */
    @Override
    public void onBackPressed() {
        Toast.makeText(NutritionInfoActivity.this, "You must complete these fields", Toast.LENGTH_SHORT).show();
    }

    /*
     * Will calculate users daily calories and nutrients
     * and save the data to the database for the logged in user
     * @params [no params]
     * @return [void]
     */
    private void handleSubmitButton() {
      //check if all fields are not null
      submitButton.setOnClickListener(v -> {
          String feet = feetField.getText().toString().trim();
          String inches = inchesField.getText().toString().trim();
          String weight = weightField.getText().toString().trim();
          String age = ageField.getText().toString().trim();
          int dietTypeIndex = dietTypeSpinner.getSelectedItemPosition();

          if(validateFields(feet, inches, weight, age)) {
              updateUser(feet, inches, weight, age, dietTypeIndex);
          }
      });
    }

    /*
     * Update nutritional fields on the user log in
     * and on successful update the homepage will be brought up
     * @params [String feet, String inches, String age, int dietTypeIndex]
     * @return [void]
     */
    private void updateUser(String feet, String inches, String weight, String age, int dietTypeIndex) {
       DatabaseReference dbUsers = mDatabase.child("/users");
       dbUsers.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if(mAuth.getCurrentUser() != null) {
                   String currentUserEmail = mAuth.getCurrentUser().getEmail().replace(".", "_");
                   HashMap<String, Object> currentUser = (HashMap<String, Object>) dataSnapshot.child(currentUserEmail).getValue();
                   double heightCm = heightToCm(Integer.parseInt(feet), Integer.parseInt(inches));
                   double dailyCalories = calculateDailyCalories(Double.parseDouble(weight), heightCm, Integer.parseInt(age), (boolean)currentUser.get("gender"));
                   double dailyCarbs = calculateDailyCarbs(dailyCalories, dietTypeIndex);
                   double dailyProtein = calculateDailyProtein(dailyCalories, dietTypeIndex);
                   double dailyFat = calculateDailyFat(dailyCalories, dietTypeIndex);

                   currentUser.put("age", Integer.parseInt(age));
                   currentUser.put("dailyCalories", dailyCalories);
                   currentUser.put("dailyCarbs", dailyCarbs);
                   currentUser.put("dailyProtein", dailyProtein);
                   currentUser.put("dailyFat", dailyFat);
                   currentUser.put("height", heightCm);
                   currentUser.put("weight", Double.parseDouble(weight));
                   currentUser.put("firstSignIn", false);
                   Log.d("CURRENT USER ", currentUser.toString());
                   mDatabase.child("users").child(currentUserEmail).setValue(currentUser).addOnCompleteListener(task -> {
                       if(task.isSuccessful()) {
                           Intent goToHomePage = new Intent(NutritionInfoActivity.this, NavigationActivity.class);
                           goToHomePage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                           startActivity(goToHomePage);
                           finish();
                       } else {
                           Toast.makeText(NutritionInfoActivity.this, "User account Update failed", Toast.LENGTH_SHORT).show();
                       }
                   });
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {
               Log.d("updateUser:onCancelled", databaseError.toException().toString());
           }
       });
    }

    /*
     * Makes sure that the entered fields are in valid format
     * @params [no params]
     * @return [boolean]
     */
    private boolean validateFields(String feet, String inches, String weight, String age) {
      return isValidHeight(feet, inches) && isValidWeight(weight) && isValidAge(age);
    }

    /*
     * Converts height from feet and inches to centimeters
     * for calculating daily calories and macro nutrients
     * @params [int feet, int inches]
     * @return [double]
     */
    private double heightToCm(int feet, int inches) {
        double heightCm = ((feet * 12) + inches) / INCHES_TO_CM;
        return Math.round(heightCm * 10) / 10.0;
    }

    /*
     * Calculates daily calorie intake for the logged in user
     * @params [double weight, double height, int age, boolean gender]
     * @return [double]
     */
    private double calculateDailyCalories(double weight, double height, int age, boolean gender) {
      if(gender) {
          return Math.round((10 * (weight * .45) + 8.25 * (height) + 5.0 * (age) + 5.0 * 1.4) * 10) / 10.0;
      } else {
          return Math.round((10 * (weight * .45) + 8.25 * (height) + 5.0 * (age) + 161.0 * 1.4) * 10) / 10.0;
      }
    }

    /*
     * Calculates the recommended grams of carbs that the user
     * should consume based on their calories and diet type
     * @params [double dailyCalories, int dietTypeIndex]
     * @return [double]
     */
    private double calculateDailyCarbs(double dailyCalories, int dietTypeIndex) {
        double dailyCarbs = 0.0;
        switch (dietTypeIndex) {
            case STANDARD_DIET:
                dailyCarbs = Math.round(((.50 * dailyCalories) / 4.0) * 10) / 10.0;
                break;
            case LOW_CARB_DIET:
                dailyCarbs = Math.round(((.25 * dailyCalories) / 4.0) * 10) / 10.0;
                break;
            case HIGH_CARB_DIET:
                dailyCarbs = Math.round(((.60 * dailyCalories) / 4.0) * 10) / 10.0;
                break;
            case PERFECT_HEALTH_DIET:
                dailyCarbs = Math.round(((.30 * dailyCalories) / 4.0) * 10) / 10.0;
                break;
        }
        return dailyCarbs;
    }

    private double calculateDailyProtein(double dailyCalories, int dietTypeIndex) {
        double dailyProtein = 0.0;
        switch (dietTypeIndex) {
            case STANDARD_DIET:
                dailyProtein = Math.round(((.20 * dailyCalories) / 4.0) * 10) / 10.0;
                break;
            case LOW_CARB_DIET:
                dailyProtein = Math.round(((.20 * dailyCalories) / 4.0) * 10) / 10.0;
                break;
            case HIGH_CARB_DIET:
                dailyProtein = Math.round(((.20 * dailyCalories) / 4.0) * 10) / 10.0;
                break;
            case PERFECT_HEALTH_DIET:
                dailyProtein = Math.round(((.15 * dailyCalories) / 4.0) * 10) / 10.0;
                break;
        }
        return dailyProtein;
    }

    private double calculateDailyFat(double dailyCalories, int dietTypeIndex) {
        double dailyFat = 0.0;
        switch (dietTypeIndex) {
            case STANDARD_DIET:
                dailyFat = Math.round(((.30 * dailyCalories) / 9.0) * 10) / 10.0;
                break;
            case LOW_CARB_DIET:
                dailyFat = Math.round(((.55 * dailyCalories) / 9.0) * 10) / 10.0;
                break;
            case HIGH_CARB_DIET:
                dailyFat = Math.round(((.20 * dailyCalories) / 9.0) * 10) / 10.0;
                break;
            case PERFECT_HEALTH_DIET:
                dailyFat = Math.round(((.55 * dailyCalories) / 9.0) * 10) / 10.0;
                break;
        }
        return dailyFat;
    }
    /*
     * Is able to ensure the entered height is proper
     * @params [String feet, String inches]
     * @return [boolean]
     */
    private boolean isValidHeight(String feet, String inches) {
      if(feet.isEmpty()) {
        feetField.setError("Feet cannot be empty");
        return false;
      } else if (inches.isEmpty()) {
          inchesField.setError("Inches cannot be empty");
          return false;
      } else if(Integer.parseInt(feet) < MIN_FEET || Integer.parseInt(feet) > MAX_FEET) {
         feetField.setError("Feet must be between 0-10");
         return false;
      } else if(Integer.parseInt(inches) < MIN_INCHES || Integer.parseInt(inches) > MAX_INCHES) {
          inchesField.setError("Feet must be between 0-12");
          return false;
      } else {
         feetField.setError(null);
         inchesField.setError(null);
         return true;
      }
    }

    /*
     * Is able to ensure the entered weight is proper
     * @params [String weight]
     * @return [boolean]
     */
    private boolean isValidWeight(String weight) {
      if(weight.isEmpty()) {
         weightField.setError("Weight cannot be empty");
         return false;
      } else if(!WEIGHT_PATTERN_DEC.matcher(weight).matches()) {
         weightField.setError("Format should be a decimal number");
         return false;
      } else {
          weightField.setError(null);
          return true;
      }
    }

    /*
     * Is able to ensure the entered age is proper
     * @params [String age]
     * @return [boolean]
     */
    private boolean isValidAge(String age) {
      if(age.isEmpty()) {
        ageField.setError("Age cannot be empty");
        return false;
      } else if((Integer.parseInt(age) < 1 && Integer.parseInt(age) > 120)) {
          ageField.setError("Age must be between 1-120 years");
          return false;
      } else {
          ageField.setError(null);
          return true;
      }
    }

    /*
     * Populates the diet type spinner with all possible
     * diet type options the user can choose from
     * @params [no params]
     * @return [void]
     */
    private void populateDietTypeSpinner() {
        List<String> dietTypeList = new ArrayList<>(Arrays.asList("Standard", "Low Carb", "High Carb", "Perfect Health"));
        ArrayAdapter<String> dietTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dietTypeList);
        dietTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dietTypeSpinner.setAdapter(dietTypeAdapter);
    }
}
