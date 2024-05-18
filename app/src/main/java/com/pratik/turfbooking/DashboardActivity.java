package com.pratik.turfbooking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pratik.turfbooking.fragments.BookingFragment;
import com.pratik.turfbooking.fragments.HomeFragment;
import com.pratik.turfbooking.fragments.SettingsFragment;
import com.pratik.turfbooking.fragments.ProfileFragment;

public class DashboardActivity extends AppCompatActivity {
    private static final String TAG = "DashboardActivity";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mAuth = FirebaseAuth.getInstance();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        // Load the default fragment (e.g., HomeFragment)
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new HomeFragment()).commit();

        // Check if the user is signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // If not signed in, navigate to LoginActivity
            startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
            finish();
        }

        // Write data to the database
        writeUserData(currentUser);

        // Read data from the database
        readUserData(currentUser.getEmail());
    }

    private void writeUserData(FirebaseUser user) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("User");

        User userData = new User(user.getDisplayName(), user.getEmail(), "", "", "", "");
        DatabaseReference newUserRef = usersRef.child(user.getUid());
        newUserRef.setValue(userData);

        Log.d(TAG, "User information added to the database");
    }

    private void readUserData(String userEmail) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("User");

        usersRef.orderByChild("email").equalTo(userEmail)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            User User = userSnapshot.getValue(User.class);
                            if (User != null) {
                                Log.d(TAG, "User email: " + User.getEmail() + ", Password: " + User.getPassword());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "Failed to read user data", databaseError.toException());
                    }
                });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    if (item.getItemId() == R.id.action_home) {
                        selectedFragment = new HomeFragment();
                        //showToast("Home clicked");
                    } else if (item.getItemId() == R.id.action_bookings) {
                        selectedFragment = new BookingFragment();
                       // showToast("Booking clicked");
                    } else if (item.getItemId() == R.id.action_profile) {
                        selectedFragment = new ProfileFragment();
                        //showToast("Profile clicked");
                    } else if (item.getItemId() == R.id.action_more) {
                        selectedFragment = new SettingsFragment();
                       // showToast("More clicked");
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, selectedFragment).commit();
                    return true;
                }

                private void showToast(String message) {
                    Toast.makeText(DashboardActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            };
}
