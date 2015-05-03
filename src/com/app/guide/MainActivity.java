package com.app.guide;

import android.content.Intent;
import android.os.Bundle;

import com.app.guide.ui.BaseActivity;
import com.app.guide.ui.CityActivity;

public class MainActivity extends BaseActivity {

    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Intent intent = new Intent(MainActivity.this, CityActivity.class);
        startActivity(intent);
    }

}
