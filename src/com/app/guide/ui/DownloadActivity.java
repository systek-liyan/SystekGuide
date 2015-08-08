package com.app.guide.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.app.guide.R;
import com.app.guide.widget.MyToggleButton;
import com.app.guide.widget.MyToggleButton.OnStateChangedListener;

public class DownloadActivity extends BaseActivity {

	private DownloadManageFragment downloadingFragment;
	
	private DownloadListFragment downloadedFragment;
	
	private Fragment mCurrentFragment;
	
	private FragmentManager mFragmentManager;
	
	private MyToggleButton tbTitle;
	
	private ImageView ivBack;
	
	private static final String Bundle_key = "state";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download);
		initViews();
		if (savedInstanceState != null) {
			tbTitle.setCurrentState(savedInstanceState.getInt(Bundle_key));
		}
		
	}

	/**
	 * 初始化视图
	 */
	private void initViews() {
		
		tbTitle = (MyToggleButton) findViewById(R.id.toggle_btn);
		ivBack = (ImageView) findViewById(R.id.iv_back);
		ivBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		tbTitle.setStateChangedListener(new OnStateChangedListener() {
			
			@Override
			public void onSwitchOn() {
				if(mCurrentFragment != null)
					mCurrentFragment.onPause();
				mCurrentFragment = downloadingFragment;
				beginTransaction();
			}
			

			@Override
			public void onSwitchOff() {
				if(mCurrentFragment != null)
					mCurrentFragment.onPause();
				mCurrentFragment = downloadedFragment;
				beginTransaction();
				
			}
		});
		mFragmentManager = getSupportFragmentManager();
		downloadingFragment = new DownloadManageFragment();
		downloadedFragment = new DownloadListFragment();
		if(tbTitle.getCurrentState() == MyToggleButton.STATE_ON)
			mCurrentFragment = downloadingFragment;
		else 
			mCurrentFragment = downloadedFragment;
		beginTransaction();
		
	}
	
	private void beginTransaction() {
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		ft.replace(R.id.download_container, mCurrentFragment);
		ft.commit();
		mCurrentFragment.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(Bundle_key, tbTitle.getCurrentState());
	}

	@Override
	protected boolean isFullScreen() {
		// TODO Auto-generated method stub
		return true;
	}

}
