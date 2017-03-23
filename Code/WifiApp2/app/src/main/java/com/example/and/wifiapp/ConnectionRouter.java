package com.example.and.wifiapp;

/**
 * Created by and on 3/16/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class ConnectionRouter extends AppCompatActivity {

    private final String USER_AGENT = "Mozilla/5.0";
    Button sendGet;
    WebView myWebView;
    WebView webView;

    ConnectionRouter http;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.router_http);
        try {

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
//            webView = new WebView(this);
//            webView.setWebViewClient(new WebViewClient() {
//                @Override
//                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//                    return false;
//                }
//
//                @Override
//                public void onPageFinished(WebView view, String url) {
//                    createWebPrintJob(view);
//                    myWebView = null;
//                }
//            });



        } catch (Exception e) {
            System.out.println("something Wrong happened");
        }

    }


    // HTTP GET request
    private void sendGet() {
        try {
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

//    private void createWebPrintJob(WebView webView) {
//
//        PrintManager printManager = (PrintManager) this
//                .getSystemService(Context.PRINT_SERVICE);
//
//        PrintDocumentAdapter printAdapter =
//                webView.createPrintDocumentAdapter("MyDocument");
//
//        String jobName = getString(R.string.app_name) + " Print Test";
//
//        printManager.print(jobName, printAdapter,
//                new PrintAttributes.Builder().build());
//    }


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
                        return null;


                    case "POSt":
                        http.sendPost();
                }
            } catch (Exception e) {
                System.out.println(e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            //writeHTML(result);
//            webView.loadDataWithBaseURL(null, result,
//                    "text/HTML", "UTF-8", null);
//
//            myWebView = webView;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(String... middle) {
        }

    }


    public void writeHTML(String toWrite) {
        String FILENAME = "C:\\Users\\and\\Desktop\\FinalProject\\index1.html";
        BufferedWriter bw = null;
        FileWriter fw = null;
        try {
            fw = new FileWriter(FILENAME);
            bw = new BufferedWriter(fw);
            bw.write(toWrite);
            System.out.println("Done");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null)
                    bw.close();
                if (fw != null)
                    fw.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
