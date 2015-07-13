package com.app.guide;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.app.guide.service.AppService;
import com.app.guide.ui.BaseActivity;
import com.app.guide.ui.CityActivity;

/**
 * App启动时的跳转页面
 */
public class MainActivity extends BaseActivity {

	private static final int MSG_SPLASH = 0x101;
	
	private static final long ACTIVE_TIME = 1000L;
	
	@SuppressLint("HandlerLeak") 
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_SPLASH:
				Intent intent = new Intent(MainActivity.this, CityActivity.class);
				startActivity(intent);
				finish();
				break;
			}
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//启动系统服务AppService
		Intent serviceIntent = new Intent(MainActivity.this, AppService.class);
		startService(serviceIntent);
		//跳转到城市界面
		changeToCityActivity();
	}

	/**
	 * 启动一个线程，一段时间后发送一条跳转到城市页面的消息。
	 */
	private void changeToCityActivity(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(ACTIVE_TIME);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }finally{
                	mHandler.sendEmptyMessage(MSG_SPLASH);
                }
            }
        }.start();
    }


}
