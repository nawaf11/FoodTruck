package com.example.z7n.foodtruck.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.z7n.foodtruck.Activity.MainActivity;
import com.example.z7n.foodtruck.LoginState;
import com.example.z7n.foodtruck.R;
import com.example.z7n.foodtruck.Truck;

/**
 */

public class TruckPageFragment extends Fragment {

    private View parentView;
    private Truck truck;
    private  TextView truckName;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        parentView = inflater.inflate(R.layout.fragment_truck_profile, container, false);
        truckName = parentView.findViewById(R.id.truckName);
        initTruck();

        truckName.setText(truck.getTruckName());

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void initTruck() {

        if(getActivity() == null)
            return;

        LoginState loginState = ((MainActivity)getActivity()).getLoginState();

        if( !loginState.isTruck() || loginState.isVisitor())
            return;

        truck = loginState.getTruck();
    }
}
