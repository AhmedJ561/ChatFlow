package com.example.chatflow;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CompleteAccountActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private Button completeButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_account);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        usernameEditText = findViewById(R.id.complete_username);
        completeButton = findViewById(R.id.complete_btn);

        completeButton.setOnClickListener(v -> completeAccount());
    }

    private void completeAccount() {
        String email = getIntent().getStringExtra("email");
        String password = getIntent().getStringExtra("password");
        String username = usernameEditText.getText().toString().trim();

        if (username.isEmpty()) {
            Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show();
            return;
        }


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();
                            saveUserData(userId, email, username);
                        }
                    } else {
                        Toast.makeText(CompleteAccountActivity.this, "Account creation failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserData(String userId, String email, String username) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("username", username);
        userData.put("userId", userId);
        userData.put("createdTimestamp", com.google.firebase.Timestamp.now());

        db.collection("users").document(userId).set(userData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(CompleteAccountActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CompleteAccountActivity.this, LoginEmailActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CompleteAccountActivity.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                });
    }
}
