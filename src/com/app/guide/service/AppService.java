package com.app.guide.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.text.TextUtils;
import android.widget.Toast;

import com.app.guide.AppContext;
import com.app.guide.Constant;
import com.app.guide.download.DownloadClient;

/**
 * AppService,应用程序Service类。用以管理downloadClient，并使下载过程运行在Service中
 * 
 * 注册网络状态广播接收器 TODO 监听网路变化 目前 Bug onReceive 超过10s
 */
public class AppService extends Service {
	
	private static String TAG;

	/** 管理下载客户端，一个博物馆对应一个客户端  */
	public static Map<String, DownloadClient> map;
	
	private static final String ACTION_NETWORK = "android.net.conn.CONNECTIVITY_CHANGE";

	private ConnectivityChangeReceiver mReceiver;

	public AppService() {
		TAG = this.getClass().getSimpleName();
		map = new ConcurrentHashMap<String, DownloadClient>();
		
		// 获取网络链接状态，注意,ConnectivityChangeReceiver必须有显式的默认构造函数
		mReceiver = new ConnectivityChangeReceiver();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		// 注册广播接收器
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_NETWORK);
		this.registerReceiver(mReceiver, filter);

	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		this.unregisterReceiver(mReceiver);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	/** 每个下载博物馆对应一个DownloadClient */
	public static DownloadClient getDownloadClient(Context context,
			String museumId) {
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

	public class ConnectivityChangeReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			
			ConnectivityManager manager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mMobNetInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo mWifiNetInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			
			
			if (!mMobNetInfo.isConnected() && !mWifiNetInfo.isConnected()) {// unconnect
				((AppContext)getApplicationContext()).networkState = Constant.NETWORK_NONE;
				Toast.makeText(context, "无网络连接", Toast.LENGTH_SHORT)
						.show();
			} else {// connect network
				if (mWifiNetInfo.isConnected())// wifi网络环境下
					((AppContext)getApplicationContext()).networkState = Constant.NETWORK_WIFI;
					Toast.makeText(context, "Wi-Fi", Toast.LENGTH_SHORT)
							.show();
				if (!mWifiNetInfo.isConnected())
					((AppContext)getApplicationContext()).networkState = Constant.NETWORK_GPRS;
					Toast.makeText(context, "GPRS", Toast.LENGTH_SHORT)
							.show();

			}
		}
	}
	
	/**
	 * 判断应用程序是否运行在前台
	 * @param context
	 * @return
	 */
	private boolean isRunningForeground(Context context){
		ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);  
	    ComponentName cn = am.getRunningTasks(1).get(0).topActivity;  
	    String currentPackageName = cn.getPackageName();  
	    if(!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(getPackageName()))  
	        return true ;  
	    return false ;  
	}

}
