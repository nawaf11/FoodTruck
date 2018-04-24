package com.example.z7n.foodtruck.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.z7n.foodtruck.Activity.MainActivity;
import com.example.z7n.foodtruck.Customer;
import com.example.z7n.foodtruck.Order;
import com.example.z7n.foodtruck.PHPHelper;
import com.example.z7n.foodtruck.R;
import com.example.z7n.foodtruck.Truck;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by z7n on 4/3/2018.
 */

public class MyOrderFragmentCustomer extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private com.example.z7n.foodtruck.Fragments.MyOrdersFragment.OrderAdapter orderAdapter;

    private Customer customer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // ========== MainActivity needs part ====================
        if (getActivity() != null) {
            MainActivity mc = (MainActivity) getActivity();
            mc.setToolbarTitle(R.string.my_orders);
            mc.hideMenuItems();
            customer = mc.getLoginState().getCustomer();
        }
        // ========== MainActivity needs part ====================

        View parentView = inflater.inflate(R.layout.truck_list_fragment, container, false);

        initViews(parentView);
        starTask();

        return parentView;
    }

    private void initViews(View parentView) {
        recyclerView = parentView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        progressBar = parentView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        orderAdapter = new com.example.z7n.foodtruck.Fragments.MyOrdersFragment.OrderAdapter(getContext(), null);
        orderAdapter.setCustomerForm();
        recyclerView.setAdapter(orderAdapter);
    }

    private void starTask() {
        progressBar.setVisibility(View.VISIBLE);

        AndroidNetworking.get(PHPHelper.Order.get_customer_orders)
                .addQueryParameter("customerID", String.valueOf(customer.getUserId()))
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("orderTask", response.toString());
                try {
                    if (response.getString("state").equals("success")) {
                        updateList(response);
                        progressBar.setVisibility(View.GONE);
                    } else
                        Toast.makeText(getContext(), R.string.unknownError, Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    Toast.makeText(getContext(), R.string.unknownError, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(ANError anError) {
                Log.d("orderTask", anError.getErrorDetail());

                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), R.string.serverNotResponse, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void restartListTask() {
        orderAdapter.orderList.clear();
        orderAdapter.notifyDataSetChanged();
        starTask();
    }

    private void updateList(JSONObject response) throws JSONException {
        ArrayList<Order> orderList = new ArrayList<>();
        JSONArray array = response.getJSONArray("data");

        if (array.length() == 0)
            Toast.makeText(getContext(), R.string.no_order_found, Toast.LENGTH_SHORT).show();

        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            Order order = new Order();
            order.setOrderID(object.getLong("oid"));
            order.setStatus(object.getString("status"));
            order.setTruckName(object.getString("name"));
            order.setTotalPrice(object.getDouble("totalprice"));
            order.setTimestamp(object.getString("timestamp"));

            ArrayList<Order.OrderItem> orderItems = new ArrayList<>();
            JSONArray json_orderItems = object.getJSONArray("orderItems");
            for (int k = 0; k < json_orderItems.length(); k++) {
                JSONObject jsonObject_orderItem = json_orderItems.getJSONObject(k);
                Order.OrderItem orderItem = new Order.OrderItem();
                orderItem.setProductID(jsonObject_orderItem.getLong("pid"));
                orderItem.setProductName(jsonObject_orderItem.getString("name"));
                orderItem.setProductPrice(jsonObject_orderItem.getDouble("price"));
                orderItem.setQuantity(jsonObject_orderItem.getInt("quantity"));
                orderItems.add(orderItem);
            }
            order.setOrderItems(orderItems);

            orderList.add(order);
        }

        orderAdapter.getOrderList().addAll(orderList);
        orderAdapter.notifyDataSetChanged();
    }

}
