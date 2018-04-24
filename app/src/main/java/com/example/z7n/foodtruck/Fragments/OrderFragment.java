package com.example.z7n.foodtruck.Fragments;

import android.annotation.SuppressLint;
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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.example.z7n.foodtruck.Activity.MainActivity;
import com.example.z7n.foodtruck.Database.DbHelper;
import com.example.z7n.foodtruck.Order;
import com.example.z7n.foodtruck.PHPHelper;
import com.example.z7n.foodtruck.Product;
import com.example.z7n.foodtruck.R;
import com.example.z7n.foodtruck.Truck;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by z7n on 3/27/2018.

 */

public class OrderFragment extends Fragment {

    private MenuEditorFragment.ProductAdapter productAdapter;
    private RecyclerView recyclerView;
    private TextView totalPrice_textView;
    private Button orderSubmit;
    private long truckID;
    private Order order;
    ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_form_fragment,container,false);
        truckID = getArguments().getLong("truck_id");
        order = new Order();

        initViews(view);
        getProductsTask();

        return view;
    }

    @SuppressLint("SetTextI18n")
    private void initViews(View parentView) {
        recyclerView = parentView.findViewById(R.id.listView_menu);
        progressBar = parentView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        totalPrice_textView = parentView.findViewById(R.id.totalPrice_textView);
        orderSubmit = parentView.findViewById(R.id.orderSubmit);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        productAdapter = new MenuEditorFragment.ProductAdapter(this, new ArrayList<Product>());
        productAdapter.withOrderForm();
        recyclerView.setAdapter(productAdapter);

        totalPrice_textView.setText(getString(R.string.totalPrice) +" 0" );

        orderSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(productAdapter.getTotalPrice() < 1){
                    Toast.makeText(getContext(), R.string.orderPrice_mustBe_moreThan_1 ,Toast.LENGTH_LONG).show();
                    return;
                }
                MainActivity mc = (MainActivity) getActivity();
                if(mc != null && mc.getLoginState().getCustomer() != null){

                order.setCustomerID(mc.getLoginState().getCustomer().getUserId());
                order.setTruckID(truckID);
                order.setTotalPrice(productAdapter.getTotalPrice());
                order.setOrderItems(getOrderItems());
                startInsertOrderTask();

                }
            }
        });
    }

    private void startInsertOrderTask() {
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.post(PHPHelper.Order.add)
                .addBodyParameter("TruckID", String.valueOf(order.getTruckID()))
                .addBodyParameter("customerid", String.valueOf(order.getCustomerID()))
                .addBodyParameter("totalprice", String.valueOf(order.getTotalPrice()))
                .addBodyParameter("orderList", order.getOrderList_json().toString())
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);
                Log.d("orderTask", response.toString());
                try {
                    if(response.getString("state").equals("success")) {
                        order.setOrderID(response.getLong("orderID"));
                        new DbHelper(getContext()).insertOrder(order.getOrderID());
                        Toast.makeText(getContext(), R.string.orderInserted_success, Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(getContext(), R.string.unknownError, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    Toast.makeText(getContext(), R.string.unknownError, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ANError anError) {
                progressBar.setVisibility(View.GONE);
                Log.d("orderTask", anError.getErrorDetail());
                Toast.makeText(getContext(), R.string.serverNotResponse, Toast.LENGTH_SHORT).show();

            }
        });

    }

    private ArrayList<Order.OrderItem> getOrderItems(){
        ArrayList<Order.OrderItem> arr = new ArrayList<>();
        ArrayList<Product> products = productAdapter.getProducts();
        for ( Product p :  products) {
            Order.OrderItem orderItem = new Order.OrderItem();
            orderItem.setProductID(p.getProductId());
            orderItem.setQuantity(p.getOrderQuantity());
            arr.add(orderItem);
        }
        return arr;
    }

    private void getProductsTask(){
        AndroidNetworking.post(PHPHelper.Product.getList)
                .addBodyParameter("truckid",String.valueOf(truckID)).build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("state").equals("success"))
                                startProductsTask(response);
                            else
                                Toast.makeText(getContext(),R.string.unknownError,Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(),R.string.unknownError,Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(getContext(),R.string.serverNotResponse,Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void startProductsTask(JSONObject response) throws JSONException {
        ArrayList<Product> arrayList = new ArrayList<>();
        JSONArray jsonArray = response.getJSONArray("data");
        for (int i=0; i < jsonArray.length(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Product p = new Product();
            p.setProductId(jsonObject.getLong("pid"));
            p.setName(jsonObject.getString("name"));
            p.setPrice(jsonObject.getDouble("price"));
            p.setDescription(jsonObject.getString("description"));
            arrayList.add(p);
        }
        productAdapter.getProducts().addAll(arrayList);
        productAdapter.notifyDataSetChanged();
    }

    @SuppressLint("SetTextI18n")
    public void updateTotalPrice(double totalPrice) {
        DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        decimalFormat.applyPattern("0.00");

        String fixedPrice = decimalFormat.format(totalPrice);

        totalPrice_textView.setText(getString(R.string.totalPrice) + " " +fixedPrice);
    }
}
