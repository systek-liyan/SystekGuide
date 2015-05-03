package com.app.guide;

import android.content.Context;

/**
 * 应用程序设置类 以一个单例模式创建c
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

}
