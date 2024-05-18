package com.pratik.turfbooking.models;

public class Turf {

    private String turfId;
    private String name;
    private String location;
    private String price;
    private String amenities;
    private String imageUrl; // New field for image URL
    private boolean isAvailable;
    private double latitude;
    private double longitude;

    public Turf(){

    }

    public Turf(String name, String location, String price, String amenities, String imageUrl, boolean b) {
        // Default constructor required for Firebase
    }

    public Turf(String turfId, String name, String location, String price, String amenities, String imageUrl, boolean isAvailable, double latitude, double longitude) {
        this.turfId = turfId;
        this.name = name;
        this.location = location;
        this.price = price;
        this.amenities = amenities;
        this.imageUrl = imageUrl;
        this.isAvailable = isAvailable;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Turf(String s, String giSportsTurfArena, int turf1, boolean b) {
    }

    // Getters and setters
    public String getTurfId() {
        return turfId;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getPrice() {
        return price;
    }

    public String getAmenities() {
        return amenities;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
