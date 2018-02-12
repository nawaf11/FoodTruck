package com.example.z7n.foodtruck;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by nasser on 2/12/2018.
 class for the User.
 */

public class User {
    private long id; // This random auto-incremented number from database
    private String userName;

    private String email;
    private String phoneNumber;


    public User(String emailOrUsername, String password) {
        // user insert email/user then other data pulled from the server.

    }


    public long geUserId() {
        return id;
    }

    public void setUserId(long truckId) {
        this.id = truckId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

}