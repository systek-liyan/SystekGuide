package com.app.guide.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.actionbarsherlock.app.ActionBar;
import com.app.guide.AppManager;
import com.app.guide.R;
import com.app.guide.ui.MenuFragment.HomeClick;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class BaseActivity extends SlidingFragmentActivity {

	protected SlidingMenu sm;

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
		initMenu();
	}

	protected void initActionbar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle(getTitleStr());
		actionBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.actionbar_bg));
	}

	private void initMenu() {
		if (isShowMenu()) {
			initSlidingMenu();
		} else {
			setBehindContentView(R.layout.sliding_menu_left);
			getSlidingMenu().setSlidingEnabled(false);
		}
	}

	protected void initSlidingMenu() {
		sm = getSlidingMenu();
		View view = getLayoutInflater().inflate(R.layout.sliding_menu_left,
				null);
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		MenuFragment menuFragment = new MenuFragment();
		menuFragment.setHomeClick(new HomeClick() {

			@Override
			public void home() {
				// TODO Auto-generated method stub
				sm.toggle();
			}
		});
		transaction.replace(R.id.sliding_container, menuFragment);
		transaction.commit();
		setBehindContentView(view);
		sm.setMode(SlidingMenu.LEFT);
		sm.setSlidingEnabled(true);
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setFadeEnabled(true);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeDegree(0.35f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
	}

	protected boolean isShowMenu() {
		return false;
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

	@Override
	public void startActivity(Intent intent) {
		// TODO Auto-generated method stub
		super.startActivity(intent);
		overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
	}

}
