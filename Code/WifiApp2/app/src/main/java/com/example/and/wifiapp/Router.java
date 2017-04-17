package com.example.and.wifiapp;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
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
    EditText Address, Port, ssid_name;
    Button buttonConnect, buttonClear, change_ssid;
    PrintStream printStream;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_page);
        backMenu = (Button) findViewById(R.id.back_menu);


        //-----------SERVER-----------
        info = (TextView) findViewById(R.id.info);
        infoip = (TextView) findViewById(R.id.infoip);
        msg = (TextView) findViewById(R.id.msg);


        //-----------CLIENT-----------
        Address = (EditText) findViewById(R.id.address);
        Port = (EditText) findViewById(R.id.port);
        ssid_name = (EditText) findViewById(R.id.new_ssid);
        buttonConnect = (Button) findViewById(R.id.connect);
        change_ssid = (Button) findViewById(R.id.change_ssid);
        buttonClear = (Button) findViewById(R.id.clear);
        textResponse = (TextView) findViewById(R.id.response);


        //-----------CLIENT WORKING-----------
        buttonConnect.setOnClickListener(ConnectListener);
        change_ssid.setOnClickListener(SSIDListener);

        //-----------SERVER WORKING-----------
        infoip.setText(getIpAddress());
        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();

        buttonClear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                textResponse.setText("");
                msg.setText("");
            }
        });

        backMenu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serverSocket != null) {
            try {
                serverSocket.close();
                printStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This variable will point a function to invoke a client task
     */
    private View.OnClickListener SSIDListener =
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Client myClientTask = new Client(
                            Address.getText().toString(),
                            Integer.parseInt(Port.getText().toString()));
                            SSID = ssid_name.getText().toString();
                    myClientTask.execute("ssid");
                }
            };
    private View.OnClickListener ConnectListener =
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Client myClientTask = new Client(
                            Address.getText().toString(),
                            Integer.parseInt(Port.getText().toString()));
                    myClientTask.execute("connect");
                }
            };

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
                try {
                    switch (command[0]) {
                        case "connect":
                            Thread.sleep(1000);
                            sendMessage(socket, "root\r");
                            Thread.sleep(1000);
                            sendMessage(socket, "123456\r");
                        case "ssid":
                            Thread.sleep(1000);
                            sendMessage(socket, "root\r");
                            Thread.sleep(1000);
                            sendMessage(socket, "123456\r");
                            Thread.sleep(2000);
                            if(SSID.length() != 0){
                                sendMessage(socket, "nvram set ath0_ssid="+SSID+"\r");
                                Thread.sleep(3000);
                                sendMessage(socket, "nvram commit\r");
                            }
                    }
                    Thread.sleep(3000);
                    sendMessage(socket, "exit\r");
                } catch (InterruptedException e) {
                    Thread.interrupted();
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

    public String getSsid() {
        String s = "this is ssid function";
        return s;
    }

    public boolean configureSsid(String s) {

        return true;
    }

    public boolean configurePasswor(String pass) {
        String s = "this is ssid function";
        return true;
    }


}
