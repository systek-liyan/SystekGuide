package com.app.guide.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.altbeacon.beacon.Beacon;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.app.guide.AppContext;
import com.app.guide.AppManager;
import com.app.guide.R;
import com.app.guide.AppContext.OnGuideModeChangedListener;
import com.app.guide.adapter.FragmentTabAdapter;
import com.app.guide.adapter.FragmentTabAdapter.OnRgsExtraCheckedChangedListener;
import com.app.guide.widget.DialogManagerHelper;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import edu.xidian.NearestBeacon.BeaconSearcher;
import edu.xidian.NearestBeacon.BeaconSearcher.OnNearestBeaconListener;
import edu.xidian.NearestBeacon.NearestBeacon;

public class HomeActivity extends BaseActivity implements
		OnNearestBeaconListener, OnGuideModeChangedListener {

	protected static RadioGroup mRadioGroup;
	private int pressedCount;
	private Timer timer;
	private List<Fragment> fragments;
	
	/**
	 * 侧滑栏,访问BaseActivity中的sm
	 */
	private static SlidingMenu sm;

	/**
	 * 侧滑栏,访问BaseActivity中的sm
	 * @return the instance of slidingMenu
	 */
	public static SlidingMenu getMenu() {
		return sm;
	}

	private final static Class<?>[] fragmentClz = {
			MuseumIntroduceFragment.class, FollowGuideFragment.class,
			SubjectSelectFragment.class, MapFragment.class };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		// 初始化beacon搜索器
		initBeaconSearcher();
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
		sm = getSlidingMenu();
		if (((AppContext)getApplication()).getGuideMode()) {
			mBeaconSearcher.openSearcher();
		} else {
			((RadioButton) mRadioGroup.findViewById(R.id.home_tab_follow))
					.setEnabled(false);
		}
		// 注册广播监听器
		IntentFilter intentFilter = new IntentFilter();
		intentFilter
				.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
		registerReceiver(mBluetoothReceiver, intentFilter);
		// 添加导游模式切换监听
		((AppContext)getApplication()).addGuideModeChangedListener(this);
	}

	/**
	 * 设置蓝牙监听
	 */
	private final BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {

		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
				switch (mBluetoothAdapter.getState()) {
				case BluetoothAdapter.STATE_ON:
					((AppContext)getApplication()).setBleEnable(true);
					break;
				case BluetoothAdapter.STATE_OFF:
					((AppContext)getApplication()).setBleEnable(false);
					break;
				}
			}
		}
	};

	@Override
	protected boolean isFullScreen() {
		return true;
	}

	@Override
	protected boolean isShowMenu() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected void onResume() {
		if (!((AppContext)getApplication()).getGuideMode() && ((AppContext)getApplication()).currentExhibitId == null) {
			((RadioButton) mRadioGroup.findViewById(R.id.home_tab_follow))
					.setEnabled(false);
		}
		if (((AppContext)getApplication()).isSelectedInSearch) {
			RadioButton btn = (RadioButton) HomeActivity.mRadioGroup
					.findViewById(R.id.home_tab_follow);
			btn.setChecked(true);
			btn.setEnabled(true);
			((AppContext)getApplication()).isSelectedInSearch = false;
		}
		super.onResume();
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

	// @SuppressLint("InflateParams")
	// @Override
	// protected void initSlidingMenu() {
	// // TODO Auto-generated method stub
	// sm = getSlidingMenu();
	// View view = getLayoutInflater().inflate(R.layout.sliding_menu_left,
	// null);
	// FragmentManager manager = getSupportFragmentManager();
	// FragmentTransaction transaction = manager.beginTransaction();
	// MenuFragment menuFragment = new MenuFragment();
	// menuFragment.setHomeClick(new HomeClick() {
	//
	// @Override
	// public void home() {
	// // TODO Auto-generated method stub
	// sm.toggle();
	// }
	// });
	// transaction.replace(R.id.sliding_container, menuFragment);
	// transaction.commit();
	// setBehindContentView(view);
	// sm.setMode(SlidingMenu.LEFT);
	// sm.setSlidingEnabled(true);
	// sm.setShadowWidthRes(R.dimen.shadow_width);
	// sm.setFadeEnabled(true);
	// sm.setShadowDrawable(R.drawable.shadow);
	// sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
	// sm.setFadeDegree(0.35f);
	// sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
	// }

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

	}

	public ActionBar getActivityActionBar() {
		return getSupportActionBar();
	}

	private static onBeaconSearcherListener mBeaconListener;

	/**
	 * 设置beacon搜索器监听接口
	 * 
	 * @param listener
	 */
	public static void setBeaconSearcherListener(
			onBeaconSearcherListener listener) {
		mBeaconListener = listener;
	}

	/**
	 * beacon搜索回调接口，用于给监听者传递nearest beacon数据
	 * 
	 * @author yetwish
	 */
	public interface onBeaconSearcherListener {

		void onNearestBeaconDiscovered(int type, Beacon beacon);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mBeaconSearcher != null) {
			mBeaconSearcher.closeSearcher();
		}
		unregisterReceiver(mBluetoothReceiver);
	}

	/**
	 * store BeaconSearcher instance, we use it to range beacon,and get the
	 * minBeacon from it.
	 */
	private static BeaconSearcher mBeaconSearcher;

	public static void setBeaconLocateType(int type) {
		if (type == NearestBeacon.GET_EXHIBIT_BEACON
				|| type == NearestBeacon.GET_LOCATION_BEACON)
			mBeaconSearcher.setNearestBeaconType(type);
	}

	@Override
	public void getNearestBeacon(int type, Beacon beacon) {
		// TODO Auto-generated method stub
		if (mBeaconListener != null) {
			mBeaconListener.onNearestBeaconDiscovered(type, beacon);
		}
	}

	/**
	 * 初始化beacon 搜索器
	 * 
	 * @param activity
	 */
	private void initBeaconSearcher() {

		mBeaconSearcher = BeaconSearcher.getInstance(this);
		// 设定用于展品定位的最小停留时间(ms)
		mBeaconSearcher.setMin_stay_milliseconds(2000);
		// 设定用于展品定位的最小距离(m)
		mBeaconSearcher.setExhibit_distance(3.0);
		// 设置获取距离最近的beacon类型
		// NearestBeacon.GET_EXHIBIT_BEACON：游客定位beacon。可以不用设置上述的最小停留时间和最小距离
		// NearestBeacon.GET_EXHIBIT_BEACON：展品定位beacon
		mBeaconSearcher.setNearestBeaconType(NearestBeacon.GET_EXHIBIT_BEACON);
		// 设置beacon监听器
		mBeaconSearcher.setNearestBeaconListener(this);
		// 当蓝牙打开时，打开beacon搜索器，开始搜索距离最近的Beacon
		new DialogManagerHelper(this).showBLESettingDialog(mBeaconSearcher);
	}

	@Override
	public void onGuideModeChanged(boolean isAutoGuide) {
		if (mBeaconSearcher == null)
			return;
		if (isAutoGuide) {
			mBeaconSearcher.openSearcher();
			((RadioButton) mRadioGroup.findViewById(R.id.home_tab_follow))
					.setEnabled(true);
		} else {
			mBeaconSearcher.closeSearcher();
			if (((AppContext)getApplication()).currentExhibitId == null)
				((RadioButton) mRadioGroup.findViewById(R.id.home_tab_follow))
						.setEnabled(false);
		}
	}

}
