package com.rincipack.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowMetrics;
import android.widget.Button;
import android.widget.ImageView;


import com.bumptech.glide.Glide;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.FirebaseDatabase;


public class SelectionActivity extends AppCompatActivity {
    private Button players, against_ai, online_btn;
    private ImageView imageView;
    private static String AD_UNIT_ID_BANNER = "ca-app-pub-5449047056721990/6167718480";
    private static String TEST_AD_UNIT_ID_BANNER = "ca-app-pub-3940256099942544/9214589741";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        new Thread(
                () -> {
                    // Initialize the Google Mobile Ads SDK on a background thread.
                    MobileAds.initialize(this, initializationStatus -> {
                    });
                })
                .start();

        players = findViewById(R.id.players);
        against_ai = findViewById(R.id.against_ai);
        online_btn = findViewById(R.id.online_btn);

        imageView = findViewById(R.id.imageView2);
        Glide.with(this).load(R.drawable.rocket).into(imageView);


        FirebaseDatabase database = FirebaseDatabase.getInstance();

        players.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent playIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(playIntent);

            }
        });

        against_ai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent playAgainstAIIntent = new Intent(getApplicationContext(), Against_Ai.class);
                startActivity(playAgainstAIIntent);

            }
        });

        online_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent online = new Intent(getApplicationContext(), OnlineCodeActivity.class);
                startActivity(online);

            }
        });
        loadBanner();

    }

    private void loadBanner() {
        AdView adView = new AdView(this);
        adView.setAdUnitId(TEST_AD_UNIT_ID_BANNER);
        adView.setAdSize(getAdSize());
        AdView adContainerView= findViewById(R.id.bannerAdView);
        adContainerView.removeAllViews();
        adContainerView.addView(adView);

        AdRequest adRequest= new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private AdSize getAdSize() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int adWidthPixels = displayMetrics.widthPixels;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = this.getWindowManager().getCurrentWindowMetrics();
            adWidthPixels = windowMetrics.getBounds().width();
        }

        float density = displayMetrics.density;
        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }
}


