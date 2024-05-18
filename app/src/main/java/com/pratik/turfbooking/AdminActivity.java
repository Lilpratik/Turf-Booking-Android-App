package com.pratik.turfbooking;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pratik.turfbooking.models.TurfCard;

// AdminActivity.java - Updated for image uploading

// Add necessary imports

public class AdminActivity extends AppCompatActivity {

    // Declare variables for views and Firebase
    private EditText editTextTurfName, editTextLocation, editTextPrice, editTextAmenities;
    private Button buttonAddTurf, buttonUploadImage;
    private DatabaseReference turfsRef;
    private StorageReference storageRef;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Initialize Firebase database and storage references
        turfsRef = FirebaseDatabase.getInstance().getReference().child("turfs");
        storageRef = FirebaseStorage.getInstance().getReference().child("turf_images");

        // Find views by ID
        editTextTurfName = findViewById(R.id.editTextName);
        editTextLocation = findViewById(R.id.editTextLocation);
        editTextPrice = findViewById(R.id.editTextPrice);
        editTextAmenities = findViewById(R.id.editTextAmenities);
        buttonAddTurf = findViewById(R.id.buttonSave);
        buttonUploadImage = findViewById(R.id.buttonUploadImage);


        // Set click listener for add turf button
        buttonAddTurf.setOnClickListener(view -> addTurf());

        // Set click listener for upload image button
        buttonUploadImage.setOnClickListener(view -> chooseImage());
    }

    // Method to handle turf addition
    private void addTurf() {
        // Get turf details from EditText fields
        String turfName = editTextTurfName.getText().toString().trim();
        String location = editTextLocation.getText().toString().trim();
        String priceStr = editTextPrice.getText().toString().trim();
        String amenities = editTextAmenities.getText().toString().trim();

        // Validate inputs
        if (turfName.isEmpty() || location.isEmpty() || priceStr.isEmpty() || amenities.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);

        // Create Turf object
        // Make sure to provide all required arguments for TurfCard constructor
        String imageUrl="";
        TurfCard turfCard = new TurfCard(turfName, location, price, amenities, imageUrl);



        // Upload image to Firebase Storage
        uploadImage(turfCard);
    }

    // Method to choose image from gallery
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    // Method to handle image selection result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            buttonUploadImage.setText("Image Selected");
        }
    }

    // Method to upload image to Firebase Storage
    private void uploadImage(TurfCard turfCard) {
        StorageReference fileRef = storageRef.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded successfully, get download URL
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        turfCard.getImageResource();

                        // Add TurfCard object to Firebase database
                        turfsRef.push().setValue(turfCard)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(AdminActivity.this, "Turf added successfully", Toast.LENGTH_SHORT).show();
                                        clearFields();
                                    } else {
                                        Toast.makeText(AdminActivity.this, "Failed to add turf", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    });
                })
                .addOnFailureListener(e -> Toast.makeText(AdminActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show());
    }

    // Method to get file extension from URI
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    // Method to clear input fields after adding turf
    private void clearFields() {
        editTextTurfName.setText("");
        editTextLocation.setText("");
        editTextPrice.setText("");
        editTextAmenities.setText("");
        buttonUploadImage.setText("Upload Image");
        imageUri = null;
    }
}
