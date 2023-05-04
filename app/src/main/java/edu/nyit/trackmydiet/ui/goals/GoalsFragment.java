package edu.nyit.trackmydiet.ui.goals;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.regex.Pattern;

import edu.nyit.trackmydiet.NavigationActivity;
import edu.nyit.trackmydiet.R;
import edu.nyit.trackmydiet.ui.home.NutritionInfoActivity;

public class GoalsFragment extends Fragment {

    //Firebase Database and Auth Instances
    private static final String TAG = "GoalsFragment";
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private static final Pattern WEIGHT_PATTERN_DEC = Pattern.compile("^[0-9]+\\.[0-9]{1,3}$"); //pattern for proper decimal number


    //View Instance Variables
    private TextView currentweightTextView;
    private EditText targetweightEditText;
    private EditText goalsProtein;
    private EditText goalsFat;
    private EditText goalsCarbs;
    private EditText goalsDailyCal;
    private Button goalsButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_goals, container, false);
        final TextView textView = root.findViewById(R.id.nav_goals);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentweightTextView = root.findViewById(R.id.currentweightTextView);
        targetweightEditText = root.findViewById(R.id.targetweightEditText);
        goalsProtein = root.findViewById(R.id.goalsProtein);
        goalsFat = root.findViewById(R.id.goalsFat);
        goalsCarbs = root.findViewById(R.id.goalsCarbs);
        goalsDailyCal = root.findViewById(R.id.goalsDailyCal);
        goalsButton = root.findViewById(R.id.goalsButton);
        initGoals();
        goalsButton.setOnClickListener(this::updateGoals);
        // setGoals();


        //Java code for goals goes here
        return root;
    }

    private void initGoals() {
        String currentUserEmail = mAuth.getCurrentUser().getEmail().replace(".", "_");
        DatabaseReference dbUsers = mDatabase.child("/users").child(currentUserEmail);
        dbUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: " + dataSnapshot.getValue().toString());
                HashMap<String, Object> userData = (HashMap<String, Object>) dataSnapshot.getValue();
                currentweightTextView.setText(userData.get("weight").toString());
                goalsProtein.setText(userData.get("dailyProtein").toString());
                goalsCarbs.setText(userData.get("dailyCarbs").toString());
                goalsFat.setText(userData.get("dailyFat").toString());
                goalsDailyCal.setText(userData.get("dailyCalories").toString());
                if (userData.containsKey("targetWeight")) {
                    targetweightEditText.setText(userData.get("targetWeight").toString());
                } else {
                    targetweightEditText.setText(userData.get("weight").toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.toString());
            }
        });

    }

    private void updateGoals(View v) {

        double newdailyCal = Double.parseDouble(goalsDailyCal.getText().toString().trim());
        double newdailyPro = Double.parseDouble(goalsProtein.getText().toString().trim());
        double newdailyCarbo = Double.parseDouble(goalsCarbs.getText().toString().trim());
        double newdailyFat = Double.parseDouble(goalsFat.getText().toString().trim());
        double newtweight = Double.parseDouble(targetweightEditText.getText().toString().trim());
        Log.d(TAG, "updateGoals: " + (newtweight + newdailyCal + newdailyCarbo + newdailyPro + newdailyFat));

        if (checkField(Double.toString(newdailyCal), Double.toString(newdailyPro), Double.toString(newdailyCarbo), Double.toString(newdailyFat), Double.toString(newtweight))) {
            updateUser(newdailyCal, newdailyPro, newdailyCarbo, newdailyFat, newtweight);

            Intent backToHomePage = new Intent(getContext(), NavigationActivity.class);
            startActivity(backToHomePage);
        }
    }

    private void updateUser(double newdailyCal, double newdailyPro, double newdailyCarbo, double newdailyFat, double newtweight) {
        String currentUserEmail = mAuth.getCurrentUser().getEmail().replace(".", "_");
        DatabaseReference dbUsers = mDatabase.child("/users").child(currentUserEmail);
        dbUsers.child("dailyCalories").setValue(newdailyCal);
        dbUsers.child("dailyCarbs").setValue(newdailyCarbo);
        dbUsers.child("dailyProtein").setValue(newdailyPro);
        dbUsers.child("dailyFat").setValue(newdailyFat);
        dbUsers.child("targetWeight").setValue(newtweight);
    }


    private boolean checkField(String dailyCal, String dailyPro, String dailyCarbs, String dailyFat, String tweight) {
        return isValidTWeight(tweight) && isValidCal(dailyCal) && isValidPro(dailyPro) && isValidCarbs(dailyCarbs) && isValidFat(dailyFat);
    }

    private boolean isValidFat(String dailyFat) {
        if (dailyFat.isEmpty()) {
            goalsFat.setError("Weight cannot be empty");
            return false;
        } else if ((Double.parseDouble(dailyFat) < 1 || Double.parseDouble(dailyFat) > 200)) {
            goalsFat.setError("Not a valid entry");
            return false;
        } else {
            goalsFat.setError(null);
            return true;
        }
    }

    private boolean isValidCarbs(String dailyCarbs) {
        if (dailyCarbs.isEmpty()) {
            goalsCarbs.setError("Carbs cannot be empty");
            return false;
        } else if ((Double.parseDouble(dailyCarbs) < 50 || Double.parseDouble(dailyCarbs) > 220)) {
            goalsCarbs.setError("Not a valid entry");
            return false;
        } else {
            goalsCarbs.setError(null);
            return true;
        }
    }


    private boolean isValidPro(String dailyPro) {
        if (dailyPro.isEmpty()) {
            goalsProtein.setError("Protein cannot be empty");
            return false;
        } else if ((Double.parseDouble(dailyPro) < 50 || Double.parseDouble(dailyPro) > 220)) {
            goalsProtein.setError("Not a valid entry");
            return false;
        } else {
            goalsProtein.setError(null);
            return true;
        }
    }

    private boolean isValidCal(String dailyCal) {
        if(dailyCal.isEmpty()) {
            goalsDailyCal.setError("Age cannot be empty");
            return false;
        }else if (!WEIGHT_PATTERN_DEC.matcher(dailyCal).matches()) {
            targetweightEditText.setError("Format should be a decimal number");
            return false;
        }
        else if((Double.parseDouble(dailyCal) < 1100 || Double.parseDouble(dailyCal) > 4200)) {
            goalsDailyCal.setError("Calories must be between 1100-4200 years");
            return false;
        } else {
            goalsDailyCal.setError(null);
            return true;
        }
    }

    private boolean isValidTWeight(String tweight) {
        if (tweight.isEmpty()) {
            targetweightEditText.setError("Weight cannot be empty");
            return false;
        } else if (!WEIGHT_PATTERN_DEC.matcher(tweight).matches()) {
            targetweightEditText.setError("Format should be a decimal number");
            return false;
        } else {
            targetweightEditText.setError(null);
            return true;
        }


    }
}
/*
        String currentUserEmail = mAuth.getCurrentUser().getEmail().replace(".", "_");
        DatabaseReference dbUsers = mDatabase.child("/users").child(currentUserEmail);
        dbUsers.addListenerForSingleValueEvent(new ValueEventListener(){

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Object> currentUser = (HashMap<String, Object>) dataSnapshot.child(currentUserEmail).getValue();
                currentUser.put("dailyCalories", goalsDailyCal);
                currentUser.put("dailyCarbs", goalsCarbs);
                currentUser.put("dailyProtein", goalsProtein);
                currentUser.put("dailyFat", goalsFat);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }
    }










/*
    // SHOULD set current user data into appropriate fields
    private void setGoals(String currentUserEmail, HashMap<String, Object> currentUser) {
        DatabaseReference dbUsers = mDatabase.child("/users").child(currentUserEmail);
        dbUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Object currentUserGoals = dataSnapshot.getValue();
                    if ;)               {
                        updateGoals(currentUser);
                    }
                    else {
                            setCurrentUserGoals(currentUser);
                        }
                }
                public void onCancelled(DatabaseError databaseError) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    /*
    private void handleSaveChanges

    private void updateGoals(HashMap<String, Object> currentUser) {

    }

    private void setCurrentUserGoals(HashMap<String, Object> currentUser) {
        goalsDailyCal.setText(currentUser.get("dailyCalories").toString());
        goalsProtein.setText(currentUser.get("dailyProteins").toString());
        goalsFat.setText(currentUser.get("dailyFat").toString());
        goalsCarbs.setText(currentUser.get("dailyCarbs").toString());
        DecimalFormat df = new DecimalFormat("###.##");
        if(currentUser.get("weight") instanceof  Double) {
            currentweightTextView.setText(df.format(currentUser.get("weight")));
        } else {
            currentweightTextView.setText(df.format(currentUser.get("weight")));

        }
    }
*/





