package com.example.phase1.Level3Game;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.phase1.BackendStorage.GameManager;
import com.example.phase1.R;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class is the Activity Class for Level 3
 */

public class Level3Activity extends GameManager implements View.OnClickListener {

    private Level3Manager Level3 = new Level3Manager(); //Current level
    private Button[] buttons = new Button[4]; //Array of buttons for the current layout
    private Timer timer = new Timer("Timer"); //Timer

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setCurrPlayer(intent.getIntExtra(sendPlayer, 0));

        // Set our window to fullscreen without the bar at the top.
        this.getWindow()
                .setFlags(
                        WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Remove the title.
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Set day or night setting

        if (getDayOrNight() == 0) {
            setContentView(R.layout.n_activity_level3);
        } else if (getDayOrNight() == 1) {
            setContentView(R.layout.activity_level3);
        }
        // Set static hero near door
        final ImageView hero = findViewById(R.id.hero);
        if (getCharacter() == 0) {
            hero.setVisibility(View.VISIBLE);
        } else {
            hero.setVisibility(View.INVISIBLE);
        }

        // initialise button components
        if (getDayOrNight() == 0) {
            buttons[0] = findViewById(R.id.b1);
            buttons[1] = findViewById(R.id.b2);
            buttons[2] = findViewById(R.id.b3);
            buttons[3] = findViewById(R.id.b4);
        } else {
            buttons[0] = findViewById(R.id.b28);
            buttons[1] = findViewById(R.id.b29);
            buttons[2] = findViewById(R.id.b30);
            buttons[3] = findViewById(R.id.b31);
        }
    }

    /**
     * Override for Activity.onStart. Displays the Sequence.
     */
    protected void onStart() {
        super.onStart();
        displaySequence();
    }

    /**
     * Get the sequence from the current level and displays it with a 1 second delay
     * between each button.
     */
    public void displaySequence() {
        final Iterator<Integer> sequence = Level3.getSequence().iterator();

        TimerTask task = new TimerTask() {  //create TimerTask
            @Override
            public void run() {
                if (sequence.hasNext()) {   // check that the iterator has more items.
                    final int i = sequence.next();
                    {
                        runOnUiThread(new Runnable() { //force task to run on UI Thread
                            @Override
                            public void run() {
                                buttons[i].setVisibility(View.VISIBLE); //Make button Visible
                            }
                        });
                    }

                } else {
                    cancel();
                }
            }
        };
        long period = 1000L;
        long delay = 1000L;
        timer.schedule(task, delay, period);
    }

    /**
     * Sends user input to the Level3Manager. Executes error and win conditional tasks.
     *
     * @param v Any button that is clicked
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        if (v.getId() == buttons[0].getId()) {    //Top left button clicked
            Level3.setUserInput(0);
        }
        if (v.getId() == buttons[1].getId()) {    //Bottom left button clicked
            Level3.setUserInput(1);
        }
        if (v.getId() == buttons[2].getId()) {    //Bottom right button clicked
            Level3.setUserInput(2);
        }
        if (v.getId() == buttons[3].getId()) {    //Top right button clicked
            Level3.setUserInput(3);
        }
        if (Level3.checkError()) {    //User did not input correct sequence
            TextView out = findViewById(R.id.pstat);
            if (Level3.attempts == 3) {    //User made 3 attempts (out of attempts)
                deductHealth(1);  //deduct a life
                out.setText("Incorrect Pattern! You ran out of attempts, -1 lives");
                out.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {    //delay the task by 5 seconds
                    @Override
                    public void run() {
                        if (getHealth() == 0)    //restart game if they run out of lives
                        {
//                  startAgain();
                        }
                        recreate();   //restart level if they still have lives remaining
                    }
                }, 5000); //5000ms = 5 seconds

            } else {
                out.setText("Incorrect Pattern! " + (3 - Level3.attempts) + " remaining!");
                out.setVisibility(View.VISIBLE);
                Level3.clearInput();  //clear input for next attempt
            }
        }
        if (Level3.checkWin()) {  //User successfully inputs correct sequence
            TextView out = findViewById(R.id.pstat);
            out.setText("Correct Pattern!");
            out.setVisibility(View.VISIBLE);
            Level3.clearInput();  //Clear input
        }
    }
}