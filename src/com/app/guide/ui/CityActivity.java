package com.app.guide.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.guide.R;
import com.app.guide.adapter.CityAdapter;
import com.app.guide.bean.CityModel;
import com.app.guide.sql.CityDBManager;
import com.app.guide.widget.DialogManagerHelper;
import com.app.guide.widget.QuicLocationBar;
import com.app.guide.widget.QuicLocationBar.OnTouchLetterChangedListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

public class CityActivity extends BaseActivity {

	
	private LocationClient mLocationClient;
	private Button loacteButton;

	private ListView mCityLit;
	private TextView overlay;
	private QuicLocationBar mQuicLocationBar;
	private HashMap<String, Integer> alphaIndexer;
	private SQLiteDatabase database;
	private ArrayList<CityModel> mCityNames;

	@Override
	@SuppressLint("InlinedApi")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_city);
		
		//检测gps是否打开 
		new DialogManagerHelper(this).showWifiLocateDialog();
		
		mQuicLocationBar = (QuicLocationBar) findViewById(R.id.city_loactionbar);
		mQuicLocationBar
				.setOnTouchLitterChangedListener(new LetterListViewListener());
		overlay = (TextView) findViewById(R.id.city_dialog);
		mCityLit = (ListView) findViewById(R.id.city_list);
		loacteButton = (Button) findViewById(R.id.city_btn_loacte);
		mLocationClient = new LocationClient(getApplicationContext());
		mQuicLocationBar.setTextDialog(overlay);
		initLocation();
		initList();
	}

	private void initList() {
		mCityNames = getCityNames();
		CityAdapter adapter = new CityAdapter(CityActivity.this, mCityNames);
		mCityLit.setAdapter(adapter);
		alphaIndexer = adapter.getCityMap();
		mCityLit.setOnItemClickListener(new CityListOnItemClick());
	}

	private ArrayList<CityModel> getCityNames() {
		CityDBManager dbManager = new CityDBManager(CityActivity.this);
		database = dbManager.openDateBase();;
		ArrayList<CityModel> names = new ArrayList<CityModel>();
		Cursor cursor = database.rawQuery(
				"SELECT * FROM T_City ORDER BY NameSort", null);
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToPosition(i);
			CityModel cityModel = new CityModel();
			cityModel.setCityName(cursor.getString(cursor
					.getColumnIndex("CityName")));
			cityModel.setNameSort(cursor.getString(cursor
					.getColumnIndex("NameSort")));
			names.add(cityModel);
		}
		database.close();
		return names;
	}

	private class CityListOnItemClick implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
				long arg3) {
			CityModel cityModel = (CityModel) mCityLit.getAdapter()
					.getItem(pos);
			Toast.makeText(CityActivity.this, cityModel.getCityName(),
					Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(CityActivity.this, MuseumActivity.class);
			startActivity(intent);
		}

	}

	private class LetterListViewListener implements
			OnTouchLetterChangedListener {

		@Override
		public void touchLetterChanged(String s) {
			// TODO Auto-generated method stub
			if (alphaIndexer.get(s) != null) {
				int position = alphaIndexer.get(s);
				mCityLit.setSelection(position);
			}
		}

	}

	private void initLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setIsNeedAddress(true);
		option.setLocationMode(LocationMode.Hight_Accuracy);
		option.setCoorType("bd0911");
		mLocationClient.setLocOption(option);
		mLocationClient.registerLocationListener(new BDLocationListener() {

			@Override
			public void onReceiveLocation(BDLocation arg0) {
				// TODO Auto-generated method stub
				String city = arg0.getCity();
				if (city == null) {
					loacteButton.setText("定位失败");
					loacteButton.setEnabled(false);
					Toast.makeText(CityActivity.this, "定位失败，请手动选择城市", Toast.LENGTH_SHORT).show();
				} else {
					loacteButton.setText(city);
					Toast.makeText(CityActivity.this, city.toString(),Toast.LENGTH_SHORT).show();
					//TODO putExtrax
					Intent intent = new Intent(CityActivity.this, MuseumActivity.class);
					startActivity(intent);
				}

			}
		});
		mLocationClient.start();
	}

	@Override
	protected String getTitleStr() {
		// TODO Auto-generated method stub
		return "选择城市";
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mLocationClient != null) {
			mLocationClient.stop();
		}
		
	}


	
	
}
