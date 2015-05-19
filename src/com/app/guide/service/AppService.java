package com.app.guide.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.app.guide.offline.OfflineDownloadHelper;

public class AppService extends IntentService {
	private static final String TAG = AppService.class.getSimpleName();

	public AppService() {
		super(AppService.class.getSimpleName());
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		Log.w(TAG, "start_service");
		OfflineDownloadHelper.downloadExhibit(AppService.this, "test");
	}
}
