package com.app.guide.ui;

import java.sql.SQLException;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.app.guide.R;
import com.app.guide.adapter.CommonAdapter;
import com.app.guide.adapter.ViewHolder;
import com.app.guide.bean.MuseumBean;
import com.app.guide.offline.GetBeanFromSql;

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
		initData();
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
				startActivity(intent);
			}
		});
	}

	@Override
	protected boolean isFullScreen() {
		return true;
	}
}
