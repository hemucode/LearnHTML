package com.hemu.html;

import androidx.annotation.NonNull;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.widget.Toolbar;


import com.google.android.material.navigation.NavigationView;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;
import com.hemu.html.models.InAppUpdate;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;

    ImageView imageView;
    Bitmap bmp;

    CardView Card1,Card2,Card3,Card4;

    int MeasuredWidth = 0;
    int MeasuredHeight = 0;

    private InAppUpdate inAppUpdate;
    String appsName, packageName;
    ReviewInfo reviewInfo;
    ReviewManager manager;

   public static final String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        inAppUpdate = new InAppUpdate(MainActivity.this);
        inAppUpdate.checkForAppUpdate();

        this.appsName = getApplication().getString(R.string.app_name);
        this.packageName = getApplication().getPackageName();

        Card1 = findViewById(R.id.Card1);
        Card2 = findViewById(R.id.Card2);
        Card3 = findViewById(R.id.Card3);
        Card4 = findViewById(R.id.Card4);

        Card1.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ShortActivity.class)));
        Card2.setOnClickListener(v -> openLink("https://play.google.com/store/apps/dev?id=7464231534566513633"));
        Card3.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, WebActivity.class).putExtra("title", getString(R.string.text_tutorial)).putExtra("url",getString(R.string.tutorial_url))));

        Card4.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, WebActivity.class).putExtra("title", "").putExtra("url",getString(R.string.editor_url))));

        imageView = findViewById(R.id.Image);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        toggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        RequestReviewInfo();
        
    }
    private void RateMe(){
        if (reviewInfo != null){
            com.google.android.play.core.tasks.Task<Void> flow = manager.launchReviewFlow(this,reviewInfo);

            flow.addOnCompleteListener(task -> {
            });
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rate_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.rateHeart) {
            RateMe();
        }
        return super.onOptionsItemSelected(item);
    }

    private void RequestReviewInfo(){
        manager = ReviewManagerFactory.create(this);
        Task<ReviewInfo> request = manager.requestReviewFlow();

        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                reviewInfo = task.getResult();
            }else {
                Toast.makeText(this, "Not Review", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void openLink(String url) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    @SuppressLint("ObsoleteSdkInt")
    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        Point size = new Point();
        WindowManager w = getWindowManager();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)    {
            w.getDefaultDisplay().getSize(size);
            MeasuredWidth = size.x;
            MeasuredHeight = size.y;
        }else{
            Display d = w.getDefaultDisplay();
            MeasuredWidth = d.getWidth();
            MeasuredHeight = d.getHeight();
        }

        int width = imageView.getWidth();
        Log.d(TAG, "1onError:" + MeasuredHeight);

        bmp = BitmapFactory.decodeResource(getResources(),R.drawable.coder);
        if (MeasuredWidth==width){
            bmp = Bitmap.createScaledBitmap(bmp, MeasuredWidth,(MeasuredWidth*60)/100, true);
        }else {
            bmp = Bitmap.createScaledBitmap(bmp, width,(width*60)/100, true);
        }
        imageView.setImageBitmap(bmp);

        LinearLayout.LayoutParams Card1Params = (LinearLayout.LayoutParams) Card1.getLayoutParams();
        if (MeasuredWidth<=MeasuredHeight){
            Card1Params.height = (MeasuredHeight*29)/100;
        }else {
            Card1Params.height = (MeasuredHeight*60)/100;
        }

        Card1Params.width = (MeasuredWidth*40)/100;
        Card1.setLayoutParams(Card1Params);

        LinearLayout.LayoutParams Card2Params = (LinearLayout.LayoutParams) Card2.getLayoutParams();
        if (MeasuredWidth<=MeasuredHeight){
            Card2Params.height = (MeasuredHeight*35)/100;
        }else {
            Card2Params.height = (MeasuredHeight*79)/100;
        }

        Card2Params.width = (MeasuredWidth*40)/100;
        Card2.setLayoutParams(Card2Params);

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);

        if (item.getItemId() == R.id.home) {
            return false;
        }
        if (item.getItemId() == R.id.policy) {
            openLink("https://www.downloadhub.cloud/p/application-policie.html#Learn_HTML");
        }

        if (item.getItemId() == R.id.share) {
            LinkShareApp();
        }

        if (item.getItemId() == R.id.rate) {
            LinkRateUs();
        }

        if (item.getItemId() == R.id.more) {
            openLink("https://play.google.com/store/apps/dev?id=7464231534566513633");
        }

        if (item.getItemId() == R.id.website) {
            openLink("https://www.codehemu.com/");
        }
        if (item.getItemId() == R.id.fb) {
            openLink("https://www.facebook.com/codehemu");
        }
        if (item.getItemId() == R.id.yt) {
            openLink("https://www.youtube.com/c/HemantaGayen");
        }
        return false;

    }
    private void LinkShareApp() {
        Intent share = new Intent("android.intent.action.SEND");
        share.setType("text/plain");
        share.putExtra("android.intent.extra.SUBJECT", MainActivity.this.appsName);
        String APP_Download_URL = "https://play.google.com/store/apps/details?id=" + MainActivity.this.packageName;
        share.putExtra("android.intent.extra.TEXT", MainActivity.this.appsName + getString(R.string.download_it) + APP_Download_URL);
        MainActivity.this.startActivity(Intent.createChooser(share, getString(R.string.share)));
    }

    private void LinkRateUs() {
        try {
            Intent intent2 = new Intent("android.intent.action.VIEW");
            intent2.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + MainActivity.this.packageName));
            MainActivity.this.startActivity(intent2);
        }
        catch (Exception e){
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setData(Uri.parse("market://details?id=" + MainActivity.this.packageName));
            MainActivity.this.startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        inAppUpdate.onActivityResult(requestCode, resultCode);
    }

    @Override
    protected void onResume() {
        super.onResume();
        inAppUpdate.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        inAppUpdate.onDestroy();
    }

}