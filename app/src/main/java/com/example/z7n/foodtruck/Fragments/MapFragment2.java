package com.example.z7n.foodtruck.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
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
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.z7n.foodtruck.Activity.MainActivity;
import com.example.z7n.foodtruck.ILocationClient;
import com.example.z7n.foodtruck.PHPHelper;
import com.example.z7n.foodtruck.R;
import com.example.z7n.foodtruck.Truck;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by z7n on 1/31/2018.

 */

public class MapFragment2 extends Fragment implements OnMapReadyCallback, ILocationClient, GoogleMap.OnInfoWindowClickListener, GoogleMap.InfoWindowAdapter {
    private MapView mapView;
    private GoogleMap googleMap;
    private LocationManager locationManager;
    private boolean comeFromGpsActivity;
    private ArrayList<Truck> truckList;
    private Target markerBitmapTarget;

    @SuppressLint("MissingPermission")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // ========== MainActivity needs part ====================
        if(getActivity() != null) {
            MainActivity mc = (MainActivity) getActivity();
            mc.setToolbarTitle(R.string.toolbar_mapTitle);
            mc.hideMenuItems();
        }
        // ========== MainActivity needs part ====================

        View parent = inflater.inflate(R.layout.fragment_map, container, false);

        mapView = parent.findViewById(R.id.map);
        truckList = new ArrayList<>();

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);


        return parent;
    }

    private void startOpenTrucksTask(){
        AndroidNetworking.get(PHPHelper.Truck.get_openTrucks)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getString("state").equals("success")){
                        responseOpenTruck_success(response);
                    }
                    else
                        Toast.makeText(getContext(),R.string.unknownError,Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(),R.string.unknownError,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(ANError anError) {
                Toast.makeText(getContext(),R.string.serverNotResponse,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void responseOpenTruck_success(JSONObject response) throws JSONException {
        JSONArray jsonArray = response.getJSONArray("data");
        for(int i=0; i < jsonArray.length(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Truck truck = new Truck();
            truck.setTruckId(jsonObject.getLong("TruckID"));
            truck.setUserName(jsonObject.getString("username"));
            truck.setTruckName(jsonObject.getString("name"));
            boolean isAcceptOrder = jsonObject.getString("canprepare")!=null &&
                    jsonObject.getString("canprepare").toLowerCase().equals("true");
            truck.setAcceptOrder(isAcceptOrder);
            truck.setRate(jsonObject.getDouble("rate"));
            truck.setDescription(jsonObject.getString("description"));
            truck.setOpenUntil(jsonObject.getString("openUntil"));
            LatLng latLng = new LatLng(jsonObject.getDouble("Latitude"), jsonObject.getDouble("Longitude"));
            truck.setLatLng(latLng);
            truckList.add(truck);

            Log.d("mapItem","1:"+ truck.getTruckName());
            Log.d("mapItem","1:"+truck.getDescription());
        }

        setTruckMarker();

    }

    private boolean checkPermissions() {
        if(getActivity() == null)
            return false;

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
        googleMap.setOnInfoWindowClickListener(this);
        googleMap.setInfoWindowAdapter(this);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        startOpenTrucksTask();

        mapView.onResume();

        boolean permissionsGranted = checkPermissions();
        if (permissionsGranted)
            googleMap.setMyLocationEnabled(true);

        boolean enabled = false;
        if (locationManager != null)
            enabled = SmartLocation.with(getContext()).location().state().isGpsAvailable();

        // Check if enabled and if not send user to the GPS settings
        if (!enabled) {
            new GpsAlertDialog(getContext()).show();
            comeFromGpsActivity = true;
        }
        else
            moveCameraToUserLocation();

    }

    private void setTruckMarker() {

        markerBitmapTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bitmap);

                for (Truck truck : truckList) {
                    final MarkerOptions markerOpt = new MarkerOptions().position(truck.getLatLng()).title(truck.getTruckName())
                            .icon(icon);
                    Marker m = googleMap.addMarker(markerOpt);
                    m.setTag(truck);
                }
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        int w = (int) getResources().getDimension(R.dimen.iconMarkerSize);
        int h = (int) getResources().getDimension(R.dimen.iconMarkerSize);
        Picasso.with(getContext()).load(R.drawable.foodtruck2).resize(w, h).into(markerBitmapTarget);
    }


    public void onResume() {
        super.onResume();

        if (comeFromGpsActivity && getActivity() != null && getActivity() instanceof MainActivity) {
            comeFromGpsActivity = false;
            moveCameraToUserLocation();;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            if (getContext() != null &&
                    ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                googleMap.setMyLocationEnabled(true);
                moveCameraToUserLocation();
            }
    }

    @Override
    public void onLocationUpdated(LocationResult locationResult) {
        if(googleMap == null)
            return;

       // moveMapCameraTo(locationResult.getLastLocation());
    }

    private void moveCameraToUserLocation(){
        SmartLocation.with(getContext()).location().oneFix().start(new OnLocationUpdatedListener() {
            @Override
            public void onLocationUpdated(Location location) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                CameraPosition cameraPosition = CameraPosition.builder().target(latLng).zoom(12).build();
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });


    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        Truck truck = (Truck) marker.getTag();
        if(truck == null)
            return null;


        View view = LayoutInflater.from(getContext()).inflate(R.layout.truck_map_item,null,false);
        TextView truckName = view.findViewById(R.id.truckItem_truckName);
        TextView truckDesc = view.findViewById(R.id.truckItem_description);
        TextView distanceTextView = view.findViewById(R.id.truckItem_distance);

        truckName.setText(truck.getTruckName());
        truckDesc.setText(truck.getDescription());
        float distance = -1;
        Location lastLocation = SmartLocation.with(getContext()).location().oneFix().getLastLocation();
        Location userLocation = new Location(LocationManager.GPS_PROVIDER);
        userLocation.setLatitude(truck.getLatLng().latitude);
        userLocation.setLongitude(truck.getLatLng().longitude);

        if(lastLocation != null)
            distance = lastLocation.distanceTo(userLocation);

        Log.d("distance","1: "+distance);
        distance = distance / 1000;
        Log.d("distance","2: "+distance);
        DecimalFormat precision = (DecimalFormat) DecimalFormat.getNumberInstance(Locale.US);
        precision.applyPattern("0.0");
        String distanceKM = precision.format(distance);
        Log.d("distance","3: "+distance);


        if(!distanceKM.equals("0.0"))
            distanceTextView.setText(distanceKM + " " + getString(R.string.KM));

        Log.d("mapItem","2:"+ truck.getTruckName());
        Log.d("mapItem","2:"+truck.getDescription());

        return view;
    }

    public static class GpsAlertDialog extends AlertDialog implements AlertDialog.OnClickListener {

        public GpsAlertDialog(Context context) {
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
                    getContext().startActivity(intent);
                    break;
                case AlertDialog.BUTTON_NEGATIVE:
                    dismiss();
                    break;
            }
        }
    }
}
