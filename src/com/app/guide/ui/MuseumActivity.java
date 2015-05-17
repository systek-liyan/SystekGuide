package com.app.guide.ui;

import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ListView;

import com.app.guide.R;
import com.app.guide.adapter.CommonAdapter;
import com.app.guide.adapter.ViewHolder;
import com.app.guide.bean.Museum;

public class MuseumActivity extends BaseActivity{

	private ListView lvMuseum;
	private List<Museum> mData;
	
	@Override
	@SuppressLint("InlinedApi")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_museum);
		initData();
		initViews();
	}
	
	private void initData(){
		
	}
	
	private void initViews(){
		lvMuseum = (ListView)findViewById(R.id.activity_museum_list);
		lvMuseum.setAdapter(new CommonAdapter<Museum>(this,mData,R.layout.item_museum) {
			@Override
			public void convert(ViewHolder holder, int position) {
				
			}
		});
	}
	
	@Override
	protected boolean isFullScreen() {
		return true;
	}
}
