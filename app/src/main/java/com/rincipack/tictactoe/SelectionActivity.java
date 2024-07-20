package com.rincipack.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


import com.bumptech.glide.Glide;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SelectionActivity extends AppCompatActivity {
    private Button players, against_ai, online_btn;
    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

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
    }

}
