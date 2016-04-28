package com.rong.xposed.headsoff.widget;

import android.content.Context;
import android.preference.SwitchPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * HeadsOff
 * Created by Rong on 2016/3/19.
 */
public class SwitchPreferenceMultiLine extends SwitchPreference {

	public SwitchPreferenceMultiLine(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	public SwitchPreferenceMultiLine(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public SwitchPreferenceMultiLine(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SwitchPreferenceMultiLine(Context context) {
		super(context);
	}

	@Override
	protected void onBindView(View view) {
		super.onBindView(view);

		TextView summary = (TextView) view.findViewById(android.R.id.summary);
		summary.setMaxLines(3);
	}
}
