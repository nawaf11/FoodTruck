package com.example.z7n.foodtruck;

/**
 * Created by z7n on 2/16/2018.
 */

public class PHPHelper {

    public static final String DOMAIN_NAME = "http://foodtruck.mywebcommunity.org/";

    public static class Truck {
        public static final String insert = DOMAIN_NAME + "php/register_truck.php";
    }

    public static class Customer {
        public static final String insert = DOMAIN_NAME + "php/register_customer.php";
    }
}
