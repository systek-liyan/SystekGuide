package com.app.guide.ui;

import java.sql.SQLException;
import java.util.List;

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
import android.widget.ToggleButton;

import com.app.guide.R;
import com.app.guide.adapter.MapExhibitAdapter;
import com.app.guide.bean.MapExhibitBean;
import com.app.guide.offline.GetBeanFromSql;
import com.app.guide.widget.MarkObject;
import com.app.guide.widget.MarkObject.MarkClickListener;
import com.app.guide.widget.MyMap;

public class MapFragment extends Fragment {

	private MyMap sceneMap;
	private ImageView locaImageView;
	private Bitmap bitmap;
	private ListView mListView;
	private ToggleButton mToggleButton;
	private Button loactionButton;
	private MapExhibitAdapter adapter;
	private List<MapExhibitBean> mapExhibitBeans;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.frag_map, null);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		sceneMap = (MyMap) view.findViewById(R.id.map_sceneMap);
		mListView = (ListView) view.findViewById(R.id.map_list_exhibit);
		mToggleButton = (ToggleButton) view.findViewById(R.id.map_tog_list);
		loactionButton = (Button) view.findViewById(R.id.map_btn_location);
		locaImageView = (ImageView) view.findViewById(R.id.map_location);

		sceneMap.setShowMark(false);
		try {
			mapExhibitBeans = GetBeanFromSql.getMapExhibit(getActivity(),
					"test", 1);
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

		mToggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

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
		loactionButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sceneMap.setLoactionPosition(0.5f, 0.5f);
			}
		});

	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		sceneMap.onPuase();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
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
		sceneMap.setLoactionPosition(0.5f, 0.5f);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		sceneMap.onDestory();
	}

}
