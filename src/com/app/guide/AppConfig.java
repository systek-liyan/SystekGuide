package com.app.guide;

import android.content.Context;

/**
 * 应用程序设置类 以一个单例模式创建c
 * TODO SP 写入   读取 方法
 */

public class AppConfig {

	private volatile static AppConfig singleInstance;

	private AppConfig() {

	}
	
	public static AppConfig getAppConfig(Context context){
		if (singleInstance == null) {
			synchronized (AppConfig.class) {
				if (singleInstance == null) {
					singleInstance = new AppConfig();
				}
			}
		}
		
		return singleInstance;
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
