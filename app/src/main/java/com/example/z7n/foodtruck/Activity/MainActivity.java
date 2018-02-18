package com.example.z7n.foodtruck.Activity;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.z7n.foodtruck.Fragments.LoginFragment;
import com.example.z7n.foodtruck.Fragments.MapFragment;
import com.example.z7n.foodtruck.Fragments.RegisterFragment;
import com.example.z7n.foodtruck.Fragments.TruckListFragment;
import com.example.z7n.foodtruck.Fragments.TruckProfileFragment;
import com.example.z7n.foodtruck.LoginState;
import com.example.z7n.foodtruck.PHPHelper;
import com.example.z7n.foodtruck.R;
import com.example.z7n.foodtruck.SHP;
import com.example.z7n.foodtruck.Truck;

import org.json.JSONException;
import org.json.JSONObject;


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
        AndroidNetworking.initialize(getApplicationContext());

        loginState = getRememberLoginState();

        setupNavigationView();

        setFragment(new TruckListFragment());

    }

    private void setupNavigationView() {
        if(getSupportActionBar() != null) {
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
                        setFragment(new LoginFragment());
                      mDrawerLayout.closeDrawer(GravityCompat.START);
                    }
                });

       navHeader.findViewById(R.id.navigationHeader_signOut)
               .setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               loginState = new LoginState();
               SHP.Login.clear(getBaseContext());
               loginChangeVisitorHeader();
               setFragment(new TruckListFragment());
               mDrawerLayout.closeDrawer(GravityCompat.START);
           }
       });

       navHeader.findViewById(R.id.navigation_header_createAccount)
               .setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       setFragment( new RegisterFragment());
                       mDrawerLayout.closeDrawer(GravityCompat.START);
                   }
               });

       final Switch statusSwitch = navHeader.findViewById(R.id.navigationHeader_truckStatusSwitch);
        statusSwitch.setChecked(false);
        statusSwitch.setText(Truck.getStatusText(getBaseContext(),false));
        statusSwitch.setTextColor(getResources().getColor((R.color.truckStatus_red)));

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
                setFragment(new MapFragment());
                return true;

            case R.id.navigationBarItem_truckList:
                setFragment(new TruckListFragment());
                return true;

            case R.id.navigationBarItem_profile:
                setFragment(new TruckProfileFragment() );
                return true;
        }

        return false;
    }


    public LoginState getLoginState() {
        return loginState;
    }

    public void setLoginState(LoginState mLoginState) { // This must be called from LoginFragment
        this.loginState = mLoginState;
        if(loginState.isTruck())
            loginChangeTruckHeader(mLoginState.getTruck());
        else
            loginChangeCustomerHeader();
    }

    public void setFragment(Fragment fragment, boolean isBackTrace){
        if(!isNavFragment(fragment) && navigationSelectedItem != -1) {
            mNavigationView.getMenu().findItem(navigationSelectedItem).setChecked(false);
            navigationSelectedItem = -1;
        } else if(navigationSelectedItem != -1){
            mNavigationView.getMenu().findItem(navigationSelectedItem).setChecked(true);
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if(isBackTrace)
            transaction.addToBackStack("myFragment");

        transaction.replace(R.id.fragment_container, fragment)
                .setPrimaryNavigationFragment(fragment)
                .commit();
    }

    public void setFragment(Fragment fragment){
        setFragment(fragment,false);
    }

    private boolean isNavFragment(Fragment f) {
        return f instanceof TruckListFragment ||
                f instanceof MapFragment ||
                f instanceof TruckProfileFragment;
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

    public LoginState getRememberLoginState() {
        if(SHP.Login.getUsernameOrEmail(this) == null ||
                SHP.Login.getPassword(this) == null) // no remembered login
            return new LoginState();

        // ========= There is remember login =============
        String link = (SHP.Login.isTruck(getBaseContext())) ? PHPHelper.Truck.login : PHPHelper.Customer.login;
        AndroidNetworking.post(link)
                .addBodyParameter("uid", SHP.Login.getUsernameOrEmail(this))
                .addBodyParameter("password", SHP.Login.getPassword(this))
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean isLoginSuccess = response.getString("state").equals("success");
                            if(!isLoginSuccess){ // Error: username/password not match.
                                return;
                            }
                            LoginState.CreateLogin      // setLogin state to MainActivity Truck/Customer
                                    (MainActivity.this, response, SHP.Login.isTruck(getBaseContext()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("server-response",anError.getErrorDetail());
                    }
                });
        return null;
    }
}
