package com.example.z7n.foodtruck;

/**
 * Created by z7n on 2/7/2018.
 *
 */

public class Product {
    private long id;
    private String name;
    private double price;

    private Product(long id){
        this.id = id;
        // ...
    }


    public long getProductId() {
        return id;
    }

    public void setProductId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
