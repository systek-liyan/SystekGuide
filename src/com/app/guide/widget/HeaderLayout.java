package com.app.guide.widget;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.guide.Constant;
import com.app.guide.R;
import com.app.guide.ui.HomeActivity;
import com.app.guide.ui.SearchActivity;

/**
 * fragment 头部view，定义setting button的点击事件，提高代码复用性
 * @author yetwish
 */
public class HeaderLayout extends LinearLayout{

	private ImageView ivMenu;
	private ImageView ivSearch;
	private TextView tvTitle;
	private MyClickListener listener;
	private Context mContext;
	public HeaderLayout(Context context, AttributeSet attrs){
		super(context,attrs);
		mContext = context;
		LayoutInflater.from(context).inflate(R.layout.frag_header, this);
		initViews();
	}
	
	private void initViews(){
		ivMenu = (ImageView) findViewById(R.id.frag_header_iv_menu);
		ivSearch = (ImageView) findViewById(R.id.frag_header_iv_search);
		tvTitle = (TextView) findViewById(R.id.frag_header_tv_title);
		listener = new MyClickListener();
		ivMenu.setOnClickListener(listener);
		ivSearch.setOnClickListener(listener);
	}
	
	/**
	 * 设置头部title
	 * @param title
	 */
	public void setTitle(String title){
		tvTitle.setText(title);
	}
	
	/**
	 * 设置头部title靠左显示
	 */
	public void setTitleShowAtLeft(){
		tvTitle.setGravity(Gravity.LEFT);
		tvTitle.setGravity(Gravity.CENTER_VERTICAL);
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
			ivMenu.setVisibility(View.VISIBLE);
		else 
			ivMenu.setVisibility(View.GONE);
	}
	
	/**
	 * 设置search按钮是否可见
	 * @param visible
	 */
	public void setSearchingVisible(boolean visible){
		if(visible)
			ivSearch.setVisibility(View.VISIBLE);
		else 
			ivSearch.setVisibility(View.GONE);
	}
	
	private class MyClickListener implements OnClickListener{
		@Override
		public void onClick(View view) {
			switch(view.getId()){
				case R.id.frag_header_iv_menu:
					HomeActivity.getMenu().toggle();
					break;
				case R.id.frag_header_iv_search:
					Intent intent = new Intent(mContext,SearchActivity.class);
					intent.putExtra(Constant.EXTRA_MUSEUM_ID, 1);
					mContext.startActivity(intent);
					break;
			}
		}
	}
}
