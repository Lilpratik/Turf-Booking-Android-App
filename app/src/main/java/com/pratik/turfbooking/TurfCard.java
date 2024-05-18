package com.pratik.turfbooking;
public class TurfCard {
    private String name;
    private String location;
    private String imageUrl;

    public TurfCard(String name, String location, String imageUrl) {
        this.name = name;
        this.location = location;
        this.imageUrl = imageUrl;
    }

    // Getters and setters for name, location, and imageUrl
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
