package com.example.and.wifiapp;

/**
 * Created by and on 3/16/2017.
 */

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class ConnectionRouter extends AppCompatActivity {

    private final String USER_AGENT = "Mozilla/5.0";
    Button sendGet;

    ConnectionRouter http;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.router_http);

            http = new ConnectionRouter();

            sendGet = (Button) findViewById(R.id.button);

            sendGet.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try {

                        System.out.println("Testing 1 - Send Http GET request");
                        System.out.println("\nTesting 2 - Send Http POST request");
                        new HttpRequest().execute("GET");

                    } catch (Exception e) {
                        System.out.println("heeeeerrrreeee");
                    }
                }
            });


        } catch (Exception e) {
            System.out.println("something Wrong happened");
        }
    }


    // HTTP GET request
    private void sendGet() {
        try {
            String url = "http://www.google.com/search?q=mkyong";
            System.out.println("got to here1111!!!");

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            System.out.println("got to here2222!!!");

            // optional default is GET
            con.setRequestMethod("GET");

            //add request header
            con.setRequestProperty("User-Agent", USER_AGENT);
            System.out.println("got to here3333!!!");

            int responseCode = con.getResponseCode();
            System.out.println("got to here444 and response code is: !!!" + responseCode);
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //print result
            System.out.println("got to here!!!");
            System.out.println(response.toString());
        } catch (Exception e) {
            System.out.println("Problem occurred");
        }

    }

    // HTTP POST request
    private void sendPost() {
        try {
            String url = "https://selfsolve.apple.com/wcResults.do";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            //add reuqest header
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

            String urlParameters = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";

            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + urlParameters);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //print result
            System.out.println(response.toString());
        } catch (Exception e) {
            System.out.println("Problem occurred");
        }
    }


    private class HttpRequest extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String pressed = params[0];
            try {
                switch (pressed) {
                    case "GET":
//                        String url = "http://www.google.com/search?q=mkyong";
                        String url = "http://192.168.1.1";
                        System.out.println("got to here1111!!!");

                        URL obj = new URL(url);
                        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                        System.out.println("got to here2222!!!");

                        // optional default is GET
                        con.setRequestMethod("GET");

                        //add request header
                        con.setRequestProperty("User-Agent", USER_AGENT);
                        System.out.println("got to here3333!!!");

                        int responseCode = con.getResponseCode();
                        System.out.println("got to here444 and response code is: !!!" + responseCode);
                        System.out.println("\nSending 'GET' request to URL : " + url);
                        System.out.println("Response Code : " + responseCode);

                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(con.getInputStream()));
                        String inputLine;
                        StringBuffer response = new StringBuffer();

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();

                        //print result
                        System.out.println("got to here!!!");
                        System.out.println(response.toString());


                    case "POSt":
                        http.sendPost();
                }
            }
            catch(Exception e){
                System.out.println(e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String last) {

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(String... middle) {
        }
    }

}
