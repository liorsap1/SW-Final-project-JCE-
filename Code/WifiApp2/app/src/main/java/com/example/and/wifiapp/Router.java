package com.example.and.wifiapp;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.PrintStream;
import java.net.ServerSocket;

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
    final Context context = this;






    //SERVER

    TextView info, infoip, msg;
    String message = "";
    ServerSocket serverSocket;


    //CLIENT

    TextView textResponse;
    EditText Address, Port;
    Button buttonConnect, buttonClear;
    PrintStream printStream;













    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_page);
        backMenu = (Button)findViewById(R.id.back_menu);






        //-----------SERVER-----------
        info = (TextView) findViewById(R.id.info);
        infoip = (TextView) findViewById(R.id.infoip);
        msg = (TextView) findViewById(R.id.msg);


        //-----------CLIENT-----------
        Address = (EditText) findViewById(R.id.address);
        Port = (EditText) findViewById(R.id.port);
        buttonConnect = (Button) findViewById(R.id.connect);
        buttonClear = (Button) findViewById(R.id.clear);
        textResponse = (TextView) findViewById(R.id.response);


        //-----------CLIENT WORKING-----------
        buttonConnect.setOnClickListener(ConnectListener);

        //-----------SERVER WORKING-----------
        infoip.setText(getIpAddress());
        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();

        buttonClear.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                textResponse.setText("");
                msg.setText("");
            }});















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
