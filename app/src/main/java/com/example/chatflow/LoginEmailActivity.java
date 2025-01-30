package com.example.chatflow;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatflow.utils.AndroidUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginEmailActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private ProgressBar progressBar;
    private TextView signUpTextView, signUpQuestionTv;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_email);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.login_email);
        passwordEditText = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_btn);
        progressBar = findViewById(R.id.login_progress_bar);
        signUpTextView = findViewById(R.id.signup_textview);
        signUpQuestionTv = findViewById(R.id.signup_question_tv);

        // Hide the progress bar initially
        progressBar.setVisibility(ProgressBar.GONE);

        loginButton.setOnClickListener(v -> loginUser());
        signUpTextView.setOnClickListener(v -> navigateToSignUp());
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show the progress bar and hide the sign-up text views when the login process starts
        progressBar.setVisibility(ProgressBar.VISIBLE);
        signUpTextView.setVisibility(TextView.GONE);
        signUpQuestionTv.setVisibility(TextView.GONE);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    // Hide the progress bar and show the sign-up text views when the login process completes
                    progressBar.setVisibility(ProgressBar.GONE);
                    signUpTextView.setVisibility(TextView.VISIBLE);
                    signUpQuestionTv.setVisibility(TextView.VISIBLE);
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Intent intent = new Intent(LoginEmailActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Handle authentication failure
                        String errorMessage = task.getException().getMessage();
                        if (errorMessage.contains("password is invalid")) {
                            passwordEditText.setError("Incorrect password");
                        } else if (errorMessage.contains("no user record")) {
                            emailEditText.setError("No user found with this email");
                        } else {
                            AndroidUtil.showToast(LoginEmailActivity.this,"Authentication failed: " + errorMessage);
                        }
                    }
                });
    }

    private void navigateToSignUp() {
        Intent intent = new Intent(LoginEmailActivity.this, SignUpEmailActivity.class);
        startActivity(intent);
    }
}
