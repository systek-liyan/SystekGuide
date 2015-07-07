package com.app.guide;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 应用程序设置类 以一个单例模式创建
 */

public class AppConfig {

	private volatile static AppConfig singleInstance;

	private Context mContext;
	
	private AppConfig(Context context) {
		//保存App的context
		mContext = context.getApplicationContext();
	}
	
	/**
	 * 获取AppConfig唯一实例
	 */
	public static AppConfig getAppConfig(Context context){
		if (singleInstance == null) {
			synchronized (AppConfig.class) {
				if (singleInstance == null) {
					singleInstance = new AppConfig(context);
				}
			}
		}
		
		return singleInstance;
	}
	
	/**
	 * 通过SharedPreferences 保存 配置参数 到本地
	 */
	public void saveConfigBySP(){
		SharedPreferences sp = mContext.getSharedPreferences(Constant.SP_DIR, 0);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(Constant.AUTO_CHECKE_GPS, this.autoCheckGPS);
		editor.putBoolean(Constant.AUTO_ENTER_MUSEUM, this.autoEnterMuseum);
		editor.putBoolean(Constant.AUTO_RECEIVE_PIC, this.autoReceivePic);
		editor.putBoolean(Constant.AUTO_UPDATE_IN_WIFI, this.autoUpdateInWifi);
		editor.commit();
		
	}
	
	/**
	 * 通过SharedPreferences 读取本地配置参数
	 */
	public void loadConfigBySP(){
		SharedPreferences sp = mContext.getSharedPreferences(Constant.SP_DIR,0);
		this.autoCheckGPS = sp.getBoolean(Constant.AUTO_CHECKE_GPS, true);
		this.autoEnterMuseum = sp.getBoolean(Constant.AUTO_ENTER_MUSEUM, true);
		this.autoReceivePic = sp.getBoolean(Constant.AUTO_RECEIVE_PIC, false);
		this.autoUpdateInWifi = sp.getBoolean(Constant.AUTO_UPDATE_IN_WIFI, true);
	}
	
	/**
	 * 自动检查GPS
	 */
	public boolean autoCheckGPS= true;
	
	/**
	 * 自动进入已经定位到的博物馆
	 */
	public boolean autoEnterMuseum = true;
	
	
	/**
	 * 在非WIFI网络下自动接收图片
	 */
	public boolean autoReceivePic = false;
	
	/**
	 * 在WIFI网络下自动更新
	 */
	public boolean autoUpdateInWifi = false;
	
	
}
