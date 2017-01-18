package com.example.and.wifiapp;

/**
 * Created by and on 12/30/2016.
 */

public class Router {

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
