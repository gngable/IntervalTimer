package com.stupidsimple.intervaltimer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.*;
import android.graphics.*;

/**
 * Created by Nick Gable on 5/3/2016.
 */
public class MinutesSecondsPickerDialog extends DialogFragment {
    protected String title = "";

    protected TimerState.State state;

    public void setState(TimerState.State state) {
        this.state = state;
    }

    public TimerState.State getState() {
        return state;
    }

//    public void setCurrent(int current) {
//        this.current = current;
//    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static int getMinutes(int seconds){
        return seconds / 60;
    }

    public static int getSeconds(int seconds){
        return seconds % 60;
    }

    public static int toSeconds(int minutes, int seconds){
        return (minutes * 60) + seconds;
    }

    public interface MinutesSecondsDialogListener {
        public void onMSSetClick(MinutesSecondsPickerDialog dialog);
    }

    MinutesSecondsDialogListener listener;

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);

        listener = (MinutesSecondsDialogListener) activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedState){
        String[] secondnums = new String[60];
        for(int i = 0; i < 10; i++) secondnums[i] = "0" + Integer.toString(i);

        for(int i = 10; i < secondnums.length; i++) secondnums[i] = Integer.toString(i);

        String[] minutenums = new String[60];

        for(int i=0; i < minutenums.length; i++) minutenums[i] = Integer.toString(i);

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View pickerview = inflater.inflate(R.layout.dialog_minutes_seconds_picker, null);

        final NumberPicker minutepicker = (NumberPicker)pickerview.findViewById(R.id.minutes_picker);

        minutepicker.setMinValue(0);
        minutepicker.setMaxValue(59);
        minutepicker.setDisplayedValues(minutenums);
        minutepicker.setValue(getMinutes(TimerState.maxCounts.get(state)));
		
		//minutepicker.addOnLayoutChangeListener(new LayoutChangeListener());
		//for (int i = 0; i < minutepicker.getChildCount(); i++){
			//if (minutepicker.getChildAt(i) instanceof EditText){
				//((EditText) minutepicker.getChildAt(i)).setTextSize(25);
				//((EditText) minutepicker.getChildAt(i)).setTextColor(Color.parseColor("#333333"));
			//}
		//}
		//((TextView)minutepicker.getChildAt(1)).setTextSize(10);

        final NumberPicker secondpicker = (NumberPicker)pickerview.findViewById(R.id.seconds_picker);

        secondpicker.setMinValue(0);
        secondpicker.setMaxValue(59);
        secondpicker.setDisplayedValues(secondnums);
        secondpicker.setValue(getSeconds(TimerState.maxCounts.get(state)));
//        picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
//            @Override
//            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//                TimerState.intervalMax = newVal;
//            }
//        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(title)
                .setView(pickerview)
                .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        TimerState.maxCounts.put(state, toSeconds(minutepicker.getValue(), secondpicker.getValue()));
                        listener.onMSSetClick(MinutesSecondsPickerDialog.this);
                        dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                });

        return builder.create();

    }
}
