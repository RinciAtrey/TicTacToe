package com.rincipack.tictactoe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.SecureRandom;

public class OnlineCodeActivity extends AppCompatActivity {

    private EditText gameCodeEditText;
    private Button createGameButton, joinGameButton, generateCodeButton, shareCodeButton;
    private DatabaseReference gameRef;
    private static final String TAG = "OnlineCodeActivity";
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 6;
    private String userUniqueID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_code); // Ensure this matches your layout file

        gameCodeEditText = findViewById(R.id.game_code);
        createGameButton = findViewById(R.id.create_game_button);
        joinGameButton = findViewById(R.id.join_game_button);
        generateCodeButton = findViewById(R.id.generate_code_button);
        shareCodeButton = findViewById(R.id.share_code_button); // Initialize the share button


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        gameRef = database.getReference("games");

        userUniqueID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        createGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String gameCode = gameCodeEditText.getText().toString().trim();
                if (TextUtils.isEmpty(gameCode)) {
                    Toast.makeText(OnlineCodeActivity.this, "Please enter a game code", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (gameCode.length() != CODE_LENGTH) {
                    Toast.makeText(OnlineCodeActivity.this, "Game code must be of 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                createGame(gameCode);
            }
        });

        joinGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String gameCode = gameCodeEditText.getText().toString().trim();
                if (TextUtils.isEmpty(gameCode)) {
                    Toast.makeText(OnlineCodeActivity.this, "Please enter a game code", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (gameCode.length() != CODE_LENGTH) {
                    Toast.makeText(OnlineCodeActivity.this, "Game code must be of 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d(TAG, "Joining game with code: " + gameCode);
                joinGame(gameCode);
            }
        });

        generateCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String randomCode = generateRandomCode();
                gameCodeEditText.setText(randomCode);
            }
        });

        shareCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String gameCode = gameCodeEditText.getText().toString().trim();
                if (TextUtils.isEmpty(gameCode)) {
                    Toast.makeText(OnlineCodeActivity.this, "Please generate a game code first", Toast.LENGTH_SHORT).show();
                    return;
                }
                shareGameCode(gameCode);
            }
        });
    }

    private void shareGameCode(String gameCode) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Join my Tic Tac Toe game! " +
                "Use this code: " + gameCode);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    private void createGame(final String gameCode) {
        gameRef.child(gameCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(OnlineCodeActivity.this, "Game code already exists", Toast.LENGTH_SHORT).show();
                } else {
                    gameRef.child(gameCode).child("player1").setValue("X");
                    gameRef.child(gameCode).child("player1_id").setValue(userUniqueID);
                    gameRef.child(gameCode).child("turn").setValue("X");
                    Log.d(TAG, "Game created with code: " + gameCode);
                    startOnlineGameActivity(gameCode, "X");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error creating game", error.toException());
                Toast.makeText(OnlineCodeActivity.this, "Error creating game", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void joinGame(final String gameCode) {
        gameRef.child(gameCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String player1ID = snapshot.child("player1_id").getValue(String.class);
                    String player2ID = snapshot.child("player2_id").getValue(String.class);

                    if (userUniqueID.equals(player1ID) || userUniqueID.equals(player2ID)) {
                        Toast.makeText(OnlineCodeActivity.this, "You already left this game", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    boolean player1Exists = snapshot.child("player1").exists();
                    boolean player2Exists = snapshot.child("player2").exists();

                    if (player1Exists && player2Exists) {
                        Toast.makeText(OnlineCodeActivity.this, "Game is already full", Toast.LENGTH_SHORT).show();
                    } else if (player1Exists && !player2Exists) {
                        gameRef.child(gameCode).child("player2").setValue("O");
                        gameRef.child(gameCode).child("player2_id").setValue(userUniqueID);
                        Log.d(TAG, "Joined game with code: " + gameCode);
                        startOnlineGameActivity(gameCode, "O");
                    } else if (!player1Exists && player2Exists) {
                        gameRef.child(gameCode).child("player1").setValue("X");
                        gameRef.child(gameCode).child("player1_id").setValue(userUniqueID);
                        Log.d(TAG, "Joined game with code: " + gameCode);
                        startOnlineGameActivity(gameCode, "X");
                    } else {
                        Toast.makeText(OnlineCodeActivity.this, "Game code does not exist", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(OnlineCodeActivity.this, "Game code does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error joining game", error.toException());
                Toast.makeText(OnlineCodeActivity.this, "Error joining game", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startOnlineGameActivity(String gameCode, String playerSymbol) {
        Intent intent = new Intent(OnlineCodeActivity.this, OnlineGameActivity.class);
        intent.putExtra("GAME_CODE", gameCode);
        intent.putExtra("PLAYER_SYMBOL", playerSymbol);
        startActivity(intent);
    }

    private String generateRandomCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return code.toString();
    }


}
