package com.example.and.wifiapp;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    // <editor-fold defaultstate="collapsed" desc="---Lior project---">

    private final String TAG = getClass().getSimpleName();
    Button infoButton;
    Button configButton;

    TextView Frequency;
    TextView LinkSpeed;
    TextView BSSID;
    TextView Contents;
    TextView IpAddress;
    TextView NetworkId;
    TextView MacAddress;
    TextView SSID;
    ListView detailsList;

    WifiManager wifi;
    String pressed;
    String results[] = new String[20];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Context context = this;

        infoButton = (Button) findViewById(R.id.info_button);
        configButton = (Button)findViewById(R.id.config_route);

        Frequency = (TextView) findViewById(R.id.textView);
        LinkSpeed = (TextView) findViewById(R.id.textView2);
        BSSID = (TextView) findViewById(R.id.textView3);
        Contents = (TextView) findViewById(R.id.textView4);
        IpAddress = (TextView) findViewById(R.id.textView6);
        NetworkId = (TextView) findViewById(R.id.textView7);
        MacAddress = (TextView) findViewById(R.id.textView8);
        SSID = (TextView) findViewById(R.id.textView9);
//        detailsList = (ListView) findViewById(R.id.detailsList);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_layout, R.id.listsForInfo, results);
        // Assign adapter to ListView
//        detailsList.setAdapter(adapter);

        // ListView Item Click Listener
/*        detailsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value
                String itemValue = (String) detailsList.getItemAtPosition(position);

                // Show Alert
                Toast.makeText(getApplicationContext(),
                        "Position :" + itemPosition + "  ListItem : " + itemValue, Toast.LENGTH_LONG)
                        .show();
            }

        });

*/
        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        infoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pressed = "infoButton";
                new LongOperation().execute(pressed);
            }
        });
        configButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, Router.class);
                startActivity(intent);
            }
        });


//        stateButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                pressed = "stateButton";
//                System.out.println("here is the problem2");
//                new LongOperation().execute(pressed);
//            }
//        });
    }

    public void fillTextsInfo(String results[]) {

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
                        results[4] = "Ip Address: " + wifi.getConnectionInfo().getIpAddress();
                        results[5] = "Network Id: " + wifi.getConnectionInfo().getNetworkId();
                        results[6] = "Mac Address: " + wifi.getConnectionInfo().getMacAddress();
                        results[7] = "SSID: " + wifi.getConnectionInfo().getSSID();
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


//    public void pressedStatus(){
//        stateButton.setText(wifi.getWifiState());
//    }
// </editor-fold>

}
