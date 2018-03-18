package com.example.z7n.foodtruck.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.z7n.foodtruck.Activity.MainActivity;
import com.example.z7n.foodtruck.PHPHelper;
import com.example.z7n.foodtruck.R;
import com.example.z7n.foodtruck.Truck;
import com.example.z7n.foodtruck.Customer;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

/**
 */

public class TruckPageFragment extends Fragment {

    private View parentView;
    private Truck truck;
    private Customer customer;
    private  TextView truckName;
    private RatingBar rateBar;
    private TextView rateValue_textView;
    private  TextView truckDescription;
    private  TextView phoneNum;
    private  TextView truckEmail;
    private  TextView truckState;
    private Button rateButton;
    private Button orderButton;
    private Button reserveButton;
    private ImageView truckImageView;
    private ImageView phoneIcon;
    private ImageView emailIcon;
    private ImageView favorite;
    private ImageView rateStarIcon;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // ========== MainActivity needs part ====================
        if(getActivity() != null) {
            MainActivity mc = (MainActivity) getActivity();
            mc.setToolbarTitle(R.string.truck_page);
            mc.hideMenuItems();
            customer = mc.getLoginState().getCustomer();
        }
        // ========== MainActivity needs part ====================

        parentView = inflater.inflate(R.layout.truckpage_fragment, container, false);


        parentView.findViewById(R.id.truckPage_layout).setVisibility(View.INVISIBLE);
        parentView.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

        initTruck();
        initViews();
        return parentView;
    }

    private void initViews() {
        truckName = parentView.findViewById(R.id.truckName);

        truckDescription =parentView.findViewById(R.id.truck_description);
        phoneNum =parentView.findViewById(R.id.phone_number);
        truckEmail=parentView.findViewById(R.id.email);
        rateValue_textView = parentView.findViewById(R.id.rateNum);
        truckState = parentView.findViewById(R.id.truckstate);
        rateBar = parentView.findViewById(R.id.ratingBarUser);
        rateButton = parentView.findViewById(R.id.rateBtn);
        favorite = parentView.findViewById(R.id.favImage);
        orderButton = parentView.findViewById(R.id.orderBtn);
        reserveButton = parentView.findViewById(R.id.bookBtn);
        truckImageView = parentView.findViewById(R.id.truckImage);
        phoneIcon = parentView.findViewById(R.id.phone_imageView);
        emailIcon = parentView.findViewById(R.id.email_imageView);
        rateStarIcon = parentView.findViewById(R.id.ratestar);

        Picasso.with(getContext()).load(R.drawable.whatsapp).into(phoneIcon);
        Picasso.with(getContext()).load(R.drawable.emails).into(emailIcon);
        Picasso.with(getContext()).load(R.drawable.star).into(rateStarIcon);
        Picasso.with(getContext()).load(R.drawable.nofav).into(favorite);

        Picasso.with(getContext()).load(PHPHelper.Truck.get_truckImage + truck.getTruckId())
                .memoryPolicy(MemoryPolicy.NO_STORE, MemoryPolicy.NO_CACHE)
                .fit().into(truckImageView, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                Picasso.with(getContext()).load(R.drawable.foodtruck).fit().into(truckImageView);
            }
        });

        favorite.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                //customer.removeFromFavroate(truck.getTruckId());
            }

        });

        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 if(customer == null){
                     Toast.makeText(getContext(),R.string.youMustRegister, Toast.LENGTH_SHORT).show();
                     return;
                 }
            }
        });

        reserveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(customer == null){
                    Toast.makeText(getContext(),R.string.youMustRegister, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(customer == null){
                    Toast.makeText(getContext(),R.string.youMustRegister, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });





    }

    private void initTruck() {
        truck = new Truck();
        if (getArguments() == null)
            return;

        truck.setTruckId(getArguments().getLong("truck_id"));

        AndroidNetworking.get(PHPHelper.Truck.get_truck_detail)
                .addQueryParameter("truckId", String.valueOf(truck.getTruckId()))
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getString("state").equals("success")){
                        initTruckFromJson(response);
                    }
                    else {
                        Toast.makeText(getContext(), R.string.unknownError, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), R.string.unknownError, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(ANError anError) {
                Toast.makeText(getContext(), R.string.serverNotResponse, Toast.LENGTH_SHORT).show();
            }
        });




    }

    private void initTruckFromJson(JSONObject response) throws JSONException {
        response = response.getJSONObject("data");

        truck.setTruckName(response.getString("name"));
        truck.setDescription(response.getString("description"));
        truck.setEmail(response.getString("email"));
        truck.setPhoneNumber(response.getString("phoneNum"));
        truck.setUserName(response.getString("username"));
        boolean stateFlag = (response.getString("status").equals("true"));
        truck.setStatus(stateFlag);

        boolean acceptOrder_Flag = (response.getString("canprepare").equals("true"));
        truck.setAcceptOrder(acceptOrder_Flag);

        double rateValue;
        try {
             rateValue = Double.parseDouble(response.getString("rate"));
        } catch (Exception e){rateValue = 5.0;}

        truck.setRate(rateValue);

        truckName.setText(truck.getTruckName());
        truckDescription.setText(truck.getDescription());
        phoneNum.setText(truck.getPhoneNumber());
        truckEmail.setText(truck.getEmail());
        rateValue_textView.setText(String.valueOf(truck.getRate()));
        rateBar.setStepSize(1);
        truckState.setText(truck.getStatusText(getContext()));
        if (truck.getStatusText(getContext()).equals(getString(R.string.truckStatus_open)) )
            truckState.setTextColor(getResources().getColor(R.color.truckStatus_green));

        parentView.findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
        parentView.findViewById(R.id.truckPage_layout).setVisibility(View.VISIBLE);
    }
}
