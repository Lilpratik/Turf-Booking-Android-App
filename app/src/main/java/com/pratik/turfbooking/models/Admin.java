package com.pratik.turfbooking.models;

// Admin.java
public class Admin {
    private String id;
    private String email;

    public Admin() {
        // Default constructor required for Firebase
    }

    public Admin(String id, String email) {
        this.id = id;
        this.email = email;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}


