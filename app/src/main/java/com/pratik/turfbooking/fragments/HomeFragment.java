package com.pratik.turfbooking.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pratik.turfbooking.R;
import com.pratik.turfbooking.adapters.FootballVenueAdapter;
import com.pratik.turfbooking.models.FootballVenue;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView, recyclerViewAllTurfs;
    private FootballVenueAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DatabaseReference databaseRef;
    private SearchView searchView;

    private static final double MAX_DISTANCE_THRESHOLD = 10.0; // Maximum distance in kilometers

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize search view
        searchView = rootView.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter turfs when search query changes
                filterTurfs(newText);
                return true;
            }
        });
        // Initialize RecyclerView
        recyclerView = rootView.findViewById(R.id.recyclerViewNearbyTurfs);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAllTurfs = rootView.findViewById(R.id.recyclerViewAllTurfs);

        // Initialize SwipeRefreshLayout
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::fetchFootballVenues);
   
        // Initialize Firebase Database reference
        databaseRef = FirebaseDatabase.getInstance().getReference("footballVenues");

        // Fetch football venues from Firebase Database
        fetchAllFootballVenues();
        fetchFootballVenues();
        ViewGroup.LayoutParams params = recyclerViewAllTurfs.getLayoutParams();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        recyclerViewAllTurfs.setLayoutParams(params);
        return rootView;
    }
    private void filterTurfs(String query) {
        swipeRefreshLayout.setRefreshing(true);

        // Check if the search query is empty
        if (query.isEmpty()) {
            // If query is empty, fetch and display all turfs
            recyclerViewAllTurfs.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            fetchAllFootballVenues();
        } else {
            // If query is not empty, filter and display turfs based on the query
            databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                 public void onDataChange(DataSnapshot dataSnapshot) {
                    List<FootballVenue> footballVenues = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        FootballVenue venue = snapshot.getValue(FootballVenue.class);
                        if (venue != null && venue.getName().toLowerCase().contains(query.toLowerCase())) {
                            footballVenues.add(venue);
                        }
                    }
                    // Initialize adapter and set it to RecyclerView
                    adapter = new FootballVenueAdapter(getContext(), footballVenues);
                    recyclerViewAllTurfs.setAdapter(adapter);

                    // Hide recyclerViewAllTurfs
                    recyclerViewAllTurfs.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);

                    swipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle database error
                    Log.e("HomeFragment", "Firebase database error: " + databaseError.getMessage());
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }
    }
    private void fetchAllFootballVenues() {
        swipeRefreshLayout.setRefreshing(true);

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<FootballVenue> footballVenues = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    FootballVenue venue = snapshot.getValue(FootballVenue.class);
                    if (venue != null) {
                        footballVenues.add(venue);
                    }
                }
                // Initialize adapter and set it to RecyclerView
                adapter = new FootballVenueAdapter(getContext(), footballVenues);
                recyclerViewAllTurfs.setAdapter(adapter);

                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error
                Log.e("FetchVenues", "Firebase database error: " + databaseError.getMessage());
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void fetchFootballVenues() {
        swipeRefreshLayout.setRefreshing(true);

        // Assuming you have user's current latitude and longitude
        double userLatitude = 0.0; // Replace with actual user's latitude
        double userLongitude = 0.0; // Replace with actual user's longitude

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<FootballVenue> footballVenues = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    FootballVenue venue = snapshot.getValue(FootballVenue.class);

                    // Calculate distance between user and venue
                    double venueLatitude = venue.getLatitude();
                    double venueLongitude = venue.getLongitude();
                    double distance = calculateDistance(userLatitude, userLongitude, venueLatitude, venueLongitude);

                    // Assuming you want to show venues within a certain distance threshold
                    double maxDistance = 4.0; // Example threshold in kilometers
                    if (distance <= maxDistance) {
                        footballVenues.add(venue);
                    }
                }
                // Initialize adapter and set it to RecyclerView
                adapter = new FootballVenueAdapter(getContext(), footballVenues);
                recyclerView.setAdapter(adapter);

                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error
                Log.e("HomeFragment", "Firebase database error: " + databaseError.getMessage());
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Calculate distance using Haversine formula
        double R = 6371; // Radius of the Earth in kilometers
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;

        return distance;
    }
}