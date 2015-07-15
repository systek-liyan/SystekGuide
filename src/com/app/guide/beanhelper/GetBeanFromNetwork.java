package com.app.guide.beanhelper;

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

public class GetBeanFromNetwork extends GetBeanStrategy{

	public GetBeanFromNetwork(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<CityModel> getCityList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MuseumBean> getMuseumList(CityModel city) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MuseumModel getMuseumModel(String MuseumId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ExhibitBean> getExhibitList(String museumId, int minPriority) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<LabelModel> getLabelList(String museumId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExhibitModel getExhibitModel(String museumId, String exhibitId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ExhibitModel> getExhibitModelsByBeaconId(String museumId,
			String beaconId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OfflineMapBean getMapBean(String museumId, int floor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<OfflineMapBean> getMapList(String museumId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MuseumAreaBean> getMuseumAreaList(String museumId, int floor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MapExhibitModel> getMapExhibitList(String museumId,
			String museumAreaId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OfflineBeaconBean getBeaconBean(String museumId, String major,
			String minor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<OfflineBeaconBean> getBeaconList(String museumId, int floor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DownloadBean> getDownloadList() {
		// TODO Auto-generated method stub
		return null;
	}

}
