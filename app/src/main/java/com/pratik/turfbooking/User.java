package com.pratik.turfbooking;

public class User {
    private String name;
    private String email;
    private String phone;
    private String password;
    private String birthDate;
    private String gender;

    public User() {
        // Default constructor required for Firebase
    }

    public User(String name, String email, String phone, String password, String birthDate, String gender) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.birthDate = birthDate;
        this.gender = gender;
    }

    // Getter and setter methods for all fields

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}

