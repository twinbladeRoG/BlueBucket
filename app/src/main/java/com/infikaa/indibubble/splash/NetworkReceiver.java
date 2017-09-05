package com.infikaa.indibubble.splash;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.infikaa.indibubble.EnterActivity;

/**
 * Created by Sohan on 24-Mar-17.
 */
public class NetworkReceiver extends BroadcastReceiver {
    SplashActivity sa;
    public NetworkReceiver(SplashActivity sa) {
        this.sa=sa;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String status = NetworkUtil.getConnectivityStatusString(context);

        Toast.makeText(context, status, Toast.LENGTH_SHORT).show();

        if (isNetworkAvailable(context)) {
            Intent i =new Intent(context, EnterActivity.class);
            context.startActivity(i);
        }
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
