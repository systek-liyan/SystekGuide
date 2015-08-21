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
import com.app.guide.model.ExhibitModel;
import com.app.guide.model.LabelModel;
import com.app.guide.model.MapExhibitModel;
import com.app.guide.model.MuseumModel;
import com.app.guide.offline.OfflineBeaconBean;
import com.app.guide.offline.OfflineMapBean;
import com.app.guide.sql.CityDBManagerHelper;
import com.app.guide.sql.DatabaseContext;
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
				Log.w(TAG,"Volley error:"+error.getMessage());
			}
		};
	}

	/**
	 * 获取城市列表
	 * 判断本地是否存在数据库，如果存在则从数据库中获取，不存在则通过实时API获取,并存入CityName数据库
	 * @return
	 */
	public void getCityList(final GetBeanCallBack<List<CityBean>> callBack) {
		// 外部数据库Context
		Context dContext = new DatabaseContext(mContext, Constant.FLODER_NAME);
		// 下面还要从FastJsonArrayRequest对象中访问，因此要有final修饰词
		final CityDBManagerHelper dbHelper = new CityDBManagerHelper(dContext,"CityName.db");
		// 获取城市列表
		Dao<CityBean, String> cityDao = null;
		List<CityBean> cityList = null;
		try {
			cityDao = dbHelper.getCityDao();
			cityList = cityDao.queryForAll();
		} catch (SQLException e) {
			cityList = null;
			Log.d(TAG,"Access the CityName.db error,may be it's not exist!");
		}
		if (cityList != null && cityList.size() > 0) {
		    callBack.onGetBeanResponse(cityList);
		    Log.d(TAG, "CityName.db is exist,read it ok!");
		    return;
		}
		/////本地获取失败，从网络获取,并存入CityName数据库
		cityList = new ArrayList<CityBean>();
		String url = Constant.HOST_HEAD + "/api/cityService/cityList";
		FastJsonArrayRequest<CityBean> request = new FastJsonArrayRequest<CityBean>(
				url, CityBean.class, new Response.Listener<List<CityBean>>() {
					@Override
					public void onResponse(List<CityBean> response) {
						// 将数据传递给参数，并存在本地数据库中
						callBack.onGetBeanResponse(response);
						Dao<CityBean, String> cityDao = null;
						try {
							cityDao = dbHelper.getCityDao();
							for (CityBean city : response) {
								cityDao.createOrUpdate(city);
								//Log.w(TAG, city.toString());
							}
						} catch (SQLException e) {
							Log.d(TAG,"write CityName.db error!" + e.toString());
						}
					}
				}, mErrorListener);
		mQueue.add(request);
		Log.w(TAG, "CityName.db is not exist，access from network!");
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
	 * 获取展品ExhibitBean列表 内部使用分页（从0开始）
	 * 
	 * @param museumId 博物馆id
	 * @param minPriority 最小优先级(即，比该优先级大的博物馆是推荐的博物馆)
	 * @param page 0,1,2,3... 表示第1,2,3...页
	 * @param callBack ExhibitBean列表
	 */
	public abstract void getExhibitList(String museumId, int minPriority,int page,
			GetBeanCallBack<List<ExhibitBean>> callBack);
	
	/**
	 * 获得名称中含有name的展品列表
	 * @param name
	 * @param callBack null 表示无此条件的展品返回
	 */
	public abstract void getExhibitList_name(String museumId,String name,
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
	 * 获取可下载离线数据包的博物馆列表，并且本地没有下载完成
	 * 即：返回本地未下载完成的和网络新获取的DownloadBean列表
	 * (1) 网络所有可下载离线数据包的博物馆列表
	 *   json：[{"city":"北京市","museumList":[{"museumId":"deadccf89ef8412a9c8a2628cee28e18","name":"保利博物馆","size":"10184184"},...
	 * (2) Download.db的downloadBean表中无此记录，使用createIfNotExists()创建之.
	 * (3) 返回上述本地数据库中isComplete=false的DownloadBean
	 * 
	 * @param callBack null 表示网络获取后本地数据库仍然没有符合条件的DownloadBean
	 * 
	 */
	public void getDownloadBeanList(final GetBeanCallBack<List<DownloadBean>> callBack) {
		// 外部数据库Context
		Context dContext = new DatabaseContext(mContext, Constant.FLODER_NAME);
		final DownloadManagerHelper dbHelper = new DownloadManagerHelper(dContext,"Download.db");

        //从网络获取,并录入数据库Download数据库的DownloadBean表。
		String url = Constant.HOST_HEAD + "/api/assetsService/assetsSizeList";
		JsonArrayRequest request = new JsonArrayRequest(url,
				new Response.Listener<JSONArray>() {
					@Override
					public void onResponse(final JSONArray response) {
						JSONObject object = null;
						DownloadBean downloadBean = null;
						try {
							Dao<DownloadBean, String> downloadDao = dbHelper.getDownloadBeanDao();
							for (int i = 0; i < response.length(); i++) {
								object = response.getJSONObject(i);
								//Log.w(TAG, "外层,"+object.toString());
								String city = object.getString("city");
								JSONArray museums = object.getJSONArray("museumList");
								//Log.w(TAG, "中层,"+museums.toString());
								for (int j = 0; j < museums.length(); j++) {
									downloadBean = new DownloadBean();
									downloadBean.setCity(city);
									object = museums.getJSONObject(j);
									//Log.w(TAG, "里层,"+object.toString());
									downloadBean.setMuseumId(object.getString("museumId"));
									downloadBean.setName(object.getString("name"));
									downloadBean.setTotal(object.getLong("size"));
									// 如果数据库Download数据库的downloadBean表无此记录，录入之
									downloadDao.createIfNotExists(downloadBean);
								}
							}
						} catch (JSONException e) {
							Log.d(TAG, "getDownloadBeanList(),Access form network error!" + e.toString());
						} catch (SQLException e) {
							Log.d(TAG, "getDownloadBeanList(),Sql exception!" + e.toString());
						}
						
						// 传递给回调，返回本地未下载完成的和网络新获取的DownloadBean列表
						List<DownloadBean> downloadList = null;
						try {
							downloadList = dbHelper.getDownloadBeanDao().queryBuilder().where()
									.eq("isCompleted", false).query();
							callBack.onGetBeanResponse(downloadList);
						} catch (SQLException e) {
							Log.d(TAG, "getDownloadBeanList(),Sql exception!" + e.toString());
						}
					}
				}, mErrorListener);
		mQueue.add(request);
		Log.w(TAG, "Download.db and network access dada in getDownloadBeanList()!");
	}
	
	/**
	 * 在本地数据库中获取所有已下载完成的bean
	 * 
	 * @param callBack
	 */
	public void getDownloadCompletedBeans(
			GetBeanCallBack<List<DownloadBean>> callBack) {
		// 外部数据库Context
		Context dContext = new DatabaseContext(mContext, Constant.FLODER_NAME);
		DownloadManagerHelper dbHelper = new DownloadManagerHelper(dContext,"Download.db");
		List<DownloadBean> list = null;
		try {
			list = dbHelper.getDownloadBeanDao().queryBuilder().where()
					.eq("isCompleted", true).query();
			callBack.onGetBeanResponse(list);
		} catch (SQLException e) {
			callBack.onGetBeanResponse(null);
			Log.d(TAG, "getDownloadCompletedBeans(),Access form Download.db error!" + e.toString());
		}
	}
}
