package com.halcyonmobile.adoption.model;


import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    public String id;
    public String username;
    public String email;
    public String password;
    public long dob;
    public String country;
    public String city;
    public String image;
    public int adoptedPets;
    public int givenPets;
    public String description;

    public User(String id, String username, String email, String password, long dob, String country, String city, String image, int adoptedPets, int givenPets, String description) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.dob = dob;
        this.country = country;
        this.city = city;
        this.image = image;
        this.adoptedPets = adoptedPets;
        this.givenPets = givenPets;
        this.description = description;
    }

    public User() {
    }
}
