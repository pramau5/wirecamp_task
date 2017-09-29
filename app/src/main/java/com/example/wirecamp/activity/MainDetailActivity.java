package com.example.wirecamp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.example.wirecamp.R;

public class MainDetailActivity extends AppCompatActivity {

    private TextView titleView;
    private WebView webView;
    private String  url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_movie);

        setTitle("News Details");

        titleView = (TextView) findViewById(R.id.titleView);
        webView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            url = (String) bundle.get("url");
            String title = (String) bundle.get("title");
            titleView.setText("Title :: " + title);
          /*  System.out.println("URL ::: " + url);
            System.out.println("TITLE ::: " + title);*/

            openURL();

        }

    }

    private void openURL() {
        if (url!=null) {
            webView.loadUrl(url);
            webView.requestFocus();
        } else {
            System.out.println("URL null");
        }

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                // Do something
            }
        });
    }


}
