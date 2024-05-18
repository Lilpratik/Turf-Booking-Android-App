package com.pratik.turfbooking.models;

public class FootballVenue {
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private String imageUrl; // New field for image URL
    private String id;


    public FootballVenue() {
        // Default constructor required for Firebase
    }

    public FootballVenue(String name, String address, double latitude, double longitude, String imageUrl) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageUrl = imageUrl;
        this.id = id;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public void setId(String id){
        this.id = id;
    }
    public String getId() {
        return id;
    }

}

