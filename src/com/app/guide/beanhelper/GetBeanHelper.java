package com.app.guide.beanhelper;

import java.util.List;

import android.content.Context;

import com.app.guide.AppContext;
import com.app.guide.bean.ExhibitBean;
import com.app.guide.bean.MuseumAreaBean;
import com.app.guide.bean.MuseumBean;
import com.app.guide.download.DownloadBean;
import com.app.guide.model.CityModel;
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
	 * @param getBeanStrategy 获取bean的策略
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
	 * @param context
	 * @return
	 */
	public static synchronized GetBeanHelper getInstance(Context context) {
			if (instance == null) {
				//第一次调用， 初始化 ，获取两个helper实例
				dbHelper = new GetBeanHelper(new GetBeanFromDB(context));
				networkHelper = new GetBeanHelper(new GetBeanFromNetwork(context));
			}
			//判断应该使用哪个策略获取bean，并返回使用该策略的实例
			if (((AppContext) context.getApplicationContext()).hasOffline) {
				instance = dbHelper;
			} else {
				instance = networkHelper;
			}
		return instance;
	}

	public List<CityModel> getCityList() {
		return mGetBeanStrategy.getCityList();
	}

	public List<MuseumBean> getMuseumList(CityModel city) {
		return mGetBeanStrategy.getMuseumList(city);
	}

	public MuseumModel getMuseumModel(String museumId) {
		return mGetBeanStrategy.getMuseumModel(museumId);
	}

	public List<ExhibitBean> getExhibitList(String museumId, int minPriority) {
		return mGetBeanStrategy.getExhibitList(museumId, minPriority);
	}

	public List<ExhibitBean> getExhibitList(String museumId) {
		// TODO Auto-generated method stub
		return getExhibitList(museumId, 0);
	}

	public List<LabelModel> getLabelList(String museumId) {
		return mGetBeanStrategy.getLabelList(museumId);
	}

	public ExhibitModel getExhibitModel(String museumId, String exhibitId) {
		return mGetBeanStrategy.getExhibitModel(museumId, exhibitId);
	}

	public List<ExhibitModel> getExhibitModelsByBeaconId(String museumId,
			String beaconId) {
		return mGetBeanStrategy.getExhibitModelsByBeaconId(museumId, beaconId);
	}

	public OfflineMapBean getMapBean(String museumId, int floor) {
		return mGetBeanStrategy.getMapBean(museumId, floor);
	}

	public List<OfflineMapBean> getMapList(String museumId) {
		// TODO Auto-generated method stub
		return mGetBeanStrategy.getMapList(museumId);
	}

	public List<MuseumAreaBean> getMuseumAreaList(String museumId, int floor) {
		return mGetBeanStrategy.getMuseumAreaList(museumId, floor);
	}

	public List<MapExhibitModel> getMapExhibitList(String museumId,
			String museumAreaId) {
		return mGetBeanStrategy.getMapExhibitList(museumId, museumAreaId);
	}

	public OfflineBeaconBean getBeaconBean(String museumId, String major,
			String minor) {
		return mGetBeanStrategy.getBeaconBean(museumId, major, minor);
	}

	public List<OfflineBeaconBean> getBeaconList(String museumId, int floor) {
		return mGetBeanStrategy.getBeaconList(museumId, floor);
	}

	public List<DownloadBean> getDownloadList() {
		return mGetBeanStrategy.getDownloadList();
	}

	/**
	 * 获取所有正在下载中的bean
	 */
	public List<DownloadBean> getDownloadingBeans() {
		return mGetBeanStrategy.getDownloadingBeans();
	}

	/**
	 * 获取所有已下载完成的bean
	 */
	public List<DownloadBean> getDownloadCompletedBeans(Context context) {
		return mGetBeanStrategy.getDownloadCompletedBeans();
	}

}
