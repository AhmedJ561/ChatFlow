package com.example.chatflow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.chatflow.model.UserModel;
import com.example.chatflow.utils.AndroidUtil;
import com.example.chatflow.utils.FirebaseUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private final ChatFragment chatFragment = new ChatFragment();
    private final ProfileFragment profileFragment = new ProfileFragment();
    private final SettingsFragment settingsFragment = new SettingsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applySettings();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupBottomNavigation();
        setupDrawer();
        fetchUserData();
        handleBackPress();
    }
    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        findViewById(R.id.main_search_btn).setOnClickListener(v ->
                startActivity(new Intent(this, SearchUserActivity.class))
        );

        findViewById(R.id.toolbar_hamburger).setOnClickListener(v ->
                drawerLayout.openDrawer(GravityCompat.START)
        );
    }
    private void setupBottomNavigation() {
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
    }
    private void setupDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_about) {
                startActivity(new Intent(this, AboutActivity.class));
            } else if (item.getItemId() == R.id.nav_logout) {
                handleLogout();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }
    private void fetchUserData() {
        FirebaseUtil.currentUserDetails().get().addOnSuccessListener(snapshot -> {
            UserModel userModel = snapshot.toObject(UserModel.class);
            if (userModel != null) updateDrawerHeader(userModel);
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Failed to fetch user data", Toast.LENGTH_SHORT).show()
        );
    }
    private void updateDrawerHeader(UserModel userModel) {
        TextView usernameTextView = navigationView.getHeaderView(0).findViewById(R.id.nav_header_username);
        ImageView profilePicImageView = navigationView.getHeaderView(0).findViewById(R.id.nav_header_profile_pic);

        if (usernameTextView != null) usernameTextView.setText(userModel.getUsername());
        if (profilePicImageView != null && userModel.getProfilePicBase64() != null) {
            AndroidUtil.setProfilePicFromBase64(this, userModel.getProfilePicBase64(), profilePicImageView);
        }
    }
    private void handleLogout() {
        FirebaseUtil.logout();
        Intent intent = new Intent(this, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        Toast.makeText(this, "User Logged Out!", Toast.LENGTH_SHORT).show();
    }

    private void handleBackPress() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Disable back press
            }
        });
    }
    private void applySettings() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        updateFontSize(sharedPreferences.getFloat("font_size", 1.0f));
    }
    private void updateFontSize(float scaleFactor) {
        Configuration config = getResources().getConfiguration();
        config.fontScale = scaleFactor;
        Resources resources = getResources();
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}
