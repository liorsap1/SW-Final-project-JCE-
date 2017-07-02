package com.example.and.wifiapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import static android.R.id.message;
import static com.example.and.wifiapp.Constant.*;


public class Details extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    TextView Frequency;
    TextView LinkSpeed;
    TextView BSSID;
    TextView Contents;
    TextView IpAddress;
    TextView NetworkId;
    TextView MacAddress;
    TextView SSID;
    TableLayout users_table;

    WifiManager wifi;


    //========  CLIENT ========//
    final int PORT = 23;
    String user_Telnet = "root";
    String pass_Telnet = "admin";
    String router_addresses = "192.168.1.1";
    PrintStream printStream;
    Vector<String> users_vec;
    private ArrayList<HashMap> list;
    private ArrayList<HashMap> listDetails;
    ListView lview;
    ListView listUsers;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.router_details);

        SSID = (TextView) findViewById(R.id.ssid);
        Frequency = (TextView) findViewById(R.id.frequency);
        IpAddress = (TextView) findViewById(R.id.ipadd);
        MacAddress = (TextView) findViewById(R.id.myMac);

        LinkSpeed = (TextView) findViewById(R.id.linkSpeed);
        NetworkId = (TextView) findViewById(R.id.netId);
        BSSID = (TextView) findViewById(R.id.routerMac);

        lview = (ListView) findViewById(R.id.listUsers);
        listUsers = (ListView)findViewById(R.id.listDetails);

        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);


        callValidationDialog();


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


    private class DetailsService extends AsyncTask<String, String, String> {

        String dstAddress, userName, pass;
        int dstPort;
        String response = "";
        Socket socket = null;
        String results[][] = new String[20][2];

        DetailsService(String addr, int port, String Name, String telnetpassword) {
            dstAddress = addr;
            dstPort = port;
            userName = Name;
            pass = telnetpassword;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected String doInBackground(String... params) {

            try {

                String pressed = params[0];

                switch(pressed){
                    case "details":
                        System.out.println("HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH");
                        results[0][0] = "SSID: ";
                        results[0][1] = ""+wifi.getConnectionInfo().getSSID();

                        String handle = ""+wifi.getConnectionInfo().getFrequency() + "GHz";
                        results[1][0] = "Frequency: ";
                        results[1][1] = handle.charAt(0) + "."+handle.substring(1);

                        results[2][0] = "Ip Address: ";
                        results[2][1] = getIpAddress();

                        results[3][0] = "My Mac Address: ";
                        results[3][1] = ""+wifi.getConnectionInfo().getMacAddress();

                        results[4][0] = "Internet Speed : ";
                        results[4][1] = wifi.getConnectionInfo().getLinkSpeed() +"Mega";

                        results[5][0] = "Network Id: ";
                        results[5][1] = ""+wifi.getConnectionInfo().getNetworkId();

                        results[6][0] = "Router MAC: ";
                        results[6][1] = wifi.getConnectionInfo().getBSSID();

                        break;

                    case "users":
                        socket = new Socket(dstAddress, dstPort);
                        configure(userName, pass, "arp", socket);
                        ByteArrayOutputStream OutputStream = new ByteArrayOutputStream(1024);
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        InputStream inputStream = socket.getInputStream();
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            OutputStream.write(buffer, 0, bytesRead);
                            response += OutputStream.toString("UTF-8");
                        }
                        break;
                }

            } catch (UnknownHostException e) {
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                e.printStackTrace();
                response = "IOException: " + e.toString();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if(result.equals("")){
                    SSID.setText(results[0][0] + results[0][1]);
                    Frequency.setText(results[1][0]+ results[1][1]);
                    IpAddress.setText(results[2][0]+ results[2][1]);
                    MacAddress.setText(results[3][0]+ results[3][1]);
                    LinkSpeed.setText(results[4][0]+ results[4][1]);
                    NetworkId.setText(results[5][0]+ results[5][1]);
                    BSSID.setText(results[6][0]+ results[6][1]);
                    //populateListDetails(results);
                }
                if(result.length() > 0){
                    usersParser(result, userName);
                    if (socket != null) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
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

    private void errMessage(String field) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("New User Request To Connect!");
        alertDialog.setMessage(field);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private boolean configure(String username, String pass, String command, Socket soc) {
        try {
            Thread.sleep(100);
            sendMessage(soc, username + "\r");
            Thread.sleep(100);
            sendMessage(soc, pass + "\r");
            Thread.sleep(100);
            sendMessage(soc, command + "\r");
            Thread.sleep(500);
            sendMessage(soc, "exit\r");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Send message through socket to other server.
     *
     * @param hostThreadSocket {Socket}
     * @param msg              {String}
     * @return {String}
     */
    private String sendMessage(Socket hostThreadSocket, String msg) {
        try {
            OutputStream outputStream;
            outputStream = hostThreadSocket.getOutputStream();
            printStream = new PrintStream(outputStream);
            printStream.print(msg);
        } catch (IOException e) {
            e.printStackTrace();
            return "Error in sending Message! " + e.toString() + "\n";
        }
        return msg;
    }

    private void usersParser(String toParse, String userName) {
        Vector<String> vec = new Vector<>();
        int start = toParse.split("arp").length;
        String tostart = userName + "@DD-WRT";
        String parser = toParse.split("arp")[start - 1].split(tostart)[0];
        String[] split = parser.split(" ");
        for (int i = 0; i < split.length; i++) {
            if (!split[i].equals("at") && !split[i].equals("[ether]") &&
                    !split[i].equals("on") && !split[i].equals("br0\r")
                    && !split[i].equals(" br0") && !split[i].equals("")
                    && !split[i].equals("br0\n") && !split[i].equals(" "))
                vec.add(split[i]);
        }
        populateList(vec);
        listviewAdapter adapter = new listviewAdapter(this, list);
        lview.setAdapter(adapter);
    }

    private void populateList(Vector <String> v) {

        list = new ArrayList<HashMap>();

        HashMap temp = new HashMap();
        temp.put(FIRST_COLUMN, "|USER");
        temp.put(SECOND_COLUMN, "|MAC");
        temp.put(THIRD_COLUMN, "|IP");
        list.add(temp);
        for (int i = 0; i < v.size(); i ++) {
            HashMap t = new HashMap();
            t.put(THIRD_COLUMN, "| "+v.get(i + 1).substring(1,v.get(i + 1).length()-1));
            t.put(SECOND_COLUMN, "| "+v.get(i + 2));
            t.put(FIRST_COLUMN, "| "+v.get(i).substring(2,(v.get(i).length())/2));
            list.add(t);
        }

    }

    private void populateListDetails(String [][] details) {

        listDetails = new ArrayList<HashMap>();

        HashMap temp = new HashMap();
        temp.put(FIRST_COLUMN, "|FIELD");
        temp.put(SECOND_COLUMN, "|INFO");
        listDetails.add(temp);
        for (int i = 0; i < details.length; i ++) {
            HashMap t = new HashMap();
            t.put(SECOND_COLUMN, "| "+details[i][1]);
            t.put(FIRST_COLUMN, "| "+details[i][0]);
            listDetails.add(t);
        }

        listviewAdapter adapter = new listviewAdapter(this, listDetails);
        listUsers.setAdapter(adapter);

    }
    private void callValidationDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.alert_connect_layout);
        dialog.setCancelable(false);

        dialog.setTitle("Login");

        final EditText edit_ip_address = (EditText) dialog.findViewById(R.id.alert_ip_address);
        final EditText edit_username = (EditText)dialog.findViewById(R.id.alert_username);
        final EditText edit_password = (EditText)dialog.findViewById(R.id.alert_password);
        Button connect = (Button)dialog.findViewById(R.id.alert_btn_connect);
        Button cancel = (Button)dialog.findViewById(R.id.alert_btn_cancel);


        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                router_addresses = edit_ip_address.getText().toString();
                user_Telnet = edit_username.getText().toString();
                pass_Telnet = edit_password.getText().toString();

                if(router_addresses.equals("")){
                    Toast.makeText(getApplicationContext(), "No IP address entered", Toast.LENGTH_LONG).show();
                }
                if(user_Telnet.equals("")){
                    Toast.makeText(getApplicationContext(), "No username entered", Toast.LENGTH_LONG).show();
                }
                if(pass_Telnet.equals("")){
                    Toast.makeText(getApplicationContext(), "No password entered", Toast.LENGTH_LONG).show();
                }

                new DetailsService(router_addresses, PORT, user_Telnet, pass_Telnet).execute("details");
                new DetailsService(router_addresses, PORT, user_Telnet, pass_Telnet).execute("users");


                dialog.dismiss();
                // if(validateIP.equals("") || validateUsername.equals("") || validatePass.equals(""))

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new DetailsService(router_addresses, PORT, user_Telnet, pass_Telnet).execute("details");

                dialog.dismiss();
            }
        });
        dialog.show();
    }

}
