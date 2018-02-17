package com.example.z7n.foodtruck.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.z7n.foodtruck.Activity.MainActivity;
import com.example.z7n.foodtruck.LoginState;
import com.example.z7n.foodtruck.R;
import com.example.z7n.foodtruck.Truck;

/**
 * Created by toshiba on 2/11/2018.

 */

public class LoginFragment extends Fragment{
    private View parentView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        parentView = inflater.inflate(R.layout.login,container,false);
        initViews();

        return parentView;
    }

    private void initViews() {
        parentView.findViewById(R.id.login_signIn_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity()!=null){
                    LoginState loginState = new LoginState();
                    Truck truck = getFakeTruck();
                    loginState.setTruck(truck);
                    ((MainActivity)getActivity()).setLoginState(loginState);

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container,new TruckListFragment()).commit();
                }
            }
        });
    }

    private Truck getFakeTruck(){
       Truck truck = new Truck("nawaf11","123456");
        truck.setTruckName("Burgent HQ");
        truck.setTruckId(1);
        truck.setAcceptOrder(true);
        truck.setDescription("عربة لتحضير البرجر و ..");
        truck.setEmail("burgerHq@gmail.com");
        truck.setPhoneNumber("055555000000");
        truck.setUserName("burgerhq1");
        truck.setStatus(false);
        truck.setRate(4.7);
        return truck;
    }
}
