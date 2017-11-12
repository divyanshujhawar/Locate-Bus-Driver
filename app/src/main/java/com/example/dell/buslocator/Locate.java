package com.example.dell.buslocator;

/**
 * Created by DELL on 11/11/2017.
 */

public class Locate {
    double latitude;
    private String id;
    double longitude;
    private String contact;
    private String name;
    private String email;

    public Locate(String id,double latitude, double longitude, String name, String contact, String email) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.contact = contact;
        this.name = name;
        this.email = email;
    }

    public Locate() {
    }

    public String getEmail() {
        return email;
    }

    public String getContact() {
        return contact;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getId() {
        return id;
    }

    public double getLongitude() {
        return longitude;
    }
}
