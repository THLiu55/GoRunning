package com.example.gorunning.utils;

import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.MyLocationData;
import com.example.gorunning.sharings.SharedViewModel;

import java.text.DecimalFormat;

public class MyLocationListener extends BDAbstractLocationListener {

    static MyLocationListener INSTANCE;

    SharedViewModel sharedViewModel;

    public MyLocationListener(SharedViewModel sharedViewModel) {
        this.sharedViewModel = sharedViewModel;
    }

    public static MyLocationListener getLocationListener(SharedViewModel sharedViewModel) {
        if (INSTANCE == null) {
            INSTANCE = new MyLocationListener(sharedViewModel);
        }
        return INSTANCE;
    }

    @Override
    public void onReceiveLocation(BDLocation location){
        if (location == null){
            return;
        }
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(location.getRadius())
                .direction(location.getDirection())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();
        double lat = locData.latitude;
        double lon = locData.longitude;
        if (lat != 4.9E-324) {
            DecimalFormat df = new DecimalFormat( "0.00");
            String loc = df.format(lon) + "," + df.format(lat);
            sharedViewModel.setLocation(loc);
        }
        sharedViewModel.setLocData(locData);
    }
}
