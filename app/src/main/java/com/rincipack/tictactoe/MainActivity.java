package com.rincipack.tictactoe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;
public class MainActivity extends AppCompatActivity {
    Button b1, b2, b3, b4, b5, b6, b7, b8, b9, restart, newgame;
    TextView x_win, o_win, draw_win, winner_result, turn_indicator;
    int count = 0;
    int counterx = 0, countero = 0, counter_draw = 0;
    boolean isXTurn = true;
    boolean nextGameStartWithX = true; // Track who should start the next game
    boolean gameActive = true; // Track if the game is active

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);


        setContentView(R.layout.activity_main);
        id();

        newgame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("New Game")
                            .setMessage("Are you sure you want to start a new game? This will reset the current scores.")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    resetGame();
                                    countero = 0;
                                    counterx = 0;
                                    counter_draw = 0;
                                    x_win.setText("0");
                                    o_win.setText("0");
                                    draw_win.setText("0");
                                    nextGameStartWithX = true; // Ensure new game starts with X
                                    isXTurn = true; // Ensure turn indicator starts with X
                                    updateTurnIndicator();
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                }

        });


        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetGame();
            }
        });
    }

    public void id() {
        b1 = findViewById(R.id.b1);
        b2 = findViewById(R.id.b2);
        b3 = findViewById(R.id.b3);
        b4 = findViewById(R.id.b4);
        b5 = findViewById(R.id.b5);
        b6 = findViewById(R.id.b6);
        b7 = findViewById(R.id.b7);
        b8 = findViewById(R.id.b8);
        b9 = findViewById(R.id.b9);
        x_win = findViewById(R.id.x_win);
        o_win = findViewById(R.id.o_win);
        draw_win = findViewById(R.id.draw_win);
        winner_result = findViewById(R.id.winner_result);
        restart = findViewById(R.id.restart);
        newgame = findViewById(R.id.newgame);
        turn_indicator = findViewById(R.id.turn_indicator); // Add this TextView to your XML layout
    }

    @SuppressLint("SetTextI18n")
    public void get_values(View view) {
        if (!gameActive) return; // Do nothing if the game is not active

        Button btncurrent = (Button) view;
        if (btncurrent.getText().toString().equals("")) {
            if (isXTurn) {
                btncurrent.setText("X");
            } else {
                btncurrent.setText("O");
            }
            count++;
            isXTurn = !isXTurn;
            updateTurnIndicator();

            if (count > 4) {
                checkWinner();
            }
        }
    }

    private void updateTurnIndicator() {
        if (isXTurn) {
            turn_indicator.setText("Turn: X");
        } else {
            turn_indicator.setText("Turn: O");
        }
    }

    private void checkWinner() {
        String btn1 = b1.getText().toString();
        String btn2 = b2.getText().toString();
        String btn3 = b3.getText().toString();
        String btn4 = b4.getText().toString();
        String btn5 = b5.getText().toString();
        String btn6 = b6.getText().toString();
        String btn7 = b7.getText().toString();
        String btn8 = b8.getText().toString();
        String btn9 = b9.getText().toString();

        boolean winnerFound = false;

        if (btn1.equals(btn2) && btn2.equals(btn3) && !btn1.equals("")) {
            setWinner(btn1, b1, b2, b3);
            winnerFound = true;
        } else if (btn4.equals(btn5) && btn5.equals(btn6) && !btn4.equals("")) {
            setWinner(btn4, b4, b5, b6);
            winnerFound = true;
        } else if (btn7.equals(btn8) && btn8.equals(btn9) && !btn7.equals("")) {
            setWinner(btn7, b7, b8, b9);
            winnerFound = true;
        } else if (btn1.equals(btn4) && btn4.equals(btn7) && !btn1.equals("")) {
            setWinner(btn1, b1, b4, b7);
            winnerFound = true;
        } else if (btn2.equals(btn5) && btn5.equals(btn8) && !btn2.equals("")) {
            setWinner(btn2, b2, b5, b8);
            winnerFound = true;
        } else if (btn3.equals(btn6) && btn6.equals(btn9) && !btn3.equals("")) {
            setWinner(btn3, b3, b6, b9);
            winnerFound = true;
        } else if (btn1.equals(btn5) && btn5.equals(btn9) && !btn1.equals("")) {
            setWinner(btn1, b1, b5, b9);
            winnerFound = true;
        } else if (btn3.equals(btn5) && btn5.equals(btn7) && !btn3.equals("")) {
            setWinner(btn3, b3, b5, b7);
            winnerFound = true;
        } else if (count == 9) {
            Toast.makeText(this, "~Draw game~", Toast.LENGTH_SHORT).show();
            winner_result.setText("Draw game, play again!");
            draw_win.setText("" + counter_draw());
            setColorForDraw();
            gameActive = false; // Game ends
            nextGameStartWithX = !nextGameStartWithX; // Alternate who starts the next game after a draw
        }

        if (winnerFound) {
            nextGameStartWithX = !nextGameStartWithX; // Alternate who starts the next game
            gameActive = false; // Game ends
        }
    }

    private void setWinner(String winner, Button... buttons) {
        for (Button btn : buttons) {
            btn.setTextColor(Color.parseColor("#F45F4F"));
        }
        if (winner.equals("X")) {
            x_win.setText("" + counter_x());
            winner_result.setText("Winner is X!");
        } else {
            o_win.setText("" + counter_o());
            winner_result.setText("Winner is O!");
        }
    }

    private void setColorForDraw() {
        b1.setTextColor(Color.parseColor("#FF5722"));
        b2.setTextColor(Color.parseColor("#FF5722"));
        b3.setTextColor(Color.parseColor("#FF5722"));
        b4.setTextColor(Color.parseColor("#FF5722"));
        b5.setTextColor(Color.parseColor("#FF5722"));
        b6.setTextColor(Color.parseColor("#FF5722"));
        b7.setTextColor(Color.parseColor("#FF5722"));
        b8.setTextColor(Color.parseColor("#FF5722"));
        b9.setTextColor(Color.parseColor("#FF5722"));
    }

    public void resetGame() {
        b1.setText("");
        b2.setText("");
        b3.setText("");
        b4.setText("");
        b5.setText("");
        b6.setText("");
        b7.setText("");
        b8.setText("");
        b9.setText("");
        count = 0;
        winner_result.setText(null);
        resetButtonColors();
        isXTurn = nextGameStartWithX;
        updateTurnIndicator();
        gameActive = true; // Reactivate the game
    }

    private void resetButtonColors() {
        b1.setTextColor(Color.parseColor("#070706"));
        b2.setTextColor(Color.parseColor("#070706"));
        b3.setTextColor(Color.parseColor("#070706"));
        b4.setTextColor(Color.parseColor("#070706"));
        b5.setTextColor(Color.parseColor("#070706"));
        b6.setTextColor(Color.parseColor("#070706"));
        b7.setTextColor(Color.parseColor("#070706"));
        b8.setTextColor(Color.parseColor("#070706"));
        b9.setTextColor(Color.parseColor("#070706"));
    }

    public int counter_x() {
        return ++counterx;
    }

    public int counter_o() {
        return ++countero;
    }

    public int counter_draw() {
        return ++counter_draw;
    }


    @Override
    public void onBackPressed() {
        // Show the confirmation dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?")
                .setTitle("Exit Game")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.super.onBackPressed(); // Call the super method to handle back press
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss(); // Dismiss the dialog
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }








}






