package com.app.guide.offline;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.app.guide.Constant;
import com.app.guide.bean.ExhibitBean;
import com.app.guide.bean.MapExhibitBean;
import com.app.guide.bean.PointM;
import com.app.guide.sql.DatabaseContext;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

public class GetBeanFromSql {
	private static final String TAG = GetBeanFromSql.class.getSimpleName();

	public static List<MapExhibitBean> getMapExhibit(Context context,
			String museumid, int floor) throws SQLException {
		List<MapExhibitBean> list = new ArrayList<MapExhibitBean>();

		OfflineBeanSqlHelper helper = new OfflineBeanSqlHelper(
				new DatabaseContext(context, "Test"), museumid + ".db");
		Dao<OfflineExhibitBean, Integer> oDao = helper.getOfflineExhibitDao();
		QueryBuilder<OfflineExhibitBean, Integer> builder = oDao.queryBuilder();
		List<OfflineExhibitBean> offlineExhibitBeans = builder.query();
		Log.w(TAG, "" + offlineExhibitBeans.size());
		for (OfflineExhibitBean offlineExhibitBean : offlineExhibitBeans) {
			MapExhibitBean bean = new MapExhibitBean();
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

	public static List<ExhibitBean> getExhibitBeans(Context context,
			String muesumid, int page) throws SQLException {
		List<ExhibitBean> list = new ArrayList<ExhibitBean>();

		OfflineBeanSqlHelper helper = new OfflineBeanSqlHelper(
				new DatabaseContext(context, "Test"), muesumid + ".db");
		Dao<OfflineExhibitBean, Integer> oDao = helper.getOfflineExhibitDao();
		QueryBuilder<OfflineExhibitBean, Integer> builder = oDao.queryBuilder();
		builder.offset(Constant.PAGE_COUNT * page);
		builder.limit(Constant.PAGE_COUNT);
		List<OfflineExhibitBean> offlineExhibitBeans = builder.query();
		for (OfflineExhibitBean offlineExhibitBean : offlineExhibitBeans) {
			ExhibitBean bean = new ExhibitBean(offlineExhibitBean.getName(),
					offlineExhibitBean.getAddress(),
					offlineExhibitBean.getIntroduce(),
					offlineExhibitBean.getIconUrl());
			list.add(bean);
		}
		offlineExhibitBeans.clear();
		offlineExhibitBeans = null;
		return list;
	}

	public static HashMap<String, PointM> getLoactionPoint(Context context,
			String museumid, int floor) throws SQLException {
		HashMap<String, PointM> map = new HashMap<String, PointM>();
		OfflineBeanSqlHelper helper = new OfflineBeanSqlHelper(
				new DatabaseContext(context, "Test"), museumid + ".db");
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

}
