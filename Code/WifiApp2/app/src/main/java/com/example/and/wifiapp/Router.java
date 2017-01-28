package com.example.and.wifiapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by and on 12/30/2016.
 */

public class Router extends AppCompatActivity {

    String SSID = "";
    String Security_Mode_encrypt = "";
    String password = "";
    String ipAddress = "...";
    String Encryption_Mode = "";

    //TODO-Wi-Fi Security Settings
    int ethernetPort;
    int voiceLines;
    int wifiChannel;

    //TODO-Wi-Fi Security Settings
    boolean SSID_Broadcast = false;
    boolean AP_Isolation = false;
    boolean WiFi_Segregation = false;
    boolean WPS_Enabled = false;


    Button backMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_page);
        backMenu = (Button)findViewById(R.id.back_menu);

        final Context context = this;

        backMenu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
            }
        });
    }




    public String getSsid(){
        String s = "this is ssid function";
        return s;
    }

    public boolean configureSsid(String s){

        return true;
    }

    public boolean configurePasswor(String pass){
        String s = "this is ssid function";
        return true;
    }

}
