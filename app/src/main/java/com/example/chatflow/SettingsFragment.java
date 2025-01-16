package com.example.chatflow;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {

    private Spinner spinnerFontSize;
    private Button buttonSave;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        spinnerFontSize = view.findViewById(R.id.spinner_font_size);
        buttonSave = view.findViewById(R.id.button_save);

        sharedPreferences = getActivity().getSharedPreferences("AppSettings", getContext().MODE_PRIVATE);

        setupFontSizeSpinner(); // Set up the font size spinner

        buttonSave.setOnClickListener(v -> saveSettings()); // Save the settings when the button is clicked

        return view;
    }

    private void setupFontSizeSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.font_sizes_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFontSize.setAdapter(adapter);
        spinnerFontSize.setSelection(getSelectedFontSizeIndex()); // Set the selected font size based on saved data
    }

    private void saveSettings() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Save font size
        float scaleFactor = getFontSizeScaleFactor(spinnerFontSize.getSelectedItemPosition());
        editor.putFloat("font_size", scaleFactor);

        editor.apply(); // Apply the changes

        // Recreate the activity to apply font size change
        getActivity().recreate();
    }

    private int getSelectedFontSizeIndex() {
        float savedFontSize = sharedPreferences.getFloat("font_size", 1.0f);
        if (savedFontSize == 0.85f) return 0;
        if (savedFontSize == 1.15f) return 2;
        return 1; // Default index for 1.0f (normal font size)
    }

    private float getFontSizeScaleFactor(int index) {
        switch (index) {
            case 0: return 0.85f; // Small font size
            case 1: return 1.0f; // Normal font size
            case 2: return 1.15f; // Large font size
            default: return 1.0f; // Default
        }
    }
}
