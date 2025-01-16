package com.example.chatflow;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    ImageButton searchButton;
    RelativeLayout mainToolbar;

    ChatFragment chatFragment;
    ProfileFragment profileFragment;
    SettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applySettings(); // Apply saved settings before super.onCreate

        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);

        chatFragment = new ChatFragment();
        profileFragment = new ProfileFragment();
        settingsFragment = new SettingsFragment();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        searchButton = findViewById(R.id.main_search_btn);
        mainToolbar = findViewById(R.id.main_toolbar);

        searchButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SearchUserActivity.class));
        });

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.menu_chat) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, chatFragment).commit();
                } else if (item.getItemId() == R.id.menu_profile) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, profileFragment).commit();
                } else if (item.getItemId() == R.id.menu_settings) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, settingsFragment).commit();
                }
                return true;
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.menu_chat);
    }

    private void applySettings() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        float fontSize = sharedPreferences.getFloat("font_size", 1.0f); // Get saved font size

        updateFontSize(fontSize); // Apply the saved font size
    }

    private void updateFontSize(float scaleFactor) {
        // Get the current configuration
        Configuration configuration = getResources().getConfiguration();

        // Update the font scale in the configuration
        configuration.fontScale = scaleFactor;

        // Create a new context with the updated configuration
        Context context = createConfigurationContext(configuration);

        // Apply this configuration to the activity
        // Update the resources using the new context
       context.getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());

    }



}
