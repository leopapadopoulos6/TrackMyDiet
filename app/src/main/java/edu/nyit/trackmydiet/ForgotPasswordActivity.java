package edu.nyit.trackmydiet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    //Firebase References
    private FirebaseAuth mAuth;

    //Android Components
    private EditText emailField;
    private Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        mAuth = FirebaseAuth.getInstance();
        emailField = findViewById(R.id.forgotPasswordEmailField);
        sendButton = findViewById(R.id.forgotPasswordButton);

        handleSendButton();
    }

    /*
     * Method will send a password reset email to the email typed
     * into the email field
     * @params [no params]
     * @return [void]
     */
    private void handleSendButton() {
        sendButton.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            if(!email.isEmpty()) {
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ForgotPasswordActivity.this, "Forgot Password email sent to: " + email, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, "Failed to send reset email", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(ForgotPasswordActivity.this, "Email cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
