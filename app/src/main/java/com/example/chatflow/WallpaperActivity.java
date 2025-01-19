package com.example.chatflow;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.example.chatflow.adapter.WallpaperPagerAdapter;

public class WallpaperActivity extends AppCompatActivity {

    private int[] wallpaperResIds = {
            R.drawable.wallpaper1,
            R.drawable.wallpaper2,
            R.drawable.wallpaper3,
            R.drawable.wallpaper4,
            R.drawable.wallpaper5
    };

    private int selectedWallpaper = -1; // Default to none

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enable the back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Handle the back button click
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        ViewPager2 viewPager = findViewById(R.id.viewpager_wallpapers);
        Button saveButton = findViewById(R.id.button_save_wallpaper);

        WallpaperPagerAdapter adapter = new WallpaperPagerAdapter(this, wallpaperResIds);
        viewPager.setAdapter(adapter);

        // Track the selected wallpaper
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                selectedWallpaper = wallpaperResIds[position];
            }
        });

        // Save the selected wallpaper
        saveButton.setOnClickListener(v -> {
            if (selectedWallpaper != -1) {
                SharedPreferences preferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("selected_wallpaper", selectedWallpaper);
                editor.apply();

                Toast.makeText(this, "Wallpaper saved!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Please select a wallpaper.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
