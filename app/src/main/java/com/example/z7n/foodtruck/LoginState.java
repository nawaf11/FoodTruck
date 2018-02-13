package com.example.z7n.foodtruck;

import android.support.annotation.Nullable;

/**
 * this class used in MainActivity if it's null that mean there's no Login(truck/customer).
 */

public class LoginState {
    private boolean state; // if false: no one logged in (visitor).
    private boolean isTruck;
    private Truck truck; // if null: visitor/customer.
    private User user;

    public LoginState(){
        state = false; // no one logged in.
    }

    public boolean  isTruck(){ // if return false: customer.
        return isTruck;
    }

    public boolean isVisitor(){return !state;} // if false: no one logged in (visitor).

    public @Nullable Truck getTruck(){return truck;}

    public @Nullable User getUser(){return user;}

    public void setTruck(Truck truck){
        this.truck = truck;
        state = true;
        this.isTruck = true;
    }

    public void setUser(User user){
        this.user = user;
        state =true;
        isTruck = false;
    }
}
