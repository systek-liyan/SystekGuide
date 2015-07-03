package com.app.guide.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.widget.Toast;

import com.actionbarsherlock.view.MenuItem;
import com.app.guide.AppConfig;
import com.app.guide.Constant;
import com.app.guide.R;

/**
 * TODO UI 风格
 * @author yetwish
 *
 */
public class SettingActivity extends BaseActivity {

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		getFragmentManager()
				.beginTransaction()
				.replace(R.id.activity_setting_content,
						new MyPreferenceFragment()).commit();
		getActionBar().setHomeButtonEnabled(true);
	}

	@Override
	protected String getTitleStr() {
		return "设置";
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return true;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//将appConfig存储的系统属性存入SP
		saveConfigToSP();
	}
	
	/**
	 * 将appConfig存储的系统属性存入SP
	 */
	private void saveConfigToSP(){
		AppConfig appConfig = AppConfig.getAppConfig(this);
		SharedPreferences sp = getSharedPreferences(Constant.SP_DIR, 0);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(Constant.AUTO_CHECKE_GPS, appConfig.autoCheckGPS);
		editor.putBoolean(Constant.AUTO_ENTER_MUSEUM, appConfig.autoEnterMuseum);
		editor.putBoolean(Constant.AUTO_RECEIVE_PIC, appConfig.autoReceivePic);
		editor.putBoolean(Constant.AUTO_UPDATE_IN_WIFI, appConfig.autoUpdateInWifi);
		//commit
		editor.commit();
	}

	public static class MyPreferenceFragment extends PreferenceFragment {
		
		
		private AppConfig appConfig ; 
		
		private CheckBoxPreference mCbGPS;
		private CheckBoxPreference mCbWifi;
		private CheckBoxPreference mCbAutoEnter;
		private CheckBoxPreference mCbPicture;
		private PreferenceScreen mPsAbout;
		private PreferenceScreen mPsUpdate;
		private PreferenceScreen mPsIntroduction;

		private MyCheckChangedListener mCheckChangedListener;
		private MyClickListener mClickListener;

		@Override
		public void onCreate(final Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.setting_preference);
			appConfig = AppConfig.getAppConfig(getActivity());
			initViews();
			

		}

		private void initViews() {

			mCbGPS = (CheckBoxPreference) findPreference(getResources()
					.getString(R.string.pref_gps));
			mCbAutoEnter = (CheckBoxPreference) findPreference(getResources()
					.getString(R.string.pref_auto_enter));

			mCbWifi = (CheckBoxPreference) findPreference(getResources()
					.getString(R.string.network_wifi));
			mCbPicture = (CheckBoxPreference) findPreference(getResources()
					.getString(R.string.network_picture));

			mPsAbout = (PreferenceScreen) findPreference(getResources()
					.getString(R.string.sys_about));
			mPsIntroduction = (PreferenceScreen) findPreference(getResources()
					.getString(R.string.sys_introduction));
			mPsUpdate = (PreferenceScreen) findPreference(getResources()
					.getString(R.string.sys_update));

			mCheckChangedListener = new MyCheckChangedListener();
			mCbGPS.setOnPreferenceChangeListener(mCheckChangedListener);
			mCbWifi.setOnPreferenceChangeListener(mCheckChangedListener);
			mCbAutoEnter.setOnPreferenceChangeListener(mCheckChangedListener);
			mCbPicture.setOnPreferenceChangeListener(mCheckChangedListener);

			mClickListener = new MyClickListener();
			mPsAbout.setOnPreferenceClickListener(mClickListener);
			mPsIntroduction.setOnPreferenceClickListener(mClickListener);
			mPsUpdate.setOnPreferenceClickListener(mClickListener);
			
			//根据appConfig初始化设置
			mCbGPS.setChecked(appConfig.autoCheckGPS);
			mCbAutoEnter.setChecked(appConfig.autoEnterMuseum);
			mCbPicture.setChecked(appConfig.autoReceivePic);
			mCbWifi.setChecked(appConfig.autoUpdateInWifi);
		}

		private class MyCheckChangedListener implements
				OnPreferenceChangeListener {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				// TODO Auto-generated method stub
				String key = preference.getKey();
				boolean value = (Boolean)newValue;
				if (getResources().getString(R.string.pref_gps).equals(key)) {
					appConfig.autoCheckGPS = value;
				} else if (getResources().getString(R.string.pref_auto_enter)
						.equals(key)) {
					appConfig.autoEnterMuseum = value;
				} else if (getResources().getString(R.string.network_wifi)
						.equals(key)) {
					appConfig.autoUpdateInWifi = value;
				} else if (getResources().getString(R.string.network_picture)
						.equals(key)){
					appConfig.autoReceivePic = value;
				}
					Toast.makeText(getActivity(), key + "," + value,
							Toast.LENGTH_SHORT).show();
				return true;
			}

		}

		private class MyClickListener implements OnPreferenceClickListener {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				// TODO Auto-generated method stub
				String key = preference.getKey();
				StringBuilder sb = new StringBuilder();
				if (getResources().getString(R.string.sys_about).equals(key)) {
					sb.append("关于");
				} else if (getResources().getString(R.string.sys_introduction)
						.equals(key)) {
					sb.append("导游通介绍");
				} else if (getResources().getString(R.string.sys_update)
						.equals(key)) {
					sb.append("检查更新");
				}
				Toast.makeText(getActivity(), sb.toString(), Toast.LENGTH_SHORT)
						.show();
				sb.delete(0, sb.length());
				return true;
			}

		}

	}
}