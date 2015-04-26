package com.yetwish.tourismguide.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yetwish.tourismguide.R;

/**
 * fragment 头部view，定义setting button的点击事件，提高代码复用性
 * @author yetwish
 */
public class HeaderLayout extends LinearLayout{

	private ImageButton ibSetting;
	private ImageButton ibSearching;
	private TextView tvTitle;
	private MyClickListener listener;
	public HeaderLayout(Context context, AttributeSet attrs){
		super(context,attrs);
		LayoutInflater.from(context).inflate(R.layout.fragment_header, this);
		initViews();
	}
	
	private void initViews(){
		ibSetting = (ImageButton) findViewById(R.id.fragment_header_ib_setting);
		ibSearching = (ImageButton) findViewById(R.id.fragment_header_ib_search);
		tvTitle = (TextView) findViewById(R.id.fragment_header_tv_title);
		listener = new MyClickListener();
		ibSetting.setOnClickListener(listener);
	}
	
	/**
	 * 设置头部title
	 * @param title
	 */
	public void setTitle(String title){
		tvTitle.setText(title);
	}
	
	/**
	 * 设置头部背景
	 * @param int, drawable resource
	 */
	public void setHeaderBackground(int res){
		this.setBackgroundResource(res);
	}
	
	/**
	 * 设置setting按钮是否可见
	 * @param visible
	 */
	public void setSettingVisible(boolean visible){
		if(visible)
			ibSetting.setVisibility(View.VISIBLE);
		else 
			ibSetting.setVisibility(View.GONE);
	}
	
	/**
	 * 设置search按钮是否可见
	 * @param visible
	 */
	public void setSearchingVisible(boolean visible){
		if(visible)
			ibSearching.setVisibility(View.VISIBLE);
		else 
			ibSearching.setVisibility(View.GONE);
	}
	
	private class MyClickListener implements OnClickListener{
		@Override
		public void onClick(View view) {
			switch(view.getId()){
				case R.id.fragment_header_ib_setting:
					break;
				case R.id.fragment_header_ib_search:
					break;
			}
		}
	}
}
