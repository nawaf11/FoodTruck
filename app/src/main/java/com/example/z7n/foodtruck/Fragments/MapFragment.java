package com.example.z7n.foodtruck.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.z7n.foodtruck.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by z7n on 1/31/2018.

 */

public class MapFragment extends Fragment implements OnMapReadyCallback, LocationListener {
    private MapView mapView;
    private GoogleMap googleMap;
    private LocationManager locationManager;
    private boolean comeFromGpsActivity = false;

    @SuppressLint("MissingPermission")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View parent = inflater.inflate(R.layout.fragment_map, container, false);

        mapView = parent.findViewById(R.id.map);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);

        return parent;
    }


    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return false;
        }
        return true;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap mMap) {
        googleMap = mMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        setTruckMarker();

        mapView.onResume();

        boolean permissionsGranted = checkPermissions();
        if (permissionsGranted)
            googleMap.setMyLocationEnabled(true);

        boolean enabled = false;
        if (locationManager != null)
            enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // Check if enabled and if not send user to the GPS settings
        if (!enabled) {
            new GpsAlertDialog(getContext()).show();
            comeFromGpsActivity = true;
        } else if(permissionsGranted) { // Enabled
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
    }

    }

    private void setTruckMarker() {

        LatLng riyadh = new LatLng(24.7136, 46.6753);
        googleMap.addMarker(new MarkerOptions().position(riyadh).title("Marker in Riyadh"));
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onResume() {
        super.onResume();
        if (comeFromGpsActivity){
            comeFromGpsActivity = false;
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                googleMap.setMyLocationEnabled(true);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            }
    }

    @Override
    public void onLocationChanged(Location location) {
        CameraPosition cameraPosition = CameraPosition.builder().target(new LatLng(location.getLatitude(),
                location.getLongitude())).zoom(12).build();
        Log.d("traceGPS",location.getLatitude()+"");
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private class GpsAlertDialog extends AlertDialog implements AlertDialog.OnClickListener {

        protected GpsAlertDialog(Context context) {
            super(context);
            setTitle(R.string.gpsAlert_title);
            setMessage(getContext().getString(R.string.gpsAlert_message));
            setButton(BUTTON_POSITIVE,getContext().getString(R.string.gpsAlert_positiveButton), this);
            setButton(BUTTON_NEGATIVE,getContext().getString(R.string.gpsAlert_negativeButton), this);
        }


        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            switch (i){
                case AlertDialog.BUTTON_POSITIVE:
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                    break;
                case AlertDialog.BUTTON_NEGATIVE:
                    dismiss();
                    break;
            }
        }
    }
}
