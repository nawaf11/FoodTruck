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


import com.example.z7n.foodtruck.Fragments.MapFragment;
import com.example.z7n.foodtruck.Fragments.TruckListFragment;
import com.example.z7n.foodtruck.R;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView mNavigationView;
    private int navigationSelectedItem;
    private DrawerLayout mDrawerLayout;

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
        }

        return false;
    }
}
