package com.example.z7n.foodtruck.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.z7n.foodtruck.Database.DbHelper;
import com.example.z7n.foodtruck.PHPHelper;
import com.example.z7n.foodtruck.R;
import com.example.z7n.foodtruck.Truck;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by z7n on 3/30/2018.
 */

public class FavoriteTrucksFragment extends Fragment {

//        http://foodtruck.mywebcommunity.org/php/getFavoriteTrucks.php?trcukIdList=63,60

    private TruckListFragment.TruckAdapter truckAdapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View parentView = inflater.inflate(R.layout.truck_list_fragment, container, false);

        initViews(parentView);
        startTruckListTask();

        return parentView;
    }

    private void initViews(View parentView) {
        recyclerView = parentView.findViewById(R.id.recyclerView);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        progressBar = parentView.findViewById(R.id.progressBar);
        truckAdapter = (new TruckListFragment.TruckAdapter(getContext(), new ArrayList<Truck>()));
        recyclerView.setAdapter(truckAdapter);

    }

    private void startTruckListTask(){

        long [] truckIds = new DbHelper(getContext()).getFavoriteTrucks();
        if(truckIds.length == 0){
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getContext(),R.string.noFavoriteTrucks,Toast.LENGTH_SHORT).show();
            return;
        }

        String listText = "";
        for ( long id: truckIds) {
            listText += id;
            listText+=",";
        }
        listText = listText.substring(0, listText.length()-1);

        AndroidNetworking.get(PHPHelper.Truck.get_favorites)
                .addQueryParameter("trcukIdList", listText)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("truckList-response",response.toString());
                try {
                    if(response.getString("state").equals("success")){
                        updateListFromJson(response);
                        progressBar.setVisibility(View.GONE);
                    }
                    else
                        Toast.makeText(getContext(),R.string.unknownError,Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(),R.string.unknownError,Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(ANError anError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(),R.string.serverNotResponse,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateListFromJson(JSONObject response) throws JSONException{
        ArrayList<Truck> truckList = new ArrayList<>();

        JSONArray jsonArray = response.getJSONArray("data");

        for(int i=0; i < jsonArray.length(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Truck truck = new Truck();
            truck.setTruckId(jsonObject.getLong("TruckID"));
            truck.setTruckName(jsonObject.getString("name"));
            boolean state_flag = jsonObject.getString("status").equals("true");
            truck.setStatus(state_flag);
            String lat = jsonObject.getString("Latitude");
            String lng = jsonObject.getString("Longitude");
            double latD = -1;
            double lngD = -1;

            try {
                latD = Double.parseDouble(lat);
                lngD = Double.parseDouble(lng);
            } catch (NumberFormatException e){e.printStackTrace();}

            if(latD != -1 && lngD != -1) {
                LatLng latLng = new LatLng(latD,lngD);
                truck.setLatLng(latLng);
            }

            truckList.add(truck);
        }

        if(jsonArray.length() == 0)
            Toast.makeText(getContext(), R.string.noFavoriteTrucks,Toast.LENGTH_SHORT).show();

        truckAdapter.truckList.addAll(truckList);
        truckAdapter.notifyDataSetChanged();
    }

}












