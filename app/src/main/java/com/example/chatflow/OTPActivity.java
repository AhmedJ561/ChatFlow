package com.example.chatflow;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class OTPActivity extends AppCompatActivity {

    private EditText otpEditText;
    private Button verifyButton;
    private ProgressBar progressBar;
    private String verificationId;
    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        otpEditText = findViewById(R.id.otp_edit_text);
        verifyButton = findViewById(R.id.verify_btn);
        progressBar = findViewById(R.id.otp_progress_bar);

        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");
        sendOTP(email);

        verifyButton.setOnClickListener(v -> verifyOTP());
    }

    private void sendOTP(String email) {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        String apiUrl = "https://express-server-five-brown.vercel.app/send-otp";

        // Generate a random OTP
        String otp = String.valueOf((int) (Math.random() * 9000) + 1000);

        RequestBody requestBody = RequestBody.create(JSON,
                "{\"email\":\"" + email + "\",\"otp\":\"" + otp + "\"}");

        Request request = new Request.Builder()
                .url(apiUrl)
                .post(requestBody)
                .build();

        new Thread(() -> {
            try {
                Response response = client.newCall(request).execute();
                String responseBody = response.body() != null ? response.body().string() : "No Response Body";
                runOnUiThread(() -> {
                    progressBar.setVisibility(ProgressBar.GONE);
                    if (response.isSuccessful()) {
                        Toast.makeText(OTPActivity.this, "OTP sent to your email", Toast.LENGTH_SHORT).show();
                        verificationId = otp; // Store the OTP for verification
                    } else {
                        Log.e("ResponseError", "Status Code: " + response.code() + ", Body: " + responseBody);
                        Toast.makeText(OTPActivity.this, "Failed to send OTP: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(ProgressBar.GONE);
                    Toast.makeText(OTPActivity.this, "Failed to send OTP: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("EmailError", "Error sending email", e);
                });
            }
        }).start();
    }

    private void verifyOTP() {
        String otp = otpEditText.getText().toString().trim();
        if (otp.isEmpty()) {
            Toast.makeText(this, "Please enter the OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(ProgressBar.VISIBLE);
        if (otp.equals(verificationId)) {
            // OTP is correct, proceed to account completion
            progressBar.setVisibility(ProgressBar.GONE);
            Intent intent = new Intent(OTPActivity.this, CompleteAccountActivity.class);
            intent.putExtra("email", email);
            intent.putExtra("password", password);
            startActivity(intent);
            finish();
        } else {
            progressBar.setVisibility(ProgressBar.GONE);
            Toast.makeText(this, "Incorrect OTP", Toast.LENGTH_SHORT).show();
        }
    }
}
