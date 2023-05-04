package edu.nyit.trackmydiet;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handleMainButton();
    }

    public void handleMainButton() {
        Button mainButton = findViewById(R.id.main_button); //Finds the main button by id in activity_main.xml
        //Creates a pop up saying "Welcome to Track My Diet" when main button is clicked
        FirebaseDatabase db = FirebaseDatabase.getInstance(); //Retrieves database root
        DatabaseReference ref = db.getReference("message"); //gets the reference to a key
        mainButton.setOnClickListener(v -> {
            ref.setValue("Random number: " + new Random().nextInt(100)); //adds value to associated key
            Toast.makeText(MainActivity.this, "Data has been added to FireBase", Toast.LENGTH_LONG).show();
        });
    }
}
