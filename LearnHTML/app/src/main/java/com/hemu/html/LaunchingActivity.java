package com.hemu.html;



import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.webkit.WebView;


import com.hemu.html.models.Common;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;

public class LaunchingActivity extends AppCompatActivity {
    WebView web;
    Handler handler;
    public static final String TAG = "TAG";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launching);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        web = findViewById(R.id.web);
        web.loadUrl("file:///android_asset/launching/index.html");
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setLoadWithOverviewMode(true);
        handler = new Handler();
        handler.postDelayed(() -> {
            LaunchingActivity.this.startActivity(new Intent(LaunchingActivity.this, MainActivity.class));
            finish();
        },2000);
        if (Common.isConnectToInternet(LaunchingActivity.this)) {
            new webScript().execute();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class webScript extends AsyncTask<Void , Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            Document document;
            Document documentE;
            Element titleE,linksE,descriptionE;
            String title,links,descriptionHTML,description,thumbnail;

            try {
                document = Jsoup.connect("https://feeds.feedburner.com/codehemu").get();
                JSONArray arr = new JSONArray();
                HashMap<String, JSONObject> map = new HashMap<>();

                for (int i =0; i< 20; i++){
                    titleE = document.select("item").select("title").get(i);
                    linksE = document.select("item").select("link").get(i);
                    descriptionE = document.select("item").select("description").get(i);

                    if (titleE!=null && linksE!=null && descriptionE!=null){
                        title = titleE.text();
                        links = linksE.text();
                        descriptionHTML = descriptionE.text();
                        documentE = Jsoup.parse(descriptionHTML);
                        Elements descriptionEs = documentE.select("p");
                        Elements thumbnailE = documentE.select("img");

                        if (descriptionEs.first()!= null) {
                            if (descriptionEs.text().length() > 300) {
                                description = descriptionEs.text().substring(0, 300);
                            } else {
                                description = descriptionEs.text();
                            }
                        }else {
                            description = null;
                        }

                        if (thumbnailE.first()!= null) {
                            if (!thumbnailE.attr("src").isEmpty()) {
                                thumbnail = thumbnailE.attr("src");
                            } else {
                                thumbnail = null;
                            }
                        }else {
                            thumbnail = null;
                        }
                        if (!title.isEmpty() && !links.isEmpty()) {
                            assert description != null;
                            if (!description.isEmpty()) {
                                assert thumbnail != null;
                                if (!thumbnail.isEmpty()) {
                                    JSONObject json = new JSONObject();
                                    json.put("id", i);
                                    json.put("title", title);
                                    json.put("desc", description);
                                    json.put("link", links);
                                    json.put("thumbnail", thumbnail);
                                    map.put("json" + i, json);
                                    arr.put(map.get("json" + i));
                                }
                            }
                        }
                        Log.d(TAG, "1onResponse: " + title);
                    }else {
                        Log.d(TAG, "1onError: RSS Feed Element not Found..");
                    }

                }
                SharedPreferences sharedPreferences = getSharedPreferences("shorts", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("edit",arr.toString());
                Log.d(TAG, "1onResponse: " + arr);
                editor.apply();

            } catch (IOException | JSONException e) {
                Log.d(TAG, "1onError: RSS Feed Url Connect Error =" + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
        }

    }

}