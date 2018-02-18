package com.example.z7n.foodtruck;

import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.z7n.foodtruck.Activity.MainActivity;
import com.example.z7n.foodtruck.Fragments.TruckListFragment;

import org.json.JSONException;
import org.json.JSONObject;

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

    public static void CreateLogin(MainActivity activity, JSONObject response, boolean isTruck) throws JSONException {
        JSONObject data = response.getJSONObject("data");

        if(activity == null)
            return;

        LoginState loginState = (isTruck) ? asTruck(data) : asCustomer(data);
        if (isTruck)
            activity.setLoginState(loginState);
        else
            activity.setLoginState(loginState);

    }

    private static LoginState asTruck(JSONObject data) throws JSONException {
        LoginState loginState = new LoginState();
        Truck truck = new Truck();
        long tid = Long.parseLong(data.getString("TruckID"));
        truck.setTruckId(tid);
        truck.setTruckName(data.getString("name"));
        truck.setUserName(data.getString("username"));
        truck.setDescription(data.getString("description"));
        truck.setEmail(data.getString("email"));
        truck.setPhoneNumber(data.getString("phoneNum"));

        loginState.setTruck(truck);
        return loginState;
    }

    private static LoginState asCustomer(JSONObject data){
        return null;
    }

}
