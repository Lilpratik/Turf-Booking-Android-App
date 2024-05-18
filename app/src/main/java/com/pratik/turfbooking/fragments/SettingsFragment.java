package com.pratik.turfbooking.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pratik.turfbooking.AdminLoginActivity;
import com.pratik.turfbooking.AdminRegisterActivity;
import com.pratik.turfbooking.R;

public class SettingsFragment extends Fragment {

    private Switch switchNotifications;
    private Spinner spinnerLanguage;
    private ArrayAdapter<CharSequence> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        Button btnAdminLogin = view.findViewById(R.id.btn_admin_login);
        Button btnAdminRegister = view.findViewById(R.id.btn_admin_register);
        switchNotifications = view.findViewById(R.id.switch_notifications);
        spinnerLanguage = view.findViewById(R.id.spinner_language);

        // Create adapter for the spinner
        adapter = ArrayAdapter.createFromResource(requireContext(), R.array.languages_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(adapter);

        btnAdminLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open AdminLoginActivity
                startActivity(new Intent(requireActivity(), AdminLoginActivity.class));
            }
        });

        btnAdminRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open AdminRegisterActivity
                startActivity(new Intent(requireActivity(), AdminRegisterActivity.class));
            }
        });

        // Load settings when the fragment is created
        loadSettings();

        // Set listeners for UI elements
        switchNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Handle notification setting change
                saveNotificationSetting(isChecked);
            }
        });

        spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Handle language selection
                String selectedLanguage = parent.getItemAtPosition(position).toString();
                saveLanguageSetting(selectedLanguage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        return view;
    }

    private void loadSettings() {
        // Load notification setting from preferences and update switch
        boolean notificationsEnabled = getNotificationSettingFromPreferences();
        switchNotifications.setChecked(notificationsEnabled);

        // Load language setting from preferences and update spinner
        String selectedLanguage = getLanguageSettingFromPreferences();
        if (selectedLanguage != null) {
            int position = adapter.getPosition(selectedLanguage);
            spinnerLanguage.setSelection(position);
        }
    }

    private boolean getNotificationSettingFromPreferences() {
        // Retrieve notification setting from preferences (example)
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        return preferences.getBoolean("notifications_enabled", true); // Default to true if not found
    }

    private void saveNotificationSetting(boolean enabled) {
        // Save notification setting to preferences (example)
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("notifications_enabled", enabled);
        editor.apply();
    }

    private String getLanguageSettingFromPreferences() {
        // Retrieve language setting from preferences (example)
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        return preferences.getString("selected_language", null);
    }

    private void saveLanguageSetting(String language) {
        // Save language setting to preferences (example)
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("selected_language", language);
        editor.apply();
    }
}
