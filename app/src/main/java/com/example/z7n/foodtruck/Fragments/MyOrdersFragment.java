package com.example.z7n.foodtruck.Fragments;

import android.content.Context;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.z7n.foodtruck.Activity.MainActivity;
import com.example.z7n.foodtruck.Customer;
import com.example.z7n.foodtruck.Database.DbHelper;
import com.example.z7n.foodtruck.Order;
import com.example.z7n.foodtruck.PHPHelper;
import com.example.z7n.foodtruck.R;
import com.example.z7n.foodtruck.Truck;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MyOrdersFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private OrderAdapter orderAdapter;

    private Truck truck;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // ========== MainActivity needs part ====================
        if(getActivity() != null) {
            MainActivity mc = (MainActivity) getActivity();
            mc.setToolbarTitle(R.string.my_orders);
            mc.hideMenuItems();
            truck = mc.getLoginState().getTruck();
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
        orderAdapter = new OrderAdapter(getContext(), null);
        recyclerView.setAdapter(orderAdapter);
    }

    private void starTask() {
        progressBar.setVisibility(View.VISIBLE);

        AndroidNetworking.get(PHPHelper.Order.get_truck_orders)
                .addQueryParameter("truckId", String.valueOf(truck.getTruckId()))
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("orderTask", response.toString());
                try {
                    if(response.getString("state").equals("success")) {
                        updateList(response);
                        progressBar.setVisibility(View.GONE);
                    }
                    else
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

    public void restartListTask(){
        orderAdapter.orderList.clear();
        orderAdapter.notifyDataSetChanged();
        starTask();
    }

    private void updateList(JSONObject response) throws JSONException {
        ArrayList<Order> orderList = new ArrayList<>();
        JSONArray array = response.getJSONArray("data");

        if(array.length() == 0)
            Toast.makeText(getContext(), R.string.no_order_found, Toast.LENGTH_SHORT).show();

        for(int i=0; i < array.length(); i ++){
            JSONObject object = array.getJSONObject(i);
            Order order = new Order();
            order.setOrderID(object.getLong("oid"));
            order.setStatus(object.getString("status"));
            order.setCustomerName(object.getString("username"));
            order.setCustomerName(object.getString("username"));
            order.setTotalPrice(object.getDouble("totalprice"));
            order.setTimestamp(object.getString("timestamp"));

            ArrayList<Order.OrderItem> orderItems = new ArrayList<>();
            JSONArray json_orderItems = object.getJSONArray("orderItems");
            for(int k=0; k < json_orderItems.length(); k++){
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

    static class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderHolder> {
        ArrayList<Order> orderList;
        private Context context;
        private boolean isCustomer = false;

        public OrderAdapter(Context context, ArrayList<Order> list){
            orderList = list;
            this.context = context;
            if(list == null)
                orderList = new ArrayList<>();
        }

        public Context getContext(){return context;}

        public ArrayList<Order> getOrderList(){return orderList;}

        @Override
        public OrderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_list_item, parent, false);

            return new OrderAdapter.OrderHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final OrderHolder holder, final int i) {
            Order order = orderList.get(holder.getAdapterPosition());
            holder.orderBy.setText(order.getCustomerName());
            holder.orderPrice.setText(String.valueOf(order.getTotalPrice()) +" "+ context.getString(R.string.price_currency));
            holder.orderStatus.setText(order.getStatus_text(getContext()));
            holder.changeStatusBackground(order.getStatus());

            holder.orderItems_headerContainer.setOnClickListener(new View.OnClickListener() {
                @Override

                public void onClick(View v) {

                    int action = (holder.orderItems_detailContainer.getVisibility() == View.VISIBLE)?
                            View.GONE : View.VISIBLE;
                    holder.orderItems_detailContainer.setVisibility(action);
                }
            });



                holder.orderItems_detailContainer.setVisibility(View.GONE);
                holder.buttonsVisables(order.isPending());

                holder.orderItems_adapter.list.addAll(order.getOrderItems());
                holder.orderItems_adapter.notifyDataSetChanged();

                holder.accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateStatusTask(holder, true);
                    }
                });

                holder.reject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateStatusTask(holder, false);
                    }
                });

                if(isCustomer) {
                    holder.buttonsVisables(false);
                    holder.orderBy.setText(order.getTruckName());
                    holder.orderBy_label.setText(getContext().getString(R.string.order_from));
                }

            }

        @Override
        public int getItemCount() {
            return orderList.size();
        }

        public void setCustomerForm() {
            isCustomer = true;
        }

        class OrderHolder extends RecyclerView.ViewHolder {
            private TextView orderBy, orderStatus, orderTime, orderPrice ;
            private TextView accept, reject;
            private RecyclerView orderItems_recyclerView; // products
            private OrderItemAdapter orderItems_adapter; // products
            private View orderItems_detailContainer;
            private View orderItems_headerContainer;
            public TextView orderBy_label;
            // TODO: product adapter

            OrderHolder(View itemView) {
                super(itemView);
                orderBy_label = itemView.findViewById(R.id.orderBy_label);
                orderBy = itemView.findViewById(R.id.orderBy_value);
                orderStatus = itemView.findViewById(R.id.orderStatus_textView);
                orderTime = itemView.findViewById(R.id.orderTime_textView);
                orderPrice = itemView.findViewById(R.id.orderPrice_value);
                accept = itemView.findViewById(R.id.accept_order);
                reject = itemView.findViewById(R.id.reject_order);
                orderItems_recyclerView = itemView.findViewById(R.id.orderItems_recyclerView);
                orderItems_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                orderItems_adapter = new OrderItemAdapter(null);
                orderItems_recyclerView.setAdapter(orderItems_adapter);
                orderItems_detailContainer = itemView.findViewById(R.id.orderItems_detailContainer);
                orderItems_headerContainer = itemView.findViewById(R.id.orderItems_header);
            }

            public void buttonsVisables(boolean isShow){
                int action = (isShow) ? View.VISIBLE : View.GONE;

                accept.setVisibility(action);
                reject.setVisibility(action);
            }

            public void changeStatusBackground(String status) {
                if(status.equals(DbHelper.Order.STATUS_ACCEPTED)) {
                    orderStatus.setText(R.string.orderStatus_accepted);
                    orderStatus.setBackgroundResource(R.drawable.order_status_background_accepted);
                }
                else if(status.equals(DbHelper.Order.STATUS_REJECTED)) {
                    orderStatus.setText(R.string.orderStatus_rejected);
                    orderStatus.setBackgroundResource(R.drawable.order_status_background_rejected);
                }
                else {
                    orderStatus.setText(R.string.pending_await_order);
                    orderStatus.setBackgroundResource(R.drawable.order_status_background_pending);
                }
            }
        }

        class OrderItemAdapter extends RecyclerView.Adapter<MyOrdersFragment.OrderAdapter.OrderItemHolder>{
            private ArrayList<Order.OrderItem> list;

            OrderItemAdapter (ArrayList<Order.OrderItem> arr){
                list = arr;
                if(list == null)
                    list = new ArrayList<>();
            }

            public ArrayList<Order.OrderItem> getList(){return list;}

            @NonNull
            @Override
            public OrderItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.order_item, parent, false);

                return new OrderItemHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull OrderItemHolder holder, int i) {
                Order.OrderItem item = list.get(holder.getAdapterPosition());
                holder.name.setText(item.getProductName());
                holder.quantity.setText(String.valueOf(item.getQuantity()));
                holder.price.setText(String.valueOf(item.getTotalPrice()));
            }

            @Override
            public int getItemCount() {
                return list.size();
            }
        }

        class OrderItemHolder extends RecyclerView.ViewHolder {
            private TextView name, quantity, price;

            public OrderItemHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.orderItem_name);
                quantity = itemView.findViewById(R.id.orderItem_quantity);
                price = itemView.findViewById(R.id.orderItem_price);
            }
        }

        public void updateStatusTask(final OrderHolder holder, boolean isAccept){
            final String status = (isAccept) ? DbHelper.Order.STATUS_ACCEPTED : DbHelper.Order.STATUS_REJECTED;

            AndroidNetworking.post(PHPHelper.Order.update_status)
                    .addBodyParameter("orderId", String.valueOf(orderList.get(holder.getAdapterPosition()).getOrderID()))
                    .addBodyParameter("status", status)
                    .build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if(response.getString("state").equals("success")) {
                            holder.buttonsVisables(false);
                            holder.changeStatusBackground(status);

                            Toast.makeText(getContext(), R.string.orderUpdated, Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(getContext(), R.string.unknownError, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), R.string.unknownError, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(ANError anError) {
                    Toast.makeText(getContext(), R.string.unknownError, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
