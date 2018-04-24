package com.example.z7n.foodtruck.Fragments;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.z7n.foodtruck.Activity.MainActivity;
import com.example.z7n.foodtruck.PHPHelper;
import com.example.z7n.foodtruck.R;
import com.example.z7n.foodtruck.Truck;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

import io.nlopez.smartlocation.SmartLocation;

/**
 * Created by nawaf on 2/7/2018.

 */

public class TruckListFragment extends Fragment {
    private TruckAdapter truckAdapter;
    private ProgressBar progressBar;
    private int page;
    private final int PAGE_LIMIT=20;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // ========== MainActivity needs part ====================
        if(getActivity() != null) {
            MainActivity mc = (MainActivity) getActivity();
            mc.setToolbarTitle(R.string.toolbar_truckListTitle);
            mc.hideMenuItems();
        }
        // ========== MainActivity needs part ====================

        page = 1;
        View parent = inflater.inflate(R.layout.truck_list_fragment,container,false);
        progressBar = parent.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        setupRecyclerView(parent); // setup List

        progressBar.setVisibility(View.VISIBLE);
        startTruckListTask();


        return parent;
    }

    private void setupRecyclerView(View parent){

        RecyclerView recyclerView = parent.findViewById(R.id.recyclerView);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        truckAdapter = (new TruckAdapter(getContext(), new ArrayList<Truck>()));
        recyclerView.setAdapter(truckAdapter);

        EndlessRecyclerViewScrollListener scrollListener =
                new EndlessRecyclerViewScrollListener(mLayoutManager){

                    @Override
                    public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                        startTruckListTask();
                    }
                };
        recyclerView.addOnScrollListener(scrollListener);
    }

    private ArrayList<Truck> getFakeList(){
        ArrayList<Truck> arr = new ArrayList<>();
        for (int i=0; i < 20 ; i++){
            Truck truck = new Truck();
            truck.setUserName("burgerhq"+i);
            truck.setTruckId(1);
            truck.setTruckName("Burger HQ"+ i);
            Location location = new Location("Truck "+i);
            location.setLatitude(24.7136); location.setLongitude(46.6753);
            truck.setLocation(location);
            arr.add(truck);
        }
        return arr;
    }

    private void startTruckListTask(){

        AndroidNetworking.get(PHPHelper.Truck.get_truck_pageList)
                .addQueryParameter("page",String.valueOf(page))
                .addQueryParameter("limit",String.valueOf(PAGE_LIMIT))
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("truckList-response",response.toString());
                try {
                    if(response.getString("state").equals("success")){
                        updateListFromJson(response);
                        page++;
                    }
                    else if(page == 1)
                        Toast.makeText(getContext(),R.string.unknownError,Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                    if(page == 1)
                        Toast.makeText(getContext(),R.string.unknownError,Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onError(ANError anError) {
                progressBar.setVisibility(View.INVISIBLE);
                if(page == 1)
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

        int position_start = truckAdapter.truckList.size();
        truckAdapter.truckList.addAll(truckList);
        truckAdapter.notifyItemRangeInserted(position_start,truckList.size());
        progressBar.setVisibility(View.INVISIBLE);
    }

    static class TruckAdapter extends RecyclerView.Adapter<TruckAdapter.TruckHolder> {
        ArrayList<Truck> truckList;
        private Context context;

        public TruckAdapter(Context context, ArrayList<Truck> list){
            truckList = list;
            this.context = context;
            if(truckList == null)
                truckList = new ArrayList<>();
        }

        public Context getContext(){return context;}

        @Override
        public TruckHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.truck_list_item, parent, false);

            return new TruckHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final TruckHolder holder, final int i) {
            holder.setTruckName(truckList.get(i).getTruckName());
            holder.distanceText.setText("");
            if(truckList.get(holder.getAdapterPosition()).isStatusOpen())
                dealWithDistance(holder);
            Picasso.with(getContext()).load(PHPHelper.Truck.get_truckImage + truckList.get(i).getTruckId())
                    .memoryPolicy(MemoryPolicy.NO_STORE, MemoryPolicy.NO_CACHE)
                    .fit().into(holder.truckImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(getContext()).load(R.drawable.foodtruck).fit().into(holder.truckImage);
                }
            });


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity ma = ((MainActivity)getContext());
                    if(ma != null) {
                        TruckPageFragment fragment = new TruckPageFragment();
                        Bundle bundle = new Bundle(); bundle.putLong("truck_id",truckList.get(i).getTruckId());
                        bundle.putBoolean("fromList",true);
                        fragment.setArguments(bundle);
                        ma.setFragment(fragment, true);
                    }
                }
            });
        }

        private void dealWithDistance(TruckHolder holder) {
            Truck truck = truckList.get(holder.getAdapterPosition());
            if(truck.getLatLng() == null) {
                holder.distanceText.setText(" ");
                return;
            }

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

            if(Double.parseDouble(distanceKM) >= 0.1)
                holder.distanceText.setText(distanceKM + " " + getContext().getString(R.string.KM));
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

    private abstract class EndlessRecyclerViewScrollListener extends RecyclerView.OnScrollListener {
        // The minimum amount of items to have below your current scroll position
        // before loading more.
        private int visibleThreshold = 5;
        // The current offset index of data you have loaded
        private int currentPage = 0;
        // The total number of items in the dataset after the last load
        private int previousTotalItemCount = 0;
        // True if we are still waiting for the last set of data to load.
        private boolean loading = true;
        // Sets the starting page index
        private int startingPageIndex = 0;

        RecyclerView.LayoutManager mLayoutManager;

        public EndlessRecyclerViewScrollListener(LinearLayoutManager layoutManager) {
            this.mLayoutManager = layoutManager;
        }

        public EndlessRecyclerViewScrollListener(GridLayoutManager layoutManager) {
            this.mLayoutManager = layoutManager;
            visibleThreshold = visibleThreshold * layoutManager.getSpanCount();
        }

        public EndlessRecyclerViewScrollListener(StaggeredGridLayoutManager layoutManager) {
            this.mLayoutManager = layoutManager;
            visibleThreshold = visibleThreshold * layoutManager.getSpanCount();
        }

        public int getLastVisibleItem(int[] lastVisibleItemPositions) {
            int maxSize = 0;
            for (int i = 0; i < lastVisibleItemPositions.length; i++) {
                if (i == 0) {
                    maxSize = lastVisibleItemPositions[i];
                }
                else if (lastVisibleItemPositions[i] > maxSize) {
                    maxSize = lastVisibleItemPositions[i];
                }
            }
            return maxSize;
        }

        // This happens many times a second during a scroll, so be wary of the code you place here.
        // We are given a few useful parameters to help us work out if we need to load some more data,
        // but first we check if we are waiting for the previous load to finish.
        @Override
        public void onScrolled(RecyclerView view, int dx, int dy) {
            int lastVisibleItemPosition = 0;
            int totalItemCount = mLayoutManager.getItemCount();

            if (mLayoutManager instanceof StaggeredGridLayoutManager) {
                int[] lastVisibleItemPositions = ((StaggeredGridLayoutManager) mLayoutManager).findLastVisibleItemPositions(null);
                // get maximum element within the list
                lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions);
            } else if (mLayoutManager instanceof GridLayoutManager) {
                lastVisibleItemPosition = ((GridLayoutManager) mLayoutManager).findLastVisibleItemPosition();
            } else if (mLayoutManager instanceof LinearLayoutManager) {
                lastVisibleItemPosition = ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();
            }

            // If the total item count is zero and the previous isn't, assume the
            // list is invalidated and should be reset back to initial state
            if (totalItemCount < previousTotalItemCount) {
                this.currentPage = this.startingPageIndex;
                this.previousTotalItemCount = totalItemCount;
                if (totalItemCount == 0) {
                    this.loading = true;
                }
            }
            // If it’s still loading, we check to see if the dataset count has
            // changed, if so we conclude it has finished loading and update the current page
            // number and total item count.
            if (loading && (totalItemCount > previousTotalItemCount)) {
                loading = false;
                previousTotalItemCount = totalItemCount;
            }

            // If it isn’t currently loading, we check to see if we have breached
            // the visibleThreshold and need to reload more data.
            // If we do need to reload some more data, we execute onLoadMore to fetch the data.
            // threshold should reflect how many total columns there are too
            if (!loading && (lastVisibleItemPosition + visibleThreshold) > totalItemCount) {
                currentPage++;
                onLoadMore(currentPage, totalItemCount, view);
                loading = true;
            }
        }

        // Call this method whenever performing new searches
        public void resetState() {
            this.currentPage = this.startingPageIndex;
            this.previousTotalItemCount = 0;
            this.loading = true;
        }

        // Defines the process for actually loading more data based on page
        public abstract void onLoadMore(int page, int totalItemsCount, RecyclerView view);

    }
}
