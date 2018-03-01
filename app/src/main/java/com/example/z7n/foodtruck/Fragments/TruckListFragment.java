package com.example.z7n.foodtruck.Fragments;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.z7n.foodtruck.Activity.MainActivity;
import com.example.z7n.foodtruck.R;
import com.example.z7n.foodtruck.Truck;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by nawaf on 2/7/2018.

 */

public class TruckListFragment extends Fragment {
    private TruckAdapter truckAdapter;

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

        View parent = inflater.inflate(R.layout.truck_list_fragment,container,false);

        setupRecyclerView(parent); // setup List


        return parent;
    }

    private void setupRecyclerView(View parent){

        RecyclerView recyclerView = parent.findViewById(R.id.listView_truckList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        truckAdapter = (new TruckAdapter(getFakeList()));
        recyclerView.setAdapter(truckAdapter);

        EndlessRecyclerViewScrollListener scrollListener =
                new EndlessRecyclerViewScrollListener(mLayoutManager){

                    @Override
                    public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                        Toast.makeText(getContext(),"Loading the more trucks ..",Toast.LENGTH_SHORT).show();
                        truckAdapter.truckList.addAll(getFakeList());
                        truckAdapter.notifyDataSetChanged();
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
            Picasso.with(getContext()).load(R.drawable.foodtruck).into(holder.truckImage);

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
                 itemView.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         MainActivity ma = ((MainActivity)getActivity());
                         if(ma != null)
                            ma.setFragment(new TruckPageFragment(),true);
                     }
                 });
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
