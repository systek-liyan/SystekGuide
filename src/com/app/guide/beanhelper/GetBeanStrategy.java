package com.app.guide.beanhelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.app.guide.Constant;
import com.app.guide.bean.CityBean;
import com.app.guide.bean.ExhibitBean;
import com.app.guide.bean.MuseumAreaBean;
import com.app.guide.bean.MuseumBean;
import com.app.guide.download.DownloadBean;
import com.app.guide.download.DownloadModel;
import com.app.guide.model.ExhibitModel;
import com.app.guide.model.LabelModel;
import com.app.guide.model.MapExhibitModel;
import com.app.guide.model.MuseumModel;
import com.app.guide.offline.OfflineBeaconBean;
import com.app.guide.offline.OfflineMapBean;
import com.app.guide.sql.CityDBManagerHelper;
import com.app.guide.sql.DownloadManagerHelper;
import com.app.guide.utils.FastJsonArrayRequest;
import com.j256.ormlite.dao.Dao;

/**
 * 获取Bean的策略类的抽象接口
 * 
 * @author yetwish
 * 
 */
public abstract class GetBeanStrategy {

	private static final String TAG = GetBeanStrategy.class.getSimpleName();
	
	protected Context mContext;
	protected ErrorListener mErrorListener;
	protected RequestQueue mQueue;

	public GetBeanStrategy(Context context) {
		this.mContext = context;
		mQueue = Volley.newRequestQueue(context);
		mErrorListener = new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				Log.w(TAG,"VolleyError:"+error.getMessage());
			}
		};
	}

	/**
	 * 获取城市列表
	 * 
	 * @return
	 */
	public void getCityList(final GetBeanCallBack<List<CityBean>> callBack) {
		// 在判断本地是否存在数据库，如果存在则从数据库中获取，不存在则通过实时API获取 或判断是否需要更新
		CityDBManagerHelper dbHelper = new CityDBManagerHelper(mContext);
		if (dbHelper.isDBExists()) {
			Log.w(TAG, "db is exist!");
			// 获取列表
			Dao<CityBean, String> cityDao = null;
			List<CityBean> cityList = null;
			try {
				cityDao = dbHelper.getCityDao();
				cityList = cityDao.queryForAll();
			} catch (SQLException e) {
				cityList = new ArrayList<CityBean>();
				e.printStackTrace();
			}
			callBack.onGetBeanResponse(cityList);
		}
		else {  
			String url = Constant.HOST_HEAD + "/a/api/city/treeData";
			FastJsonArrayRequest<CityBean> request = new FastJsonArrayRequest<CityBean>(
					url, CityBean.class, new Response.Listener<List<CityBean>>() {
						@Override
						public void onResponse(List<CityBean> response) {
							// 将数据传递给参数，并存在本地数据库中
							callBack.onGetBeanResponse(response);
							CityDBManagerHelper dbHelper = new CityDBManagerHelper(
									mContext);
							Dao<CityBean, String> cityDao = null;
							try {
								cityDao = dbHelper.getCityDao();
								for (CityBean city : response) {
									cityDao.createOrUpdate(city);
									Log.w(TAG, city.toString());
								}
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
					}, mErrorListener);
			mQueue.add(request);
			Log.w(TAG, "db is not exist，access from network!");
		}
	}

	/**
	 * 获取某城市下的所有博物馆MuseumBean列表
	 * 
	 * @param city
	 * @param getBeanCallBack
	 * @return
	 */
	public abstract void getMuseumList(String city,
			GetBeanCallBack<List<MuseumBean>> getBeanCallBack);

	/**
	 * 根据博物馆id 获取某一博物馆的页面展示数据存储类对象
	 * 
	 * @param callBack
	 * 
	 * @param MuseumId
	 * @return
	 */
	public abstract void getMuseumModel(String museumId,
			GetBeanCallBack<MuseumModel> callBack);

	/**
	 * 获取展品ExhibitBean列表 内部使用分页
	 * 
	 * @param museumId
	 * @param minPriority
	 * @param callBack
	 * @return
	 */
	public abstract void getExhibitList(String museumId, int minPriority,int page,
			GetBeanCallBack<List<ExhibitBean>> callBack);

	/**
	 * 获取该博物馆下的所有标签列表
	 * 
	 * @param museumId
	 * @param callBack
	 * @return
	 */
	public abstract void getLabelList(String museumId,
			GetBeanCallBack<List<LabelModel>> callBack);

	/**
	 * 根据museumId和exhibitId获取该博物馆下某一展品的页面展示数据存储类对象
	 * 
	 * @param museumId
	 * @param exhibitId
	 * @param callBack
	 * @return
	 */
	public abstract void getExhibitModel(String museumId, String exhibitId,
			GetBeanCallBack<ExhibitModel> callBack);

	/**
	 * 通过
	 * 
	 * @param museumId
	 * @param beaconId
	 * @param callBack
	 * @return
	 */
	public abstract void getExhibitModelsByBeaconId(String museumId,
			String beaconId, GetBeanCallBack<List<ExhibitModel>> callBack);

	/**
	 * 根据museumId和楼层数floor获取某一博物馆的某一楼层的地图bean
	 * 
	 * @param museumId
	 * @param floor
	 * @param callBack
	 * @return
	 */
	public abstract void getMapBean(String museumId, int floor,
			GetBeanCallBack<OfflineMapBean> callBack);

	/**
	 * 根据museumId 获取某一博物馆的所有地图Bean 与#getMapBean用同一个URL，考虑能否合并
	 * 
	 * @param museumId
	 * @param callBack
	 * @return
	 */
	public abstract void getMapList(String museumId,
			GetBeanCallBack<List<OfflineMapBean>> callBack);

	/**
	 * 返回博物馆某一楼层的所有展厅列表
	 * 
	 * @param museumId
	 * @param floor
	 * @param callBack
	 * @return
	 */
	public abstract void getMuseumAreaList(String museumId, int floor,
			GetBeanCallBack<List<MuseumAreaBean>> callBack);

	/**
	 * 根据博物馆Id和展厅Id获取该展厅下的所有展品的列表
	 * 
	 * @param museumId
	 * @param museumAreaId
	 * @param callBack
	 * @return
	 */
	public abstract void getMapExhibitList(String museumId,
			String museumAreaId, GetBeanCallBack<List<MapExhibitModel>> callBack);

	/**
	 * 根据beacon major和minor获取 BeaconBean
	 * 
	 * @param museumId
	 * @param major
	 * @param minor
	 * @param callBack
	 * @return
	 */
	public abstract void getBeaconBean(String museumId, String major,
			String minor, GetBeanCallBack<OfflineBeaconBean> callBack);

	/**
	 * 获取博物馆某一楼层的所有BeaconBean
	 * 
	 * @param museumId
	 * @param floor
	 * @param callBack
	 * @return
	 */
	public abstract void getBeaconList(String museumId, int floor,
			GetBeanCallBack<List<OfflineBeaconBean>> callBack);

	/**
	 * 获取下载列表 <br>
	 * 现阶段，一个DownloadBean表示一个博物馆
	 * 
	 * @param callBack
	 * 
	 * @return
	 */
	/**
	 * 获取下载列表 <br>
	 * 现阶段，一个DownloadBean表示一个博物馆
	 * 
	 * @param callBack
	 * 
	 * @return
	 */
	public void getDownloadList(
			final GetBeanCallBack<List<DownloadModel>> callBack) {
		// 在判断本地是否存在数据库，如果存在则从数据库中获取，不存在则通过实时API获取 或判断是否需要更新
		DownloadManagerHelper dbHelper = new DownloadManagerHelper(mContext);
		if (dbHelper.isDownloadListExist()) {
			Log.w(TAG, "db is exist!");
			// 获取列表
			Dao<DownloadModel, String> modelDao = null;
			List<DownloadModel> downloadList = null;
			try {
				modelDao = dbHelper.getModelDao();
				downloadList = modelDao.queryForAll();
			} catch (SQLException e) {
				downloadList = new ArrayList<DownloadModel>();
				e.printStackTrace();
			}
			callBack.onGetBeanResponse(downloadList);
		} else {
			String url = "http://182.92.82.70/a/api/assets/treeData";
			JsonArrayRequest request = new JsonArrayRequest(url,
					new Response.Listener<JSONArray>() {
						@Override
						public void onResponse(final JSONArray response) {
							DownloadManagerHelper dbHelper = new DownloadManagerHelper(
									mContext);
							Dao<DownloadModel, String> modelDao = null;
							List<DownloadModel> downloadList = new ArrayList<DownloadModel>(
									response.length());
							List<DownloadBean> downloadBeans = null;
							JSONObject object = null;
							DownloadModel downloadModel = null;
							DownloadBean downloadBean = null;
							try {
								modelDao = dbHelper.getModelDao();
								for (int i = 0; i < response.length(); i++) {
									object = response.getJSONObject(i);
									Log.w("TAG", "外层,"+object.toString());
									downloadModel = new DownloadModel();
									downloadModel.setCity(object
											.getString("city"));
									Log.w("TAG", "CITY"+object
											.getString("city"));
									JSONArray museums = object
											.getJSONArray("museumList");
									Log.w("TAG", "中层,"+museums.toString());
									downloadBeans = new ArrayList<DownloadBean>(
											museums.length());
									for (int j = 0; j < museums.length(); j++) {
										object = museums.getJSONObject(j);
										Log.w("TAG", "里层,"+object.toString());
										downloadBean = new DownloadBean();
										downloadBean.setMuseumId(object
												.getString("museumId"));
										downloadBean.setName(object
												.getString("name"));
										downloadBean.setTotal(object
												.getLong("size"));
										downloadBeans.add(downloadBean);
									}
									downloadModel.setMuseumsUrl(downloadBeans);
//									modelDao.createOrUpdate(downloadModel);
									downloadList.add(downloadModel);
									
									callBack.onGetBeanResponse(downloadList);
								}
							} catch (JSONException e) {
								// TODO: handle exception
							} catch (SQLException e1) {

							}
						}
					}, mErrorListener);
			mQueue.add(request);
		}
	}

	/**
	 * 获取所有正在下载中的bean
	 * 
	 * @param callBack
	 */
	public void getDownloadingBeans(GetBeanCallBack<List<DownloadBean>> callBack) {
		DownloadManagerHelper helper = new DownloadManagerHelper(mContext);
		List<DownloadBean> list = null;
		try {
			list = helper.getBeanDao().queryBuilder().where()
					.eq("isCompleted", false).query();
			callBack.onGetBeanResponse(list);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 获取所有已下载完成的bean
	 * 
	 * @param callBack
	 */
	public void getDownloadCompletedBeans(
			GetBeanCallBack<List<DownloadBean>> callBack) {
		DownloadManagerHelper helper = new DownloadManagerHelper(mContext);
		List<DownloadBean> list = null;
		try {
			list = helper.getBeanDao().queryBuilder().where()
					.eq("isCompleted", true).query();
			callBack.onGetBeanResponse(list);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
