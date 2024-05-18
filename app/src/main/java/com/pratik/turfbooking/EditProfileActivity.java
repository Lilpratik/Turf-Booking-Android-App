package com.pratik.turfbooking;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfileActivity extends AppCompatActivity {

    private EditText nameEditText, phoneEditText, emailEditText, passwordEditText, againPasswordEditText, birthDateEditText, genderEditText ;
    private Button saveButton;
    private ImageView profileImageView;

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            // Handle the case when the user is not logged in
            finish(); // Close this activity if the user is not logged in
        }

        usersRef = FirebaseDatabase.getInstance().getReference("User").child(currentUser.getUid());

        nameEditText = findViewById(R.id.editProfileNameEditText);
        phoneEditText = findViewById(R.id.editProfilePhoneEditText);
        emailEditText = findViewById(R.id.editProfileEmailEditText);
        passwordEditText = findViewById(R.id.editProfilePasswordEditText);
        againPasswordEditText = findViewById(R.id.editProfileAgainPasswordEditText);
        saveButton = findViewById(R.id.editProfileSaveButton);
        profileImageView = findViewById(R.id.editProfileImageView);
        birthDateEditText = findViewById(R.id.editProfileBirthDateEditText);
        genderEditText = findViewById(R.id.editProfileGenderEditText);

        // Retrieve user data and set it to the corresponding fields
        retrieveUserData(currentUser);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChanges();
            }
        });
    }

    private void retrieveUserData(FirebaseUser user) {
        // Assume you have a User class with appropriate getters
        // For simplicity, let's assume you have a database structure like "User" -> "userUid" -> { "name": "John Doe", "phone": "1234567890", "email": "john.doe@example.com", "password": "password123" }
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User userData = dataSnapshot.getValue(User.class);
                    if (userData != null) {
                        nameEditText.setText(userData.getName());
                        phoneEditText.setText(userData.getPhone());
                        emailEditText.setText(userData.getEmail());
                        passwordEditText.setText(userData.getPassword());
                        // You may want to handle the profile image separately based on your app's design
                        // profileImageView.setImageDrawable(/* Set the profile image */);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error if data retrieval is unsuccessful
                Toast.makeText(EditProfileActivity.this, "Error retrieving user data", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void saveChanges() {
        String name = nameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String againPassword = againPasswordEditText.getText().toString().trim();
        String birthDate = birthDateEditText.getText().toString().trim();
        String gender = genderEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(againPassword)) {
            // Handle empty fields
            Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(againPassword)) {
            // Handle password mismatch
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update user data in the Firebase Database
        updateUserInDatabase(name,email,phone,password,birthDate,gender);
    }

    private void updateUserInDatabase(String name, String email, String phone, String password, String birthDate, String gender) {
        // Update the user data in the database
        User updatedUser = new User(name,email,phone,password,birthDate,gender);
        usersRef.setValue(updatedUser);

        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        finish(); // Close the EditProfileActivity after saving changes
    }
}
