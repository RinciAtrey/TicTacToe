package com.rincipack.tictactoe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android.app.AlertDialog;
import android.content.DialogInterface;

import java.util.ArrayList;
import java.util.Random;

public class Against_Ai extends AppCompatActivity {

    Button b1, b2, b3, b4, b5, b6, b7, b8, b9, restart, newgame;
    int flag = 0;
    TextView x_win, o_win, draw_win, winner_result, turn_indicator;
    int count = 0;
    int counterx = 0, countero = 0, counter_draw = 0;
    boolean playerStartsNext = true; // Variable to track who starts the next game
    final int AI_MOVE_DELAY = 300; // AI move delay in milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_against_ai);
        id();

        newgame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewGame();
            }
        });

        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restart();
            }
        });

        // Initially set to the player's turn
        turn_indicator.setText("Player's Turn (X)");
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
        restart = findViewById(R.id.restart);
        winner_result = findViewById(R.id.winner_result);
        newgame = findViewById(R.id.newgame);
        draw_win = findViewById(R.id.draw_win);
        turn_indicator = findViewById(R.id.turn_indicator);
    }

    @SuppressLint("SetTextI18n")
    public void ptap(View view) { // Renamed from get_values to ptap
        Button btncurrent = (Button) view;
        if (btncurrent.getText().toString().equals("") && flag != 3) {
            if (flag == 0) {
                btncurrent.setText("X");
                flag = 1;
                count++;
                checkWinner();
                if (flag != 3 && count < 9) {
                    turn_indicator.setText("AI's Turn (O)");
                    aiMoveWithDelay();
                }
            }
        }
    }

    private void aiMoveWithDelay() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                aiMove();
            }
        }, AI_MOVE_DELAY);
    }

    private void aiMove() {
        Button bestMove = precomputeBestMove();
        if (bestMove != null) {
            bestMove.setText("O");
            flag = 0;
            count++;
            checkWinner();
            Log.d("AI_MOVE", "AI is making a move");
            if (flag != 3) {
                turn_indicator.setText("Player's Turn (X)");
            }
        }
    }

    private Button precomputeBestMove() {
        if (count == 0 || (count == 1 && !playerStartsNext)) {
            // First AI move: Choose a random available button
            Random random = new Random();
            ArrayList<Button> availableButtons = new ArrayList<>();
            for (Button btn : new Button[]{b1, b2, b3, b4, b5, b6, b7, b8, b9}) {
                if (btn.getText().toString().equals("")) {
                    availableButtons.add(btn);
                }
            }
            if (!availableButtons.isEmpty()) {
                return availableButtons.get(random.nextInt(availableButtons.size()));
            }
        } else {
            // Subsequent moves: Use minimax algorithm for optimal play
            int bestScore = Integer.MIN_VALUE;
            Button bestMove = null;

            for (Button btn : new Button[]{b1, b2, b3, b4, b5, b6, b7, b8, b9}) {
                if (btn.getText().toString().equals("")) {
                    btn.setText("O");
                    int score = minimax(0, false);
                    btn.setText("");
                    if (score > bestScore) {
                        bestScore = score;
                        bestMove = btn;
                    }
                }
            }

            return bestMove;
        }

        return null;
    }

    private int minimax(int depth, boolean isMaximizing) {
        String result = checkWinnerForMinimax();
        if (result != null) {
            if (result.equals("X")) return -1;
            if (result.equals("O")) return 1;
            return 0;
        }

        if (isMaximizing) {
            int bestScore = Integer.MIN_VALUE;
            for (Button btn : new Button[]{b1, b2, b3, b4, b5, b6, b7, b8, b9}) {
                if (btn.getText().toString().equals("")) {
                    btn.setText("O");
                    int score = minimax(depth + 1, false);
                    btn.setText("");
                    bestScore = Math.max(score, bestScore);
                }
            }
            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            for (Button btn : new Button[]{b1, b2, b3, b4, b5, b6, b7, b8, b9}) {
                if (btn.getText().toString().equals("")) {
                    btn.setText("X");
                    int score = minimax(depth + 1, true);
                    btn.setText("");
                    bestScore = Math.min(score, bestScore);
                }
            }
            return bestScore;
        }
    }

    private String checkWinnerForMinimax() {
        String[] positions = new String[]{
                b1.getText().toString(), b2.getText().toString(), b3.getText().toString(),
                b4.getText().toString(), b5.getText().toString(), b6.getText().toString(),
                b7.getText().toString(), b8.getText().toString(), b9.getText().toString()
        };

        String[][] winningCombinations = new String[][]{
                {positions[0], positions[1], positions[2]},
                {positions[3], positions[4], positions[5]},
                {positions[6], positions[7], positions[8]},
                {positions[0], positions[3], positions[6]},
                {positions[1], positions[4], positions[7]},
                {positions[2], positions[5], positions[8]},
                {positions[0], positions[4], positions[8]},
                {positions[2], positions[4], positions[6]}
        };

        for (String[] combination : winningCombinations) {
            if (combination[0].equals(combination[1]) && combination[1].equals(combination[2])) {
                if (!combination[0].equals("")) return combination[0];
            }
        }

        for (String pos : positions) {
            if (pos.equals("")) return null;
        }

        return "Draw";
    }

    private void checkWinner() {
        Button[][] buttons = {
                {b1, b2, b3},
                {b4, b5, b6},
                {b7, b8, b9},
                {b1, b4, b7},
                {b2, b5, b8},
                {b3, b6, b9},
                {b1, b5, b9},
                {b3, b5, b7}
        };

        for (Button[] combination : buttons) {
            if (combination[0].getText().toString().equals(combination[1].getText().toString()) &&
                    combination[1].getText().toString().equals(combination[2].getText().toString()) &&
                    !combination[0].getText().toString().equals("")) {
                if (combination[0].getText().toString().equals("X")) {
                    x_win.setText("" + counter_x());
                    winner_result.setText("Winner is X!");
                } else {
                    o_win.setText("" + counter_o());
                    winner_result.setText("Winner is O!");
                }
                combination[0].setTextColor(Color.parseColor("#FF5722"));
                combination[1].setTextColor(Color.parseColor("#FF5722"));
                combination[2].setTextColor(Color.parseColor("#FF5722"));
                flag = 3;
                playerStartsNext = !playerStartsNext; // Toggle for next game
                return;
            }
        }

        if (count == 9) {
            Toast.makeText(this, "~Draw game~", Toast.LENGTH_SHORT).show();
            winner_result.setText("Draw game, play again!");
            draw_win.setText("" + counter_draw());
            for (Button btn : new Button[]{b1, b2, b3, b4, b5, b6, b7, b8, b9}) {
                btn.setTextColor(Color.parseColor("#FF5722"));
            }
            playerStartsNext = !playerStartsNext; // Toggle for next game
        }
    }

    private int counter_x() {
        counterx++;
        return counterx;
    }

    private int counter_o() {
        countero++;
        return countero;
    }

    private int counter_draw() {
        counter_draw++;
        return counter_draw;
    }

    private void restart() {
        for (Button btn : new Button[]{b1, b2, b3, b4, b5, b6, b7, b8, b9}) {
            btn.setText("");
            btn.setTextColor(Color.BLACK); // Reset to default text color
        }
        winner_result.setText("");
        count = 0;
        flag = 0;
        if (playerStartsNext) {
            turn_indicator.setText("Player's Turn (X)");
        } else {
            turn_indicator.setText("AI's Turn (O)");
            aiMoveWithDelay();
        }
    }

    private void startNewGame() {
        restart();
        countero = 0;
        counterx = 0;
        counter_draw = 0;
        x_win.setText("0");
        o_win.setText("0");
        draw_win.setText("0");
        Toast.makeText(Against_Ai.this, "New game has started", Toast.LENGTH_SHORT).show();
        playerStartsNext = true; // Ensure player starts the new game
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

                        Against_Ai.super.onBackPressed(); // Call the super method to handle back press
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
