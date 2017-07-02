package com.example.and.wifiapp;

import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;

public class ConfirmActivity extends AppCompatActivity {

    Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        Bundle bund = getIntent().getExtras();
        socket = MyService.socket;
        sendNotification();
        if (socket == null) {
            Toast.makeText(this, "socket null", Toast.LENGTH_SHORT).show();
        }
        if (bund.getString("mes") != null) {
            errMessage(bund.getString("mes"));
            // socket = gson.fromJson(bund.getString("soc"), Socket.class);
        }
    }


    private void errMessage(String field) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(field);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        ConfirmActivity.SocketServerReplyThread socketServerReplyThread = new ConfirmActivity.SocketServerReplyThread("1");
                        socketServerReplyThread.run();
                        Intent intent1 = new Intent(ConfirmActivity.this, MainActivity.class);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent1);
                        finish();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        ConfirmActivity.SocketServerReplyThread socketServerReplyThread = new ConfirmActivity.SocketServerReplyThread("No");
                        socketServerReplyThread.run();

                        Intent intent1 = new Intent(ConfirmActivity.this, MainActivity.class);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent1);
                        finish();
                    }
                });
        alertDialog.show();
    }


    public class SocketServerReplyThread extends Thread {


        String cnt = "";

        SocketServerReplyThread(String c) {

            //errMessages(c);
            cnt = c;
        }

        @Override
        public void run() {
            OutputStream outputStream = null;
            try {
                outputStream = socket.getOutputStream();
                PrintStream printStream = new PrintStream(outputStream);
                printStream.print(cnt);
                printStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendNotification() {
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.start_icon)
                        .setContentTitle("MySignal")
                        .setContentText("New User Try Connecting!");
        NotificationManager mNotificationManager = (NotificationManager)
                getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
        mNotificationManager.notify(001, mBuilder.build());
    }
}
