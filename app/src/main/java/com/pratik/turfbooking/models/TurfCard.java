package com.pratik.turfbooking.models;

public class TurfCard {
    private String title;
    private String location;
    private int imageResource;

    public TurfCard(String title, String location, int imageResource) {
        this.title = title;
        this.location = location;
        this.imageResource = imageResource;
    }

    public TurfCard(String turfName, String location, double price, String amenities, String imageUrl) {
    }

    // Getter methods
    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public int getImageResource() {
        return imageResource;
    }

    // Setter methods if needed
    public void setTitle(String title) {
        this.title = title;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }
}
