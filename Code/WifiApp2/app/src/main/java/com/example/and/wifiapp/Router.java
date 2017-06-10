package com.example.and.wifiapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.*;
import java.net.*;
import java.util.Enumeration;

/**
 * Created by and on 12/30/2016.
 */

public class Router extends AppCompatActivity {

    String SSID = "";
    String password = "";

    class Telnet {
        String user_Telnet = "";
        String pass_Telnet = "";
        String command = "";
        public Telnet(String user_Telnet, String pass_Telnet, String command) {
            this.command = command;
            this.pass_Telnet = pass_Telnet;
            this.user_Telnet = user_Telnet;
        }
        public String getUser_Telnet() {return user_Telnet;}
        public void setUser_Telnet(String user_Telnet) {this.user_Telnet = user_Telnet;}
        public String getCommand() {return command;}
        public void setCommand(String command) {this.command = command;}
        public String getPass_Telnet() {return pass_Telnet;}
        public void setPass_Telnet(String pass_Telnet) {this.pass_Telnet = pass_Telnet;}
    }


    String Security_Mode_encrypt = "";
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

    final Context context = this;

    //SERVER
    TextView info, infoip, msg;
    String message = "";
    ServerSocket serverSocket;


    //CLIENT
    TextView textResponse;
    EditText Address, Port, ssid_name, new_pass;
    Button buttonConnect, buttonClear, change_ssid, change_pass, btn_save;
    PrintStream printStream;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_page);

        //-----------SERVER-----------
        info = (TextView) findViewById(R.id.info);
        infoip = (TextView) findViewById(R.id.infoip);
        msg = (TextView) findViewById(R.id.msg);
        Address = (EditText) findViewById(R.id.address);
        Port = (EditText) findViewById(R.id.port);

        //-----------CLIENT-----------

        ssid_name = (EditText) findViewById(R.id.new_ssid);
        new_pass = (EditText) findViewById(R.id.new_wifi_pass);
        buttonClear = (Button) findViewById(R.id.clear);
        textResponse = (TextView) findViewById(R.id.response);
        btn_save = (Button) findViewById(R.id.save); //this is the new button


        //-----------SERVER WORKING-----------
        infoip.setText(getIpAddress());
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
            @Override
            public void onClick(View v) {
                if(!new_pass.getText().equals("")){
                    SSID = ssid_name.getText().toString();
                    Change("ssid");}
                if(!new_pass.getText().equals("")){
                    password = new_pass.getText().toString();
                    Change("password");}
            }
        });

    }

    private void Change(String chanage){
        String Address = getText().toString();
        try{
            if(Address.length() <= 0){
            }
            int portum = Integer.parseInt(Port.getText().toString())
            Client myClientTask = new Client(Address, portum);
            myClientTask.execute(chanage);
        }
        catch(NumberFormatException  e){
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("Dana pussy is so not wet");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
    }

    public boolean configure(String username, String pass, String command, Socket soc) {
        try {
            Thread.sleep(1000);
            sendMessage(soc, username + "\r");
            Thread.sleep(1000);
            sendMessage(soc, pass + "\r");
            Thread.sleep(1000);
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
                        info.setText("Listen to Port: " + serverSocket.getLocalPort());
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
                        Deviceip += "Device ip is: " + inetAddress.getHostAddress() + "\n";}}}
        } catch (SocketException e) {
            e.printStackTrace();
            Deviceip += "Can't get ip! " + e.toString() + "\n";
        }
        return Deviceip;
    }


    public class Client extends AsyncTask<String, Void, Void> {
        String dstAddress;
        int dstPort;
        String response = "";

        Client(String addr, int port) {
            dstAddress = addr;
            dstPort = port;
        }

        @Override
        protected Void doInBackground(String... command) {
            Socket socket = null;
            try {
                socket = new Socket(dstAddress, dstPort);
                switch (command[0]) {
                    case "ssid":
                        if (SSID.length() != 0) {
                            configure("root", "admin", "nvram set ath0_ssid=" + SSID, socket);
                            //wl0_channel = channel
                            //wl0_net_mode = mixed
                        }
                    case "password":
                        if (password.length() != 0) {
                            configure("root", "admin", "nvram set ath0_wpa_psk=" + password, socket);
                            configure("root", "admin", "nvram set wl0_wpa_psk=" + password, socket);
                        }
                }
                ByteArrayOutputStream OutputStream = new ByteArrayOutputStream(
                        1024);
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



}
