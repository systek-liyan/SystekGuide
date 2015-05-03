package com.app.guide.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.app.guide.AppManager;
import com.app.guide.R;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class BaseActivity extends SlidingFragmentActivity {

	@SuppressLint("InlinedApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 添加Activity到堆栈
		AppManager.getAppManager().addActivity(this);
		if (!isFullScreen()) {
			initActionbar();
		} else {
			setTheme(android.R.style.Theme_Holo_Light_NoActionBar);
		}
		initSlidingMenu();
	}

	protected void initActionbar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle(getTitleStr());
		actionBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.actionbar_bg));
	}
	
	protected void initSlidingMenu() {
		setBehindContentView(R.layout.sliding_menu_left);
		getSlidingMenu().setSlidingEnabled(false);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 结束Activity&从堆栈中移除
		AppManager.getAppManager().finishActivity(this);
	}

	protected boolean isFullScreen() {
		return false;
	}

	protected String getTitleStr() {
		return "GuideApp";
	}

}
