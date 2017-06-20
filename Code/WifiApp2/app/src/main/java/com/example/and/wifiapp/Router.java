package com.example.and.wifiapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.*;

import java.io.*;
import java.net.*;
import java.util.Enumeration;

import static java.lang.Integer.parseInt;

/**
 * Created by and on 12/30/2016.
 */

public class Router extends AppCompatActivity {

    private String validateIP = "";
    private String validateUsername = "";
    private String validatePass = "";


    private String SSID = "";
    private String password = "";
    private String channel = "";

    final int PORT = 23;

    //SERVER
    TextView info, infoip, msg;
    String message = "";
    ServerSocket serverSocket;


    //CLIENT
    TextView textResponse;
    EditText telnet_username, telnet_pass;
    EditText Address, ssid_name, new_pass;
    Button buttonClear, btn_save;
    PrintStream printStream;
    Spinner WiFi_net_mode, WiFi_mode, wifi_BroadMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_page);



        //-----------SERVER-----------
//        info = (TextView) findViewById(R.id.info);
//        infoip = (TextView) findViewById(R.id.infoip);
//        msg = (TextView) findViewById(R.id.msg);
     //   Address = (EditText) findViewById(R.id.address);

        //-----------CLIENT-----------

       // telnet_username = (EditText) findViewById(R.id.username);
     //   telnet_pass = (EditText) findViewById(R.id.password);
        ssid_name = (EditText) findViewById(R.id.new_ssid);
        new_pass = (EditText) findViewById(R.id.new_wifi_pass);
        buttonClear = (Button) findViewById(R.id.clear);
//        textResponse = (TextView) findViewById(R.id.response);
        btn_save = (Button) findViewById(R.id.save);
        WiFi_mode = (Spinner) findViewById(R.id.wifi_mode);
        WiFi_net_mode = (Spinner) findViewById(R.id.wifi_net_mode);
        wifi_BroadMode = (Spinner) findViewById(R.id.wifi_BroadMode);

        //-----------SERVER WORKING-----------

//        infoip.setText(getIpAddress());
        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();

        //-----------  BUTTON SAVE LISTENER  -----------

        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textResponse.setText("");
                ssid_name.setText("");
                new_pass.setText("");
                msg.setText("");
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            boolean change = false;

            @Override
            public void onClick(View v) {
                if (ssid_name.getText().toString().length() != 0) {
                    SSID = ssid_name.getText().toString();
                    change = true;
                    Change("ssid");
                } else {
                    errMessage("lo tov: " + ssid_name.getText().toString());
                }
                if (new_pass.getText().toString().length() != 0) {
                    password = new_pass.getText().toString();
                    change = true;
                    Change("password");
                }
                if (!WiFi_mode.getSelectedItem().toString().equals("Wifi Mode")) {
                    change = true;
                    Change("wifiMode");
                }
                if (!WiFi_net_mode.getSelectedItem().toString().equals("Wifi Network Mode")) {
                    change = true;
                    Change("WiFiNetMode");
                }
                if (!wifi_BroadMode.getSelectedItem().toString().equals("Wireless SSID Broadcast")) {
                    change = true;
                    Change("WiFiBrodcast");
                }
                if(!change){
                    errMessage("Nothing to change");
                }
            }
        });

        callValidationDialog();

    }

    private void Change(String chanage) {
      //  String Add = Address.getText().toString();
        String Add = validateIP;
       // Add = "192.168.1.1";
       // String username = telnet_username.getText().toString();
        String username = validateUsername;
       // String pass = telnet_pass.getText().toString();
        String pass = validatePass;
      // pass = "admin";
        if (Add.length() <= 0 || username.length() <= 0 || pass.length() <= 0) { // not necessary...
            errMessage("Please check the values");
        } else {
            Client myClientTask = new Client(Add, PORT, username, pass);
            myClientTask.execute(chanage);
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

    public boolean configure(String username, String pass, String command, Socket soc) {
        try {
            Thread.sleep(100);
            sendMessage(soc, username + "\r");
            Thread.sleep(100);
            sendMessage(soc, pass + "\r");
            Thread.sleep(100);
            sendMessage(soc, command + "\r");
            Thread.sleep(1000);
            sendMessage(soc, "nvram commit\r");
            Thread.sleep(1000);
            sendMessage(soc, "exit\r");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serverSocket != null) {
            try {
                serverSocket.close();
                if (printStream != null)
                    printStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private class SocketServerThread extends Thread {
        static final int PORT = 1300;

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(PORT);
                Router.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
               //         info.setText("Listen to Port: " + serverSocket.getLocalPort());
                    }
                });
                // it suppose to synchronize with the client
                // cause if i want to connect to ddwrt its have to stop the while loop
                while (true) {
                    Socket socketListen = serverSocket.accept();
                    message += getMessage(socketListen);
                    Router.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            msg.setText(message);
                        }
                    });
                    ServerReply socketServerReplyThread = new ServerReply(socketListen);
                    socketServerReplyThread.run();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ServerReply extends Thread {
        private Socket hostThreadSocket;

        ServerReply(Socket socket) {
            hostThreadSocket = socket;
        }

        @Override
        public void run() {
            String msgReply = sendMessage(hostThreadSocket, "Reply form Server: ");
            message += "Server sent to Client: " + msgReply + "\n";
            Router.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    msg.setText(message);
                }
            });
        }
    }

    /**
     * Send message through socket to other server.
     *
     * @param hostThreadSocket {Socket}
     * @param msg              {String}
     * @return {String}
     */
    public String sendMessage(Socket hostThreadSocket, String msg) {
        try {
            OutputStream outputStream;
            outputStream = hostThreadSocket.getOutputStream();
            printStream = new PrintStream(outputStream);
            printStream.print(msg);
        } catch (IOException e) {
            e.printStackTrace();
            message += "Error in sending Message! " + e.toString() + "\n";
        }
        return msg;
    }

    /**
     * Get info from client who send to my server
     *
     * @param socket {Socket}
     * @return {String}
     */
    public String getMessage(Socket socket) {
        String message = "";
        message += "from " + socket.getInetAddress() + "And the port:" + socket.getPort() + "\n";
        return message;
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
                        Deviceip += "Device ip is: " + inetAddress.getHostAddress() + "\n";
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            Deviceip += "Can't get ip! " + e.toString() + "\n";
        }
        return Deviceip;
    }

    private String getMode() {
        String mode = WiFi_mode.getSelectedItem().toString();
        if (mode.equals("Disabled")) {
            return "disabled";
        }
        if (mode.equals("Mixed")) {
            return "mixed";
        }
        if (mode.equals("B-Only")) {
            return "b-only";
        }
        if (mode.equals("G-Only")) {
            return "g-only";
        }
        return "mixed";
    }

    private String getNetMode() {
        String mode = WiFi_mode.getSelectedItem().toString();
        if (mode.equals("AP")) {
            return "ap";
        }
        if (mode.equals("Client")) {
            return "sta";
        }
        if (mode.equals("Client Bridge")) {
            return "wet";
        }
        if (mode.equals("Adhoc")) {
            return "infra";
        }
        if (mode.equals("Repeater")) {
            return "apsta";
        }
        if (mode.equals("Repeater Bridge")) {
            return "apstawet";
        }
        return "ap";
    }

    private String getBroadCastMode() {
        String mode = WiFi_mode.getSelectedItem().toString();
        if (mode.equals("Disabled")) {
            return "1";
        }
        if (mode.equals("Enable")) {
            return "0";
        }
        return "0";
    }



//===================================================================================
    //                          Client AsyncTask
//===================================================================================
//configure(userName, password, "nvram set ath0_wpa_psk=" + password, socket);
//configure(userName, password, "nvram set ath0_ssid=" + SSID, socket);

    public class Client extends AsyncTask<String, Void, Void> {
        String dstAddress, userName, pass;
        int dstPort; String response = "";
        Client(String addr, int port, String Name, String telnetpassword) {
            dstAddress = addr;
            dstPort = port;
            userName = Name;
            pass = telnetpassword;
        }
        @Override
        protected Void doInBackground(String... command) {
            Socket socket = null;
            try {
                socket = new Socket(dstAddress, dstPort);
                switch (command[0]) {
                    case "ssid":
                        configure(userName, pass, "nvram set wl0_ssid=" + SSID, socket);
                        break;
                    case "password":
                        configure(userName, pass, "nvram set wl0_wpa_psk=" + password, socket);
                        break;
                    case "channel":
                        configure(userName, pass, "nvram set wl0_channel=" + channel, socket);
                        break;
                    case "wifiMode":
                        configure(userName, pass, "nvram set wl0_net_mode=" + getMode(), socket);
                        break;
                    case "WiFiNetMode":
                        configure(userName, pass, "nvram set wl0_mode=" + getNetMode(), socket);
                        break;
                    case "WiFiBrodcast":
                        configure(userName, pass, "nvram set wl0_closed=" + getBroadCastMode(), socket);
                        break;
                }
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
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            textResponse.setText(response);
            super.onPostExecute(result);
        }
    }
    //===================================================================================

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

                validateIP = edit_ip_address.getText().toString();
                validateUsername = edit_username.getText().toString();
                validatePass = edit_password.getText().toString();

                if(validateIP.equals("")){
                    Toast.makeText(getApplicationContext(), "No IP address entered", Toast.LENGTH_LONG).show();
                }
                if(validateUsername.equals("")){
                    Toast.makeText(getApplicationContext(), "No username entered", Toast.LENGTH_LONG).show();
                }
                if(validatePass.equals("")){
                    Toast.makeText(getApplicationContext(), "No password entered", Toast.LENGTH_LONG).show();
                }

                dialog.dismiss();
               // if(validateIP.equals("") || validateUsername.equals("") || validatePass.equals(""))

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        try {
            dialog.getWindow().setLayout(600, 600);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    //===========================private class======================================

//    private class Telnet {
//        String user_Telnet = "";
//        String pass_Telnet = "";
//        String command = "";
//
//        public Telnet(String user_Telnet, String pass_Telnet, String command) {
//            this.command = command;
//            this.pass_Telnet = pass_Telnet;
//            this.user_Telnet = user_Telnet;
//        }
//
//        public String getUser_Telnet() {
//            return user_Telnet;
//        }
//
//        public void setUser_Telnet(String user_Telnet) {
//            this.user_Telnet = user_Telnet;
//        }
//
//        public String getCommand() {
//            return command;
//        }
//
//        public void setCommand(String command) {
//            this.command = command;
//        }
//
//        public String getPass_Telnet() {
//            return pass_Telnet;
//        }
//
//        public void setPass_Telnet(String pass_Telnet) {
//            this.pass_Telnet = pass_Telnet;
//        }
//    }

    //==============================================================================

}
