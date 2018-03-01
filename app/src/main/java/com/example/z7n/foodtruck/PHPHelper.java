package com.example.z7n.foodtruck;

/**
 * Created by z7n on 2/16/2018.

 */

public class PHPHelper {

    public static final String DOMAIN_NAME = "http://foodtruck.mywebcommunity.org/";

    public static class Truck {
        public static final String insert = DOMAIN_NAME + "php/register_truck.php";
        public static final String login = DOMAIN_NAME +  "php/login_truck.php";
        public static final String update_profile = DOMAIN_NAME +  "php/update_truck_prof.php";
        public static final String update_status = DOMAIN_NAME + "/php/update_state.php";
        public static final String update_location = DOMAIN_NAME + "/php/update_truck_location.php";
    }

    public static class Customer {
        public static final String insert = DOMAIN_NAME + "php/register_customer.php";
        public static final String login = DOMAIN_NAME +  "php/login_customer.php";

    }

    public static class Product {
        public static final String add = DOMAIN_NAME +  "php/insert_product.php";
        public static final String delete = DOMAIN_NAME +  "php/delete_product.php";
        public static final String update = DOMAIN_NAME +  "php/update_product.php";
        public static final String getList = DOMAIN_NAME + "php/get_product.php";

    }
}
