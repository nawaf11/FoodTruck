package com.example.z7n.foodtruck;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by nawaf on 2/7/2018.
   class for the trucks.
 */

public class Truck {
    private long id; // This random auto-incremented number from database
    private String userName;
    private String truckName;
    private String email;
    private String phoneNumber;
    private String description;
    private boolean isOpen=false; // On , Off
    private boolean isAcceptOrder=false; // is the truck want accept order from customer.
    private double rate; // ex: 4.5

    private Location location; // current location of the truck.

    public Truck(){

    }

    public Truck getTruckFromServer(String emailOrUsername, String password){
        // TODO: ....
        return null;
    }

    public static String getStatusText(Context context, boolean isOpen){
        if (isOpen)
            return context.getResources().getString(R.string.truckStatus_open);

        return context.getResources().getString(R.string.truckStatus_closed);
    }

    public long getTruckId() {
        return id;
    }

    public void setTruckId(long truckId) {
        this.id = truckId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTruckName() {
        return truckName;
    }

    public void setTruckName(String truckName) {
        this.truckName = truckName;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isStatusOpen() {
        return isOpen;
    }

    public void setStatus(boolean isOpen) {
        this.isOpen = isOpen;
    }

    public String getStatusText(Context context){
        if (isOpen)
            return context.getResources().getString(R.string.truckStatus_open);

        return context.getResources().getString(R.string.truckStatus_closed);
    }

    public boolean isAcceptOrder() {
        return isAcceptOrder;
    }

    public void setAcceptOrder(boolean acceptOrder) {
        isAcceptOrder = acceptOrder;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
