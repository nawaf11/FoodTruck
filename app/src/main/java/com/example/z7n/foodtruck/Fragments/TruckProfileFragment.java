package com.example.z7n.foodtruck.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.z7n.foodtruck.Activity.MainActivity;
import com.example.z7n.foodtruck.LoginState;
import com.example.z7n.foodtruck.R;
import com.example.z7n.foodtruck.Truck;


/**
 *  // This fragment when the truck want to update his Account info like Edit the Menu, password.
 */
public class TruckProfileFragment extends Fragment {

    private Truck truck;

    private View parentView;

    private TextView headerTruckName;
    private TextView changePassword;
    private EditText userName_editText;
    private EditText email_editText;
    private EditText phone_editText;
    private EditText truckName_editText;
    private EditText truckDescription_editText;
    private ImageView truckImageView;

    public TruckProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        parentView = inflater.inflate(R.layout.fragment_truck_profile, container, false);
        initViews();
        initTruck(); // set truck obj from MainActivity and show the data.


        return parentView;
    }

    private void initViews() {
        headerTruckName = parentView.findViewById(R.id.truckProfile_truckHeaderName);
        userName_editText = parentView.findViewById(R.id.truckProfile_truckUsernameEdit);
        email_editText = parentView.findViewById(R.id.truckProfile_truckEmailEdit);
        phone_editText = parentView.findViewById(R.id.truckProfile_truckPhoneEdit);
        truckName_editText = parentView.findViewById(R.id.truckProfile_truckNameEdit);
        truckDescription_editText = parentView.findViewById(R.id.truckProfile_truckDescriptionEdit);
        truckImageView = parentView.findViewById(R.id.truckProfile_truckImage);
        changePassword = parentView.findViewById(R.id.truckProfile_changePassword);
    }

    private void initTruck() {

        if(getActivity() == null)
            return;

        LoginState loginState = ((MainActivity)getActivity()).getLoginState();

        if( !loginState.isTruck() || loginState.isVisitor())
            return;

        truck = loginState.getTruck();
        if(truck == null)
            return;

        headerTruckName.setText(truck.getTruckName());
        userName_editText.setText(truck.getUserName());
        email_editText.setText(truck.getEmail());
        phone_editText.setText(truck.getPhoneNumber());
        truckName_editText.setText(truck.getTruckName());
        truckDescription_editText.setText(truck.getDescription());
    }

}
