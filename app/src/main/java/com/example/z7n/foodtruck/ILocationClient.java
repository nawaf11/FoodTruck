package com.example.z7n.foodtruck;

import com.google.android.gms.location.LocationResult;

/**
 * Created by z7n on 2/21/2018.
 */

public interface ILocationClient {
     void onLocationUpdated(LocationResult locationResult);
}
