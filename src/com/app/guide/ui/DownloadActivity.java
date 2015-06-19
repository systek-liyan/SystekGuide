package com.app.guide.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.widget.TabHost.TabSpec;

import com.app.guide.R;

public class DownloadActivity extends BaseActivity {

	private FragmentTabHost mTabHost;
	private static final Class[] fragments = { DownloadingFragment.class,
			DownloadCompletedFragment.class };
	private static final int[] title = { R.string.download_text_ing,
			R.string.download_text_completed };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download);
		initViews();
		if (savedInstanceState != null) {
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
		}
	}

	private void initViews() {
		mTabHost = (FragmentTabHost) findViewById(R.id.download_tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.tab_content);
		for (int i = 0; i < fragments.length; i++) {
			// 为每一个Tab按钮设置图标、文字和内容
			TabSpec tabSpec = mTabHost.newTabSpec(getString(title[i]))
					.setIndicator(getString(title[i]));
			// 将Tab按钮添加进Tab选项卡中
			mTabHost.addTab(tabSpec, fragments[i], null);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("tab", mTabHost.getCurrentTabTag());
	}

	@Override
	protected boolean isFullScreen() {
		// TODO Auto-generated method stub
		return true;
	}

}
