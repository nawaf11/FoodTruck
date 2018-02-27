package com.example.z7n.foodtruck.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.z7n.foodtruck.Activity.MainActivity;
import com.example.z7n.foodtruck.LoginState;
import com.example.z7n.foodtruck.R;
import com.example.z7n.foodtruck.Truck;
import com.example.z7n.foodtruck.User;

/**
 */

public class TruckPageFragment extends Fragment {

    private View parentView;
    private Truck truck;
    private User    user;
    private  TextView truckName;
    private RatingBar userRate;
    private TextView  truckRateNum;
    private ImageView truckratestar;
    private  TextView truckDiscription;
    private  TextView truckNum;
    private  TextView truckEmail;
    private  TextView truckState;
    private TextView  truckrate;
    private ImageButton nofav;
    private ImageButton fav;


    private Button rateBtn;
    private Context Status;







    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        parentView = inflater.inflate(R.layout.truckpage_fragment, container, false);
        truckName = parentView.findViewById(R.id.truckName);
        truckratestar=parentView.findViewById(R.id.ratestar);

        truckDiscription=parentView.findViewById(R.id.truckdiscrption);
        truckNum=parentView.findViewById(R.id.number);
        truckEmail=parentView.findViewById(R.id.email);
        truckrate=parentView.findViewById(R.id.rateNum);

        initTruck();
        initViews();
        if (truck == null) {
            nofav = parentView.findViewById(R.id.favImage);

            userRate = parentView.findViewById(R.id.ratingBarUser);
            rateBtn = parentView.findViewById(R.id.rateBtn);
            rateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    double rateValue=userRate.getRating();
                    if (rateValue != 0)
                    truck.setRate(userRate.getRating());
                }
            });

//        truckName.setText(truck.getTruckName());
        }
        return parentView;
    }

    private void initViews() {
        truckName.setText(truck.getTruckName());
        truckDiscription.setText(truck.getDescription());
        truckNum.setText(truck.getPhoneNumber());
        truckEmail.setText(truck.getEmail());
        truckState.setText(truck.getStatusText(parentView));

        rateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             truck.setRate(userRate.getRating());
            }


        });
        nofav.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v){
            user.addToFavorate(truck.getTruckId());
            nofav=fav;

        }

        });
        fav.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                user.removeFromFavroate(truck.getTruckId());
                nofav=fav;

            }

        });
      //  truckState.setText(truck.getStatusText(Stutes));

    }

    private void initTruck() {



        String id = getArguments().getString("truckId");

        Truck t = new Truck();
        t.setTruckName("Brvggfeg");
        t.setDescription("hello");
        t.setEmail("@.com");
        t.setPhoneNumber("05698554665");
        t.setRate(4);
        t.setStatus(true);
        truck = t;

    }
}
