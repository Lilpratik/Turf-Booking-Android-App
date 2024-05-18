package com.pratik.turfbooking.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pratik.turfbooking.EditProfileActivity;
import com.pratik.turfbooking.LoginActivity;
import com.pratik.turfbooking.R;

public class ProfileFragment extends Fragment {

    private ImageView userAvatarImageView;
    private TextView userEmailTextView;
    private Button signOutButton;
    private Button editProfileButton;

    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize UI elements
        userAvatarImageView = view.findViewById(R.id.userAvatarImageView);
        userEmailTextView = view.findViewById(R.id.userEmailTextView);
        signOutButton = view.findViewById(R.id.signOutButton);
        editProfileButton = view.findViewById(R.id.editProfileButton);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Load user information
        loadUserData();

        // Sign out button click listener
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        // Edit profile button click listener
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch the EditProfileActivity
                startActivity(new Intent(requireContext(), EditProfileActivity.class));
            }
        });

        return view;
    }

    private void loadUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Load user's email
            String userEmail = currentUser.getEmail();
            userEmailTextView.setText(userEmail);

            // Load user's avatar (Assuming you have a user avatar URL)
            // You may use a library like Picasso or Glide to load images
            // Example: Picasso.get().load(userAvatarUrl).into(userAvatarImageView);
        }
    }

    private void signOut() {
        mAuth.signOut();
        // Redirect to the LoginActivity
        startActivity(new Intent(requireContext(), LoginActivity.class));
        requireActivity().finish();
    }
}
