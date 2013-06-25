package com.example.androidorange;



import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by mgarcia on 23/06/13.
 */
public class MapActivity extends Activity {

    private WebView webView;
    private ProgressDialog progressDialog;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Sets the visibility of the indeterminate progress bar in the
        // title
        setProgressBarIndeterminateVisibility(true);
        // Show progress dialog
        progressDialog = ProgressDialog.show(MapActivity.this,
                "ProgressDialog", "Loading!");

        this.webView = (WebView) findViewById(R.id.webView);
        // Tells JavaScript to open windows automatically.
        webView.getSettings().setJavaScriptEnabled(true);
        // Sets our custom WebViewClient.
        webView.setWebViewClient(new myWebClient());
        // Loads the given URL
        webView.loadUrl("https://maps.google.es/maps?saddr=Av.+Gran+V%C3%ADa&daddr=Venezuela,+vigo&hl=es&ie=UTF8&sll=42.233362,-8.718294&sspn=0.006871,0.013937&geocode=FcFvhAIdB_16_w%3BFZJphAId7fd6_yHdONLFOqj9HClPbd76bmIvDTHdONLFOqj9HA&mra=dme&mrsp=0&sz=17&t=m&z=17");

    }

    private class myWebClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // Load the given URL on our WebView.
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            // When the page has finished loading, hide progress dialog and
            // progress bar in the title.
            super.onPageFinished(view, url);
            setProgressBarIndeterminateVisibility(false);
            progressDialog.dismiss();
        }
    }
}