package com.app.guide.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.altbeacon.beacon.Beacon;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.app.guide.AppContext;
import com.app.guide.Constant;
import com.app.guide.R;
import com.app.guide.adapter.CommonAdapter;
import com.app.guide.adapter.ViewHolder;
import com.app.guide.beanhelper.GetBeanCallBack;
import com.app.guide.beanhelper.GetBeanHelper;
import com.app.guide.model.MapExhibitModel;
import com.app.guide.offline.OfflineMapBean;
import com.app.guide.ui.HomeActivity.onBeaconSearcherListener;
import com.app.guide.widget.MarkObject;
import com.app.guide.widget.MarkObject.MarkClickListener;
import com.app.guide.widget.MyMap;
import com.app.guide.widget.PopMapMenu;

import edu.xidian.NearestBeacon.NearestBeacon;

public class MapFragment extends Fragment implements onBeaconSearcherListener {

	private static final String TAG = MapFragment.class.getSimpleName();
	private MyMap sceneMap;// 地图类
	private ImageView locaImageView;
	private Bitmap bitmap;// 显示地图的Bitmap
	private ListView mListView;// 用于现实展品列表
	private PopMapMenu mPopMapMenu;
	private Button showMenuBtn;
	private CommonAdapter<MapExhibitModel> adapter;
	private List<MapExhibitModel> mapExhibitBeans;

	/**
	 * 人所在地图X坐标,0.5表示在地图的水平中点
	 */
	private float personX;

	/**
	 * 人所在地图Y坐标，0.5表示人在地图垂直中点
	 */
	private float personY;

	/**
	 * 当前博物馆的楼层数
	 */
	private int floorCount;

	/**
	 * 当前选中博物馆
	 */
	private String mMuseumId;

	/**
	 * 表示当前选中楼层
	 */
	private int currentFloor;

	/**
	 * 存储各个楼层的图片路径
	 */
	private Map<Integer, String> mFloorUrls;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// 加载图片
		initData();
	}

	/**
	 * 加载各楼层地图
	 */
	private void initData() {
		currentFloor = 1;
		mMuseumId = ((AppContext) getActivity().getApplication()).currentMuseumId;
		mFloorUrls = new HashMap<Integer, String>();
		// 加载楼层数
		floorCount = ((AppContext) getActivity().getApplication()).floorCount;
		// 加载各个楼层的图片
		for (int i = 0; i < floorCount; i++) {
			final int floor = i + 1;
			GetBeanHelper.getInstance(getActivity()).getMapBean(mMuseumId,
					floor, new GetBeanCallBack<OfflineMapBean>() {

						@Override
						public void onGetBeanResponse(OfflineMapBean response) {
							if (response != null) {

								mFloorUrls.put(floor, response.getImgurl());
							} else {
								// 表示后面没有了，不再加载
								return;
								// TODO 表示没有该地图资源，加载图片显示错误的图片 有一个URL
								// mFloorUrls.put(i+1, "piture_error!");
							}
						}
					});
		}

		GetBeanHelper.getInstance(getActivity()).getMapExhibitList(mMuseumId,
				"", new GetBeanCallBack<List<MapExhibitModel>>() {

					@Override
					public void onGetBeanResponse(List<MapExhibitModel> response) {
						mapExhibitBeans = response;
						initAdapter();
					}
				});

	}

	private void initAdapter() {
		adapter = new CommonAdapter<MapExhibitModel>(getActivity(),
				mapExhibitBeans, R.layout.item_map_exhibit) {

			@Override
			public void convert(ViewHolder holder, int position) {
				holder.setTvText(R.id.item_map_exhibit_name,
						getItem(position).getName()).setTvText(
						R.id.item_map_exhibit_address,
						getItem(position).getAddress());

			}

		};
		if (mListView != null)
			mListView.setAdapter(adapter);
	}

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

		mPopMapMenu = new PopMapMenu(getActivity(), floorCount);
		showMenuBtn = (Button) view.findViewById(R.id.map_btn_showmenu);
		locaImageView = (ImageView) view.findViewById(R.id.map_location);
		sceneMap.setShowMark(false);

		if (adapter != null) 
			mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				MapExhibitModel bean = adapter.getItem(position);
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
		if (!((AppContext) getActivity().getApplication()).exhibitsIds
				.equals("")) {
			// 获取筛选的exhibits
			String[] ids = ((AppContext) getActivity().getApplication()).exhibitsIds
					.split(",");
			for (int i = 0, j = 0; i < ids.length; i++, j++) {
				if (!ids[i].equals(mapExhibitBeans.get(j).getId())) {
					mapExhibitBeans.remove(j);
					j++;
				}
			}
			adapter.notifyDataSetChanged();
		}
		if (((AppContext) getActivity().getApplication()).getGuideMode()) {
			HomeActivity.setBeaconLocateType(NearestBeacon.GET_LOCATION_BEACON);
			HomeActivity.setBeaconSearcherListener(this);
		}
		// 加载当前层数的图片
		bitmap = BitmapFactory.decodeFile(Constant.getImageDownloadPath(
				mFloorUrls.get(currentFloor), mMuseumId));
		sceneMap.setBitmap(bitmap);
		for (final MapExhibitModel bean : mapExhibitBeans) {
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
