package com.example.dell.buslocator;

/**
 * Created by DELL on 11/12/2017.
 */

public class User {
    private String name;
    private String email;
    private String contact;
    private String id;

    public User() {
    }

    public User(String name, String email, String contact, String id) {
        this.name = name;
        this.email = email;
        this.contact = contact;
        this.id = id;
    }



    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getContact() {
        return contact;
    }

    public String getId() {
        return id;
    }
}
