package com.example.gorunning.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import cn.leancloud.LeanCloud;
import com.example.gorunning.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // hide action bar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        // initialize database
        LeanCloud.initialize(this, "xjpv1WsKDO5g4KtWDFcE0q3d-gzGzoHsz", "AUPWrExKBKkjczQjGGs3EqN2", "https://please-replace-with-your-customized.domain.com");
    }


}