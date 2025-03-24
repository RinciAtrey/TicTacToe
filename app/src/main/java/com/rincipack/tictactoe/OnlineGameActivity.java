package com.rincipack.tictactoe;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class OnlineGameActivity extends AppCompatActivity {

    private DatabaseReference gameRef;
    private String gameCode;
    private String playerSymbol;
    private String currentPlayer;
    private Button[] buttons = new Button[9];
    private TextView turnIndicator, x_win, o_win, draw_win, winner_result;
    private Button restart, newgame;
    private int count = 0;
    private int counterx = 0, countero = 0, counter_draw = 0;
    private boolean gameActive = true;
    private String startingPlayer = "X"; // Track the starting player
    private boolean isResetting = false; // Track if a reset is in progress

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_game);

        gameCode = getIntent().getStringExtra("GAME_CODE");
        playerSymbol = getIntent().getStringExtra("PLAYER_SYMBOL");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        gameRef = database.getReference("games").child(gameCode);

        turnIndicator = findViewById(R.id.turn_indicator);
        x_win = findViewById(R.id.x_win);
        o_win = findViewById(R.id.o_win);
        draw_win = findViewById(R.id.draw_win);
        winner_result = findViewById(R.id.winner_result);
        restart = findViewById(R.id.restart);
        newgame = findViewById(R.id.newgame);

        buttons[0] = findViewById(R.id.b1);
        buttons[1] = findViewById(R.id.b2);
        buttons[2] = findViewById(R.id.b3);
        buttons[3] = findViewById(R.id.b4);
        buttons[4] = findViewById(R.id.b5);
        buttons[5] = findViewById(R.id.b6);
        buttons[6] = findViewById(R.id.b7);
        buttons[7] = findViewById(R.id.b8);
        buttons[8] = findViewById(R.id.b9);

        for (int i = 0; i < buttons.length; i++) {
            final int index = i;
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    makeMove(index);
                }
            });
        }

        newgame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(OnlineGameActivity.this)
                        .setTitle("Restart Game")
                        .setMessage("Are you sure you want to start new round?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startNewGame();
                            }
                        }).setNegativeButton("No", null).show();
            }
        });

        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(OnlineGameActivity.this)
                        .setTitle("Restart Game")
                        .setMessage("Are you sure you want to start new round?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                resetGame();
                            }
                        }).setNegativeButton("No", null).show();
            }
        });

        gameRef.child("turn").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentPlayer = snapshot.getValue(String.class);
                updateTurnIndicator();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled if needed
            }
        });

        gameRef.child("board").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot cell : snapshot.getChildren()) {
                    String key = cell.getKey();
                    String value = cell.getValue(String.class);
                    int index = Integer.parseInt(key);
                    buttons[index].setText(value);
                }
                countMoves(snapshot); // Count moves after board state update
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled if needed
            }
        });

        gameRef.child("reset").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean reset = snapshot.getValue(Boolean.class);
                if (reset != null && reset && !isResetting) {
                    isResetting = true;
                    resetGameLocally();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled if needed
            }
        });

        gameRef.child("newgame").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean newGame = snapshot.getValue(Boolean.class);
                if (newGame != null && newGame) {
                    startNewGameLocally();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled if needed
            }
        });

        gameRef.child("players").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Check number of players
                long count = snapshot.getChildrenCount();
                if (count >= 2) {
                    // Game is full, notify user
                    Toast.makeText(OnlineGameActivity.this, "Game is already full", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled if needed
            }
        });

        gameRef.child("playerExits").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean playerExited = snapshot.getValue(Boolean.class);
                if (playerExited != null && playerExited) {
                    removeGameIfBothExited();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled if needed
            }
        });

        // Track player presence
        DatabaseReference playerRef = gameRef.child("players").child(playerSymbol);
        playerRef.setValue(true);
        playerRef.onDisconnect().removeValue();
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
                        gameRef.child("players").child(playerSymbol).removeValue();
                        OnlineGameActivity.super.onBackPressed(); // Call the super method to handle back press
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove player from the game when activity is destroyed
        gameRef.child("players").child(playerSymbol).removeValue();
    }

    private void makeMove(int index) {
        if (!gameActive) return; // Do nothing if the game is not active

        if (buttons[index].getText().toString().isEmpty() && playerSymbol.equals(currentPlayer)) {
            gameRef.child("board").child(String.valueOf(index)).setValue(playerSymbol);
            gameRef.child("turn").setValue(playerSymbol.equals("X") ? "O" : "X");
            countMoves(null); // Count moves after each valid move
        } else {
            Toast.makeText(this, "It's not your turn or cell is already occupied", Toast.LENGTH_SHORT).show();
        }
    }

    private void countMoves(DataSnapshot snapshot) {
        // Count moves based on the board state
        count = 0;
        for (Button button : buttons) {
            if (!button.getText().toString().isEmpty()) {
                count++;
            }
        }
        if (count > 4) {
            checkWinner(); // Check for winner after counting moves
        }
    }

    private void checkWinner() {
        String btn1 = buttons[0].getText().toString();
        String btn2 = buttons[1].getText().toString();
        String btn3 = buttons[2].getText().toString();
        String btn4 = buttons[3].getText().toString();
        String btn5 = buttons[4].getText().toString();
        String btn6 = buttons[5].getText().toString();
        String btn7 = buttons[6].getText().toString();
        String btn8 = buttons[7].getText().toString();
        String btn9 = buttons[8].getText().toString();

        boolean winnerFound = false;

        if (btn1.equals(btn2) && btn2.equals(btn3) && !btn1.equals("")) {
            setWinner(btn1, buttons[0], buttons[1], buttons[2]);
            winnerFound = true;
        } else if (btn4.equals(btn5) && btn5.equals(btn6) && !btn4.equals("")) {
            setWinner(btn4, buttons[3], buttons[4], buttons[5]);
            winnerFound = true;
        } else if (btn7.equals(btn8) && btn8.equals(btn9) && !btn7.equals("")) {
            setWinner(btn7, buttons[6], buttons[7], buttons[8]);
            winnerFound = true;
        } else if (btn1.equals(btn4) && btn4.equals(btn7) && !btn1.equals("")) {
            setWinner(btn1, buttons[0], buttons[3], buttons[6]);
            winnerFound = true;
        } else if (btn2.equals(btn5) && btn5.equals(btn8) && !btn2.equals("")) {
            setWinner(btn2, buttons[1], buttons[4], buttons[7]);
            winnerFound = true;
        } else if (btn3.equals(btn6) && btn6.equals(btn9) && !btn3.equals("")) {
            setWinner(btn3, buttons[2], buttons[5], buttons[8]);
            winnerFound = true;
        } else if (btn1.equals(btn5) && btn5.equals(btn9) && !btn1.equals("")) {
            setWinner(btn1, buttons[0], buttons[4], buttons[8]);
            winnerFound = true;
        } else if (btn3.equals(btn5) && btn5.equals(btn7) && !btn3.equals("")) {
            setWinner(btn3, buttons[2], buttons[4], buttons[6]);
            winnerFound = true;
        } else if (count == 9) {
            winner_result.setText("~Draw game~");
            counter_draw++;
            draw_win.setText("" + counter_draw);
            gameActive = false;
        }

        if (winnerFound) {
            gameActive = false; // Set game as inactive after a winner is found
            // Alternate the starting player for the next game
            startingPlayer = startingPlayer.equals("X") ? "O" : "X";
        }
    }

    private void setWinner(String winner, Button b1, Button b2, Button b3) {
        winner_result.setText("Winner: " + winner);
        b1.setTextColor(Color.parseColor("#F45F4F"));
        b2.setTextColor(Color.parseColor("#F45F4F"));
        b3.setTextColor(Color.parseColor("#F45F4F"));

        if (winner.equals("X")) {
            counterx++;
            x_win.setText("" + counterx);
        } else {
            countero++;
            o_win.setText("" + countero);
        }

        Toast.makeText(this, "Winner is: " + winner, Toast.LENGTH_SHORT).show();
    }

    private void resetGame() {
        gameRef.child("reset").setValue(true);
    }

    private void resetGameLocally() {
        // Reset game locally
        for (Button button : buttons) {
            button.setText("");
            button.setTextColor(Color.parseColor("#000000"));
        }
        count = 0;
        gameActive = true;
        winner_result.setText("");
        gameRef.child("board").removeValue(); // Clear the board in the database
        gameRef.child("turn").setValue(startingPlayer); // Set the turn to the starting player

        // Resetting process is complete
        isResetting = false;

        // Set reset field back to false after local reset
        gameRef.child("reset").setValue(false);
    }

    private void startNewGame() {
        gameRef.child("newgame").setValue(true);
    }

    private void startNewGameLocally() {
        resetGameLocally(); // Reset game state locally

        // Reset counters and UI elements
        counterx = 0;
        countero = 0;
        counter_draw = 0;
        x_win.setText("0");
        o_win.setText("0");
        draw_win.setText("0");

        // Update Firebase with the starting player ("X")
        gameRef.child("turn").setValue("X");

        // Reset the new game flag in Firebase if needed
        gameRef.child("newgame").setValue(false);

        // Ensure starting player resets to "X" for a full new game
        startingPlayer = "X";

    }

    private void removeGameIfBothExited() {
        gameRef.child("players").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() == 0) {
                    // Remove the game from the database if both players have exited
                    gameRef.removeValue();
                    finish(); // Close this activity
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled if needed
            }
        });
    }

    private void updateTurnIndicator() {
        if (playerSymbol.equals(currentPlayer)) {
            turnIndicator.setText("Your Turn (" + playerSymbol + ")");
        } else {
            turnIndicator.setText("Opponent's Turn (" + currentPlayer + ")");
        }
    }
}
