package com.example.and.wifiapp;

import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.io.*;
import java.net.*;
import java.util.Enumeration;
import java.util.Vector;

import static android.R.id.message;


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

    WifiManager wifi;
    String pressed;
    String results[] = new String[20];

    //========  CLIENT ========//
    final int PORT = 23;
    String user_Telnet = "root";
    String pass_Telnet = "admin";
    String router_addresses = "192.168.1.1";
    PrintStream printStream;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.router_details);

        Frequency = (TextView) findViewById(R.id.textView);
        LinkSpeed = (TextView) findViewById(R.id.textView2);
        BSSID = (TextView) findViewById(R.id.textView3);
        Contents = (TextView) findViewById(R.id.textView4);
        IpAddress = (TextView) findViewById(R.id.textView6);
        NetworkId = (TextView) findViewById(R.id.textView7);
        MacAddress = (TextView) findViewById(R.id.textView8);
        SSID = (TextView) findViewById(R.id.textView9);

        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        pressed = "infoButton";
        new DetailsService(router_addresses, PORT, user_Telnet, pass_Telnet).execute(pressed);

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
                socket = new Socket(dstAddress, dstPort);
                String pressed = params[0];

                results[0] = "Frequency: " + wifi.getConnectionInfo().getFrequency();
                results[1] = "Link Speed: " + wifi.getConnectionInfo().getLinkSpeed();
                results[2] = "BSSID: " + wifi.getConnectionInfo().getBSSID();
                results[3] = "SSID: " + wifi.getConnectionInfo().getSSID();
                results[4] = "Ip Address: " + getIpAddress();
                results[5] = "Network Id: " + wifi.getConnectionInfo().getNetworkId();
                results[6] = "Mac Address: " + wifi.getConnectionInfo().getMacAddress();
                results[7] = "";
                configure(userName, pass, "arp", socket);


                ByteArrayOutputStream OutputStream = new ByteArrayOutputStream(1024);
                byte[] buffer = new byte[1024];
                int bytesRead;
                InputStream inputStream = socket.getInputStream();
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    OutputStream.write(buffer, 0, bytesRead);
                    response += OutputStream.toString("UTF-8");
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                e.printStackTrace();
                response = "IOException: " + e.toString();
            } finally {

            }

            System.out.println(response);
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                Frequency.setText(results[0]);
                LinkSpeed.setText(results[1]);
                BSSID.setText(results[2]);
                //Contents.setText(response);
                IpAddress.setText(results[4]);
                NetworkId.setText(results[5]);
                MacAddress.setText(results[6]);
                SSID.setText(results[7]);
                usersParser(result,userName);
                //errMessage(usersParser(result));
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
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
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(field);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
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

    private String usersParser(String toParse,String userName) {

        Vector<String> Names = new Vector<>();
        Vector<String> MACAdd = new Vector<>();
        Vector<String> IPAdd = new Vector<>();


        int start = toParse.split("arp").length;
        String tostart = userName+"@DD-WRT";
        String parser = toParse.split("arp")[start-1].split(tostart)[0];

        errMessage(parser);
        System.out.println(toParse);
        return parser;
    }


}