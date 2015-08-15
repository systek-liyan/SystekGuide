package com.app.guide.beanhelper;

import java.util.List;

import android.content.Context;

import com.app.guide.bean.ExhibitBean;
import com.app.guide.bean.MuseumAreaBean;
import com.app.guide.bean.MuseumBean;
import com.app.guide.model.ExhibitModel;
import com.app.guide.model.LabelModel;
import com.app.guide.model.MapExhibitModel;
import com.app.guide.model.MuseumModel;
import com.app.guide.offline.OfflineBeaconBean;
import com.app.guide.offline.OfflineMapBean;

public class GetBeanFromNetwork extends GetBeanStrategy {

	public GetBeanFromNetwork(Context context) {
		super(context);
	}

	@Override
	public void getMuseumList(String city,
			GetBeanCallBack<List<MuseumBean>> callBack) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getMuseumModel(String museumId,
			GetBeanCallBack<MuseumModel> callBack) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getExhibitList(String museumId, int minPriority,int page,
			GetBeanCallBack<List<ExhibitBean>> callBack) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * 获得名称中含有name的展品列表
	 * @param name
	 * @param callBack null 表示无此条件的展品返回
	 */
	@Override
	public void getExhibitList_name(String museumId,String name,
			GetBeanCallBack<List<ExhibitBean>> callBack) {
		// TODO Auto-generated method stub
	}

	@Override
	public void getLabelList(String museumId,
			GetBeanCallBack<List<LabelModel>> callBack) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getExhibitModel(String museumId, String exhibitId,
			GetBeanCallBack<ExhibitModel> callBack) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getExhibitModelsByBeaconId(String museumId, String beaconId,
			GetBeanCallBack<List<ExhibitModel>> callBack) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getMapBean(String museumId, int floor,
			GetBeanCallBack<OfflineMapBean> callBack) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getMapList(String museumId,
			GetBeanCallBack<List<OfflineMapBean>> callBack) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getMuseumAreaList(String museumId, int floor,
			GetBeanCallBack<List<MuseumAreaBean>> callBack) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getMapExhibitList(String museumId, String museumAreaId,
			GetBeanCallBack<List<MapExhibitModel>> callBack) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getBeaconBean(String museumId, String major, String minor,
			GetBeanCallBack<OfflineBeaconBean> callBack) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getBeaconList(String museumId, int floor,
			GetBeanCallBack<List<OfflineBeaconBean>> callBack) {
		// TODO Auto-generated method stub

	}


}
