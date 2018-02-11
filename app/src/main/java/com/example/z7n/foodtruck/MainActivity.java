package com.example.z7n.foodtruck;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.z7n.foodtruck.Fragments.MapFragment;


public class MainActivity extends AppCompatActivity {
/*TODO:
  ================== Fragments: ====================
   - MapFragment.
   - TruckListFragment
   - CustomerProfileFragment
   - TruckProfileFragment
   - TruckPageFragment
   - NotificationsFragment
   - RegisterFragment ( 2 option as truck/customer )
   - LoginFragment ( 2 option as truck/customer)
     ------- Fragment for Truck -------
      - MyOrderFragment
      - MenuFragment

     ------- Fragment for Customer -------

    */

public void nasser(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        setupToolbar(); // include items listener .


        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MapFragment()).commit();

    }

    private void setupToolbar() {
        findViewById(R.id.barItem_container_account)
                .setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        Log.d("traceer","baItem.OnClick start");
                        unselectBarItems();
                        view.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));

                    }
                });

        findViewById(R.id.barItem_container_map)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("traceer","baItem.OnClick start");
                        unselectBarItems();
                        view.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));

                    }
                });

        findViewById(R.id.barItem_container_list)
                .setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        Log.d("traceer","baItem.OnClick start");
                        unselectBarItems();
                        view.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
                    }
                });

    }

    private void unselectBarItems(){
        findViewById(R.id.barItem_container_account).setBackgroundColor(Color.TRANSPARENT);
        findViewById(R.id.barItem_container_map).setBackgroundColor(Color.TRANSPARENT);
        findViewById(R.id.barItem_container_list).setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
}
