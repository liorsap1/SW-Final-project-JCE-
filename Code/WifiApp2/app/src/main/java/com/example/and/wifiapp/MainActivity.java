package com.example.and.wifiapp;


import android.content.*;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    // <editor-fold defaultstate="collapsed" desc="---Project---">

    private final String TAG = getClass().getSimpleName();
    private ImageView infoButton;
    private ImageView config_telnet_Button;
    private ImageView indicesButton;
    //private SharedPreferences

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Context context = this;

        infoButton = (ImageView) findViewById(R.id.info_button);
        config_telnet_Button = (ImageView) findViewById(R.id.config_route);
        indicesButton = (ImageView) findViewById(R.id.indices_button);

        infoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (checkWifiOnAndConnected()) {
                    Intent intent = new Intent(context, Details.class);
                    startActivity(intent);
                } else {
                    errMessage("Wi-Fi adapter is OFF Or Not connected to an access point");
                }
            }
        });
        config_telnet_Button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (checkWifiOnAndConnected()) {
                    Intent intent = new Intent(context, Router.class);
                    startActivity(intent);
                } else {
                    errMessage("Wi-Fi adapter is OFF Or Not connected to an access point");
                }
            }
        });
        indicesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (checkWifiOnAndConnected()) {
                    Intent intent = new Intent(context, Indices.class);
                    startActivity(intent);
                } else {
                    errMessage("Wi-Fi is off or Not connected to access point");
                }
            }
        });

    }

    private boolean checkWifiOnAndConnected() {
        WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (wifiMgr.isWifiEnabled()) { // Wi-Fi adapter is ON
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
            if (wifiInfo.getNetworkId() == -1) {
                return false; // Not connected to an access point
            }
            return true; // Connected to an access point
        } else {
            return false; // Wi-Fi adapter is OFF
        }
    }

    private void errMessage(String field) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(field);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

// </editor-fold>

}
