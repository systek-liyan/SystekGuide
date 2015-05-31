package com.app.guide.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.app.guide.AppManager;
import com.app.guide.R;
import com.app.guide.adapter.FragmentTabAdapter;
import com.app.guide.adapter.FragmentTabAdapter.OnRgsExtraCheckedChangedListener;
import com.app.guide.ui.MenuFragment.HomeClick;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import edu.xidian.NearestBeacon.BeaconSearcher;

public class HomeActivity extends BaseActivity {

	protected static RadioGroup mRadioGroup;
	private int pressedCount;
	private Timer timer;
	private List<Fragment> fragments;

	protected static SlidingMenu sm;

	private final static Class<?>[] fragmentClz = {
			MuseumIntroduceFragment.class, FollowGuideFragment.class,
			SubjectSelectFragment.class, MapFragment.class };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		timer = new Timer();
		fragments = new ArrayList<Fragment>();
		for (int i = 0; i < fragmentClz.length; i++) {
			try {
				Class<?> clazz = Class.forName(fragmentClz[i].getName());
				fragments.add((Fragment) clazz.newInstance());
			} catch (ClassNotFoundException e) {
				// TODO: handle exception
			} catch (InstantiationException e) {
				// TODO: handle exception
			} catch (IllegalAccessException e) {
				// TODO: handle exception
			}
		}
		mRadioGroup = (RadioGroup) findViewById(R.id.home_tab_group);
		FragmentTabAdapter adapter = new FragmentTabAdapter(this, fragments,
				R.id.home_realtabcontent, mRadioGroup);
		adapter.setOnRgsExtraCheckedChangedListener(new OnRgsExtraCheckedChangedListener() {

			@Override
			public void OnRgsExtraCheckedChanged(RadioGroup radioGroup,
					int checkedId, int index) {
				// TODO Auto-generated method stub
				sm.setSlidingEnabled(true);
				switch (index) {
				case 3:
					sm.setSlidingEnabled(false);
					break;
				case 2:
					break;
				default:

					break;
				}
			}

		});
	}

	@Override
	protected boolean isFullScreen() {
		return true;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (sm.isMenuShowing()) {
			sm.toggle();
			return;
		}
		if (pressedCount == 1) {
			AppManager.getAppManager().appExit(HomeActivity.this);
		} else {
			pressedCount += 1;
			Toast.makeText(HomeActivity.this, "再次点击退出程序", Toast.LENGTH_SHORT)
					.show();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					pressedCount = 0;
				}
			}, 2000);
		}
	}

	@Override
	protected void initSlidingMenu() {
		// TODO Auto-generated method stub
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
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		if (getSlidingMenu().isMenuShowing()) {
			getSlidingMenu().toggle();
		}
		super.onPause();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == BeaconSearcher.REQUEST_ENABLE_BT){
			FollowGuideFragment followGuideFragment = (FollowGuideFragment) fragments
					.get(1);
			followGuideFragment.onBluetoothResult(requestCode, resultCode);
		}
	}

	public ActionBar getActivityActionBar() {
		return getSupportActionBar();
	}

}
