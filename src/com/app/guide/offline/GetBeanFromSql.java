package com.app.guide.offline;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.app.guide.Constant;
import com.app.guide.bean.DownloadBean;
import com.app.guide.bean.Exhibit;
import com.app.guide.bean.ExhibitBean;
import com.app.guide.bean.ImageOption;
import com.app.guide.bean.LabelBean;
import com.app.guide.bean.MapExhibitBean;
import com.app.guide.bean.MuseumBean;
import com.app.guide.bean.MuseumDetailBean;
import com.app.guide.bean.PointM;
import com.app.guide.sql.DatabaseContext;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

/**
 * 从本地数据库中获得数据信息
 * 
 * @author joe_c
 * 
 */
public class GetBeanFromSql {
	private static final String TAG = GetBeanFromSql.class.getSimpleName();
	private static final String FLOADER = "Guide/Test";

	public static List<MapExhibitBean> getMapExhibit(Context context,
			int museumid, int floor) throws SQLException {
		List<MapExhibitBean> list = new ArrayList<MapExhibitBean>();

		OfflineBeanSqlHelper helper = new OfflineBeanSqlHelper(
				new DatabaseContext(context, FLOADER), museumid + ".db");
		Dao<OfflineExhibitBean, Integer> oDao = helper.getOfflineExhibitDao();
		QueryBuilder<OfflineExhibitBean, Integer> builder = oDao.queryBuilder();
		List<OfflineExhibitBean> offlineExhibitBeans = builder.query();
		Log.w(TAG, "" + offlineExhibitBeans.size());
		for (OfflineExhibitBean offlineExhibitBean : offlineExhibitBeans) {
			MapExhibitBean bean = new MapExhibitBean();
			bean.setId(offlineExhibitBean.getId());
			bean.setAddress(offlineExhibitBean.getAddress());
			bean.setName(offlineExhibitBean.getName());
			bean.setMapX(offlineExhibitBean.getMapX());
			bean.setMapY(offlineExhibitBean.getMapY());
			list.add(bean);
			Log.w(TAG, "add-once");
		}
		offlineExhibitBeans.clear();
		offlineExhibitBeans = null;
		return list;
	}

	@SuppressWarnings("deprecation")
	public static List<ExhibitBean> getExhibitBeans(Context context,
			int muesumid, int page) throws SQLException {
		List<ExhibitBean> list = new ArrayList<ExhibitBean>();

		OfflineBeanSqlHelper helper = new OfflineBeanSqlHelper(
				new DatabaseContext(context, FLOADER), muesumid + ".db");
		Dao<OfflineExhibitBean, Integer> oDao = helper.getOfflineExhibitDao();
		QueryBuilder<OfflineExhibitBean, Integer> builder = oDao.queryBuilder();
		builder.offset(Constant.PAGE_COUNT * page);
		builder.limit(Constant.PAGE_COUNT);
		List<OfflineExhibitBean> offlineExhibitBeans = builder.query();
		for (OfflineExhibitBean offlineExhibitBean : offlineExhibitBeans) {
			HashMap<String, String> map = JSON.parseObject(offlineExhibitBean.getLabelJson(),
					new TypeReference<HashMap<String, String>>() {
					});
			ExhibitBean bean = new ExhibitBean(offlineExhibitBean.getId(),offlineExhibitBean.getName(),
					offlineExhibitBean.getAddress(),
					offlineExhibitBean.getIntroduce(),
					offlineExhibitBean.getIconUrl(),
					map);
			list.add(bean);
		}
		offlineExhibitBeans.clear();
		offlineExhibitBeans = null;
		return list;
	}

	public static HashMap<String, PointM> getLoactionPoint(Context context,
			int museumid, int floor) throws SQLException {
		HashMap<String, PointM> map = new HashMap<String, PointM>();
		OfflineBeanSqlHelper helper = new OfflineBeanSqlHelper(
				new DatabaseContext(context, FLOADER), museumid + ".db");
		Dao<OfflineBeaconBean, Integer> beaconDao = helper
				.getOfflineBeaconDao();
		QueryBuilder<OfflineBeaconBean, Integer> builder = beaconDao
				.queryBuilder();
		builder.where().eq("floor", floor);
		List<OfflineBeaconBean> list = builder.query();
		for (OfflineBeaconBean offlineBean : list) {
			PointM pointM = new PointM();
			pointM.setMapX(offlineBean.getPersonX());
			pointM.setMapY(offlineBean.getPersonY());
			map.put(offlineBean.getUuid(), pointM);
		}
		list.clear();
		list = null;
		return map;
	}

	public static Exhibit getExhibit(Context context, int museumId, int exhibitId)
			throws SQLException {
		OfflineBeanSqlHelper helper = new OfflineBeanSqlHelper(
				new DatabaseContext(context, FLOADER), museumId + ".db");
		Dao<OfflineExhibitBean, Integer> oDao = helper.getOfflineExhibitDao();
		QueryBuilder<OfflineExhibitBean, Integer> builder = oDao.queryBuilder();
		builder.where().eq("id", exhibitId);
		OfflineExhibitBean bean = builder.queryForFirst();
		Exhibit exhibit = null;
		if (bean != null) {
			exhibit = new Exhibit();
			exhibit.setId(exhibitId);
			exhibit.setName(bean.getName());
			exhibit.setBeaconUId(bean.getBeaconUId());
			exhibit.setIconUrl(bean.getIconUrl());
			exhibit.setAudioUrl(bean.getAudioUrl());
			exhibit.setlExhibitBeanId(bean.getlExhibitBeanId());
			exhibit.setrExhibitBeanId(bean.getrExhibitBeanId());
			List<ImageOption> imgList = JSON.parseArray(bean.getImgJson(),
					ImageOption.class);
			exhibit.setImgList(imgList);
			HashMap<String, String> map = JSON.parseObject(bean.getLabelJson(),
					new TypeReference<HashMap<String, String>>() {
					});
			exhibit.setLabels(map);
		}
		return exhibit;
	}

	public static MuseumDetailBean getMuseunDetailBean(Context context,
			int museumId) throws SQLException {
		OfflineBeanSqlHelper helper = new OfflineBeanSqlHelper(
				new DatabaseContext(context, FLOADER), museumId + ".db");
		Dao<OfflineMuseumBean, Integer> oDao = helper.getOfflineMuseumDao();
		QueryBuilder<OfflineMuseumBean, Integer> builder = oDao.queryBuilder();
		builder.where().eq("id", museumId);
		OfflineMuseumBean bean = builder.queryForFirst();
		MuseumDetailBean museum = null;
		if (bean != null) {
			museum = new MuseumDetailBean();
			museum.setName(bean.getName());
			museum.setTextUrl(bean.getTextUrl());
			museum.setAudioUrl(bean.getAudioUrl());
			List<String> imaList = JSON.parseArray(bean.getImgList(),
					String.class);
			museum.setImageList(imaList);
		}
		return museum;
	}

	public static List<MuseumBean> getMuseumBeans(Context context)
			throws SQLException {
		DownloadManagerHelper helper = new DownloadManagerHelper(context);
		Dao<DownloadBean, Integer> downloaDao = helper.getDownloadDao();
		List<DownloadBean> list = downloaDao.queryForAll();
		List<MuseumBean> museumBeans = new ArrayList<MuseumBean>();
		for (DownloadBean bean : list) {
			MuseumBean museumBean = new MuseumBean();
			museumBean.setId(bean.getId());
			museumBean.setAddress(bean.getAddress());
			museumBean.setIconUrl(bean.getIconUrl());
			museumBean.setLongitudX(bean.getLongitudX());
			museumBean.setLongitudY(bean.getLongitudY());
			museumBean.setName(bean.getName());
			museumBean.setOpen(bean.isOpen());
			museumBean.setOpentime(bean.getOpentime());
			museumBeans.add(museumBean);
		}
		list.clear();
		list = null;
		return museumBeans;
	}

	public static List<LabelBean> getLabelBeans(Context context, int museumId)
			throws SQLException {
		OfflineBeanSqlHelper helper = new OfflineBeanSqlHelper(
				new DatabaseContext(context, FLOADER), museumId + ".db");
		Dao<OfflineLabelBean, Integer> oDao = helper.getOfflineLabelDao();
		List<OfflineLabelBean> list = oDao.queryForAll();
		List<LabelBean> labels = new ArrayList<LabelBean>();
		for (OfflineLabelBean offlineLabelBean : list) {
			LabelBean bean = new LabelBean(offlineLabelBean.getName(),
					JSON.parseArray(offlineLabelBean.getLabels(), String.class));
			labels.add(bean);
		}
		return labels;
	}
}
