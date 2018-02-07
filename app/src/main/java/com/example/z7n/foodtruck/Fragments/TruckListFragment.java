package com.example.z7n.foodtruck.Fragments;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.z7n.foodtruck.R;
import com.example.z7n.foodtruck.Truck;

import java.util.ArrayList;

/**
 * Created by nawaf on 2/7/2018.

 */

public class TruckListFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View parent = inflater.inflate(R.layout.truck_list_fragment,container,false);

        RecyclerView recyclerView = parent.findViewById(R.id.listView_truckList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(new TruckAdapter(getFakeList()));

        return parent;
    }

    private ArrayList<Truck> getFakeList(){
        ArrayList<Truck> arr = new ArrayList<>();
        for (int i=0; i < 20; i++){
            Truck truck = new Truck("rr","rrr");
            truck.setTruckId(1);
            truck.setTruckName("Burger HQ"+ i);
            Location location = new Location("Truck "+i);
            location.setLatitude(24.7136); location.setLongitude(46.6753);
            truck.setLocation(location);
            arr.add(truck);
        }
        return arr;
    }

    private class TruckAdapter extends RecyclerView.Adapter<TruckAdapter.TruckHolder> {
        ArrayList<Truck> truckList;

        public TruckAdapter(ArrayList<Truck> list){
            truckList = list;
            if(truckList == null)
                truckList = new ArrayList<>();
        }

        @Override
        public TruckHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.truck_list_item, parent, false);

            return new TruckHolder(view);
        }

        @Override
        public void onBindViewHolder(TruckHolder holder, int i) {
            holder.setTruckName(truckList.get(i).getTruckName());
            holder.setTruckDistance(20.1);

        }

        @Override
        public int getItemCount() {
            return truckList.size();
        }
         class TruckHolder extends RecyclerView.ViewHolder {
            private TextView truckName;
            private ImageView truckImage;
            private TextView distanceText;
              TruckHolder(View itemView) {
                 super(itemView);
                 truckName = itemView.findViewById(R.id.truckItem_truckName);
                 truckImage = itemView.findViewById(R.id.truckItem_ImageView);
                 distanceText = itemView.findViewById(R.id.truckItem_distanceTextView);
             }

             public void setTruckName(String name){
                 truckName.setText(name);
             }

             public void setTruckDistance(double dist){
                 distanceText.setText(dist+ " KM");
             }
         }
    }

}
