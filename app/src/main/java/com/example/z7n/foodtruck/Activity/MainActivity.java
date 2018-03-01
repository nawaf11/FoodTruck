package com.example.z7n.foodtruck.Activity;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.example.z7n.foodtruck.Customer;
import com.example.z7n.foodtruck.Fragments.LoginFragment;
import com.example.z7n.foodtruck.Fragments.MapFragment;
import com.example.z7n.foodtruck.Fragments.MapFragment2;
import com.example.z7n.foodtruck.Fragments.MenuEditorFragment;
import com.example.z7n.foodtruck.Fragments.RegisterFragment;
import com.example.z7n.foodtruck.Fragments.TruckListFragment;
import com.example.z7n.foodtruck.Fragments.TruckProfileFragment;
import com.example.z7n.foodtruck.ILocationClient;
import com.example.z7n.foodtruck.LoginState;
import com.example.z7n.foodtruck.PHPHelper;
import com.example.z7n.foodtruck.R;
import com.example.z7n.foodtruck.SHP;
import com.example.z7n.foodtruck.Truck;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView mNavigationView;
    private int navigationSelectedItem;
    private DrawerLayout mDrawerLayout;
    private LoginState loginState; // Detail of current login: isVisitor? , isTruck?, get Truck/Customer Object
    private Menu menu;
    private Location userLocation; // The current location of user (Truck/Customer/Visitor)

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
      - MenuEditorFragment

     ------- Fragment for Customer -------

    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AndroidNetworking.initialize(getApplicationContext());

        loginState = getRememberLoginState();
        //setupNavigationView();
        setupToolbar_NavigationView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(checkLocationPermission())
            new LocationGetter(this); // To keep update user location.
    }

    private void setupToolbar_NavigationView(){
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        mNavigationView = findViewById(R.id.navigationView);
        mNavigationView.setItemIconTintList(null);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle
                (this,mDrawerLayout,myToolbar,R.string.nav_open,R.string.nav_close);
        mDrawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);

        mNavigationView.setCheckedItem(R.id.navigationBarItem_truckList);
        navigationSelectedItem = R.id.navigationBarItem_truckList;

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
        navHeader.findViewById(R.id.navigationHeader_signOutCustomer)
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
                if(loginState.getTruck() == null)
                    return;

                if(isChecked) {
                    statusSwitch.setText(Truck.getStatusText(getBaseContext(),true));
                    statusSwitch.setTextColor(getResources().getColor((R.color.truckStatus_green)));
                    //loginState.getTruck().updateTruckStatusTask(MainActivity.this,true);
                }
                else {
                    statusSwitch.setText(Truck.getStatusText(getBaseContext(),false));
                    statusSwitch.setTextColor(getResources().getColor((R.color.truckStatus_red)));
                    //loginState.getTruck().updateTruckStatusTask(MainActivity.this,false);
                }
            }
        });
    }

    private boolean checkLocationPermission() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        this.menu = menu;
        hideMenuItems();
        setFragment(new TruckListFragment());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

//       if(item.getItemId() == 16908332){ // 16908332 is id of nav_item
//            mDrawerLayout = findViewById(R.id.drawer_layout);
//           if(mDrawerLayout.isDrawerOpen(GravityCompat.START))
//               mDrawerLayout.closeDrawer(GravityCompat.START);
//           else
//               mDrawerLayout.openDrawer(GravityCompat.START);
//       }

        Fragment currentFragment = getSupportFragmentManager().getPrimaryNavigationFragment();
        switch (item.getItemId()){
            case R.id.menuItem_editProfile:
                if( currentFragment instanceof TruckProfileFragment) {
                    ((TruckProfileFragment)currentFragment).editClicked();
                    break;
                }

            case R.id.menuItem_saveProfileUpdate:
                if( currentFragment instanceof TruckProfileFragment) {
                    ((TruckProfileFragment) currentFragment).saveClicked();
                    break;
                }
        }

        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mDrawerLayout.closeDrawer(GravityCompat.START);

        for(int i=0; i < getSupportFragmentManager().getBackStackEntryCount(); i++ ) {
            getSupportFragmentManager().popBackStack();
        }

       if(item.getItemId() == navigationSelectedItem)
           return true;

        navigationSelectedItem = item.getItemId();
        mNavigationView.setCheckedItem(item.getItemId());

        switch (item.getItemId()){
            case R.id.navigationBarItem_map:
                setFragment(new MapFragment2());
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
            loginChangeCustomerHeader(mLoginState.getCustomer());
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

    private void loginChangeCustomerHeader(Customer customer){
        mNavigationView.getHeaderView(0).findViewById(R.id.truckHeader_container).setVisibility(View.GONE);
        mNavigationView.getHeaderView(0).findViewById(R.id.visitorHeader_container).setVisibility(View.GONE);
        mNavigationView.getHeaderView(0).findViewById(R.id.customerHeader_container).setVisibility(View.VISIBLE);

        if (customer == null)
            return;

        TextView customerUsername = mNavigationView.getHeaderView(0).findViewById(R.id.navigation_header_customerUsername);

        customerUsername.setText(customer.getUserName());
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
                            setLoginState(LoginState.CreateLogin
                                    (response, SHP.Login.isTruck(getBaseContext())));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("server-response",anError.getErrorDetail());
                    }
                });

        return new LoginState();
    }

    public void setToolbarTitle(int titleId) {
        if(getSupportActionBar() != null)
            getSupportActionBar().setTitle(titleId);
    }

    public void hideMenuItems(){
        if(menu == null)
            return;

        for(int i=0; i < menu.size();i++)
            menu.getItem(i).setVisible(false);
    }

    public Menu getMenu(){return menu;}

    public NavigationView getNavigationView(){return mNavigationView;}

    public Location getUserLocation(){
        return userLocation;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Uri resultUri = result.getUri();
                Log.d("uri-result", resultUri.getPath());
                Fragment currentFragment = getSupportFragmentManager().getPrimaryNavigationFragment();
                if(currentFragment instanceof MenuEditorFragment){
                    ((MenuEditorFragment)currentFragment).productImageResult(resultUri);
            }
        }
    }

    private class LocationGetter extends LocationCallback implements GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener {
//        private GoogleApiClient mGoogleApiClient;
        private FusedLocationProviderClient fusedLocationClient;
        private LocationRequest locationRequest;

        @SuppressLint("MissingPermission")
        private LocationGetter(Context context){
//            mGoogleApiClient = new GoogleApiClient.Builder(context)
//                    .addConnectionCallbacks(this)
//                    .addOnConnectionFailedListener(this)
//                    .addApi(LocationServices.API).build();

            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
            locationRequest = new LocationRequest();
            locationRequest.setInterval(1000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            fusedLocationClient.requestLocationUpdates(locationRequest,this,null);
        }

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            Log.d("fusedgps","onConnected()");
        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.d("fusedgps","onConnectionSuspended");
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Log.d("fusedgps","onConnectionFailed");
        }

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Log.d("fusedgps","onLocationResult: "+ locationResult.getLastLocation().getLatitude()
                    +" , "+locationResult.getLastLocation().getLongitude());
            Log.d("fusedgps","onLocationResult: "+ locationResult.getLastLocation().getTime());

            super.onLocationResult(locationResult);
            userLocation = locationResult.getLastLocation();

            Fragment currentFragment = getSupportFragmentManager().getPrimaryNavigationFragment();
            if( currentFragment instanceof ILocationClient) // If current fragment want to receive location updates.
                ((ILocationClient)currentFragment).onLocationUpdated(locationResult);
        }
    }
}
