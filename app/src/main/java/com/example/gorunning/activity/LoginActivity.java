package com.example.gorunning.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.gorunning.R;
import com.qweather.sdk.view.HeConfig;

import cn.bmob.v3.Bmob;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // hide action bar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        Bmob.initialize(this, "ebbe137833bf421a6e3584f81e56b462");
    }
}