package com.app.guide.ui;

import java.sql.SQLException;
import java.util.List;

import org.altbeacon.beacon.Beacon;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.app.guide.AppContext;
import com.app.guide.R;
import com.app.guide.adapter.MapExhibitAdapter;
import com.app.guide.bean.MapExhibitBean;
import com.app.guide.offline.GetBeanFromSql;
import com.app.guide.ui.HomeActivity.onBeaconSearcherListener;
import com.app.guide.widget.MarkObject;
import com.app.guide.widget.MarkObject.MarkClickListener;
import com.app.guide.widget.MyMap;
import com.app.guide.widget.PopMapMenu;

import edu.xidian.NearestBeacon.NearestBeacon;

public class MapFragment extends Fragment implements onBeaconSearcherListener {

	private MyMap sceneMap;// 地图类
	private ImageView locaImageView;
	private Bitmap bitmap;// 显示地图的Bitmap
	private ListView mListView;// 用于现实展品列表
	private PopMapMenu mPopMapMenu;
	private Button showMenuBtn;
	private MapExhibitAdapter adapter;
	private List<MapExhibitBean> mapExhibitBeans;
	private float personX;
	private float personY;
	private int floorCount;

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.frag_map, null);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {// 初始化视图
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		sceneMap = (MyMap) view.findViewById(R.id.map_sceneMap);
		mListView = (ListView) view.findViewById(R.id.map_list_exhibit);
		try {
			floorCount = GetBeanFromSql.getFloorCount(getActivity(),
					((AppContext)getActivity().getApplication()).currentMuseumId);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		mPopMapMenu = new PopMapMenu(getActivity(), floorCount);
		showMenuBtn = (Button) view.findViewById(R.id.map_btn_showmenu);
		locaImageView = (ImageView) view.findViewById(R.id.map_location);
		sceneMap.setShowMark(false);
		try {
			mapExhibitBeans = GetBeanFromSql.getMapExhibit(getActivity(),
					((AppContext)getActivity().getApplication()).currentMuseumId, 1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		adapter = new MapExhibitAdapter(getActivity(), mapExhibitBeans);
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				MapExhibitBean bean = adapter.getItem(position);
				sceneMap.adjust(bean.getMapX(), bean.getMapY());
				MapDialog dialog = new MapDialog(getActivity(), bean);
				int offsetX = (int) sceneMap.convertToScreenX(bean.getMapX());
				int offsetY = (int) sceneMap.convertToScreenY(bean.getMapY());
				dialog.showAsDropDown(locaImageView, offsetX, offsetY);

			}
		});

		mPopMapMenu.toggleList
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							mListView.setVisibility(View.VISIBLE);

						} else {
							mListView.setVisibility(View.GONE);
						}
						sceneMap.setShowMark(isChecked);
					}
				});
		mPopMapMenu.locationButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sceneMap.setLoactionPosition(personX, personY);
			}
		});
		showMenuBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mPopMapMenu != null) {
					mPopMapMenu.setHeight(v.getHeight());
					mPopMapMenu.showAsDropDown(v, 0, -v.getHeight());
				}
			}
		});

	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		sceneMap.onPuase();
		if (mPopMapMenu != null && mPopMapMenu.isShowing())
			mPopMapMenu.dismiss();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (!((AppContext)getActivity().getApplication()).exhibitsIdList.equals("")) {
			// 获取筛选的exhibits
			String[] ids = ((AppContext)getActivity().getApplication()).exhibitsIdList.split(",");
			for (int i = 0, j = 0; i < ids.length; i++, j++) {
				if (Integer.parseInt(ids[i]) != mapExhibitBeans.get(j).getId()) {
					mapExhibitBeans.remove(j);
					j++;
				}
			}
			adapter.notifyDataSetChanged();
		}
		if (((AppContext)getActivity().getApplication()).isAutoGuide()) {
			HomeActivity.setBeaconLocateType(NearestBeacon.GET_LOCATION_BEACON);
			HomeActivity.setBeaconSearcherListener(this);
		}
		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test);
		sceneMap.setBitmap(bitmap);
		for (final MapExhibitBean bean : mapExhibitBeans) {
			MarkObject object = new MarkObject();
			object.setMapX(bean.getMapX());
			object.setMapY(bean.getMapY());
			object.setMarkListener(new MarkClickListener() {

				@Override
				public void onMarkClick(MarkObject object, int x, int y) {
					// TODO Auto-generated method stub
					bean.getAddress();
				}
			});
			sceneMap.addMark(object);
		}
		sceneMap.setLoactionPosition(personX, personY);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		sceneMap.onDestory();
	}

	@Override
	public void onNearestBeaconDiscovered(int type, Beacon beacon) {
		if (beacon == null)// 不做处理
			return;
		Log.w("BeaconSearcher", beacon.getId2().toString());
		if (beacon.getId2().toString().contains("44")) {
			personX = 0.9f;
			personY = 0.9f;
		} else if (beacon.getId2().toString().contains("47")) {
			personX = 0.4f;
			personY = 0.4f;
		} else if (beacon.getId2().toString().contains("58")) {
			personX = 0.1f;
			personY = 0.1f;
		}
		sceneMap.setLocation(personX, personY);
	}

}
