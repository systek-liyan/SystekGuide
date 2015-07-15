package com.app.guide.beanhelper;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;

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
import com.app.guide.sql.DownloadManagerHelper;

/**
 * 获取Bean的策略类的抽象接口
 * 
 * @author yetwish
 * 
 */
public abstract class GetBeanStrategy {

	protected Context mContext;

	public GetBeanStrategy(Context context) {
		this.mContext = context;
	}

	/**
	 * 获取城市列表
	 * 
	 * @return
	 */
	public abstract List<CityModel> getCityList();

	/**
	 * 获取某城市下的所有博物馆MuseumBean列表
	 * 
	 * @param city
	 * @return
	 */
	public abstract List<MuseumBean> getMuseumList(CityModel city);


	/**
	 * 根据博物馆id 获取某一博物馆的页面展示数据存储类对象
	 * 
	 * @param MuseumId
	 * @return
	 */
	public abstract MuseumModel getMuseumModel(String museumId);

	/**
	 * 获取展品ExhibitBean列表 内部使用分页
	 * 
	 * @param museumId
	 * @param minPriority
	 * @return
	 */
	public abstract List<ExhibitBean> getExhibitList(String museumId,
			int minPriority);

//	/**
//	 * 根据museumId获取该博物馆下的所有展品
//	 * 
//	 * @param museumId
//	 * @return
//	 */
//	public List<ExhibitBean> getExhibitList(String museumId) {
//		return getExhibitList(museumId, 0);
//	}

//	/**
//	 * 根据museumId和minPriority 获取该博物馆下的所有优先级不小于minPriority的展品
//	 * 
//	 * @param museumId
//	 * @param minPriority
//	 * @return
//	 */
//	public List<ExhibitBean> getExhibitList(String museumId, int minPriority) {
//		return getExhibitList(museumId, minPriority, null);
//	}
//
//	/**
//	 * 根据museumId和beaconId 获取该博物馆下的所有属于该Beacon管理的的展品
//	 * 
//	 * @param museumId
//	 * @param beaconId
//	 * @return
//	 */
//	public List<ExhibitBean> getExhibitList(String museumId, String beaconId) {
//		return getExhibitList(museumId, 0, beaconId);
//	}

	/**
	 * 获取该博物馆下的所有标签列表
	 * 
	 * @param museumId
	 * @return
	 */
	public abstract List<LabelModel> getLabelList(String museumId);

	/**
	 * 根据museumId和exhibitId获取该博物馆下某一展品的页面展示数据存储类对象
	 * 
	 * @param museumId
	 * @param exhibitId
	 * @return
	 */
	public abstract ExhibitModel getExhibitModel(String museumId,
			String exhibitId);

	/**
	 * 通过
	 * 
	 * @param museumId
	 * @param beaconId
	 * @return
	 */
	public abstract List<ExhibitModel> getExhibitModelsByBeaconId(
			String museumId, String beaconId);

	/**
	 * 根据museumId和楼层数floor获取某一博物馆的某一楼层的地图bean
	 * 
	 * @param museumId
	 * @param floor
	 * @return
	 */
	public abstract OfflineMapBean getMapBean(String museumId, int floor);

	/**
	 * 根据museumId 获取某一博物馆的所有地图Bean 与#getMapBean用同一个URL，考虑能否合并
	 * 
	 * @param museumId
	 * @return
	 */
	public abstract List<OfflineMapBean> getMapList(String museumId);

	/**
	 * 返回博物馆某一楼层的所有展厅列表
	 * 
	 * @param museumId
	 * @param floor
	 * @return
	 */
	public abstract List<MuseumAreaBean> getMuseumAreaList(String museumId,
			int floor);

	/**
	 * 根据博物馆Id和展厅Id获取该展厅下的所有展品的列表
	 * 
	 * @param museumId
	 * @param museumAreaId
	 * @return
	 */
	public abstract List<MapExhibitModel> getMapExhibitList(String museumId,
			String museumAreaId);

	/**
	 * 根据beacon major和minor获取 BeaconBean
	 * 
	 * @param museumId
	 * @param major
	 * @param minor
	 * @return
	 */
	public abstract OfflineBeaconBean getBeaconBean(String museumId,
			String major, String minor);

	/**
	 * 获取博物馆某一楼层的所有BeaconBean
	 * 
	 * @param museumId
	 * @param floor
	 * @return
	 */
	public abstract List<OfflineBeaconBean> getBeaconList(String museumId,
			int floor);

	/**
	 * 获取下载列表 <br>
	 * 现阶段，一个DownloadBean表示一个博物馆
	 * @return
	 */
	public abstract List<DownloadBean> getDownloadList();
	
	/**
	 * 获取所有正在下载中的bean 
	 */
	public List<DownloadBean> getDownloadingBeans(){
		DownloadManagerHelper helper = new DownloadManagerHelper(mContext);
		List<DownloadBean> list = null;
		try {
			list = helper.getBeanDao().queryBuilder().where()
					.eq("isCompleted", false).query();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 获取所有已下载完成的bean 
	 */
	public List<DownloadBean> getDownloadCompletedBeans(){
		DownloadManagerHelper helper = new DownloadManagerHelper(mContext);
		List<DownloadBean> list = null;
		try {
			list = helper.getBeanDao().queryBuilder().where()
					.eq("isCompleted", true).query();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
}
