package com.pratik.turfbooking.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pratik.turfbooking.R;
import com.pratik.turfbooking.models.Booking;

import java.util.ArrayList;
import java.util.List;

public class BookingFragment extends Fragment {

    private ListView currentBookingsListView;
    private ListView bookingHistoryListView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private DatabaseReference bookingsRef;
    private FirebaseUser currentUser;

    private List<String> currentBookingsList;
    private List<String> bookingHistoryList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking, container, false);

        currentBookingsListView = view.findViewById(R.id.currentBookingsListView);
        bookingHistoryListView = view.findViewById(R.id.bookingHistoryListView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        // Initialize Firebase
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        bookingsRef = FirebaseDatabase.getInstance().getReference("bookings");

        // Initialize lists
        currentBookingsList = new ArrayList<>();
        bookingHistoryList = new ArrayList<>();

        // Set up swipe refresh listener
        swipeRefreshLayout.setOnRefreshListener(() -> fetchBookings());

        // Fetch and display current bookings and booking history
        fetchBookings();

        return view;
    }

    private void fetchBookings() {
        Log.d("BookingFragment", "Fetching bookings...");

        // Clear existing lists
        currentBookingsList.clear();
        bookingHistoryList.clear();

        // Fetch the latest bookings for the current user
        Query query = bookingsRef.orderByChild("userId").equalTo(currentUser.getUid()).limitToLast(10); // Adjust the limit as needed
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String bookingId = dataSnapshot.getKey();
                    String turfId = dataSnapshot.child("turfId").getValue(String.class);
                    Long dateTimeValue = dataSnapshot.child("dateTime").getValue(Long.class);

                    if (bookingId != null && turfId != null && dateTimeValue != null) {
                        // Fetch the turf details using the name field from the 'footballVenues' node
                        DatabaseReference footballVenuesRef = FirebaseDatabase.getInstance().getReference("footballVenues");
                        Query turfQuery = footballVenuesRef.orderByChild("name").equalTo(turfId); // Assuming turfId is stored as the name in footballVenues
                        turfQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot turfSnapshot) {
                                if (turfSnapshot.exists()) {
                                    for (DataSnapshot childSnapshot : turfSnapshot.getChildren()) {
                                        String name = childSnapshot.child("name").getValue(String.class);
                                        String address = childSnapshot.child("address").getValue(String.class);

                                        if (name != null) {
                                            long dateTimeMillis = dateTimeValue;
                                            Booking booking = new Booking(bookingId, name, dateTimeMillis);

                                            String bookingDetails = "Booking ID: " + bookingId +
                                                    " | Turf: " + name +
                                                    " | Date: " + dataSnapshot.child("fullDate").getValue(String.class) +
                                                    " | Time: " + dataSnapshot.child("fullTime").getValue(String.class);

                                            if (isBookingInFuture(dateTimeMillis)) {
                                                currentBookingsList.add(bookingDetails);
                                            } else {
                                                bookingHistoryList.add(bookingDetails);
                                            }
                                        } else {
                                            Log.e("BookingFragment", "Turf name is null for turfId: " + turfId);
                                        }
                                    }
                                } else {
                                    Log.e("BookingFragment", "Turf details not found for turfId: " + turfId);
                                }

                                // Update the UI with the fetched data
                                updateUI();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Handle errors from the footballVenues node
                                Log.e("BookingFragment", "Football venues database error: " + error.getMessage());
                            }
                        });
                    } else {
                        Log.e("BookingFragment", "Null value found for Booking ID, Turf ID, or DateTime Value");
                    }
                }

                // Hide the swipe refresh indicator
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database errors
                Log.e("BookingFragment", "Database error: " + error.getMessage());

                // Hide the swipe refresh indicator
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void updateUI() {
        if (!isAdded()) {
            return; // Fragment not attached, return early
        }

        // Use ArrayAdapter or any other suitable adapter to populate the ListView
        ArrayAdapter<String> currentBookingsAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, currentBookingsList);
        ArrayAdapter<String> bookingHistoryAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, bookingHistoryList);

        currentBookingsListView.setAdapter(currentBookingsAdapter);
        bookingHistoryListView.setAdapter(bookingHistoryAdapter);
    }


    private boolean isBookingInFuture(long dateTimeMillis) {
        return dateTimeMillis > System.currentTimeMillis();
    }
}
