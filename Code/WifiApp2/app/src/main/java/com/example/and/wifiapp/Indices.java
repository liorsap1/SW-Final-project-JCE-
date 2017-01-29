package com.example.and.wifiapp;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by and on 1/28/2017.
 */


public class Indices extends AppCompatActivity {

    Button backToMenu;
    final Context context = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.indices);
        backToMenu = (Button)findViewById(R.id.back_menu);

        backToMenu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
            }
        });

    }
    private int getWifiStrength() {
        try {
            WifiManager wifiManager = (WifiManager) getApplicationContext()
                    .getSystemService(Context.WIFI_SERVICE);
            int rssi = wifiManager.getConnectionInfo().getRssi();
            int level = WifiManager.calculateSignalLevel(rssi, 10);
            int percentage = (int) ((level/10.0)*100);
            return percentage;
        }
        catch (Exception e) {
            return -1;
        }
    }
}
