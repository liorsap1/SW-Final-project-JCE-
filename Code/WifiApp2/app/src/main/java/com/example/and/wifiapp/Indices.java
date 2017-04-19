package com.example.and.wifiapp;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

/**
 * Created by and on 1/28/2017.
 */


public class Indices extends AppCompatActivity {

    Button backToMenu;
    Button refresh;
    TextView signal;


    GraphView graph;
    LineGraphSeries<DataPoint> series;

    final Context context = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.indices);
        backToMenu = (Button) findViewById(R.id.back_menu);
        signal = (TextView) findViewById(R.id.range);
        refresh = (Button) findViewById(R.id.refresh);

        backToMenu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
            }
        });
        getWifiStrength();
        refresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getWifiStrength();
            }
        });
    }

    private void getWifiStrength() {
        try {
            WifiManager wifiManager = (WifiManager) getApplicationContext()
                    .getSystemService(Context.WIFI_SERVICE);
            int rssi = wifiManager.getConnectionInfo().getRssi();
            int level = WifiManager.calculateSignalLevel(rssi, 100);
            int percentage = (int) (level);
            signal.setText("The signal is: " + percentage + "%");
        } catch (Exception e) {
            signal.setText("The signal is: " + "problem occure");
        }
    }
}
