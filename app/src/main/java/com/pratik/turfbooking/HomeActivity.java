package com.pratik.turfbooking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {

    private TextView welcomeTextView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        welcomeTextView = findViewById(R.id.welcomeTextView);

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String displayName = currentUser.getEmail();
            welcomeTextView.setText("Welcome, " + displayName + "!");
            launchDashboardActivity();
        } else {
            // If the user is not signed in, redirect them to the login screen.
            launchLoginActivity();
        }
    }

    private void launchDashboardActivity() {
        try {
            Log.d("HomeActivity", "Launching DashboardActivity");
            Intent intent = new Intent(HomeActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e("DashboardLaunchError", "Error launching DashboardActivity", e);
        }
    }

    private void launchLoginActivity() {
        Log.d("HomeActivity", "Launching LoginActivity");
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
