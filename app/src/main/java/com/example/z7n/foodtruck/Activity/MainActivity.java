package com.example.z7n.foodtruck.Activity;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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
import com.example.z7n.foodtruck.LoginState;
import com.example.z7n.foodtruck.PHPHelper;
import com.example.z7n.foodtruck.R;
import com.example.z7n.foodtruck.SHP;
import com.example.z7n.foodtruck.Truck;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnLocationUpdatedListener {

    private NavigationView mNavigationView;
    private int navigationSelectedItem;
    private DrawerLayout mDrawerLayout;
    private LoginState loginState; // Detail of current login: isVisitor? , isTruck?, get Truck/Customer Object
    private Menu menu;
    //private Location userLocation; // The current location of user (Truck/Customer/Visitor)

/** TODO:
  ================== Fragments: ====================
   - MapFragment
   - TruckListFragment
   - CustomerProfileFragment
   - TruckPageFragment
   - NotificationsFragment

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

        //setupNavigationView();
        setupToolbar_NavigationView();

        loginState = getRememberLoginState();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(checkLocationPermission())
            SmartLocation.with(this).location().start(this); // To keep update user location.
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

        statusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("truckStatus","onCheckedChanged" + isChecked );
                Log.d("truckStatus","statusSwitch.getTag() == null" + (statusSwitch.getTag() == null)+", isChecked: "+ isChecked );

                if(loginState.getTruck() == null)
                    return;

                Log.d("truckStatus","onCheckedChanged after(getTruck() != null)" + isChecked );

                if(isChecked) {
                    statusSwitch.setText(Truck.getStatusText(getBaseContext(),true));
                    statusSwitch.setTextColor(getResources().getColor((R.color.truckStatus_green)));
                }
                else {
                    statusSwitch.setText(Truck.getStatusText(getBaseContext(),false));
                    statusSwitch.setTextColor(getResources().getColor((R.color.truckStatus_red)));
                }

                // because if statusSwitch.getTag() != null it's mean just changeView of switch.

                if(isChecked && statusSwitch.getTag() == null) {
                    statusSwitch.setEnabled(false);
                    new MyTimePickerDialog(MainActivity.this, statusSwitch).show();
                }
                if(!isChecked && statusSwitch.getTag() == null) {
                    statusSwitch.setEnabled(false);
                    closeTruckStatus(statusSwitch);
                }
            }
        });

    }

    private void closeTruckStatus(final Switch statusSwitch){
        if(loginState.getTruck() == null) {
            statusSwitch.setEnabled(true);
            return;
        }

        AndroidNetworking.post(PHPHelper.Truck.update_status)
                .addBodyParameter("TruckID", loginState.getTruck().getTruckId()+"")
                .addBodyParameter("status", "false")
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("closeTruckStatus", response.toString());
                statusSwitch.setEnabled(true);
                try {
                    if(response.getString("state").equals("success"))
                        Toast.makeText(getBaseContext(), R.string.truckStatus_update_success,Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getBaseContext(), R.string.unknownError,Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getBaseContext(), R.string.unknownError,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(ANError anError) {
                statusSwitch.setEnabled(true);
                Log.d("closeTruckStatus", anError.getResponse().toString());
                Toast.makeText(getBaseContext(), R.string.serverNotResponse,Toast.LENGTH_SHORT).show();
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
            case R.id.menuItem_backButton:
                onBackPressed();
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

        transaction.replace(R.id.fragment_container, fragment).setPrimaryNavigationFragment(fragment).commit();

        if(getSupportFragmentManager().getBackStackEntryCount() > 0)
            menu.findItem(R.id.menuItem_backButton).setVisible(true);

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
        final ImageView truckImage = mNavigationView.getHeaderView(0).findViewById(R.id.navigationHeader_truckImage);

        truckName.setText(truck.getTruckName());
        truckUsername.setText(truck.getUserName());

        truckStatus.setTag("freeze");
        Log.d("truckStatus","truck.isStatusOpen(): "+truck.isStatusOpen() );
        if(truck.isStatusOpen()){ // Default color is green.
            truckStatus.setChecked(true);
            truckStatus.setText(Truck.getStatusText(getBaseContext(),true));
            truckStatus.setTextColor(getResources().getColor((R.color.truckStatus_green)));

        } else {
            truckStatus.setChecked(false);
            truckStatus.setText(Truck.getStatusText(getBaseContext(),false));
            truckStatus.setTextColor(getResources().getColor((R.color.truckStatus_red)));
        }

        truckStatus.setTag(null);

        Picasso.with(this).load(PHPHelper.Truck.get_truckImage + truck.getTruckId())
                .memoryPolicy(MemoryPolicy.NO_STORE, MemoryPolicy.NO_CACHE)
                .fit().into(truckImage, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                Picasso.with(MainActivity.this).load(R.drawable.foodtruck).fit().into(truckImage);
            }
        });
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

//    public Location getUserLocation(){
//        return userLocation;
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Uri resultUri = result.getUri();
                Log.d("uri-result", resultUri.getPath());
                Fragment currentFragment = getSupportFragmentManager().getPrimaryNavigationFragment();
                if(currentFragment instanceof MenuEditorFragment)
                    ((MenuEditorFragment)currentFragment).productImageResult(resultUri);
                else if(currentFragment instanceof TruckProfileFragment) {
                    ImageView headerImageView = mNavigationView.getHeaderView(0).findViewById(R.id.navigationHeader_truckImage);
                    ((TruckProfileFragment) currentFragment).startImageUpdateTask(resultUri, headerImageView);
                }
                else if(currentFragment instanceof RegisterFragment)
                    ((RegisterFragment) currentFragment).truckImageReceiver(resultUri);

        }
    }

    @Override
    public void onLocationUpdated(Location location) {
        Log.d("locationUpdate","locationUpdate: "+location.getLongitude()+" , "+ location.getLatitude());
        //userLocation = location;
    }

    private class MyTimePickerDialog extends Dialog implements TimePicker.OnTimeChangedListener {
        private ProgressBar progressBar;
        private TimePicker timePicker;
        private TextView timeExplain;
        private Button ok_btn,cancel_btn;
        private int selectedHour;
        private int selectedMinute;
        private Switch aSwitch;
        private CompoundButton.OnCheckedChangeListener listener;

        protected MyTimePickerDialog(Context context, Switch statusSwitch) {
            super(context);
            aSwitch = statusSwitch;
            View view = LayoutInflater.from(context).inflate(R.layout.update_truck_status_dialog,null,false);
            setContentView(view);
            progressBar = findViewById(R.id.progressBar);
            timeExplain = findViewById(R.id.timeExplain);
            ok_btn = findViewById(R.id.ok);
            cancel_btn = findViewById(R.id.cancel);
            progressBar.setVisibility(View.INVISIBLE);
            timePicker = findViewById(R.id.timepicker);
            timePicker.setIs24HourView(true);
            timePicker.setOnTimeChangedListener(this);


            cancel_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkedSwitchView(false);
                    aSwitch.setEnabled(true);
                    dismiss();
                }
            });

            ok_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(! SmartLocation.with(getContext()).location().state().isGpsAvailable()){
                        new MapFragment2.GpsAlertDialog(getContext()).show();
                        return;
                    }

                    if(!isValidTime(selectedHour,selectedMinute)){
                        Toast.makeText(getContext(),
                                R.string.theInterval_mustNotBe_moreThan_12Hours, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    submitStatus();
                }
            });

            int current_hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            int current_min = Calendar.getInstance().get(Calendar.MINUTE);

            selectedHour = current_hour;
            selectedMinute = current_min;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                timePicker.setHour(current_hour);
                timePicker.setMinute(current_min);
            } else {
                timePicker.setCurrentHour(current_hour);
                timePicker.setCurrentMinute(current_min);
            }

        }

        private void checkedSwitchView(boolean flag){
            aSwitch.setTag("freeze");
            aSwitch.setChecked(flag);
            aSwitch.setTag(null);
        }

        private void submitStatus() {
            if(loginState.getTruck() == null)
                return;

            progressBar.setVisibility(View.VISIBLE);

            SmartLocation.with(getContext()).location().oneFix().start(new OnLocationUpdatedListener() {
                @Override
                public void onLocationUpdated(Location location) {
                    String openUntilTime = getSelectedTimeFormat();
                    AndroidNetworking.post(PHPHelper.Truck.update_status)
                            .addBodyParameter("TruckID", loginState.getTruck().getTruckId()+"")
                            .addBodyParameter("status", "true")
                            .addBodyParameter("openUntil", openUntilTime)
                            .addBodyParameter("lantitude", String.valueOf(location.getLatitude()))
                            .addBodyParameter("longitude", String.valueOf(location.getLongitude()))
                            .build().getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            dismiss();
                            progressBar.setVisibility(View.INVISIBLE);

                            Log.d("updateStatus-response", response.toString());
                            try {
                                if(response.getString("state").equals("success")) {
                                    checkedSwitchView(true);
                                    aSwitch.setEnabled(true);
                                    Toast.makeText(getContext(), R.string.truckStatus_update_success, Toast.LENGTH_LONG).show();
                                }
                                else {
                                    aSwitch.setEnabled(true);
                                    checkedSwitchView(false);
                                    Toast.makeText(getContext(), R.string.unknownError, Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                aSwitch.setEnabled(true);
                                checkedSwitchView(false);
                                Toast.makeText(getContext(), R.string.unknownError,Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            dismiss();
                            progressBar.setVisibility(View.INVISIBLE);
                            aSwitch.setEnabled(true);
                            checkedSwitchView(false);
                            Toast.makeText(getContext(), R.string.serverNotResponse,Toast.LENGTH_LONG).show();
                            Log.d("updateStatus-response", "anError: "+anError.toString());
                        }
                    });
                }
            });

        }

        private String getSelectedTimeFormat(){
            // 2018-02-21 17:59:43
            String hour = (selectedHour > 9) ? selectedHour +"" : "0"+selectedHour; // 9 to 09
            String min = (selectedMinute > 9) ? selectedMinute +"" : "0"+selectedMinute;

            String utcHour="";
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH", Locale.getDefault());
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

                Date date = new SimpleDateFormat("HH", Locale.getDefault()).parse(hour);

                utcHour = simpleDateFormat.format(date);
                Log.d("selectedTimeConverted",simpleDateFormat.format(date));
                Log.d("selectedTimeConverted","selectedTimeConverted");
            } catch (ParseException e) {e.printStackTrace();}

            utcHour = arabicNumToEnglish(utcHour);


            int yearPart = Calendar.getInstance().get(Calendar.YEAR);
            int monthPart = Calendar.getInstance().get(Calendar.MONTH) +1;
            int dayPart = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

            if(isTomowrowDay(utcHour)){
                GregorianCalendar gc = new GregorianCalendar();
                gc.add(Calendar.DATE, 1);
                yearPart = gc.get(Calendar.YEAR);
                monthPart = gc.get(Calendar.MONTH) + 1;
                dayPart = gc.get(Calendar.DAY_OF_MONTH);
            }

            Log.d("finalDayPart", dayPart+"");

            return yearPart +"-"
                    + monthPart + "-" // since month start from 0!
                    + dayPart +" "
                    + utcHour + ":"
                    + min + ":"
                    + "00" // miliseconds
                    ;
        }

        private boolean isTomowrowDay(String utcHour){
            int utcH = Integer.parseInt(utcHour);

            ArrayList<Integer> range = new ArrayList<>();

            int index =  Calendar.getInstance(TimeZone.getTimeZone("UTC")).get(Calendar.HOUR_OF_DAY);
            Log.d("isTomowrowDay", "current UTC: "+index +"v, utcSelectedHour:" + utcH);
            while ( index != utcH){
                index = (index+1) % 24;
                range.add(index);
            }

            return range.contains(0);
        }

        private String arabicNumToEnglish(String original){
                return original
                        .replaceAll("٠", "0")
                        .replaceAll("١", "1")
                        .replaceAll("٢", "2")
                        .replaceAll("٣", "3")
                        .replaceAll("٤", "4")
                        .replaceAll("٥", "5")
                        .replaceAll("٦", "6")
                        .replaceAll("٧", "7")
                        .replaceAll("٨", "8")
                        .replaceAll("٩", "9");

        }

        @Override
        public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
            selectedHour = hourOfDay;
            selectedMinute = minute;
            Log.d("dsfsfsrgegrga",hourOfDay + " , " + minute);
            Log.d("getSelectedTimeFormat", getSelectedTimeFormat());

            if(! isValidTime(hourOfDay,minute)){ // wrong
                timeExplain.setTextColor(getColor(R.color.truckStatus_red));
                timeExplain.setText(R.string.theInterval_mustNotBe_moreThan_12Hours);
                return;
            }

            timeExplain.setTextColor(getColor(R.color.colorTextBlack));

            int clockHour = hourOfDay;
            if(hourOfDay > 12)
                clockHour = hourOfDay - 12;
            if(hourOfDay == 0)
                clockHour = 12;

            String gk = (minute > 9 ) ? clockHour + ":" + minute : clockHour + ":0" + minute;
            String res = getString(R.string.truckWillBe_offAt);
            res+= " "+gk+" ";

            String morning = (hourOfDay > 12) ? getString(R.string.night) : getString(R.string.morning);
            if(hourOfDay == 0)
                morning = getString(R.string.midnight);
            else if (hourOfDay == 12) {
                morning = getString(R.string.noon);
            }

            res+=morning;

            String afterPart = "(بعد $$$ ساعة/ساعات)";
            afterPart = afterPart.replace("$$$", getAfterHourPart(hourOfDay) + "");

            res+= "\n" + afterPart;

            timeExplain.setText(res);

        }

        private boolean isValidTime(int hour, int minute){
            int current_hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            int current_min = Calendar.getInstance().get(Calendar.MINUTE);

            if(getAfterHourPart(hour) == 12 && minute > current_min)
                return false;

            int [] hourCiclye = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23};

            ArrayList<Integer> validRange = new ArrayList<>();

            int index = current_hour;
            for(int i = 0; i < 12;i++) {
                index = (index+1) % 24;
                int validNum = hourCiclye[index];
                validRange.add(validNum);
                Log.d("validHour",validNum+"");
            }

            return validRange.contains(hour);
        }

        private int getAfterHourPart(int hour){
            int current_hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

            Log.d("getAfterHourPart()","currentHour: "+current_hour + " , hour: "+hour);

            int stepCount=0;
            int value = current_hour;
           while(true) {
               value = (value+1) % 24;
               stepCount++;

               if(value == hour)
                   break;
            }

            return stepCount;
        }
    }

//    private class LocationGetter extends LocationCallback implements GoogleApiClient.ConnectionCallbacks,
//            GoogleApiClient.OnConnectionFailedListener {
////        private GoogleApiClient mGoogleApiClient;
//        private FusedLocationProviderClient fusedLocationClient;
//        private LocationRequest locationRequest;
//
//        @SuppressLint("MissingPermission")
//        private LocationGetter(Context context){
////            mGoogleApiClient = new GoogleApiClient.Builder(context)
////                    .addConnectionCallbacks(this)
////                    .addOnConnectionFailedListener(this)
////                    .addApi(LocationServices.API).build();
//
//            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
//            locationRequest = new LocationRequest();
//            locationRequest.setInterval(1000);
//            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//            fusedLocationClient.requestLocationUpdates(locationRequest,this,null);
//        }
//
//        @Override
//        public void onConnected(@Nullable Bundle bundle) {
//            Log.d("fusedgps","onConnected()");
//        }
//
//        @Override
//        public void onConnectionSuspended(int i) {
//            Log.d("fusedgps","onConnectionSuspended");
//        }
//
//        @Override
//        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//            Log.d("fusedgps","onConnectionFailed");
//        }
//
//        @Override
//        public void onLocationResult(LocationResult locationResult) {
//            Log.d("fusedgps","onLocationResult: "+ locationResult.getLastLocation().getLatitude()
//                    +" , "+locationResult.getLastLocation().getLongitude());
//            Log.d("fusedgps","onLocationResult: "+ locationResult.getLastLocation().getTime());
//
//            super.onLocationResult(locationResult);
//            userLocation = locationResult.getLastLocation();
//
//            Fragment currentFragment = getSupportFragmentManager().getPrimaryNavigationFragment();
//            if( currentFragment instanceof ILocationClient) // If current fragment want to receive location updates.
//                ((ILocationClient)currentFragment).onLocationUpdated(locationResult);
//        }
//    }
}
