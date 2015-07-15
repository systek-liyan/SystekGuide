package com.app.guide.beanhelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;

import com.app.guide.Constant;
import com.app.guide.bean.ExhibitBean;
import com.app.guide.bean.MuseumAreaBean;
import com.app.guide.bean.MuseumBean;
import com.app.guide.download.DownloadBean;
import com.app.guide.model.CityModel;
import com.app.guide.model.ExhibitModel;
import com.app.guide.model.ImageModel;
import com.app.guide.model.LabelModel;
import com.app.guide.model.MapExhibitModel;
import com.app.guide.model.MuseumModel;
import com.app.guide.offline.OfflineBeaconBean;
import com.app.guide.offline.OfflineBeanSqlHelper;
import com.app.guide.offline.OfflineExhibitBean;
import com.app.guide.offline.OfflineLabelBean;
import com.app.guide.offline.OfflineMapBean;
import com.app.guide.offline.OfflineMuseumBean;
import com.app.guide.sql.DatabaseContext;
import com.app.guide.sql.DownloadManagerHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

/**
 * 从本地数据库（离线数据包）中获取各种bean
 * 
 * @author yetwish
 * 
 */
public class GetBeanFromDB extends GetBeanStrategy {

	private static final String TAG = GetBeanFromDB.class.getSimpleName();
	
	public GetBeanFromDB(Context context) {
		super(context);
	}

	@Override
	public List<CityModel> getCityList() {
//		try {
//			
//		} catch (SQLException e) {
//		}
		//TODO 
		return null;
	}

	@Override
	public List<MuseumBean> getMuseumList(CityModel city) {
		List<MuseumBean> list = new ArrayList<MuseumBean>();
		try {
			DownloadManagerHelper helper = new DownloadManagerHelper(mContext);
			Dao<MuseumBean, Integer> downloaDao = helper.getDownloadedDao();
			List<DownloadBean> downloadBeans = helper.getBeanDao()
					.queryForAll();
			for (DownloadBean downloadBean : downloadBeans) {
				if (downloadBean.isCompleted()) {
					MuseumBean bean = downloaDao.queryBuilder().where()
							.eq("museumId", downloadBean.getMuseumId())
							.queryForFirst();
					list.add(bean);
				}
			}
		} catch (SQLException e) {
		}
		return list;
	}

	@Override
	public MuseumModel getMuseumModel(String museumId) {
		OfflineMuseumBean bean = null;
		try {
			OfflineBeanSqlHelper helper = new OfflineBeanSqlHelper(
					new DatabaseContext(mContext, Constant.FLODER_NAME
							+ museumId), museumId + ".db");
			Dao<OfflineMuseumBean, Integer> oDao = helper.getOfflineMuseumDao();
			QueryBuilder<OfflineMuseumBean, Integer> builder = oDao
					.queryBuilder();
			builder.where().eq("id", museumId);
			bean = builder.queryForFirst();
		} catch (SQLException e) {
		}
		MuseumModel museum = null;
		if (bean != null) {
			museum = new MuseumModel();
			museum.setName(bean.getName());
			museum.setTextUrl(bean.getTexturl());
			museum.setAudioUrl(bean.getAudiourl());
			List<String> imaList = new ArrayList<String>();
			String urls[] = bean.getImgurl().split(",");
			for (int i = 0; i < urls.length; i++) {
				imaList.add(Constant.getImageDownloadPath(urls[i], museumId));
			}
			museum.setImageList(imaList);
		}
		return museum;
	}

	@Override
	public List<ExhibitBean> getExhibitList(String museumId, int minPriority) {
		int page = 0;// TODO
		List<OfflineExhibitBean> offlineExhibitBeans = null;
		List<ExhibitBean> list = new ArrayList<ExhibitBean>();
		try {
			OfflineBeanSqlHelper helper = new OfflineBeanSqlHelper(
					new DatabaseContext(mContext, Constant.FLODER_NAME
							+ museumId), museumId + ".db");
			Dao<OfflineExhibitBean, Integer> oDao = helper
					.getOfflineExhibitDao();
			QueryBuilder<OfflineExhibitBean, Integer> builder = oDao
					.queryBuilder();
			builder.offset(Constant.PAGE_COUNT * page);
			builder.limit(Constant.PAGE_COUNT);
			offlineExhibitBeans = builder.query();
		} catch (SQLException e) {
		}
		if (offlineExhibitBeans == null)
			return null;
		for (OfflineExhibitBean offlineExhibitBean : offlineExhibitBeans) {
			String lables = offlineExhibitBean.getLabels();
			ExhibitBean bean = new ExhibitBean(offlineExhibitBean.getId(),
					offlineExhibitBean.getName(),
					offlineExhibitBean.getAddress(),
					offlineExhibitBean.getIntroduce(),
					Constant.getImageDownloadPath(
							offlineExhibitBean.getIconurl(), museumId), lables);
			list.add(bean);
		}
		offlineExhibitBeans.clear();
		offlineExhibitBeans = null;
		return list;

	}

	@Override
	public List<LabelModel> getLabelList(String museumId) {
		List<LabelModel> labels = null;
		try {
			OfflineBeanSqlHelper helper = new OfflineBeanSqlHelper(
					new DatabaseContext(mContext, Constant.FLODER_NAME
							+ museumId), museumId + ".db");
			Dao<OfflineLabelBean, Integer> oDao = helper.getOfflineLabelDao();
			List<OfflineLabelBean> list = oDao.queryForAll();
			labels = new ArrayList<LabelModel>();
			for (OfflineLabelBean offlineLabelBean : list) {
				LabelModel bean = new LabelModel(offlineLabelBean.getName(),
						Arrays.asList(offlineLabelBean.getLables().split(",")));
				labels.add(bean);
			}
		} catch (SQLException e) {
		}
		return labels;
	}

	@Override
	public ExhibitModel getExhibitModel(String museumId, String exhibitId) {
		OfflineExhibitBean bean = null;
		try {
			OfflineBeanSqlHelper helper = new OfflineBeanSqlHelper(
					new DatabaseContext(mContext, Constant.FLODER_NAME
							+ museumId), museumId + ".db");
			Dao<OfflineExhibitBean, Integer> oDao = helper
					.getOfflineExhibitDao();
			QueryBuilder<OfflineExhibitBean, Integer> builder = oDao
					.queryBuilder();
			builder.where().eq("id", exhibitId);
			bean = builder.queryForFirst();
		} catch (SQLException e) {
		}
		ExhibitModel exhibit = null;
		if (bean != null) {
			exhibit = new ExhibitModel();
			exhibit.setId(exhibitId);
			exhibit.setName(bean.getName());
			exhibit.setBeaconUId(bean.getBeaconId());
			exhibit.setIconUrl(Constant.getImageDownloadPath(bean.getIconurl(),
					museumId));
			exhibit.setAudioUrl(bean.getAudiourl());
			exhibit.setTextUrl(bean.getTexturl());
			exhibit.setlExhibitBeanId(bean.getLexhibit());
			exhibit.setrExhibitBeanId(bean.getRexhibit());
			String imgOptions[] = bean.getImgsurl().split(",");
			List<ImageModel> imgList = new ArrayList<ImageModel>();
			imgList.clear();
			String options[];
			for (int i = 0; i < imgOptions.length; i++) {
				options = imgOptions[i].split("\\*");
				ImageModel option = new ImageModel(
						Constant.getImageDownloadPath(options[0], museumId),
						Integer.valueOf(options[1]));
				imgList.add(option);
			}
			exhibit.setImgList(imgList);
			exhibit.setLabels(bean.getLabels());
		}
		return exhibit;
	}

	@Override
	public List<ExhibitModel> getExhibitModelsByBeaconId(String museumId,
			String beaconId) {
		List<OfflineExhibitBean> offlineList = null;
		try {
			OfflineBeanSqlHelper helper = new OfflineBeanSqlHelper(
					new DatabaseContext(mContext, Constant.FLODER_NAME
							+ museumId), museumId + ".db");
			Dao<OfflineExhibitBean, Integer> oDao = helper
					.getOfflineExhibitDao();
			QueryBuilder<OfflineExhibitBean, Integer> builder = oDao
					.queryBuilder();
			builder.where().eq("beacon_id", beaconId);
			offlineList = builder.query();

		} catch (SQLException e) {
		}
		if (offlineList == null)
			return null;
		List<ExhibitModel> exhibits = new ArrayList<ExhibitModel>();
		for (OfflineExhibitBean bean : offlineList) {
			ExhibitModel exhibit = new ExhibitModel();
			exhibit.setId(bean.getId());
			exhibit.setName(bean.getName());
			exhibit.setBeaconUId(bean.getBeaconId());
			exhibit.setIconUrl(Constant.getImageDownloadPath(bean.getIconurl(),
					museumId));
			exhibit.setAudioUrl(bean.getAudiourl());
			exhibit.setTextUrl(bean.getTexturl());
			exhibit.setlExhibitBeanId(bean.getLexhibit());
			exhibit.setrExhibitBeanId(bean.getRexhibit());
			String imgOptions[] = bean.getImgsurl().split(",");
			List<ImageModel> imgList = new ArrayList<ImageModel>();
			imgList.clear();
			String options[];
			for (int i = 0; i < imgOptions.length; i++) {
				options = imgOptions[i].split("\\*");
				ImageModel option = new ImageModel(
						Constant.getImageDownloadPath(options[0], museumId),
						Integer.valueOf(options[1]));
				imgList.add(option);
			}
			exhibit.setImgList(imgList);
			exhibit.setLabels(bean.getLabels());
			exhibits.add(exhibit);
		}
		return exhibits;
	}

	@Override
	public OfflineMapBean getMapBean(String museumId, int floor) {
		OfflineMapBean bean = null;
		try {
			OfflineBeanSqlHelper helper = new OfflineBeanSqlHelper(
					new DatabaseContext(mContext, Constant.FLODER_NAME
							+ museumId), museumId + ".db");
			Dao<OfflineMapBean, Integer> oDao = helper.getOfflineMapDao();
			QueryBuilder<OfflineMapBean, Integer> builder = oDao.queryBuilder();
			builder.where().eq("floor", floor);
			bean = builder.queryForFirst();
		} catch (SQLException e) {
		}
		return bean;
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
		List<MapExhibitModel> list = new ArrayList<MapExhibitModel>();
		try {
			OfflineBeanSqlHelper helper = new OfflineBeanSqlHelper(
					new DatabaseContext(mContext, Constant.FLODER_NAME
							+ museumId), museumId + ".db");
			Dao<OfflineExhibitBean, Integer> oDao = helper
					.getOfflineExhibitDao();
			QueryBuilder<OfflineExhibitBean, Integer> builder = oDao
					.queryBuilder();
			// TODO museumArea
			List<OfflineExhibitBean> offlineExhibitBeans = builder.query();
			for (OfflineExhibitBean offlineExhibitBean : offlineExhibitBeans) {
				MapExhibitModel bean = new MapExhibitModel();
				bean.setAddress(offlineExhibitBean.getAddress());
				bean.setName(offlineExhibitBean.getName());
				bean.setMapX(offlineExhibitBean.getMapx());
				bean.setMapY(offlineExhibitBean.getMapy());
				bean.setIconUrl(Constant.getImageDownloadPath(
						offlineExhibitBean.getIconurl(), museumId));
				list.add(bean);
			}
			offlineExhibitBeans.clear();
			offlineExhibitBeans = null;
		} catch (SQLException e) {
		}
		return list;
	}

	@Override
	public OfflineBeaconBean getBeaconBean(String museumId, String major,
			String minor) {
		OfflineBeaconBean bean = null;
		try {
			OfflineBeanSqlHelper helper = new OfflineBeanSqlHelper(
					new DatabaseContext(mContext, Constant.FLODER_NAME
							+ museumId), museumId + ".db");
			Dao<OfflineBeaconBean, Integer> oDao = helper.getOfflineBeaconDao();
			QueryBuilder<OfflineBeaconBean, Integer> builder = oDao
					.queryBuilder();
			builder.where().eq("major", major).eq("minor", minor);
			bean = builder.queryForFirst();
		} catch (SQLException e) {
		}
		return bean;
	}

	@Override
	public List<OfflineBeaconBean> getBeaconList(String museumId, int floor) {
		List<OfflineBeaconBean> list = null;
		try {
			OfflineBeanSqlHelper helper = new OfflineBeanSqlHelper(
					new DatabaseContext(mContext, Constant.FLODER_NAME
							+ museumId), museumId + ".db");
			Dao<OfflineBeaconBean, Integer> beaconDao = helper
					.getOfflineBeaconDao();
			QueryBuilder<OfflineBeaconBean, Integer> builder = beaconDao
					.queryBuilder();
			builder.where().eq("floor", floor);
			list = builder.query();
		} catch (SQLException e) {
		}
		return list;
	}

	@Override
	public List<DownloadBean> getDownloadList() {
		// TODO Auto-generated method stub
		return null;
	}

}
