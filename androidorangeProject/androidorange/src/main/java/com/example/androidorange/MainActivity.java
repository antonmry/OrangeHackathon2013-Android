package com.example.androidorange;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Timer;
import java.util.TimerTask;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import android.os.Handler;


public class MainActivity extends Activity {
    private static final int CONNECTION_TIMEOUT = 100;
    private static final int DATARETRIEVAL_TIMEOUT = 100;
    private Bitmap bitmap = null;
    static boolean active = false;

    Handler vistaHandler = new Handler() {

        public void handleMessage(Message msg) {
            ImageView image = (ImageView) findViewById(R.id.imageView);
            if(bitmap != null && !bitmap.isRecycled())
                image.setImageBitmap(bitmap);

        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        active = true;


        try{

            Timer myTimer = new Timer();
            TimerTask myTask = new MyTimerTask();
            myTimer.schedule(myTask, 1000, 5000);
           // AsyncCallWS task = new AsyncCallWS();
           // task.execute(image);


        }catch (Exception e) {
             e.printStackTrace();
         }


    }

       /** Called when the user clicks the New Activity */
    public void sendMessage(View view){

        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);

    }

    /**
     * required in order to prevent issues in earlier Android version.
     */
    private static void disableConnectionReuseIfNecessary() {
        // see HttpURLConnection API doc
        if (Integer.parseInt(Build.VERSION.SDK)
                < Build.VERSION_CODES.FROYO) {
            System.setProperty("http.keepAlive", "false");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

    class MyTimerTask extends TimerTask {
        private static final String LOGTAG = "LogsAndroid";

        public void run() {

            // how update TextView in link below
            // http://android.okhelp.cz/timer-task-timertask-run-cancel-android-example/
            if(active){
                eliminaBitmap();
                try{
                    HttpClient client = new DefaultHttpClient();
                    HttpResponse httpResponse;
                    //HttpGet request = new HttpGet("http://172.20.34.154:8090/status/519472182");
                    HttpGet request = new HttpGet("http://hackaton.omslabs.com:8090/status/519472182");
                    httpResponse = client.execute(request);
                    int responseCode = httpResponse.getStatusLine().getStatusCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        HttpEntity entity = httpResponse.getEntity();
                        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder builder = factory.newDocumentBuilder();
                        Document doc = builder.parse(httpResponse.getEntity().getContent());
                        NodeList advertisers = doc.getElementsByTagName("advertiser");
                        Element advertiser = (Element)advertisers.item(0);
                        NodeList messages = advertiser.getElementsByTagName("message");
                        Element message = (Element)messages.item(0);

                        bitmap = getBitmap(message.getTextContent());

                    }else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        // handle unauthorized (if service requires user login)
                        Log.e(LOGTAG, "Mensaje de error");
                    } else if (responseCode != HttpURLConnection.HTTP_OK) {
                        // handle any other errors, like 404, 500,..
                        Log.e(LOGTAG, "Mensaje de error"+responseCode);
                    }
                    Message msg = new Message();
                    vistaHandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                    eliminaBitmap();
                }
            }

        }

        private Bitmap getBitmap(String url){

            Bitmap map = null;
            if(url == "" || url == null ){
                url ="http://www.savannah.gi/files/Savannah%20Happy%20Hour.jpg";
            }

            eliminaBitmap();

            try {
                InputStream in = new java.net.URL(url).openStream();
                map = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e(LOGTAG, "Mensaje de error BitMAP");
                e.printStackTrace();
            }

            return map;

        }

        private void eliminaBitmap(){

            if(bitmap != null && !bitmap.isRecycled()){
                bitmap.recycle();
                bitmap = null;
                System.gc();
            }
        }
    }

}
