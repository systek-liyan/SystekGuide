package com.app.guide.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.app.guide.R;

public class SearchActivity extends BaseActivity{

	private ListView lvResults;
	private Button btnBack;
	
	@Override
	@SuppressLint("InlinedApi")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		initViews();
	}
	
	private void initViews(){
		btnBack = (Button)findViewById(R.id.search_header_btn);
		btnBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}
	
	@Override
	protected boolean isFullScreen() {
		return true;
	}
}
