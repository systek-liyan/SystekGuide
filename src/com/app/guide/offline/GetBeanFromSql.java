package com.app.guide.offline;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.content.Context;

import com.app.guide.Constant;
import com.app.guide.bean.Exhibit;
import com.app.guide.bean.ExhibitBean;
import com.app.guide.bean.ImageOption;
import com.app.guide.bean.LabelBean;
import com.app.guide.bean.MapExhibitBean;
import com.app.guide.bean.MuseumBean;
import com.app.guide.bean.MuseumDetailBean;
import com.app.guide.bean.PointM;
import com.app.guide.download.DownloadBean;
import com.app.guide.sql.DatabaseContext;
import com.app.guide.sql.DownloadManagerHelper;
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

	/**
	 * 从数据库中获取某一地图中所有展品的列表。<br>
	 * 这里，一个博物馆的一层表示一张地图
	 */
	public static List<MapExhibitBean> getMapExhibit(Context context,
			String museumid, int floor) throws SQLException {
		List<MapExhibitBean> list = new ArrayList<MapExhibitBean>();

		OfflineBeanSqlHelper helper = new OfflineBeanSqlHelper(
				new DatabaseContext(context, Constant.FLODER_NAME + museumid),
				museumid + ".db");
		Dao<OfflineExhibitBean, Integer> oDao = helper.getOfflineExhibitDao();
		QueryBuilder<OfflineExhibitBean, Integer> builder = oDao.queryBuilder();
		List<OfflineExhibitBean> offlineExhibitBeans = builder.query();
		for (OfflineExhibitBean offlineExhibitBean : offlineExhibitBeans) {
			MapExhibitBean bean = new MapExhibitBean();
			bean.setAddress(offlineExhibitBean.getAddress());
			bean.setName(offlineExhibitBean.getName());
			bean.setMapX(offlineExhibitBean.getMapx());
			bean.setMapY(offlineExhibitBean.getMapy());
			bean.setIconUrl(Constant.getImageDownloadPath(
					offlineExhibitBean.getIconurl(), museumid));
			list.add(bean);
		}
		offlineExhibitBeans.clear();
		offlineExhibitBeans = null;
		return list;
	}

	
	/**
	 * 从数据库中获取整个博物馆下的所有展品
	 */
	@SuppressWarnings("deprecation")
	public static List<ExhibitBean> getExhibitBeans(Context context,
			String muesumid, int page) throws SQLException {
		List<ExhibitBean> list = new ArrayList<ExhibitBean>();

		OfflineBeanSqlHelper helper = new OfflineBeanSqlHelper(
				new DatabaseContext(context, Constant.FLODER_NAME + muesumid),
				muesumid + ".db");
		Dao<OfflineExhibitBean, Integer> oDao = helper.getOfflineExhibitDao();
		QueryBuilder<OfflineExhibitBean, Integer> builder = oDao.queryBuilder();
		builder.offset(Constant.PAGE_COUNT * page);
		builder.limit(Constant.PAGE_COUNT);
		List<OfflineExhibitBean> offlineExhibitBeans = builder.query();
		for (OfflineExhibitBean offlineExhibitBean : offlineExhibitBeans) {
			String lables = offlineExhibitBean.getLabels();
			ExhibitBean bean = new ExhibitBean(offlineExhibitBean.getId(),
					offlineExhibitBean.getName(),
					offlineExhibitBean.getAddress(),
					offlineExhibitBean.getIntroduce(),
					Constant.getImageDownloadPath(
							offlineExhibitBean.getIconurl(), muesumid), lables);
			list.add(bean);
		}
		offlineExhibitBeans.clear();
		offlineExhibitBeans = null;
		return list;
	}

	/**
	 * 从数据库中获取某一博物馆中某一层中所有的beacon的位置
	 */
	public static HashMap<String, PointM> getLoactionPoint(Context context,
			String museumid, int floor) throws SQLException {
		HashMap<String, PointM> map = new HashMap<String, PointM>();
		OfflineBeanSqlHelper helper = new OfflineBeanSqlHelper(
				new DatabaseContext(context, Constant.FLODER_NAME + museumid),
				museumid + ".db");
		Dao<OfflineBeaconBean, Integer> beaconDao = helper
				.getOfflineBeaconDao();
		QueryBuilder<OfflineBeaconBean, Integer> builder = beaconDao
				.queryBuilder();
		builder.where().eq("floor", floor);
		List<OfflineBeaconBean> list = builder.query();
		for (OfflineBeaconBean offlineBean : list) {
			PointM pointM = new PointM();
			pointM.setMapX(offlineBean.getPersonx());
			pointM.setMapY(offlineBean.getPersony());
			map.put(offlineBean.getUuid(), pointM);
		}
		list.clear();
		list = null;
		return map;
	}

	/**
	 * 根据exhibitId获取展品详细信息
	 */
	public static Exhibit getExhibit(Context context, String museumId,
			String exhibitId) throws SQLException {
		OfflineBeanSqlHelper helper = new OfflineBeanSqlHelper(
				new DatabaseContext(context, Constant.FLODER_NAME + museumId),
				museumId + ".db");
		Dao<OfflineExhibitBean, Integer> oDao = helper.getOfflineExhibitDao();
		QueryBuilder<OfflineExhibitBean, Integer> builder = oDao.queryBuilder();
		builder.where().eq("id", exhibitId);
		OfflineExhibitBean bean = builder.queryForFirst();
		Exhibit exhibit = null;
		if (bean != null) {
			exhibit = new Exhibit();
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
			List<ImageOption> imgList = new ArrayList<ImageOption>();
			imgList.clear();
			String options[] ;
			for (int i = 0; i < imgOptions.length; i++) {
				options = imgOptions[i].split("\\*");
				ImageOption option = new ImageOption(
						Constant.getImageDownloadPath(options[0], museumId),
						Integer.valueOf(options[1]));
				imgList.add(option);
			}
			exhibit.setImgList(imgList);
			exhibit.setLabels(bean.getLabels());
		}
		return exhibit;
	}

	/**
	 * 获取所有已下载的博物馆的列表 
	 */
	public static List<MuseumBean> getMuseumBeans(Context context)
			throws SQLException {
		DownloadManagerHelper helper = new DownloadManagerHelper(context);
		Dao<MuseumBean, Integer> downloaDao = helper.getDownloadedDao();
		List<MuseumBean> list = new ArrayList<MuseumBean>();
		List<DownloadBean> downloadBeans = helper.getBeanDao().queryForAll();
		for (DownloadBean downloadBean : downloadBeans) {
			if (downloadBean.isCompleted()) {
				MuseumBean bean = downloaDao.queryBuilder().where()
						.eq("museumId", downloadBean.getMuseumId()).queryForFirst();
				list.add(bean);
			}
		}
		return list;
	}

	/**
	 * 获取某一博物馆下所有的标签列表（即该博物馆中所有的标签）
	 */
	public static List<LabelBean> getLabelBeans(Context context, String museumId)
			throws SQLException {
		OfflineBeanSqlHelper helper = new OfflineBeanSqlHelper(
				new DatabaseContext(context, Constant.FLODER_NAME + museumId),
				museumId + ".db");
		Dao<OfflineLabelBean, Integer> oDao = helper.getOfflineLabelDao();
		List<OfflineLabelBean> list = oDao.queryForAll();
		List<LabelBean> labels = new ArrayList<LabelBean>();
		for (OfflineLabelBean offlineLabelBean : list) {
			LabelBean bean = new LabelBean(offlineLabelBean.getName(),
					Arrays.asList(offlineLabelBean.getLables().split(",")));
			labels.add(bean);
		}
		return labels;
	}
	
	/**
	 * 通过beaconId 获取该beacon管理的展品列表
	 * 
	 * @param context
	 * @param museumId
	 * @param beaconId
	 * @return
	 * @throws SQLException
	 */
	public static List<Exhibit> getExhibitsByBeaconId(Context context, String museumId,String beaconId)
					throws SQLException{
		OfflineBeanSqlHelper helper = new OfflineBeanSqlHelper(
				new DatabaseContext(context, Constant.FLODER_NAME + museumId),
				museumId + ".db");
		Dao<OfflineExhibitBean, Integer> oDao = helper.getOfflineExhibitDao();
		QueryBuilder<OfflineExhibitBean, Integer> builder = oDao.queryBuilder();
		builder.where().eq("beacon_id", beaconId);
		List<OfflineExhibitBean> offlineList = builder.query();
		List<Exhibit> exhibits = new ArrayList<Exhibit>();
		for(OfflineExhibitBean bean: offlineList) {
			Exhibit exhibit = new Exhibit();
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
			List<ImageOption> imgList = new ArrayList<ImageOption>();
			imgList.clear();
			String options[] ;
			for (int i = 0; i < imgOptions.length; i++) {
				options = imgOptions[i].split("\\*");
				ImageOption option = new ImageOption(
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
	
	/**
	 * 通过beacon的minor 和major 获取offlineBeaconBean
	 * 
	 * @param context
	 * @param museumId
	 * @param minor
	 * @param major
	 * @return
	 */
	public static OfflineBeaconBean getBeaconBean(Context context,String museumId, String minor,String major)
			throws SQLException{
		OfflineBeanSqlHelper helper = new OfflineBeanSqlHelper(
				new DatabaseContext(context, Constant.FLODER_NAME + museumId), 
				museumId+".db");
		Dao<OfflineBeaconBean, Integer> oDao = helper.getOfflineBeaconDao();
		QueryBuilder<OfflineBeaconBean, Integer> builder = oDao.queryBuilder();
		builder.where().eq("major", major).eq("minor", minor);
		OfflineBeaconBean bean = builder.queryForFirst();
		return bean;
		
	}

	/**
	 * 从数据库中获取某一博物馆某一楼层地图的bean
	 * 
	 * @param context
	 * @param museumId
	 * @return
	 * @throws SQLException 
	 */
	public static OfflineMapBean getMapBean(Context context, String museumId, int floor) 
			throws SQLException{
		OfflineBeanSqlHelper helper = new OfflineBeanSqlHelper(
				new DatabaseContext(context, Constant.FLODER_NAME+museumId), 
				museumId+".db");
		Dao<OfflineMapBean,Integer> oDao = helper.getOfflineMapDao();
		QueryBuilder<OfflineMapBean, Integer> builder = oDao.queryBuilder();
		builder.where().eq("floor", floor);
		OfflineMapBean bean = builder.queryForFirst();
		return bean;
	}
	
	
	/**
	 * 根据博物馆id获取博物馆的详细bean，用以展示博物馆 
	 */
	public static MuseumDetailBean getMuseunDetailBean(Context context,
			String museumId) throws SQLException {
		OfflineBeanSqlHelper helper = new OfflineBeanSqlHelper(
				new DatabaseContext(context, Constant.FLODER_NAME + museumId),
				museumId + ".db");
		Dao<OfflineMuseumBean, Integer> oDao = helper.getOfflineMuseumDao();
		QueryBuilder<OfflineMuseumBean, Integer> builder = oDao.queryBuilder();
		builder.where().eq("id", museumId);
		OfflineMuseumBean bean = builder.queryForFirst();
		MuseumDetailBean museum = null;
		if (bean != null) {
			museum = new MuseumDetailBean();
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

	/**
	 * 根据博物馆id获取该博物馆的层数
	 */
	public static int getFloorCount(Context context, String museumId)
			throws SQLException {
		OfflineBeanSqlHelper helper = new OfflineBeanSqlHelper(
				new DatabaseContext(context, Constant.FLODER_NAME + museumId),
				museumId + ".db");
		OfflineMuseumBean bean = helper.getOfflineMuseumDao().queryBuilder()
				.where().eq("id", museumId).queryForFirst();
		if (bean != null) {
			return bean.getFloorcount();
		} else {
			return 1;
		}
	}

	/**
	 * 获取所有正在下载中的bean 
	 */
	public static List<DownloadBean> getDownloadingBeans(Context context)
			throws SQLException {
		DownloadManagerHelper helper = new DownloadManagerHelper(context);
		List<DownloadBean> list = helper.getBeanDao().queryBuilder().where()
				.eq("isCompleted", false).query();
		return list;
	}

	/**
	 * 获取所有已下载完成的bean 
	 */
	public static List<DownloadBean> getDownloadCompletedBeans(Context context)
			throws SQLException {
		DownloadManagerHelper helper = new DownloadManagerHelper(context);
		List<DownloadBean> list = helper.getBeanDao().queryBuilder().where()
				.eq("isCompleted", true).query();
		return list;
	}
}
