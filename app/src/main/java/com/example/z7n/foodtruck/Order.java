package com.example.z7n.foodtruck;

import android.content.Context;
import android.util.Log;

import com.example.z7n.foodtruck.Database.DbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by z7n on 3/27/2018.
 */

public class Order {
    private long orderID;
    private long truckID;
    private long customerID;
    private String customerName;
    private double totalPrice;
    private String status;
    private ArrayList<OrderItem> orderItems;
    private String timestamp;
    private String truckName;

    public Order(){
        status = DbHelper.Order.STATUS_PENDING;
    }

    public ArrayList<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(ArrayList<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public long getTruckID() {
        return truckID;
    }

    public void setTruckID(long truckID) {
        this.truckID = truckID;
    }

    public long getOrderID() {
        return orderID;
    }

    public void setOrderID(long orderID) {
        this.orderID = orderID;
    }

    public long getCustomerID() {
        return customerID;
    }

    public void setCustomerID(long customerID) {
        this.customerID = customerID;
    }

    public JSONArray getOrderList_json(){
        JSONArray array = new JSONArray();

        for (OrderItem item: orderItems) {
            if(item.getQuantity() > 0) {
                JSONObject object = new JSONObject();
                try {
                    object.put("pid", item.getProductID());
                    object.put("qty", item.getQuantity());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                array.put(object);
            }
        }

        Log.d("jsonArr", array.toString());
        return array;
    }

    public String getStatus() {
        return status;
    }

    public String getStatus_text(Context context){

        switch (status.toUpperCase()){
            case DbHelper.Order.STATUS_PENDING:
                return context.getString(R.string.pending_await_order);
            case DbHelper.Order.STATUS_ACCEPTED:
                return context.getString(R.string.orderStatus_accepted);
            case DbHelper.Order.STATUS_REJECTED:
                return context.getString(R.string.orderStatus_rejected);
        }

        return "";
    }

    public void setStatus(String status) {
        this.status = status.toUpperCase();
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public boolean isPending() {
        return status.equals(DbHelper.Order.STATUS_PENDING);
    }

    public boolean isAccepted() {
        return status.equals(DbHelper.Order.STATUS_ACCEPTED);
    }

    public boolean isRejected() {
        return status.equals(DbHelper.Order.STATUS_REJECTED);
    }

    public void setTruckName(String truckName) {
        this.truckName = truckName;
    }

    public String getTruckName() {
        return truckName;
    }

    public static class OrderItem {
        private long productID;
        private String productName;
        private double productPrice;
        private int quantity;
        private double totalPrice;


        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
            totalPrice = quantity * productPrice;
        }


        public long getProductID() {
            return productID;
        }

        public void setProductID(long productID) {
            this.productID = productID;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public double getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(double totalPrice) {
            this.totalPrice = totalPrice;
        }

        public double getProductPrice() {
            return productPrice;
        }

        public void setProductPrice(double productPrice) {
            this.productPrice = productPrice;
        }
    }
}
