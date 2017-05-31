package com.example.and.wifiapp;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;


public class Details extends AppCompatActivity {

    Button infoButton;
    private final String TAG = getClass().getSimpleName();
    TextView Frequency;
    TextView LinkSpeed;
    TextView BSSID;
    TextView Contents;
    TextView IpAddress;
    TextView NetworkId;
    TextView MacAddress;
    TextView SSID;

    WifiManager wifi;
    String pressed;
    String results[] = new String[20];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.router_details);

        infoButton = (Button) findViewById(R.id.info_button);
        Frequency = (TextView) findViewById(R.id.textView);
        LinkSpeed = (TextView) findViewById(R.id.textView2);
        BSSID = (TextView) findViewById(R.id.textView3);
        Contents = (TextView) findViewById(R.id.textView4);
        IpAddress = (TextView) findViewById(R.id.textView6);
        NetworkId = (TextView) findViewById(R.id.textView7);
        MacAddress = (TextView) findViewById(R.id.textView8);
        SSID = (TextView) findViewById(R.id.textView9);


        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        infoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pressed = "infoButton";
                new LongOperation().execute(pressed);
            }
        });

    }
    private String getIpAddress() {
        String Deviceip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();
                    if (inetAddress.isSiteLocalAddress()) {
                        Deviceip += inetAddress.getHostAddress() + "\n";
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            Deviceip += "Can't get ip! " + e.toString() + "\n";
        }
        return Deviceip;
    }



    private class LongOperation extends AsyncTask<String, String, String> {

        String send;
        int numberSend;

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected String doInBackground(String... params) {
            try {

                String pressed = params[0];
                switch (pressed) {
                    case "infoButton":
                        results[0] = "Frequency: " + wifi.getConnectionInfo().getFrequency();
                        results[1] = "Link Speed: " + wifi.getConnectionInfo().getLinkSpeed();
                        results[2] = "BSSID: " + wifi.getConnectionInfo().getBSSID();
                        results[3] = "Contents: " + wifi.getConnectionInfo().describeContents();
                        results[4] = "Ip Address: " + getIpAddress();
                        results[5] = "Network Id: " + wifi.getConnectionInfo().getNetworkId();
                        results[6] = "Mac Address: " + wifi.getConnectionInfo().getMacAddress();
                        results[7] = "SSID: " + wifi.getConnectionInfo().getSSID();
                        return pressed;
                    case "stateButton":
                    default:
                        return pressed;
                }
            } catch (Exception e) {
                Log.d(TAG, "doInBackground problem");
                return null;
            }
        }

        @Override
        protected void onPostExecute(String last) {
            try {
                switch (last) {
                    case "infoButton":
                        Frequency.setText(results[0]);
                        LinkSpeed.setText(results[1]);
                        BSSID.setText(results[2]);
                        Contents.setText(results[3]);
                        IpAddress.setText(results[4]);
                        NetworkId.setText(results[5]);
                        MacAddress.setText(results[6]);
                        SSID.setText(results[7]);

                    case "stateButton":

                }
            } catch (Exception e) {
                Log.d(TAG, "onPostExecute problem");
            }
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(String... middle) {
        }
    }


}
