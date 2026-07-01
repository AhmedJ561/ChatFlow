package com.example.chatflow;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpEmailActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_email);

        emailEditText = findViewById(R.id.signup_email);
        passwordEditText = findViewById(R.id.signup_password);
        confirmPasswordEditText = findViewById(R.id.signup_confirm_password);
        signUpButton = findViewById(R.id.signup_btn);

        signUpButton.setOnClickListener(v -> signUpUser());
    }

    private void signUpUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the user already exists
        checkIfUserExists(email);
    }

    private void checkIfUserExists(String email) {
        CollectionReference usersRef = FirebaseFirestore.getInstance().collection("users");
        usersRef.whereEqualTo("email", email).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (!task.getResult().isEmpty()) {
                    // User already exists
                    Toast.makeText(SignUpEmailActivity.this, "User already exists", Toast.LENGTH_SHORT).show();
                    emailEditText.setError("User already exists");
                } else {
                    // Proceed with sign-up
                    proceedWithSignUp(email);
                }
            } else {
                Toast.makeText(SignUpEmailActivity.this, "Error checking user existence", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void proceedWithSignUp(String email) {
        String password = passwordEditText.getText().toString().trim();

        Intent intent = new Intent(SignUpEmailActivity.this, OTPActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("password", password);
        startActivity(intent);
        finish();
    }
}
