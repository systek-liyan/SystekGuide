package com.app.guide.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.app.guide.R;

/**
 * 自定义样式ToggleButton，由两个RadioButton组成，分别作为开/关按钮(由一个RadioGroup管理) Created by
 * Yetwish on 2015/8/1.
 */
public class MyToggleButton extends LinearLayout {

	/**
	 * 两种状态
	 */
	public static final int STATE_ON = 1;

	public static final int STATE_OFF = 2;

	/**
	 * 开状态的btn
	 */
	private RadioButton btnOn;

	/**
	 * 关状态的btn
	 */
	private RadioButton btnOff;

	private RadioGroup rgToggle;

	private OnStateChangedListener mListener;

	private int mState;

	public int getCurrentState() {
		return mState;
	}

	public void setCurrentState(int state) {
		if (mState == state)
			return;
		mState = state;
		if (mState == STATE_ON)
			btnOn.setChecked(true);
		else
			btnOff.setChecked(true);
	}

	public void setStateChangedListener(OnStateChangedListener listener) {
		this.mListener = listener;
	}

	public MyToggleButton(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.custom_toggle_btn, this,
				true);

	}

	/**
	 * 设置文本
	 * 
	 * @param textOn
	 * @param textOff
	 */
	public void setText(String textOn, String textOff) {
		this.btnOn.setText(textOn);
		this.btnOff.setText(textOff);
	}

	public void toggle() {
		if (mState == STATE_ON) {
			btnOff.setChecked(true);
			if(mListener != null){
				mListener.onSwitchOff();
	    	}
		} else {
			btnOn.setChecked(true);
			if(mListener != null){
				mListener.onSwitchOn();
	    	}
			
		}
	}

	public MyToggleButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.custom_toggle_btn, this,
				true);
		initViews();
		TypedArray ta = context.obtainStyledAttributes(attrs,
				R.styleable.MyToggleButton);
		String onText = ta.getString(R.styleable.MyToggleButton_onText);
		String offText = ta.getString(R.styleable.MyToggleButton_offText);
		// 如果用户没定义，则默认设为开状态
		mState = ta.getInt(R.styleable.MyToggleButton_defaultState, STATE_ON);
		btnOn.setText(onText);
		btnOff.setText(offText);
		if (mState == STATE_ON)
			btnOn.setChecked(true);
		else
			btnOff.setChecked(true);
		ta.recycle();
	}

	private void initViews() {
		btnOn = (RadioButton) findViewById(R.id.btn_on);
		btnOff = (RadioButton) findViewById(R.id.btn_off);
		rgToggle = (RadioGroup) findViewById(R.id.custom_toggle_btn);
		rgToggle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.btn_on:
					mState = STATE_ON;
					if (mListener != null)
						mListener.onSwitchOn();
					break;
				case R.id.btn_off:
					mState = STATE_OFF;
					if (mListener != null)
						mListener.onSwitchOff();
					break;
				}
			}
		});
	}

	public interface OnStateChangedListener {

		void onSwitchOn();

		void onSwitchOff();

	}

}
