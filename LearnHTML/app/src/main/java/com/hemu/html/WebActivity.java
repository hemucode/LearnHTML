package com.hemu.html;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.Objects;

public class WebActivity extends AppCompatActivity {
    WebView web;
    AdView adView1;
    String title;

    @SuppressLint({"SetJavaScriptEnabled", "CutPasteId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        web = findViewById(R.id.web);
        adView1 = findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder().build();

        Bundle extras = getIntent().getExtras();

        assert extras != null;
        MobileAds.initialize(this, initializationStatus -> {});

        title = extras.getString("title");
        String url = extras.getString("url");

        assert title != null;
        if (!title.isEmpty()){
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            Objects.requireNonNull(getSupportActionBar()).setTitle(title);
        }else {
            Objects.requireNonNull(getSupportActionBar()).hide();
        }

        assert url != null;
        if (url.isEmpty()) {
            web.loadUrl("file:///android_asset/404/index.html");
        }else {
            web.loadUrl(url);
            if (!title.isEmpty()){
                adView1.loadAd(adRequest);
            }
        }

        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setLoadWithOverviewMode(true);
        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                web.loadUrl("file:///android_asset/404/index.html");
                adView1.destroy();
                super.onReceivedError(view, request, error);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                web.setVisibility(View.VISIBLE);
                String Script = "document.querySelector('#htmlEditor')?Android.showToast('HTML Editor'):document.querySelector('h1')&&Android.showToast(document.querySelector('h1').innerText);";
                web.evaluateJavascript(Script,null);
                super.onPageFinished(view, url);
            }

        });
        web.addJavascriptInterface(new WebAppInterface(this), "Android");
    }
    public class WebAppInterface {
        Context mContext;
        /** Instantiate the interface and set the context */
        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void showToast(String toast) {
            if (!toast.isEmpty()){
                Objects.requireNonNull(getSupportActionBar()).setTitle(toast);
            }else {
                Objects.requireNonNull(getSupportActionBar()).setTitle(title);
            }
        }
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (web!=null && web.canGoBack()){
            web.goBack();
        }else {
            super.onBackPressed();
        }
    }
}