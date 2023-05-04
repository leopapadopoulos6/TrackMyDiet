package edu.nyit.trackmydiet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

import edu.nyit.trackmydiet.models.User;

public class CreateAccountActivity extends AppCompatActivity {

    //Firebase References
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    //Regex for input validation
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final Pattern EMAIL_PATTERN = Patterns.EMAIL_ADDRESS; //Built in email address Regex
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Z]([a-z])+$"); // Regex First character is capital letter rest are lowercase letters
    private static final Pattern TWO_PART_NAME_PATTERN = Pattern.compile("^[A-Z]([a-z])+[ -][A-Z][a-z]+$"); //Handles two part names
    //private static final Pattern PASSWORD_PATTERN_ALPHA = Pattern.compile("([a-zA-Z]+)"); //Regex alphabet chars
    //private static final Pattern PASSWORD_PATTERN_NUM = Pattern.compile("([0-9]+)"); //Regex numeric chars
    //private static final Pattern PASSWORD_PATTERN_SPEC_CHAR = Pattern.compile("([!\"#\\$%&'\\(\\)\\*\\+,-\\.\\/:;<=>\\?@\\[\\]\\^_`{\\|}~]+)"); //Regex special chars
    //private static final Pattern WEIGHT_PATTERN = Pattern.compile("^([1-9])[0-9]{1,3}$"); //Regex for numbers from 10 - 9999

    //Views for user input
    private EditText firstNameField;
    private EditText lastNameField;
    private RadioButton maleRadioButton;
    private RadioButton femaleRadioButton;
    private EditText emailField;
    private EditText passwordField;
    private EditText confirmPasswordField;
    private Button registerButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        firstNameField = findViewById(R.id.createActFirstNameField);
        lastNameField = findViewById(R.id.createActLastNameField);
        maleRadioButton = findViewById(R.id.createActMaleRadioButton);
        femaleRadioButton = findViewById(R.id.createActFemaleRadioButton);
        emailField = findViewById(R.id.createActEmailField);
        passwordField = findViewById(R.id.createActPasswordField);
        confirmPasswordField = findViewById(R.id.createActConfirmPasswordField);
        registerButton = findViewById(R.id.registerActButton);

        registerAccount();
    }

    /*
     * Method executes when register button is pressed and
     * will verify user input and add the new account to the database
     * @params [no params]
     * @return [void]
     */
    private void registerAccount() {
        registerButton.setOnClickListener(v -> {
          String email = emailField.getText().toString().trim();
          String password = passwordField.getText().toString().trim();

          if(validateFields()){
             createUser(email, password);
          }
        });
    }

    /*
     * Method will create the user account and adds their details to the database
     * and will redirect back to the login page on successful account creation
     * @params [String email] [String password]
     * @return [void]
     */
    private void createUser(String email, String password) {
        //adds user to FireBase authentication
        String firstName = firstNameField.getText().toString().trim();
        String lastName = lastNameField.getText().toString().trim();
        boolean gender = maleRadioButton.isChecked();
        boolean isUserAdded = addUserToDatabase(email, firstName, lastName, gender); //methods add user to FireBase realtime database

        if(isUserAdded) {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Intent goToLoginPage = new Intent(this, LoginActivity.class);
                    startActivity(goToLoginPage);
                } else {
                    Toast.makeText(CreateAccountActivity.this, "Account Creation Failed:\n" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(CreateAccountActivity.this, "Account Creation Failed", Toast.LENGTH_LONG).show();
        }
    }

    /*
     * Method adds the user to Realtime database and returns if users is added properly
     * @params [String email] [String firstName] [String lastName] [boolean gender]
     * @return [boolean]
     */
    private boolean addUserToDatabase(String email, String firstName, String lastName, boolean gender) {
       User newUser = new User(firstName, lastName, gender);
       email = email.replace(".", "_");
       mDatabase.child("users").child(email).setValue(newUser);
       Log.d("DB DEBUG", "User added to db: " + mDatabase.child("users").child(email).getKey());
       return mDatabase.child("users").child(email).getKey().equals(email);
    }

    /*
     * Method verifies that all user input is valid
     * @params [no params]
     * @return [boolean]
     */
    private boolean validateFields() {
        String firstName = firstNameField.getText().toString().trim();
        String lastName = lastNameField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString();

        return (isValidFirstName(firstName) && isValidLastName(lastName) && isValidGender() && isValidEmail(email) && isValidPassword(password));
    }

    /*
     * method is used for checking proper email format and will pop an error if invalid
     * @params [String email] user input from email field
     * @return [boolean]
     */
    private boolean isValidEmail(String email) {
        if(email.isEmpty()) {
            emailField.setError("Email cannot be empty");
            return false;
        } else if(!EMAIL_PATTERN.matcher(email).matches()) {
            emailField.setError("Invalid email address");
            return false;
        } else {
            emailField.setError(null);
            return true;
        }
    }

    /*
     * method is used for checking proper password format and that password and confirm password matches
     * will pop an error if invalid
     * @params [String password] user input from password field
     * @return [boolean]
     */
    private boolean isValidPassword(String password) {
        //TODO put in better password checker
        if(password.isEmpty()) {
            passwordField.setError("Password cannot be empty");
            return false;
        } else if(!(password.length() >= MIN_PASSWORD_LENGTH)) {
            passwordField.setError("Password is not strong enough");
            return false;
        } else if (!password.equals(confirmPasswordField.getText().toString())){
            passwordField.setError("Passwords do not match");
            confirmPasswordField.setError("Passwords do not match");
            return false;
        } else {
            passwordField.setError(null);
            return true;
        }
    }

    /*
     * method is used for checking first name format and will pop an error if invalid
     * @params [String name] user input from first name field
     * @return [boolean]
     */
    private boolean isValidFirstName(String name) {
        if(name.isEmpty()) {
            firstNameField.setError("First name cannot be empty");
            return false;
        } else if(!NAME_PATTERN.matcher(name).matches() && !TWO_PART_NAME_PATTERN.matcher(name).matches()) {
            firstNameField.setError("Invalid first name: must start with capital letter");
            return false;
        } else {
            firstNameField.setError(null);
            return true;
        }
    }

    /*
     * method is used for checking last name format and will pop an error if invalid
     * @params [String name] user input from last name field
     * @return [boolean]
     */
    private boolean isValidLastName(String name) {
        if(name.isEmpty()) {
            lastNameField.setError("Last name cannot be empty");
            return false;
        } else if(!NAME_PATTERN.matcher(name).matches() && !TWO_PART_NAME_PATTERN.matcher(name).matches()) {
            lastNameField.setError("Invalid last name: must start with capital letter");
            return false;
        } else {
            lastNameField.setError(null);
            return true;
        }
    }

    /*
     * method is used for checking if the user chose a gender
     * @params [no params]
     * @return [boolean]
     */
    private boolean isValidGender() {
        if(!(maleRadioButton.isChecked() || femaleRadioButton.isChecked())) {
          femaleRadioButton.setError("You must choose a gender");
          return false;
        } else {
            femaleRadioButton.setError(null);
            return true;
        }
    }

}
