package com.infikaa.indibubble.splash;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.infikaa.indibubble.R;

public class SplashActivity extends AppCompatActivity {


    protected ImageView imageView;
    BroadcastReceiver networkReceiver = new NetworkReceiver(SplashActivity.this);
    final IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        imageView = (ImageView) findViewById(R.id.splash_image);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                registerReceiver(networkReceiver, filter);
            }
        }, 2000);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(networkReceiver);
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
