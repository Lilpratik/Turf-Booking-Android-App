package com.pratik.turfbooking.models;

public class Venue {
    private String name;
    private String location;
    private String imageUrl;

    public Venue(String name, String location, String imageUrl) {
        this.name = name;
        this.location = location;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
