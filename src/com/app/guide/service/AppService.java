package com.app.guide.service;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.app.guide.Constant;
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
		File file = new File(Constant.ROOT_SDCARD + "/Gudie");
		if (!file.exists()) {
			file.mkdir();
		}
		OfflineDownloadHelper downloadHelper = new OfflineDownloadHelper(
				getApplicationContext(), 1);
		try {
			downloadHelper.download();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
