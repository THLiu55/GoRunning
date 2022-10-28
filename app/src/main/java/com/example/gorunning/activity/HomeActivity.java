package com.example.gorunning.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.example.gorunning.R;
import com.example.gorunning.sharings.SharedViewModel;
import com.example.gorunning.utils.MyLocationListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.qweather.sdk.view.HeConfig;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends AppCompatActivity {

    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE
    };

    private static final int PERMISSION_CODES = 1001;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermission();
        SDKInitializer.setAgreePrivacy(getApplicationContext(), true);
        setContentView(R.layout.activity_home);

        SharedViewModel sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        // hide action bar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        // bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        NavController bottomNavController = Navigation.findNavController(this, R.id.home_fragment_container);
        AppBarConfiguration bottomConfiguration = new AppBarConfiguration.Builder(bottomNavController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, bottomNavController, bottomConfiguration);
        NavigationUI.setupWithNavController(bottomNavigationView, bottomNavController);

        // allows for privacy agreement
        LocationClient.setAgreePrivacy(true);
        try {
            // set configurations for location detector
            LocationClient mLocationClient = new LocationClient(getApplicationContext());
            LocationClientOption option = new LocationClientOption();
            option.setOpenGps(true);
            option.setCoorType("bd09ll");
            option.setScanSpan(1000);
            mLocationClient.setLocOption(option);
            MyLocationListener myLocationListener = MyLocationListener.getLocationListener(sharedViewModel);
            // register the location listener
            mLocationClient.registerLocationListener(myLocationListener);
            mLocationClient.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // weather test
        HeConfig.init("HE2210131026591303", "39a5989f3bf74506a9fc6a3c43ab40d7");
        //切换至免费订阅
        HeConfig.switchToDevService();

    }

    // turn off the back function
    @Override
    public void onBackPressed() { }

    // require service permission from user (location ...)
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermission() {
        List<String> p = new ArrayList<>();
        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                p.add(permission);
            }
        }
        if (p.size() > 0) {
            requestPermissions(p.toArray(new String[0]), PERMISSION_CODES);
        }
    }
}