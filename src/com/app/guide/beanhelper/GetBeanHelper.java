package com.app.guide.beanhelper;

import java.util.List;

import android.content.Context;

import com.app.guide.AppContext;
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

/**
 * 获取Bean的帮助类，根据当前系统状态（有无离线数据包）使用不同的策略获取数据
 * 
 * @author yetwish
 */
public class GetBeanHelper {

	private GetBeanStrategy mGetBeanStrategy;

	/**
	 * 使用单例模式
	 * 
	 * @param getBeanStrategy
	 *            获取bean的策略
	 */
	private GetBeanHelper(GetBeanStrategy getBeanStrategy) {
		this.mGetBeanStrategy = getBeanStrategy;
	}

	/**
	 * 保存当前实例
	 */
	private static GetBeanHelper instance;

	/**
	 * 保存使用本地获取数据的策略构造的helper
	 */
	private static GetBeanHelper dbHelper;

	/**
	 * 保存使用网络获取数据的策略构造的helper
	 */
	private static GetBeanHelper networkHelper;

	/**
	 * 获得helper实例，通过判断当前是否有离线包而获取使用相应策略的helper TODO 考虑该同步的代码块
	 * 
	 * @param context
	 * @return
	 */
	public static synchronized GetBeanHelper getInstance(Context context) {
		if (instance == null) {
			// 第一次调用， 初始化 ，获取两个helper实例
			dbHelper = new GetBeanHelper(new GetBeanFromDB(context));
			networkHelper = new GetBeanHelper(new GetBeanFromNetwork(context));
		}
		// 判断应该使用哪个策略获取bean，并返回使用该策略的实例
		if (((AppContext) context.getApplicationContext()).hasOffline) {
			instance = dbHelper;
		} else {
			instance = networkHelper;
		}
		return instance;
	}

	public void getCityList(GetBeanCallBack<List<CityBean>> callBack) {
		mGetBeanStrategy.getCityList(callBack);
	}

	public void getMuseumList(String city,
			GetBeanCallBack<List<MuseumBean>> getBeanCallBack) {
		mGetBeanStrategy.getMuseumList(city, getBeanCallBack);
	}

	public void getMuseumModel(String museumId,
			GetBeanCallBack<MuseumModel> callBack) {
		mGetBeanStrategy.getMuseumModel(museumId, callBack);
	}

	public void getExhibitList(String museumId, int minPriority,int page,
			GetBeanCallBack<List<ExhibitBean>> callBack) {
		mGetBeanStrategy.getExhibitList(museumId, minPriority, page,callBack);
	}
	
	/**
	 * 获得名称中含有name的展品列表
	 * @param name
	 * @param callBack null 表示无此条件的展品返回
	 */
	public void getExhibitList_name(String museumId,String name,
			GetBeanCallBack<List<ExhibitBean>> callBack) {
		mGetBeanStrategy.getExhibitList_name(museumId,name,callBack);
	}

	public void getExhibitList(String museumId,int page,
			GetBeanCallBack<List<ExhibitBean>> callBack) {
		// TODO Auto-generated method stub
		getExhibitList(museumId, 0, page,callBack);
	}

	public void getLabelList(String museumId,
			GetBeanCallBack<List<LabelModel>> callBack) {
		mGetBeanStrategy.getLabelList(museumId, callBack);
	}

	public void getExhibitModel(String museumId, String exhibitId,
			GetBeanCallBack<ExhibitModel> callBack) {
		mGetBeanStrategy.getExhibitModel(museumId, exhibitId, callBack);
	}

	public void getExhibitModelsByBeaconId(String museumId, String beaconId,
			GetBeanCallBack<List<ExhibitModel>> callBack) {
		mGetBeanStrategy.getExhibitModelsByBeaconId(museumId, beaconId,
				callBack);
	}

	public void getMapBean(String museumId, int floor,
			GetBeanCallBack<OfflineMapBean> callBack) {
		mGetBeanStrategy.getMapBean(museumId, floor, callBack);
	}

	public void getMapList(String museumId,
			GetBeanCallBack<List<OfflineMapBean>> callBack) {
		mGetBeanStrategy.getMapList(museumId, callBack);
	}

	public void getMuseumAreaList(String museumId, int floor,
			GetBeanCallBack<List<MuseumAreaBean>> callBack) {
		mGetBeanStrategy.getMuseumAreaList(museumId, floor, callBack);
	}

	public void getMapExhibitList(String museumId, String museumAreaId,
			GetBeanCallBack<List<MapExhibitModel>> callBack) {
		mGetBeanStrategy.getMapExhibitList(museumId, museumAreaId, callBack);
	}

	public void getBeaconBean(String museumId, String major, String minor,
			GetBeanCallBack<OfflineBeaconBean> callBack) {
		mGetBeanStrategy.getBeaconBean(museumId, major, minor, callBack);
	}

	public void getBeaconList(String museumId, int floor,
			GetBeanCallBack<List<OfflineBeaconBean>> callBack) {
		mGetBeanStrategy.getBeaconList(museumId, floor, callBack);
	}

	/**
	 * 获取下载离线数据包的博物馆列表
	 * 本地获取数据库Download.db中的BownloadBean
	 * 本地失败，网络获取,json：[{"city":"北京市","museumList":[{"museumId":"deadccf89ef8412a9c8a2628cee28e18","name":"保利博物馆","size":"10184184"},...
	 * 网络获取后，录入本地数据库
	 * 
	 * @param callBack null本地数据库、网络获取失败
	 **/
	public void getDownloadBeanList(GetBeanCallBack<List<DownloadBean>> callBack) {
		mGetBeanStrategy.getDownloadBeanList(callBack);
	}

	/**
	 * 在本地数据库中获取所有已下载完成的bean
	 */
	public void getDownloadCompletedBeans(
			GetBeanCallBack<List<DownloadBean>> callBack) {
		mGetBeanStrategy.getDownloadCompletedBeans(callBack);
	}

}
