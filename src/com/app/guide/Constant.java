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
	
	/**
	 * sharedPreferences 相关
	 */
	public static final String SP_DIR = "preferences";
	
	public static final String AUTO_CHECKE_GPS = "auto check gps"; 
	public static final String AUTO_ENTER_MUSEUM = "auto enter museum"; 
	public static final String AUTO_RECEIVE_PIC = "auto receive picture"; 
	public static final String AUTO_UPDATE_IN_WIFI= "auto update in wifi"; 

	/**
	 * 表示离线数据的存放根目录
	 */
	public static final String FLODER_NAME = "Guide/";
	public static final String FLODER = ROOT_SDCARD + "/" + FLODER_NAME;

	/**
	 * 
	 * 获得音频文件下载路径
	 * 
	 * @param url
	 * @param museumId
	 * @return
	 */
	public static String getAudioDownloadPath(String url, String museumId) {
		String fileName = URLEncoder.encode(url);
		return FLODER + museumId + "/audio/" + fileName;
	}

	/**
	 * 
	 * 获得歌词文件下载路径
	 * 
	 * @param url
	 * @param museumId
	 * @return
	 */
	public static String getLrcDownloadPath(String url, String museumId) {
		String fileName = URLEncoder.encode(url);
		return FLODER + museumId + "/lrc/" + fileName;
	}
	
	/**
	 * 
	 * 获得图片文件下载路径
	 * 
	 * @param url
	 * @param museumId
	 * @return
	 */
	public static String getImageDownloadPath(String url, String museumId) {
		String fileName = URLEncoder.encode(url);
		return FLODER + museumId + "/img/" + fileName;
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
