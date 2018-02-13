package com.example.z7n.foodtruck.Activity;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;


import com.example.z7n.foodtruck.Fragments.LoginFragment;
import com.example.z7n.foodtruck.Fragments.MapFragment;
import com.example.z7n.foodtruck.Fragments.TruckListFragment;
import com.example.z7n.foodtruck.Fragments.TruckProfileFragment;
import com.example.z7n.foodtruck.LoginState;
import com.example.z7n.foodtruck.R;
import com.example.z7n.foodtruck.Truck;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView mNavigationView;
    private int navigationSelectedItem;
    private DrawerLayout mDrawerLayout;
    private LoginState loginState; // Detail of current login: isVisitor? , isTruck?, get Truck/User Object

/** TODO:
  ================== Fragments: ====================
   - MapFragment
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginState = new LoginState();

        setupNavigationView();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TruckListFragment()).commit();

    }

    private void setupNavigationView() {
        if(getSupportActionBar() != null) {
            Log.d("actionBar","insideIf statement!");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        mNavigationView = findViewById(R.id.navigationView);

        mNavigationView.setCheckedItem(R.id.navigationBarItem_truckList);
        navigationSelectedItem = R.id.navigationBarItem_truckList;
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.setItemIconTintList(null);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this
                ,mDrawerLayout,R.string.nav_open,R.string.nav_close);
        mDrawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // =========== Setup Navigation Header ==============
        View navHeader =  mNavigationView.getHeaderView(0);
        loginChangeVisitorHeader(); // Default header.
       navHeader.findViewById(R.id.navigation_header_signIn)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new LoginFragment()).commit();
                      mDrawerLayout.closeDrawer(GravityCompat.START);
                    }
                });

       navHeader.findViewById(R.id.navigationHeader_signOut)
               .setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               loginChangeVisitorHeader();
               getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                       new TruckListFragment()).commit();
               mDrawerLayout.closeDrawer(GravityCompat.START);
           }
       });

       final Switch statusSwitch = navHeader.findViewById(R.id.navigationHeader_truckStatusSwitch);

       statusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               if(isChecked) {
                   statusSwitch.setText(Truck.getStatusText(getBaseContext(),true));
                   statusSwitch.setTextColor(getResources().getColor((R.color.truckStatus_green)));
               }
               else {
                   statusSwitch.setText(Truck.getStatusText(getBaseContext(),false));
                   statusSwitch.setTextColor(getResources().getColor((R.color.truckStatus_red)));
               }
           }
       });

    }

    private void setupBottomToolbar() {
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
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new MapFragment()).commit();

                    }
                });

        findViewById(R.id.barItem_container_list)
                .setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        Log.d("traceer","baItem.OnClick start");
                        unselectBarItems();
                        view.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new TruckListFragment()).commit();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

       if(item.getItemId() == 16908332){ // 16908332 is id of nav_item
            mDrawerLayout = findViewById(R.id.drawer_layout);
           if(mDrawerLayout.isDrawerOpen(GravityCompat.START))
               mDrawerLayout.closeDrawer(GravityCompat.START);
           else
               mDrawerLayout.openDrawer(GravityCompat.START);
       }

        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mDrawerLayout.closeDrawer(GravityCompat.START);

       if(item.getItemId() == navigationSelectedItem)
           return true;

        navigationSelectedItem = item.getItemId();
        mNavigationView.setCheckedItem(item.getItemId());

        switch (item.getItemId()){
            case R.id.navigationBarItem_map:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MapFragment()).commit();
                return true;

            case R.id.navigationBarItem_truckList:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new TruckListFragment()).commit();
                return true;
            case R.id.navigationBarItem_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new TruckProfileFragment()).commit();
                return true;
        }

        return false;
    }


    public LoginState getLoginState() {
        return loginState;
    }

    public void setLoginState(LoginState mLoginState, boolean isTruck) { // This must be called from LoginFragment
        this.loginState = mLoginState;
        if(isTruck)
            loginChangeTruckHeader(mLoginState.getTruck());
        else
            loginChangeCustomerHeader();
    }

    private void loginChangeTruckHeader(Truck truck){
        mNavigationView.getHeaderView(0).findViewById(R.id.truckHeader_container).setVisibility(View.VISIBLE);
        mNavigationView.getHeaderView(0).findViewById(R.id.visitorHeader_container).setVisibility(View.GONE);
        mNavigationView.getHeaderView(0).findViewById(R.id.customerHeader_container).setVisibility(View.GONE);

        if (truck == null)
            return;

        TextView truckName = mNavigationView.getHeaderView(0).findViewById(R.id.navigationHeader_truckName);
        TextView truckUsername = mNavigationView.getHeaderView(0).findViewById(R.id.navigationHeader_truckUserName);
        Switch truckStatus = mNavigationView.getHeaderView(0).findViewById(R.id.navigationHeader_truckStatusSwitch);

        truckName.setText(truck.getTruckName());
        truckUsername.setText(truck.getUserName());
        if(truck.isStatusOpen()){ // Default color is green.
            truckStatus.setChecked(true);
        }

    }

    private void loginChangeCustomerHeader(){

    }

    private void loginChangeVisitorHeader(){ // when user click Sign-Out become visitor.
        mNavigationView.getHeaderView(0).findViewById(R.id.visitorHeader_container).setVisibility(View.VISIBLE);
        mNavigationView.getHeaderView(0).findViewById(R.id.truckHeader_container).setVisibility(View.GONE);
        mNavigationView.getHeaderView(0).findViewById(R.id.customerHeader_container).setVisibility(View.GONE);

    }
}
