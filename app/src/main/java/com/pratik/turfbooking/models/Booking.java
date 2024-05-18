package com.pratik.turfbooking.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Booking {
    private String userId;
    private String turfId;
    private long dateTime; // Change type to long

    public Booking(String uid, String turfId, long time, long endDateTimeTime) {
        // Default constructor required for Firebase deserialization
    }

    public Booking(String userId, String turfId, long dateTime) {
        this.userId = userId;
        this.turfId = turfId;
        this.dateTime = dateTime;
    }

    // Getters and setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTurfId() {
        return turfId;
    }

    public void setTurfId(String turfId) {
        this.turfId = turfId;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public String getFullDate() {
        Date date = new Date(dateTime);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return sdf.format(date);
    }

    public String getFullTime() {
        Date date = new Date(dateTime);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return sdf.format(date);
    }
}
