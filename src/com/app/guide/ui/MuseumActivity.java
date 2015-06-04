package com.app.guide.ui;

import java.sql.SQLException;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.app.guide.AppContext;
import com.app.guide.Constant;
import com.app.guide.R;
import com.app.guide.adapter.CommonAdapter;
import com.app.guide.adapter.ViewHolder;
import com.app.guide.bean.MuseumBean;
import com.app.guide.offline.GetBeanFromSql;
import com.app.guide.ui.MenuFragment.HomeClick;
import com.app.guide.widget.HeaderLayout;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

/**
 * 修改ListView数据加载方式为数据库
 * 
 * @author joe_c
 * 
 */
public class MuseumActivity extends BaseActivity {

	private ListView lvMuseum;
	private List<MuseumBean> mData;

	private SlidingMenu sm;
	@Override
	@SuppressLint("InlinedApi")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_museum);
		//初始化数据
		initData();
		//初始化视图
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
		//初始化头部
		HeaderLayout headerLayout = (HeaderLayout)findViewById(R.id.activity_museum_header);
		headerLayout.setSearchingVisible(false);
		headerLayout.setTitle(getResources().getString(R.string.title_activity_museum));
		headerLayout.setTitleShowAtLeft();
		headerLayout.findViewById(R.id.frag_header_iv_menu).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sm.toggle();
			}
		});
		lvMuseum = (ListView) findViewById(R.id.activity_museum_list);
		lvMuseum.setAdapter(new CommonAdapter<MuseumBean>(this, mData,
				R.layout.item_museum) {
			@Override
			public void convert(ViewHolder holder, int position) {
				MuseumBean bean = mData.get(position);
				holder.setImageBitmap(R.id.item_museum_iv_icon,
						MuseumActivity.this, bean.getIconUrl())
						.setText(R.id.item_museum_tv_name, bean.getName())
						.setText(R.id.item_museum_tv_address, bean.getAddress())
						.setText(R.id.item_museum_tv_time, bean.getOpentime());
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
				AppContext.currentMuseumId = mData.get(position).getId();
				startActivity(intent);
			}
		});
	}

	@Override
	protected boolean isFullScreen() {
		return true;
	}
	
	@SuppressLint("InflateParams")
	@Override
	protected void initSlidingMenu() {
		// TODO Auto-generated method stub
		sm = getSlidingMenu();
		View view = getLayoutInflater().inflate(R.layout.sliding_menu_left,
				null);
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		MenuFragment menuFragment = new MenuFragment();
		menuFragment.setHomeClick(new HomeClick() {

			@Override
			public void home() {
				// TODO Auto-generated method stub
				sm.toggle();
			}
		});
		transaction.replace(R.id.sliding_container, menuFragment);
		transaction.commit();
		setBehindContentView(view);
		sm.setMode(SlidingMenu.LEFT);
		sm.setSlidingEnabled(true);
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setFadeEnabled(true);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeDegree(0.35f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
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
