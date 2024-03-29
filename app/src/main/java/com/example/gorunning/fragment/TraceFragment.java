package com.example.gorunning.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.JsonToken;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.platform.comapi.map.MapController;
import com.example.gorunning.R;
import com.example.gorunning.sharings.SharedViewModel;
import com.example.gorunning.utils.MyLocationListener;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class TraceFragment extends Fragment {

    private TextView location_text, trace_status_text, mile_text, calories_text;
    private BaiduMap mBaiduMap;
    private TextureMapView mMapView;
    public LocationClient mLocationClient = null;
    SharedViewModel sharedViewModel;
    private Button runButton, clearButton;
    private Overlay overlay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SDKInitializer.initialize(requireActivity().getApplicationContext());

        View view = inflater.inflate(R.layout.fragment_trace, container, false);
        mMapView = view.findViewById(R.id.bmapView);
        location_text = view.findViewById(R.id.location_text_trace);
        runButton = view.findViewById(R.id.run_button);
        clearButton = view.findViewById(R.id.clear_button);
        mile_text = view.findViewById(R.id.running_miles);
        calories_text = view.findViewById(R.id.calories_text);
        trace_status_text = view.findViewById(R.id.trace_status_text);
        LocationClient.setAgreePrivacy(true);
        try {
            mLocationClient = new LocationClient(requireContext().getApplicationContext()); //声明LocationClient类
            MyLocationListener myLocationListener = MyLocationListener.getLocationListener(sharedViewModel);
            mLocationClient.registerLocationListener(myLocationListener);//注册监听函数
            // 开启定位图层
            mBaiduMap = mMapView.getMap();
            mBaiduMap.setMyLocationEnabled(true);//显示定位层并且可以触发定位,默认是flase
            mLocationClient.start();//开启定位
        } catch (Exception e) {
            Log.d("map", e.getMessage());
        }
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        sharedViewModel.getLocData().observe(requireActivity(), new Observer<MyLocationData>() {
            @Override
            public void onChanged(MyLocationData myLocationData) {
                System.out.println(mBaiduMap);
                if (mBaiduMap != null) {
                    System.out.println(myLocationData.latitude + " " + myLocationData.longitude);
                    System.out.println("changed here");
                    LatLng ll = new LatLng(myLocationData.latitude, myLocationData.longitude);
                    MapStatus.Builder builder = new MapStatus.Builder();
                    //设置缩放中心点；缩放比例；
                    builder.target(ll).zoom(18);
                    //给地图设置状态
                    MyLocationConfiguration myLocationConfiguration = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL,
                            false, BitmapDescriptorFactory.fromResource(R.drawable.ellipse), 0, 0);
                    mBaiduMap.setMyLocationConfiguration(myLocationConfiguration);
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                    mBaiduMap.setMyLocationData(myLocationData);
                }
            }
        });

        sharedViewModel.getLocation().observe(requireActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                String[] list = s.split(",");
                s = list[0] + ", " + list[1];
                location_text.setText(s);
            }
        });

        sharedViewModel.getTrack().observe(requireActivity(), new Observer<List<LatLng>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onChanged(List<LatLng> latLngs) {
                if (latLngs.size() >= 2) {



                    OverlayOptions mOverlayOptions = new PolylineOptions()
                            .width(10)
                            .color(0xAAFF0000)
                            .points(latLngs);
                    overlay = mBaiduMap.addOverlay(mOverlayOptions);
                    double dis = sharedViewModel.getDistance();
                    mile_text.setText(dis + " m");
                    calories_text.setText(getCalories(dis) + " Kcal");
                    for (LatLng latLng : latLngs) {
                        Log.d("tracing", latLng.latitude + "," + latLng.longitude + "(" + dis + ")" + " : ");
                    }
                }
            }
        });

        runButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedViewModel.resetRunningState();

                // rest UI
                boolean isRunning = sharedViewModel.getRunningState();
                if (isRunning) {
                    runButton.setText("Stop");
                    trace_status_text.setText("running");
                } else {
                    runButton.setText("Run");
                    trace_status_text.setText("having rest");
                }
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedViewModel.setTrack(new LinkedList<>());
                if (overlay != null) {
                    mBaiduMap.clear();
                }
            }
        });
    }

    public double getCalories(double dis) {
        return 70 * dis * 1.036 / 1000;
    }
}