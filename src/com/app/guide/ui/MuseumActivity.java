package com.app.guide.ui;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.app.guide.AppContext;
import com.app.guide.R;
import com.app.guide.adapter.CommonAdapter;
import com.app.guide.adapter.ViewHolder;
import com.app.guide.bean.MuseumBean;
import com.app.guide.beanhelper.GetBeanCallBack;
import com.app.guide.beanhelper.GetBeanHelper;
import com.app.guide.widget.TopBar;

/**
 * 修改ListView数据加载方式为数据库
 * 
 * @author joe_c
 * 
 */
public class MuseumActivity extends BaseActivity {

	private ListView lvMuseum;
	private List<MuseumBean> mData;

	/**
	 * 当前博物馆所在城市
	 */
	private String city;

	@Override
	@SuppressLint("InlinedApi")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_museum);
		// 初始化视图
		initViews();
		// 初始化数据
		initData();
	}

	private void initData() {
		city = getIntent().getStringExtra("city");
		GetBeanHelper.getInstance(this).getMuseumList(city,
				new GetBeanCallBack<List<MuseumBean>>() {

					@Override
					public void onGetBeanResponse(List<MuseumBean> response) {
						mData = response;
						initAdapter();
					}

				});
	}

	private void initAdapter() {
		lvMuseum.setAdapter(new CommonAdapter<MuseumBean>(this, mData,
				R.layout.item_museum) {
			@Override
			public void convert(ViewHolder holder, int position) {
				MuseumBean bean = mData.get(position);
				holder.setImageBitmap(R.id.item_museum_iv_icon,
						MuseumActivity.this, bean.getIconUrl())
						.setTvText(R.id.item_museum_tv_name, bean.getName())
						.setTvText(R.id.item_museum_tv_address,
								bean.getAddress())
						.setTvText(R.id.item_museum_tv_time, bean.getOpentime());
				Button btnOpen = holder.getView(R.id.item_museum_btn_isopen);
				if (bean.isOpen())
					btnOpen.setVisibility(View.GONE);
				else
					btnOpen.setVisibility(View.VISIBLE);
			}
		});
	}

	private void initViews() {
		// 初始化头部
		TopBar headerLayout = (TopBar) findViewById(R.id.activity_museum_header);
		headerLayout.setSearchingVisible(false);
		headerLayout.setTitle(getResources().getString(
				R.string.title_activity_museum));
		headerLayout.setTitleLeftGravity();
		headerLayout.findViewById(R.id.frag_header_iv_menu).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						sm.toggle();
					}
				});
		lvMuseum = (ListView) findViewById(R.id.activity_museum_list);
		lvMuseum.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MuseumActivity.this,
						HomeActivity.class);
				((AppContext) getApplication()).currentMuseumId = mData.get(
						position).getMuseumId();
				((AppContext) getApplication()).hasOffline = true;
				startActivity(intent);
			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected boolean isFullScreen() {
		return true;
	}

	@Override
	protected boolean isShowMenu() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		if (getSlidingMenu().isMenuShowing()) {
			getSlidingMenu().toggle();
		}
		super.onPause();
	}

}
