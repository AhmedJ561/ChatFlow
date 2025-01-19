package com.example.chatflow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.chatflow.model.UserModel;
import com.example.chatflow.utils.AndroidUtil;
import com.example.chatflow.utils.FirebaseUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    ImageButton searchButton;
    RelativeLayout mainToolbar;

    ChatFragment chatFragment;
    ProfileFragment profileFragment;
    SettingsFragment settingsFragment;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

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
        drawerLayout = findViewById(R.id.drawer_layout); // Initialize drawerLayout
        navigationView = findViewById(R.id.navigation_view); // Initialize navigationView

        // Set up the toolbar and drawer toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Set up search button click listener
        searchButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SearchUserActivity.class));
        });

        // Bottom navigation item selection handling
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.menu_chat) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, chatFragment).commit();
            } else if (item.getItemId() == R.id.menu_profile) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, profileFragment).commit();
            } else if (item.getItemId() == R.id.menu_settings) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, settingsFragment).commit();
            }
            return true;
        });

        bottomNavigationView.setSelectedItemId(R.id.menu_chat);

        // Set up drawer item click listener
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_about) {
                // Open AboutActivity
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            } else if (item.getItemId() == R.id.nav_logout) {
                // Handle Logout click
                FirebaseUtil.logout();
                Intent intent = new Intent(MainActivity.this, SplashActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                Toast.makeText(MainActivity.this, "User Logged Out!", Toast.LENGTH_SHORT).show();
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
            // Close drawer after selection
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Handle the hamburger icon in the toolbar to open the drawer
        ImageButton hamburgerIcon = findViewById(R.id.toolbar_hamburger);
        hamburgerIcon.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START); // Open the drawer
        });

        // Fetch user data from Firebase and update the drawer
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                // Retrieve the user model from the task result
                UserModel userModel = task.getResult().toObject(UserModel.class);

                if (userModel != null) {
                    // Set the username in the drawer
                    TextView usernameTextView = navigationView.getHeaderView(0).findViewById(R.id.nav_header_username);
                    if (usernameTextView != null) {
                        usernameTextView.setText(userModel.getUsername());
                    }

                    // Set the Base64 profile picture if it exists
                    if (userModel.getProfilePicBase64() != null && !userModel.getProfilePicBase64().isEmpty()) {
                        ImageView profilePicImageView = navigationView.getHeaderView(0).findViewById(R.id.nav_header_profile_pic);
                        if (profilePicImageView != null) {
                            try {
                                AndroidUtil.setProfilePicFromBase64(MainActivity.this, userModel.getProfilePicBase64(), profilePicImageView);
                            } catch (Exception e) {
                                // Log error if setting profile picture fails
                                Log.e("MainActivity", "Error setting profile picture", e);
                            }
                        }
                    }
                } else {
                    // Handle case where userModel is null
                    Log.w("MainActivity", "User model is null");
                }
            } else {
                // Handle failure in fetching user data
                Log.e("MainActivity", "Error fetching user data", task.getException());
                // Optionally, show a toast or error message
                Toast.makeText(MainActivity.this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applySettings() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        float fontSize = sharedPreferences.getFloat("font_size", 1.0f); // Get saved font size
        updateFontSize(fontSize); // Apply the saved font size
    }

    private void updateFontSize(float scaleFactor) {
        // Get the current configuration
        Configuration configuration = getResources().getConfiguration();

        // Update the font scale
        configuration.fontScale = scaleFactor;

        // Update the resources configuration
        Resources resources = getResources();
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }
}
