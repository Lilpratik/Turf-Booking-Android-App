package com.pratik.turfbooking;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

// AddTurfActivity.java
public class AddTurfActivity extends AppCompatActivity {

    private EditText edtTurfName;
    private EditText edtTurfLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_turf);

        edtTurfName = findViewById(R.id.edtTurfName);
        edtTurfLocation = findViewById(R.id.edtTurfLocation);

        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtTurfName.getText().toString().trim();
                String location = edtTurfLocation.getText().toString().trim();
                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(location)) {
                    saveTurfDetails(name, location);
                } else {
                    Toast.makeText(AddTurfActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveTurfDetails(String name, String location) {
        // Access a Cloud Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a new turf object with the provided name and location
        Map<String, Object> turf = new HashMap<>();
        turf.put("name", name);
        turf.put("location", location);

        // Add a new document with a generated ID
        db.collection("turfs")
                .add(turf)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(AddTurfActivity.this, "Turf added successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddTurfActivity.this, "Error adding turf: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}

