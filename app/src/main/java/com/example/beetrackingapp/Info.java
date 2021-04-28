package com.example.beetrackingapp;

public class Info {
    private String ID;
    private String address;
    private String color;
    private String country;
    private String feature;
    private String latitude;
    private String longitude;
    private String status;
    private String time;
    private String weather;
    private String image;

    public Info() {
    }

    public Info(String id, String address, String color, String country, String feature, String latitude, String longitude, String status, String image, String time, String weather) {
        this.ID = id;
        this.address = address;
        this.color = color;
        this.country = country;
        this.feature = feature;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
        this.time = time;
        this.weather = weather;
        this.image = image;
    }

    public String getColor() { return color; }

    public void setColor(String color) { this.color = color; }

    public String getFeature() { return feature; }

    public void setFeature(String feature) { this.feature = feature; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getTime() { return time; }

    public void setTime(String time) { this.time = time; }

    public String getWeather() { return weather; }

    public void setWeather(String weather) { this.weather = weather; }

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
