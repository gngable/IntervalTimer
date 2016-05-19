/*
 MIT License
 
 Copyright (c) 2016 Nick Gable (Servant Software)

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

package com.servantsoftware.intervaltimer;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

/**
 * Created by Nick Gable on 5/2/2016.
 */
public class SettingsActivity extends Activity implements SinglePickerDialog.SinglePickerDialogListener, MinutesSecondsPickerDialog.MinutesSecondsDialogListener {


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        String[] nums = new String[100];
        for(int i=0; i<nums.length; i++) nums[i] = Integer.toString(i + 1);

        TextView tv = (TextView)findViewById(R.id.intervals_label);

        tv.setText("Intervals: " + TimerState.intervalMax);

        tv = (TextView)findViewById(R.id.warmup_label);

        tv.setText("Warm up: " + TimerState.getTimeString(TimerState.maxCounts.get(TimerState.State.WARMUP)));

        tv = (TextView)findViewById(R.id.slow_label);

        tv.setText("Slow: " + TimerState.getTimeString(TimerState.maxCounts.get(TimerState.State.SLOW)));

        tv = (TextView)findViewById(R.id.fast_label);

        tv.setText("Fast: " + TimerState.getTimeString(TimerState.maxCounts.get(TimerState.State.FAST)));

        tv = (TextView)findViewById(R.id.cool_label);

        tv.setText("Cool Down: " + TimerState.getTimeString(TimerState.maxCounts.get(TimerState.State.COOLDOWN)));
    }

    public void actionButtonClick(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void editIntervalsButtonClick(View view){
        SinglePickerDialog spd = new SinglePickerDialog();
        spd.setTitle("How many intervals?");
        spd.setCurrent(TimerState.intervalMax);
        spd.show(getFragmentManager(), "SinglePickerDialog");
    }

    public void editWarmUpButtonClick(View view){
        MinutesSecondsPickerDialog mspd = new MinutesSecondsPickerDialog();
        mspd.setState(TimerState.State.WARMUP);
        mspd.setTitle("How long for warm up?");
        mspd.show(getFragmentManager(), "WarmUpDialog");
    }

    public void editSlowButtonClick(View view){
        MinutesSecondsPickerDialog mspd = new MinutesSecondsPickerDialog();
        mspd.setState(TimerState.State.SLOW);
        mspd.setTitle("How long for slow?");
        mspd.show(getFragmentManager(), "SlowDialog");
    }

    public void editFastButtonClick(View view){
        MinutesSecondsPickerDialog mspd = new MinutesSecondsPickerDialog();
        mspd.setState(TimerState.State.FAST);
        mspd.setTitle("How long for fast?");
        mspd.show(getFragmentManager(), "FastDialog");
    }

    public void editCoolDownButtonClick(View view){
        MinutesSecondsPickerDialog mspd = new MinutesSecondsPickerDialog();
        mspd.setState(TimerState.State.COOLDOWN);
        mspd.setTitle("How long for cool down?");
        mspd.show(getFragmentManager(), "CoolDownDialog");
    }

    @Override
    public void onSetClick(DialogFragment dialog) {
        TextView tv = (TextView)findViewById(R.id.intervals_label);
        tv.setText("Intervals: " + TimerState.intervalMax);
    }

    public void onMSSetClick(MinutesSecondsPickerDialog dialog) {
        switch (dialog.getState()){
            case WARMUP: {
                TextView tv = (TextView)findViewById(R.id.warmup_label);
                tv.setText("Warm up: " + TimerState.getTimeString(TimerState.maxCounts.get(TimerState.State.WARMUP)));
                break;
            }

            case SLOW: {
                TextView tv = (TextView)findViewById(R.id.slow_label);
                tv.setText("Slow: " + TimerState.getTimeString(TimerState.maxCounts.get(TimerState.State.SLOW)));
                break;
            }

            case FAST: {
                TextView tv = (TextView)findViewById(R.id.fast_label);
                tv.setText("Fast: " + TimerState.getTimeString(TimerState.maxCounts.get(TimerState.State.FAST)));
                break;
            }

            case COOLDOWN: {
                TextView tv = (TextView)findViewById(R.id.cool_label);
                tv.setText("Cool Down: " + TimerState.getTimeString(TimerState.maxCounts.get(TimerState.State.COOLDOWN)));
                break;
            }
        }
    }
}
