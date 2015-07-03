package com.app.guide.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.app.guide.download.DownloadClient;

public class AppService extends Service {
	private static final String TAG = AppService.class.getSimpleName();

	public static Map<String, DownloadClient> map;

	public AppService() {
		map = new ConcurrentHashMap<String, DownloadClient>();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public static DownloadClient getDownloadClient(Context context, String museumId) {
		if (map == null) {
			map = new ConcurrentHashMap<String, DownloadClient>();
		}
		DownloadClient client = map.get(museumId);
		if (client == null) {
			client = new DownloadClient(context, museumId);
			map.put(museumId, client);
		}
		return client;
	}

	public static void remove(String museumId) {
		map.remove(museumId);
	}

}
