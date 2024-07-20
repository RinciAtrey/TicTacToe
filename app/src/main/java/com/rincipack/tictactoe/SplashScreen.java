package com.rincipack.tictactoe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.rincipack.tictactoe.R;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_splash_screen);

        Intent Play= new Intent(getApplicationContext(), SelectionActivity.class);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(Play);
                finish();
            }
        }, 3000);
    }



}

