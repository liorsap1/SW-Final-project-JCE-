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
    Button indicesButton;

    ListView detailsList;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Context context = this;

        infoButton = (Button) findViewById(R.id.info_button);
        configButton = (Button)findViewById(R.id.config_route);
        indicesButton = (Button)findViewById(R.id.indices_button);



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
        infoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, Details.class);
                startActivity(intent);
            }
        });
        configButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, Router.class);
                startActivity(intent);
            }
        });
        indicesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, Indices.class);
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



//    public void pressedStatus(){
//        stateButton.setText(wifi.getWifiState());
//    }
// </editor-fold>

}
