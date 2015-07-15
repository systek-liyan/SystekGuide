package com.app.guide.ui;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.app.guide.AppContext;
import com.app.guide.AppContext.OnBluetoothStateChangedListener;
import com.app.guide.AppContext.OnGuideModeChangedListener;
import com.app.guide.Constant;
import com.app.guide.R;
import com.app.guide.adapter.CommonAdapter;
import com.app.guide.adapter.ViewHolder;
import com.app.guide.model.MenuModel;

public class MenuFragment extends Fragment implements
		OnGuideModeChangedListener, OnBluetoothStateChangedListener {

	private HomeClick homeClick;
	private ListView lvMenu;
	private List<MenuModel> mData;
	private Context mContext;

	private static final int ITEM_CITY = 0;
	private static final int ITEM_DOWNLOAD = 1;
	private static final int ITEM_SETTING = 2;
	private static final int ITEM_MORE = 3;
	private static final int ITEM_BACK = 4;

	private ToggleButton tbAutoGuide;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
		initData();
	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.frag_menu, null);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		tbAutoGuide = (ToggleButton) view
				.findViewById(R.id.frag_menu_tb_autoguide);
		((AppContext)mContext.getApplicationContext()).addGuideModeChangedListener(this);
		((AppContext)mContext.getApplicationContext()).setBleListener(this);
		tbAutoGuide.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					((AppContext)mContext.getApplicationContext()).setGuideMode(Constant.GUIDE_MODE_AUTO);
//					Toast.makeText(mContext, "进入自动导航模式",
//							Toast.LENGTH_SHORT).show();
				} else {
					((AppContext)mContext.getApplicationContext()).setGuideMode(false);
//					Toast.makeText(mContext, "进入手动导航模式",
//							Toast.LENGTH_SHORT).show();
				}
			}
		});
		lvMenu = (ListView) view.findViewById(R.id.frag_menu_lv_menu);
		lvMenu.setAdapter(new CommonAdapter<MenuModel>(this.getActivity(), mData,
				R.layout.item_menu) {
			@Override
			public void convert(ViewHolder holder, int position) {
				TextView textView = (TextView) holder
						.getView(R.id.item_menu_tv_title);
				textView.setText(mData.get(position).getTitle());
				Drawable drawable = getActivity().getResources().getDrawable(
						mData.get(position).getIconResId());
				drawable.setBounds(0, 0, drawable.getMinimumWidth(),
						drawable.getMinimumHeight());
				textView.setCompoundDrawables(drawable, null, null, null);
			}

		});

		lvMenu.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Intent intent = null;
				switch (position) {
				case ITEM_CITY:
					// 返回选择城市
					intent = new Intent(getActivity(), CityActivity.class);
					break;
				case ITEM_DOWNLOAD:
					intent = new Intent(getActivity(),DownloadActivity.class);
					break;
				case ITEM_SETTING:
					intent = new Intent(getActivity(),SettingActivity.class);
					break;
				case ITEM_MORE:
					break;
				case ITEM_BACK:
					// 返回到博物馆
					intent = new Intent(getActivity(), MuseumActivity.class);
					break;
				}
				if (intent != null) {
					startActivity(intent);
				}
			}
		});
	}

	private void initData() {
		String[] titles = { "城市选择", "下载中心", "设置", "更多", "返回" };
		int[] iconResources = { R.drawable.sliding_menu_city,
				R.drawable.sliding_menu_download,
				R.drawable.sliding_menu_setting, R.drawable.sliding_menu_more,
				R.drawable.sliding_menu_back };
		mData = new ArrayList<MenuModel>();
		for (int i = 0; i < titles.length; i++)
			mData.add(new MenuModel(iconResources[i], titles[i]));
	}

	public void setHomeClick(HomeClick homeClick) {
		this.homeClick = homeClick;
	}

	public interface HomeClick {
		public void home();
	}

	@Override
	public void onGuideModeChanged(boolean isAutoGuide) {
		if (tbAutoGuide != null) {
			if (tbAutoGuide.isChecked() != isAutoGuide) {
				// 当传入的状态与当前选中状态不同时，更新UI
				tbAutoGuide.setChecked(isAutoGuide);
			}
		}

	}

	/**
	 * 当蓝牙状态改变时的回调方法
	 */
	@Override
	public void onBluetoothStateChanged(boolean isEnable) {

		if (!isEnable) {
			tbAutoGuide.setChecked(false);
			tbAutoGuide.setEnabled(false);
		} else {
			tbAutoGuide.setEnabled(true);
		}
	}

}
