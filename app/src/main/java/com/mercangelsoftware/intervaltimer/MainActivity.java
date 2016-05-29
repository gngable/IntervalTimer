/*
MIT License

 Copyright (c) 2016 Nick Gable (Mercangel Software)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package com.mercangelsoftware.intervaltimer;

import android.app.Activity;
//import android.net.Uri;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;
import android.content.*;
import android.app.*;

//import com.google.android.gms.appindexing.Action;
//import com.google.android.gms.appindexing.AppIndex;
//import com.google.android.gms.common.api.GoogleApiClient;

//public class MainActivity extends AppCompatActivity {
public class MainActivity extends Activity {
    private TextView statusLabel = null;
    private TextView countLabel = null;
    private TextView intervalLabel = null;
    private Button actionButton = null;
    private static Timer timer = null;

    public static final String prePauseLabel = "prePauseState";
    public static final String currentCountLabel = "currentCount";
    public static final String currentIntervalLabel = "currentInterval";

    protected MediaPlayer startMP = null;
    protected MediaPlayer slowMP = null;
    protected MediaPlayer fastMP = null;
    protected MediaPlayer coolMP = null;
    protected MediaPlayer finishedMP = null;
	
	//public static Context applicationContext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		
		TimerState.readSettings(getApplicationContext());
		
		//applicationContext = getApplicationContext();

        statusLabel = (TextView)findViewById(R.id.status_label);
        countLabel = (TextView)findViewById(R.id.count_label);
        intervalLabel = (TextView)findViewById(R.id.interval_label);
        actionButton = (Button)findViewById(R.id.action_button);

        if (startMP == null) startMP = MediaPlayer.create(getApplicationContext(), R.raw.sound_start_new);
        if (slowMP == null) slowMP = MediaPlayer.create(getApplicationContext(), R.raw.sound_slow_new);
        if (fastMP == null) fastMP = MediaPlayer.create(getApplicationContext(), R.raw.sound_fast_new);
        if (coolMP == null) coolMP = MediaPlayer.create(getApplicationContext(), R.raw.sound_cool_new);
        if (finishedMP == null) finishedMP = MediaPlayer.create(getApplicationContext(), R.raw.sound_finished_new);

        if (savedInstanceState != null) {
            String prestate = savedInstanceState.getString(prePauseLabel);
            int currentcount = savedInstanceState.getInt(currentCountLabel, -1);
            int currentinterval = savedInstanceState.getInt(currentIntervalLabel, -1);

            if (prestate != null && currentcount != -1 && currentinterval != -1) {
                if (timer != null){
                    timer.cancel();
                    timer.purge();
                    timer = null;
                }

                actionButton.setText("Resume");
                TimerState.prePauseState = TimerState.State.valueOf(prestate);
                TimerState.currentState = TimerState.State.PAUSED;
                intervalLabel.setText("Interval " + TimerState.currentInterval + " of " + TimerState.intervalMax);
                countLabel.setText(TimerState.getTimeString(TimerState.maxCounts.get(TimerState.currentState) - TimerState.currentCount));
            }
        } else {
            resetAll();
        }
    }
	
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(prePauseLabel, TimerState.currentState.name());
        outState.putInt(currentCountLabel, TimerState.currentCount);
        outState.putInt(currentIntervalLabel, TimerState.currentInterval);
    }

    public void actionButtonClick(View view){
        if (timer != null){
            timer.cancel();
            timer.purge();
            timer = null;
        }

        switch (TimerState.currentState){
            case FINISHED:
            case NOTSTARTED: {
                actionButton.setText("Pause");
                TimerState.currentState = TimerState.State.WARMUP;
					intervalLabel.setText("Interval 1 of " + TimerState.intervalMax);
                countLabel.setText(TimerState.getTimeString(TimerState.maxCounts.get(TimerState.currentState)));
                playSound(startMP);
                break;
            }

            case PAUSED: {
                actionButton.setText("Pause");
                TimerState.currentState = TimerState.prePauseState;
                break;
            }

            default: {
                actionButton.setText("Resume");
                TimerState.prePauseState = TimerState.currentState;
                TimerState.currentState = TimerState.State.PAUSED;
                break;
            }
        }

        statusLabel.setText(TimerState.labels.get(TimerState.currentState));

        if (TimerState.currentState != TimerState.State.PAUSED) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    doTimerAction();
                }
            }, 1000, 1000);
        }
    }

    public void settingsButtonClick(View view){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
	

    public void nextButtonClick(View view){
		boolean changed = false;
        synchronized (TimerState.stateLock) {
            if (TimerState.currentState == TimerState.State.SLOW || TimerState.currentState == TimerState.State.FAST || TimerState.currentState == TimerState.State.WARMUP || TimerState.currentState == TimerState.State.COOLDOWN) {
                TimerState.currentCount = TimerState.maxCounts.get(TimerState.currentState) + 5;
				changed = true;
            }
        }

        if (changed) doTimerAction();
    }

    public void resetButtonClick(View view){
        resetAll();
    }

    protected void resetAll(){
        if (timer != null){
            timer.cancel();
            timer.purge();
            timer = null;
        }

        actionButton.setText("Start!");
        TimerState.currentState = TimerState.State.NOTSTARTED;
        TimerState.currentInterval = 1;
        TimerState.currentCount = 0;
        intervalLabel.setText("Interval " + TimerState.currentInterval + " of " + TimerState.intervalMax);
        countLabel.setText(TimerState.getTimeString(TimerState.currentCount));
        statusLabel.setText("Ready?");
    }

    public void playSound(final MediaPlayer mp){
        if (mp == null) return;

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                mp.start();
            }
        });

        t.start();
    }

    public void doTimerAction(){
        boolean transitioned = false;
        boolean updatecount = true;

        synchronized (TimerState.stateLock) {
            TimerState.currentCount++;

            if (TimerState.currentCount > TimerState.maxCounts.get(TimerState.currentState) && (TimerState.currentCount != 0 && TimerState.maxCounts.get(TimerState.currentState) != -1)) {
                transitioned = true;
                TimerState.currentCount = 0;

                switch (TimerState.currentState) {
                    case WARMUP: {
                        TimerState.currentState = TimerState.State.FAST;
                        playSound(fastMP);
                        break;
                    }

                    case SLOW: {
                        TimerState.currentState = TimerState.State.FAST;
                        playSound(fastMP);
                        break;
                    }

                    case FAST: {
                        TimerState.currentInterval++;

                        if (TimerState.currentInterval > TimerState.intervalMax) {
                            TimerState.currentState = TimerState.State.COOLDOWN;
                            playSound(coolMP);
                        } else {
                            TimerState.currentState = TimerState.State.SLOW;
                            playSound(slowMP);
                        }

                        break;
                    }

                    case COOLDOWN: {
                        TimerState.currentState = TimerState.State.FINISHED;
                        playSound(finishedMP);
                        break;
                    }

                    default:
                        return;
                }
            }
        }

        if (transitioned){
            statusLabel.post(new Runnable() {
                @Override
                public void run() {
                    statusLabel.setText(TimerState.labels.get(TimerState.currentState));
                }
            });

            if (TimerState.currentState == TimerState.State.SLOW){
                intervalLabel.post(new Runnable() {
                    @Override
                    public void run() {
                        intervalLabel.setText("Interval " + TimerState.currentInterval + " of " + TimerState.intervalMax);
                    }
                });
            } else if (TimerState.currentState == TimerState.State.FINISHED) {
                updatecount = false;
                actionButton.post(new Runnable() {
                    @Override
                    public void run() {
                        resetAll();
                        actionButton.setText("Do it again!");
                    }
                });
            }
        }

        if (updatecount) {
            countLabel.post(new Runnable() {
                @Override
                public void run() {
                    countLabel.setText(TimerState.getTimeString(TimerState.maxCounts.get(TimerState.currentState) - TimerState.currentCount));
                }
            });
        }
    }
}
