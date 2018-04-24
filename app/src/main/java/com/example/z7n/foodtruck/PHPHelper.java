package com.example.z7n.foodtruck;

/**
 * Created by z7n on 2/16/2018.

 */

public class PHPHelper {

    public static final String DOMAIN_NAME = "http://foodtruck.mywebcommunity.org/";

    public static class Truck {
        public static final String insert = DOMAIN_NAME + "php/register_truck.php";
        public static final String login = DOMAIN_NAME +  "php/login_truck.php";
        public static final String get_truck_pageList = DOMAIN_NAME + "php/getTruckPage.php";
        public static final String get_truck_detail = DOMAIN_NAME + "php/getTruck_detail.php";
        public static final String update_profile = DOMAIN_NAME +  "php/update_truck_prof.php";
        public static final String update_status = DOMAIN_NAME + "php/update_state.php";
        public static final String update_location = DOMAIN_NAME + "php/update_truck_location.php";
        public static final String upload_truckImage = DOMAIN_NAME + "php/upload_truckImage.php";
        public static final String get_truckImage = DOMAIN_NAME + "images/trucks/";
        public static String get_openTrucks = DOMAIN_NAME + "php/getOpenTrucks.php";
        public static String rate = DOMAIN_NAME + "php/rate.php";
        public static String acceptOrder = DOMAIN_NAME + "php/can_prepare.php";
        public static String get_favorites = DOMAIN_NAME + "php/getFavoriteTrucks.php";
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
        public static final String upload_image = DOMAIN_NAME + "php/upload_productImage.php";
        public static final String image = DOMAIN_NAME + "images/products/";


    }

    public static class Order {
        public static final String add = DOMAIN_NAME +  "php/insert_order.php";
        public static final String get_truck_orders = DOMAIN_NAME +  "php/my_order.php";
        public static String update_status = DOMAIN_NAME + "php/update_orderStatus.php";
        public static String truck_get_newOrder = DOMAIN_NAME + "php/myNewOrders.php";
        public static String get_customer_orders = DOMAIN_NAME + "php/customer_order.php";
        public static String get_customer_orderUpdates = DOMAIN_NAME + "php/orderListCustomers.php";

    }
}
