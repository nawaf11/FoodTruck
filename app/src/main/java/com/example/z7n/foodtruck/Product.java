package com.example.z7n.foodtruck;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;

/**
 * Created by z7n on 2/7/2018.
 *
 */

public class Product {
    private long id;
    private String name;
    private double price;
    private String description = "";

    private Drawable image;
    private Uri imageUri;

    private int orderQuantity;

    public Product(){

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

    public Drawable getImage() {
        return image;
    }

    public String getImageLink(){
        return PHPHelper.Product.image + id;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getOrderQuantity() {
        return orderQuantity;
    }

    public void setOrderQuantity(int orderQuantity) {
        this.orderQuantity = orderQuantity;
    }
}
