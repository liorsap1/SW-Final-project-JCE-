package com.example.and.wifiapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

/**
 * Created by Lior Sapir on 1/28/2017.
 */

public class Indices extends AppCompatActivity {
    private TextView signal;
    private GraphView graph;

    private Button map;
    private Button save_on_map;
    LocationManager mLocationManager;

    public double holdSignal;

    private int xProg;
    private LineGraphSeries<DataPoint> series;
    private final Context context = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.indices);

        signal = (TextView) findViewById(R.id.range);
        graph = (GraphView) findViewById(R.id.graph);

        map = (Button) findViewById(R.id.map);
        save_on_map = (Button) findViewById(R.id.save);

        Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(100);
        viewport.setScrollable(true);
        xProg = 0;

        //===============================
        series = new LineGraphSeries<>();

        map.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, MapsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("point", "Signal: "+holdSignal+"%");
                startActivity(intent);
            }
        });
        save_on_map.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, MapsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("point", "Signal: "+holdSignal+"%");
                startActivity(intent);
            }
        });

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Constant.LOCATION_REFRESH_TIME,
                Constant.LOCATION_REFRESH_DISTANCE, mLocationListener);

        runIndices();

    }

    private DataPoint getWifiStrength() {

        WifiManager wifiManager = (WifiManager) getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        int rssi = wifiManager.getConnectionInfo().getRssi();
        int level = WifiManager.calculateSignalLevel(rssi, 100);
        xProg++;
        return new DataPoint(xProg, level);
    }

    public void runIndices() {
        final Handler ha = new Handler();
        final long INVOKE_FUNC = 1000;
        ha.postDelayed(new Runnable() {
            @Override
            public void run() {
                DataPoint dot = getWifiStrength();
                series.appendData(dot, true, 100);
                Log.d("indices", dot.toString());
                holdSignal = dot.getY();
                signal.setText("Signal: "+holdSignal+"%");
                graph.addSeries(series);
                ha.postDelayed(this, INVOKE_FUNC);
            }
        }, INVOKE_FUNC);
    }


    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            //your code here
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };
}


