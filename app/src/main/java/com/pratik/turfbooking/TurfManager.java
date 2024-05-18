package com.pratik.turfbooking;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TurfManager {

    public void bookTurf(String turfId) {
        DatabaseReference turfRef = FirebaseDatabase.getInstance().getReference("turfs").child(turfId);
        turfRef.child("available").setValue(false); // Setting availability to false when booked
    }

    public void cancelBooking(String turfId) {
        DatabaseReference turfRef = FirebaseDatabase.getInstance().getReference("turfs").child(turfId);
        turfRef.child("available").setValue(true); // Setting availability to true when canceled
    }
}

