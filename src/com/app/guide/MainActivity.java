package com.app.guide;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.app.guide.service.AppService;
import com.app.guide.ui.BaseActivity;
import com.app.guide.ui.CityActivity;

public class MainActivity extends BaseActivity {

	private static final int MSG_SPLASH = 0x101;
	
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

		Intent serviceIntent = new Intent(MainActivity.this, AppService.class);
		startService(serviceIntent);
		
		changeToCityActivity();
	}
	
	private void changeToCityActivity(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(800L);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }finally{
                	mHandler.sendEmptyMessage(MSG_SPLASH);
                }
            }
        }.start();
    }


}
