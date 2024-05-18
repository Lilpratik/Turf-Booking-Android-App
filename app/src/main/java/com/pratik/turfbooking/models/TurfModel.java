package com.pratik.turfbooking.models;
public class TurfModel {
    private String turfName;
    private String location;

    public TurfModel(String turfName, String location) {
        this.turfName = turfName;
        this.location = location;
    }

    public String getTurfName() {
        return turfName;
    }

    public String getLocation() {
        return location;
    }
}
