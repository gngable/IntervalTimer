package com.stupidsimple.intervaltimer;
import android.content.*;
import android.graphics.*;
import android.util.*;
import android.view.*;
import android.widget.*;

public class NumberPicker extends android.widget.NumberPicker {

	public NumberPicker(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void addView(View child) {
		super.addView(child);
		updateView(child);
	}

	@Override
	public void addView(View child, int index, android.view.ViewGroup.LayoutParams params) {
		super.addView(child, index, params);
		updateView(child);
	}

	@Override
	public void addView(View child, android.view.ViewGroup.LayoutParams params) {
		super.addView(child, params);
		updateView(child);
	}

	private void updateView(View view) {
		if(view instanceof EditText){
			((EditText) view).setTextSize(33);
			((EditText) view).setTextColor(Color.parseColor("#333333"));
		}
	}

}
