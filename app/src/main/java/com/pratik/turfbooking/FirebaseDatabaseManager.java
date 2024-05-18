package com.pratik.turfbooking;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pratik.turfbooking.models.Booking;

import java.util.Calendar;

public class FirebaseDatabaseManager {

    private DatabaseReference databaseReference;

    public FirebaseDatabaseManager() {
        // Initialize the Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public String saveBooking(Booking booking) {
        // Save the booking to the Firebase Realtime Database
        DatabaseReference bookingsRef = databaseReference.child("booking");
        String bookingId = bookingsRef.push().getKey();
        bookingsRef.child(bookingId).setValue(booking);
        return bookingId;
    }

    public void updateTurfAvailability(String turfId, boolean isAvailable) {
        // Update the turf availability in the Firebase Realtime Database
        // For demonstration purposes, we are not connecting to the database, but you should update the actual database
        // ...

        // For now, we print a log indicating that the turf availability has been updated
        databaseReference.child("turfs").child(turfId).child("isAvailable").setValue(isAvailable);
    }

    public void checkTurfAvailability(String turfId, Calendar selectedDateTime, ValueEventListener listener) {
        // Implement your logic to check if the turf is available at the selected date and time
        // For example, you can query the Firebase Realtime Database to check the availability

        // For demonstration purposes, we add a listener to fetch data from the database
        // Replace this with your actual logic to check turf availability
        databaseReference.child("turfs").child(turfId).addListenerForSingleValueEvent(listener);
    }
}
