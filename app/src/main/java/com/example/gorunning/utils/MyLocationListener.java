package com.example.gorunning.utils;

import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.example.gorunning.sharings.SharedViewModel;

import java.io.Flushable;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

public class MyLocationListener extends BDAbstractLocationListener {

    static MyLocationListener INSTANCE;
    private static double EARTH_RADIUS = 6371000;//赤道半径(单位m)

    SharedViewModel sharedViewModel;
    List<LatLng> track;

    public MyLocationListener(SharedViewModel sharedViewModel) {
        this.sharedViewModel = sharedViewModel;
        track = new LinkedList<>();
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
            double dis = 0;
            if (track.size() > 0) {
                LatLng prev = track.get(track.size() - 1);
                dis += getDistanceByLoc(lon, lat, prev.longitude, prev.latitude);
                if (dis > 30) {
                    return;
                }
            }
            track.add(new LatLng(lat, lon));
            sharedViewModel.setTrack(track);
            sharedViewModel.setDistance(sharedViewModel.getDistance() + dis);
        }
        sharedViewModel.setLocData(locData);
    }


    private static double rad(double d)
    {
        return d * Math.PI / 180.0;
    }
    /**
     * @param lon1 第一点的精度
     * @param lat1 第一点的纬度
     * @param lon2 第二点的精度
     * @param lat2 第二点的纬度
     * @return 返回的距离，单位m
     * */
    public static double getDistanceByLoc(double lon1, double lat1, double lon2, double lat2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lon1) - rad(lon2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

}
