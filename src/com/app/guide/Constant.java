package com.app.guide;

import java.net.URLEncoder;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;

public class Constant {

	public static final int PAGE_COUNT = 20;
	public static final String EXTRA_MUSEUM_ID = "museumId";

	public static final String EXTRA_EXHIBIT_ID = "exhibitId";

	public static final String ROOT_SDCARD = Environment
			.getExternalStorageDirectory().getAbsolutePath();
	public static final String FLODER = "Guide/Test";

	/**
	 * sharedPreferences 相关
	 */
	public static final String SP_DIR = "preferences";
	
	public static final String AUTO_CHECKE_GPS = "auto check gps"; 
	public static final String AUTO_ENTER_MUSEUM = "auto enter museum"; 
	public static final String AUTO_RECEIVE_PIC = "auto receive picture"; 
	public static final String AUTO_UPDATE_IN_WIFI= "auto update in wifi"; 
	
	
	
	public static String getCachePath(int museumId, String url) {
		return getImgCacheDir(museumId) + "/" + getCacheFilename(url);
	}

	public static String getImgCacheDir(int museumId) {
		return ROOT_SDCARD + "/" + FLODER + museumId + "/img";
	}
	
	public static String getCacheFilename(String url) {
		String name = URLEncoder.encode(url.substring(7));
		name = name.substring(0, name.lastIndexOf("."));
		return name;
	}

	/**
	 * 获得当前应用程序的版本号
	 * 
	 * @param context
	 * @return
	 */
	public static int getAppVersion(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return info.versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 1;
	}
}
