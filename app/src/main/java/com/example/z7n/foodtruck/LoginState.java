package com.example.z7n.foodtruck;

import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * this class used in MainActivity if it's null that mean there's no Login(truck/customer).
 */

public class LoginState {
    private boolean state; // if false: no one logged in (visitor).
    private boolean isTruck;
    private Truck truck; // if null: visitor/customer.
    private Customer customer;

    public LoginState(){
        state = false; // no one logged in.
    }

    public boolean  isTruck(){ // if return false: customer.
        return isTruck;
    }

    public boolean isVisitor(){return !state;} // if false: no one logged in (visitor).

    public @Nullable Truck getTruck(){return truck;}

    public @Nullable Customer getCustomer(){return customer;}

    public void setTruck(Truck truck){
        this.truck = truck;
        state = true;
        this.isTruck = true;
    }

    public void setCustomer(Customer customer){
        this.customer = customer;
        state =true;
        isTruck = false;
    }

    public static LoginState CreateLogin(JSONObject response, boolean isTruck) throws JSONException {
        JSONObject data = response.getJSONObject("data");
        return (isTruck) ? asTruck(data) : asCustomer(data);
    }

    private static LoginState asTruck(JSONObject data) throws JSONException {
        LoginState loginState = new LoginState();
        Truck truck = new Truck();
        long tid = Long.parseLong(data.getString("TruckID"));
        Log.d("dsfdsfsffdsfdsfs",tid+"");
        truck.setTruckId(tid);
        truck.setTruckName(data.getString("name"));
        truck.setUserName(data.getString("username"));
        truck.setDescription(data.getString("description"));
        truck.setEmail(data.getString("email"));
        truck.setPhoneNumber(data.getString("phoneNum"));
        truck.setStatus(data.getString("status").equals("true"));
        truck.setAcceptOrder(data.getString("canprepare").equals("true"));
        truck.setLastOrderID(data.getLong("lastOrder"));

        loginState.setTruck(truck);
        return loginState;
    }

    private static LoginState asCustomer(JSONObject data) throws JSONException {
        LoginState loginState = new LoginState();
        Customer customer = new Customer();
        customer.setUserId(data.getLong("customerId"));
        customer.setUserName(data.getString("username"));
        customer.setEmail(data.getString("email"));
        customer.setPhoneNumber(data.getString("phoneNum"));
        loginState.setCustomer(customer);

        return loginState;
    }

}
