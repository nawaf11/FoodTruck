package com.example.z7n.foodtruck;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by nasser on 2/12/2018.
 class for the Customer.
 */

public class Customer {
    private long id; // This random auto-incremented number from database
    private String userName;
    private List<Long> favorateTrucks;
    private String email;
    private String phoneNumber;



    public Customer() {

    }

    public void addToFavorate(Long id){
        favorateTrucks.add(id);
    }

    public boolean removeFromFavroate(Long id){
       return favorateTrucks.remove(id);
    }

    public long getUserId() {
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