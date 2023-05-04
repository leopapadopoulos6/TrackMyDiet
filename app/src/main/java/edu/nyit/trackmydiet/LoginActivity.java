package edu.nyit.trackmydiet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    //FireBase references
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    //Android Gui Components
    private EditText emailField;
    private EditText passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        emailField = findViewById(R.id.loginEmailField);
        passwordField = findViewById(R.id.loginPasswordField);
        handleSignIn();
        handleRegisterUser();
        handleForgotPassword();
    }

    /*
     * When the sign in button is clicked, FireBase will attempt to authenticate the entered credentials
     * If successful, then the homepage is opened, otherwise an error message is shown
     * @params [no params]
     * @return [void]
     */
    private void handleSignIn() {
        Button signInButton = findViewById(R.id.signInButton);
        signInButton.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            if(!(email.isEmpty() || password.isEmpty())) {
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (verifyEmail(email)) {
                            Intent goToHomePage = new Intent(this, NavigationActivity.class);
                            goToHomePage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(goToHomePage);
                            finish();
                        } else {
                           mAuth.signOut();
                        }
                    } else {
                        emailField.getText().clear();
                        passwordField.getText().clear();
                        Toast.makeText(LoginActivity.this, "Username or Password is incorrect", Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                emailField.getText().clear();
                passwordField.getText().clear();
                Toast.makeText(LoginActivity.this, "Username or Password is incorrect", Toast.LENGTH_LONG).show();
            }
        });
    }

    /*
     * Once the register user link is pressed the create account page is opened
     * @params [no params]
     * @return [void]
     */
    private void handleRegisterUser() {
        TextView registerUserTextView = findViewById(R.id.registerTextView);
        registerUserTextView.setOnClickListener(v -> {
            //Intent object is used to switch between activities
            Intent registerAcct = new Intent(this, CreateAccountActivity.class);
            startActivity(registerAcct);
        });
    }

    /*
     * Once the forgot password link is pressed the forgot password page is opened
     * @params [no params]
     * @return [void]
     */
    private void handleForgotPassword() {
        TextView forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);
        forgotPasswordTextView.setOnClickListener(v -> {
            Intent forgotPasswordIntent = new Intent(this, ForgotPasswordActivity.class);
            startActivity(forgotPasswordIntent);
        });
    }

    /*
     * Checks if the email is verified, if it is not then a
     * verification email is sent to the user that signed in
     * @params [String email]
     * @return [boolean]
     */
    private boolean verifyEmail(String email) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(!currentUser.isEmailVerified()) {
            currentUser.sendEmailVerification().addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Verification sent to: " + email + ". Please verify your email", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Check your email for verification", Toast.LENGTH_LONG).show();
                }
            });
            return false;
        } else {
            return true;
        }
    }
}
