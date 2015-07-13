package com.app.guide.ui;

import java.sql.SQLException;
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
import com.app.guide.offline.GetBeanFromSql;
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


	@Override
	@SuppressLint("InlinedApi")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_museum);
		// 初始化数据
		initData();
		// 初始化视图
		initViews();
	}

	private void initData() {
		try {
			mData = GetBeanFromSql.getMuseumBeans(this);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initViews() {
		// 初始化头部
		TopBar headerLayout = (TopBar) findViewById(R.id.activity_museum_header);
		headerLayout.setSearchingVisible(false);
		headerLayout.setTitle(getResources().getString(
				R.string.title_activity_museum));
		headerLayout.setTitleShowAtLeft();
		headerLayout.findViewById(R.id.frag_header_iv_menu).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						sm.toggle();
					}
				});
		lvMuseum = (ListView) findViewById(R.id.activity_museum_list);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initData();
		lvMuseum.setAdapter(new CommonAdapter<MuseumBean>(this, mData,
				R.layout.item_museum) {
			@Override
			public void convert(ViewHolder holder, int position) {
				MuseumBean bean = mData.get(position);
				holder.setImageBitmap(R.id.item_museum_iv_icon,
						MuseumActivity.this, bean.getIconUrl())
						.setTvText(R.id.item_museum_tv_name, bean.getName())
						.setTvText(R.id.item_museum_tv_address, bean.getAddress())
						.setTvText(R.id.item_museum_tv_time, bean.getOpentime());
				Button btnOpen = holder.getView(R.id.item_museum_btn_isopen);
				if (bean.isOpen())
					btnOpen.setVisibility(View.GONE);
				else
					btnOpen.setVisibility(View.VISIBLE);
			}
		});
		lvMuseum.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MuseumActivity.this,
						HomeActivity.class);
				((AppContext)getApplication()).currentMuseumId = mData.get(position).getMuseumId();
				startActivity(intent);
			}
		});
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
