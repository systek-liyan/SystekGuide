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

import com.app.guide.R;
import com.app.guide.ui.HomeActivity;
import com.app.guide.ui.SearchActivity;

/**
 * 主界面头部view，使用自定义view,提高代码复用性
 * @author yetwish
 */
public class TopBar extends LinearLayout{

	/**
	 * 菜单imageView 点击弹出侧滑栏
	 */
	private ImageView ivMenu;
	
	/**
	 * 搜索imageView, 点击进入搜索界面
	 */
	private ImageView ivSearch;
	
	/**
	 * 显示标题栏的textView 
	 */
	private TextView tvTitle;
	
	/**
	 * 点击时间监听器，定义点击事件
	 */
	private MyClickListener listener;
	
	/**
	 * 上下文对象
	 */
	private Context mContext;
	
	public TopBar(Context context, AttributeSet attrs){
		super(context,attrs);
		mContext = context;
		LayoutInflater.from(context).inflate(R.layout.frag_header, this);
		initViews();
	}
	
	/**
	 * 初始化各个组件
	 */
	private void initViews(){
		ivMenu = (ImageView) findViewById(R.id.frag_header_iv_menu);
		ivSearch = (ImageView) findViewById(R.id.frag_header_iv_search);
		tvTitle = (TextView) findViewById(R.id.frag_header_tv_title);
		listener = new MyClickListener();
		ivMenu.setOnClickListener(listener);
		ivSearch.setOnClickListener(listener);
	}
	
	/**
	 * 设置标题栏文字
	 * @param title
	 */
	public void setTitle(String title){
		tvTitle.setText(title);
	}
	
	/**
	 * 设置标题栏文字左对齐
	 */
	public void setTitleLeftGravity(){
		tvTitle.setGravity(Gravity.LEFT);
		tvTitle.setGravity(Gravity.CENTER_VERTICAL);
	}
	
	/**
	 * 设置背景资源
	 * @param int, drawable resource
	 */
	public void setHeaderBackground(int res){
		this.setBackgroundResource(res);
	}
	
	
	/**
	 * 设置ivMenu可见性
	 * @param visible : boolean
	 */
	public void setMenuVisible(boolean visible){
		if(visible)
			ivMenu.setVisibility(View.VISIBLE);
		else 
			ivMenu.setVisibility(View.GONE);
	}
	
	/**
	 * 设置ivSearch可见性
	 * @param visible ：boolean
	 */
	public void setSearchingVisible(boolean visible){
		if(visible)
			ivSearch.setVisibility(View.VISIBLE);
		else 
			ivSearch.setVisibility(View.GONE);
	}
	
	/**
	 * 点击事件监听器
	 * @author yetwish
	 *
	 */
	private class MyClickListener implements OnClickListener{
		@Override
		public void onClick(View view) {
			switch(view.getId()){
				case R.id.frag_header_iv_menu:
					HomeActivity.getMenu().toggle();
					break;
				case R.id.frag_header_iv_search:
					Intent intent = new Intent(mContext,SearchActivity.class);
					mContext.startActivity(intent);
					break;
			}
		}
	}
}
