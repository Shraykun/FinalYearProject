package com.example.beetrackingapp;

public class Info {
    private String ID;
    private String address;
    private String country;
    private String latitude;
    private String longitude;
    private String image;

    public Info() {
    }

    public Info(String id, String address, String country, String latitude, String longitude, String image) {
        this.ID = id;
        this.address = address;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
        this.image = image;
    }

    public String getID() { return ID; }

    public void setID(String ID) { this.ID = ID; }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
