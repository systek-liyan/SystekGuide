package com.app.guide;

import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.view.Menu;
import com.app.guide.service.AppService;
import com.app.guide.ui.BaseActivity;
import com.app.guide.ui.CityActivity;

public class MainActivity extends BaseActivity {

    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Intent serviceIntent = new Intent(MainActivity.this, AppService.class);
        startService(serviceIntent);
        Intent intent = new Intent(MainActivity.this, CityActivity.class);
        startActivity(intent);
    }
    
}
