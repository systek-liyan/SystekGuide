package com.app.guide.ui;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.app.guide.R;
import com.app.guide.adapter.CommonAdapter;
import com.app.guide.adapter.ViewHolder;
import com.app.guide.bean.MuseumBean;

public class MuseumActivity extends BaseActivity{

	private ListView lvMuseum;
	private List<MuseumBean> mData;
	
	@Override
	@SuppressLint("InlinedApi")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_museum);
		initData();
		initViews();
	}
	
	private void initData(){
		mData = new ArrayList<MuseumBean>();
		MuseumBean museum = new MuseumBean();
		museum.setName("北京博物馆");
		museum.setAddress("北二环路222号");
		museum.setOpentime("8:00-17:00");
		museum.setOpen(true);
		mData.add(museum);
		museum = new MuseumBean();
		museum.setName("故宫博物馆");
		museum.setAddress("天安门后方200m");
		museum.setOpentime("7:00-18:00");
		museum.setOpen(false);
		mData.add(museum);
	}
	
	private void initViews(){
		lvMuseum = (ListView)findViewById(R.id.activity_museum_list);
		lvMuseum.setAdapter(new CommonAdapter<MuseumBean>(this,mData,R.layout.item_museum) {
			@Override
			public void convert(ViewHolder holder, int position) {
				holder.setImageResource(R.id.item_museum_iv_icon,R.drawable.icon)
					.setText(R.id.item_museum_tv_name, mData.get(position).getName())
					.setText(R.id.item_museum_tv_address, mData.get(position).getAddress())
					.setText(R.id.item_museum_tv_time, mData.get(position).getOpentime());
				Button btnOpen = holder.getView(R.id.item_museum_btn_isopen);
				if(mData.get(position).isOpen())
					btnOpen.setVisibility(View.GONE);
				else btnOpen.setVisibility(View.VISIBLE);
			}
		});
	}
	
	@Override
	protected boolean isFullScreen() {
		return true;
	}
}
