package com.app.guide;

import java.net.URLEncoder;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;

/**
 * 系统常量类，用以存储系统所有常量 提供音频文件、歌词文件和图片文件的存储路径
 */
public class Constant {

	/**
	 * 每页显示数据项数，用于ListView
	 */
	public static final int PAGE_COUNT = 20;
	public static final String ROOT_SDCARD = Environment
			.getExternalStorageDirectory().getAbsolutePath();

	/**
	 * sharedPreferences 相关
	 */
	public static final String SP_DIR = "preferences";

	public static final String AUTO_CHECKE_GPS = "auto check gps";
	public static final String AUTO_ENTER_MUSEUM = "auto enter museum";
	public static final String AUTO_RECEIVE_PIC = "auto receive picture";
	public static final String AUTO_UPDATE_IN_WIFI = "auto update in wifi";

	/**
	 * 常量,自动模式
	 */
	public static final boolean GUIDE_MODE_AUTO = true;
	
	/**
	 * 常量，手动模式
	 */
	public static final boolean GUIDE_MODE_MANUALLY =  false;
	
	/**
	 * 网络状态常量  无网络状态
	 */
	public static final int NETWORK_NONE = 0x100;
	
	/**
	 * 网络状态常量  当前网络为WIFI
	 */
	public static final int NETWORK_WIFI = 0x101;
	
	/**
	 * 网络状态常量  当前网络为GPRS
	 */
	public static final int NETWORK_GPRS = 0x102;
	
	/**
	 * 表示离线数据的存放根目录
	 */
	public static final String FLODER_NAME = "Guide/";
	public static final String FLODER = ROOT_SDCARD + "/" + FLODER_NAME;

	public static final String HOST_HEAD = "http://182.92.82.70";
	//public static final String HOST_HEAD = "http://192.168.1.115";

	/**
	 * 获取合法的下载地址<br>
	 * 判断传入url是否是一个下载合法地址，如果只是一个相对路径，则应该在头部加上一个地址头<br>
	 * @param url
	 * @return 合法的下载地址
	 */
	public static String getURL(String url) {
		if (!url.startsWith("http"))
			url = Constant.HOST_HEAD + url;
		return url;
	}


	/**
	 * 
	 * 获得音频文件下载路径
	 * 
	 * @param url
	 * @param museumId
	 * @return
	 */
	public static String getAudioDownloadPath(String url, String museumId) {
		String fileName = URLEncoder.encode(getURL(url));
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
		String fileName = URLEncoder.encode(getURL(url));
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
		String fileName = URLEncoder.encode(getURL(url));
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
