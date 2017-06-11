package com.example.and.wifiapp;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    private int xProg;
    private LineGraphSeries<DataPoint> series;
    private final Context context = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.indices);

        signal = (TextView) findViewById(R.id.range);
        graph = (GraphView) findViewById(R.id.graph);

        Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(100);
        viewport.setScrollable(true);
        xProg = 0;

        //===============================
        series = new LineGraphSeries<>();

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
                signal.setText("Signal: "+dot.getY()+"%");
                graph.addSeries(series);
                ha.postDelayed(this, INVOKE_FUNC);
            }
        }, INVOKE_FUNC);
    }
}


