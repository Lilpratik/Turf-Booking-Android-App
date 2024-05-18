package com.pratik.turfbooking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.pratik.turfbooking.adapters.TurfAdapter;
import com.pratik.turfbooking.models.Turf;

import java.util.ArrayList;

public class TurfListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turf_list);

        // Turf List
        final ArrayList<Turf> turfs = new ArrayList<>();
        // Add turf objects here...

        // Adapter for the Turf ListView
        final TurfAdapter turfAdapter = new TurfAdapter(this, turfs);

        // ListView for Turfs
        ListView listView = findViewById(R.id.turfListView);
        listView.setAdapter((ListAdapter) turfAdapter); // Set the adapter

        // Set click listener for Turf items
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Handle click on Turf item (e.g., open Turf details activity)
                openTurfDetailsActivity(turfs.get(position));
            }
        });
    }

    // Open TurfDetailsActivity with the selected Turf
    private void openTurfDetailsActivity(Turf selectedTurf) {
        Intent intent = new Intent(this, TurfDetailActivity.class);
        intent.putExtra("TURF_NAME", selectedTurf.getName());
        intent.putExtra("TURF_IMAGE", selectedTurf.getImageUrl());
        startActivity(intent);
    }
}
