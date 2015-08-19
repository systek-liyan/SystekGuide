package com.app.guide.beanhelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.app.guide.Constant;
import com.app.guide.bean.ExhibitBean;
import com.app.guide.bean.MuseumAreaBean;
import com.app.guide.bean.MuseumBean;
import com.app.guide.download.DownloadBean;
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
 * 从本地数据库（离线数据包）中获取各种bean //出错时应如何处理TODO
 * 
 * @author yetwish
 * 
 */
public class GetBeanFromDB extends GetBeanStrategy {

	private static final String TAG = GetBeanFromDB.class.getSimpleName();

	public GetBeanFromDB(Context context) {
		super(context);
	}

	// TODO 下载任务完成后，再调用此函数，从离线博物馆表中填充表MuseumBean，条件Downloadben isCompleted=true
	@Override
	public void getMuseumList(String city,
			GetBeanCallBack<List<MuseumBean>> callBack) {
		List<MuseumBean> list = new ArrayList<MuseumBean>();
		try {
			// 外部数据库Context
			Context dContext = new DatabaseContext(mContext, Constant.FLODER_NAME);
			DownloadManagerHelper dbHelper = new DownloadManagerHelper(dContext,"Download.db");
			
			Dao<MuseumBean, String> downloaDao = dbHelper.getDownloadedDao();
			List<DownloadBean> downloadBeans = dbHelper.getDownloadBeanDao().queryForAll();
			for (DownloadBean downloadBean : downloadBeans) {
				if (downloadBean.isCompleted()) {
					MuseumBean bean = downloaDao.queryBuilder().where().eq("museumId", downloadBean.getMuseumId()).queryForFirst();
					list.add(bean);
				}
			}
			callBack.onGetBeanResponse(list);
		} catch (SQLException e) {
		}
	}

 
	@Override
	public void getMuseumModel(String museumId,
			GetBeanCallBack<MuseumModel> callBack) {
		OfflineMuseumBean bean = null;
		try {
			OfflineBeanSqlHelper helper = new OfflineBeanSqlHelper(
					new DatabaseContext(mContext, Constant.FLODER_NAME
							+ museumId), museumId + ".db");
			Dao<OfflineMuseumBean, String> oDao = helper.getOfflineMuseumDao();
			QueryBuilder<OfflineMuseumBean, String> builder = oDao
					.queryBuilder();
			builder.where().eq("id", museumId);
			bean = builder.queryForFirst();
		} catch (SQLException e) {
		}
		MuseumModel museum = null;
		if (bean != null) {
			museum = new MuseumModel();
			museum.setName(bean.getName());
			museum.setIntroduce(bean.getTexturl());
			museum.setAudioUrl(bean.getAudiourl());
			museum.setFloorCount(bean.getFloorcount());
			List<String> imaList = new ArrayList<String>();
			String urls[] = bean.getImgurl().split(",");
			for (int i = 0; i < urls.length; i++) {
				imaList.add(Constant.getImageDownloadPath(urls[i], museumId));
			}
			museum.setImgsUrl(imaList);
		}
		callBack.onGetBeanResponse(museum);
	}

	/**
	 * 获取展品ExhibitBean列表 内部使用分页（从0开始）
	 * 
	 * @param museumId 博物馆id
	 * @param minPriority 最小优先级(即，比该优先级大的博物馆是推荐的博物馆)
	 * @param page 0,1,2,3... 表示第1,2,3...页
	 * @param callBack ExhibitBean列表
	 */
	@Override
	public void getExhibitList(String museumId, int minPriority,int page,
			GetBeanCallBack<List<ExhibitBean>> callBack) {
		List<OfflineExhibitBean> offlineExhibitBeans = null;
		List<ExhibitBean> list = new ArrayList<ExhibitBean>();
		try {
			OfflineBeanSqlHelper helper = new OfflineBeanSqlHelper(
					new DatabaseContext(mContext, Constant.FLODER_NAME
							+ museumId), museumId + ".db");
			Dao<OfflineExhibitBean, String> oDao = helper
					.getOfflineExhibitDao();
			QueryBuilder<OfflineExhibitBean, String> builder = oDao
					.queryBuilder();
			builder.offset((long)Constant.PAGE_COUNT * page);
			builder.limit((long)Constant.PAGE_COUNT);
			offlineExhibitBeans = builder.query();
		} catch (SQLException e) {
		}
		if (offlineExhibitBeans == null) {
			Log.d(TAG, "database "+ museumId + ".db, has not the table OfflineExhibitBean!!");
			return; 
		}
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
		callBack.onGetBeanResponse(list);
	}
	
	/**
	 * 获得名称中含有name的展品列表
	 * @param name
	 * @param callBack null 表示无此条件的展品返回
	 */
	@Override
	public void getExhibitList_name(String museumId,String name,
			GetBeanCallBack<List<ExhibitBean>> callBack) {
		List<OfflineExhibitBean> offlineExhibitBeans = null;
		List<ExhibitBean> list = new ArrayList<ExhibitBean>();
		try {
			OfflineBeanSqlHelper helper = new OfflineBeanSqlHelper(
					new DatabaseContext(mContext, Constant.FLODER_NAME
							+ museumId), museumId + ".db");
			Dao<OfflineExhibitBean, String> oDao = helper
					.getOfflineExhibitDao();
			QueryBuilder<OfflineExhibitBean, String> builder = oDao
					.queryBuilder();
			builder.where().like("name", "%"+name+"%");
			offlineExhibitBeans = builder.query();
		} catch (SQLException e) {
		}
		
		//  无此条件的展品返回null
		if (offlineExhibitBeans == null) {
			callBack.onGetBeanResponse(null);
			return; 
		}
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
		callBack.onGetBeanResponse(list);
	}

	@Override
	public void getLabelList(String museumId,
			GetBeanCallBack<List<LabelModel>> callBack) {
		List<LabelModel> labels = null;
		try {
			OfflineBeanSqlHelper helper = new OfflineBeanSqlHelper(
					new DatabaseContext(mContext, Constant.FLODER_NAME
							+ museumId), museumId + ".db");
			Dao<OfflineLabelBean, String> oDao = helper.getOfflineLabelDao();
			List<OfflineLabelBean> list = oDao.queryForAll();
			labels = new ArrayList<LabelModel>();
			for (OfflineLabelBean offlineLabelBean : list) {
				LabelModel bean = new LabelModel(offlineLabelBean.getName(),
						Arrays.asList(offlineLabelBean.getLables().split(",")));
				labels.add(bean);
			}
			callBack.onGetBeanResponse(labels);
		} catch (SQLException e) {

		}

	}

	@Override
	public void getExhibitModel(String museumId, String exhibitId,
			GetBeanCallBack<ExhibitModel> callBack) {
		OfflineExhibitBean bean = null;
		try {
			OfflineBeanSqlHelper helper = new OfflineBeanSqlHelper(
					new DatabaseContext(mContext, Constant.FLODER_NAME
							+ museumId), museumId + ".db");
			Dao<OfflineExhibitBean, String> oDao = helper
					.getOfflineExhibitDao();
			QueryBuilder<OfflineExhibitBean, String> builder = oDao
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
			
			List<ImageModel> imgList = new ArrayList<ImageModel>();
			// 实际上，无多角度的情况，在OfflineDownloadHelper.java中的downloadExhibit()已经处理，这里可以安全使用
			// 如果没有多角度图片，用主图作为第一个多角度图片,从0开始播放
			if (bean.getImgsurl() == null || bean.getImgsurl().equals(""))  {
				ImageModel option = new ImageModel(exhibit.getIconUrl(),0);
				imgList.add(option);
			}
			else {
				String imgOptions[] = bean.getImgsurl().split(",");
				String options[];
				for (int i = 0; i < imgOptions.length; i++) {
					options = imgOptions[i].split("\\*");
					ImageModel option = new ImageModel(
							Constant.getImageDownloadPath(options[0], museumId),
							Integer.valueOf(options[1]));
					imgList.add(option);
				}
			}
			exhibit.setImgList(imgList);
			exhibit.setLabels(bean.getLabels());
		}
		callBack.onGetBeanResponse(exhibit);
	}

	@Override
	public void getExhibitModelsByBeaconId(String museumId, String beaconId,
			GetBeanCallBack<List<ExhibitModel>> callBack) {
		List<OfflineExhibitBean> offlineList = null;
		try {
			OfflineBeanSqlHelper helper = new OfflineBeanSqlHelper(
					new DatabaseContext(mContext, Constant.FLODER_NAME
							+ museumId), museumId + ".db");
			Dao<OfflineExhibitBean, String> oDao = helper
					.getOfflineExhibitDao();
			QueryBuilder<OfflineExhibitBean, String> builder = oDao
					.queryBuilder();
			builder.where().eq("beaconId", beaconId);
			offlineList = builder.query();

		} catch (SQLException e) {
		}
		if (offlineList == null) {
			Log.d(TAG, "database "+ museumId + ".db, has not the table OfflineExhibitBean!!");
			return; 
		}
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
			// 无多角度的情况，在OfflineDownloadHelper.java中的downloadExhibit()已经处理，这里可以安全使用
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
		callBack.onGetBeanResponse(exhibits);
	}

	@Override
	public void getMapBean(String museumId, int floor,
			GetBeanCallBack<OfflineMapBean> callBack) {
		OfflineMapBean bean = null;
		try {
			OfflineBeanSqlHelper helper = new OfflineBeanSqlHelper(
					new DatabaseContext(mContext, Constant.FLODER_NAME
							+ museumId), museumId + ".db");
			Dao<OfflineMapBean, String> oDao = helper.getOfflineMapDao();
			QueryBuilder<OfflineMapBean, String> builder = oDao.queryBuilder();
			builder.where().eq("floor", floor);
			bean = builder.queryForFirst();
			callBack.onGetBeanResponse(bean);
		} catch (SQLException e) {
		}
	}

	@Override
	public void getMapList(String museumId,
			GetBeanCallBack<List<OfflineMapBean>> callBack) {
		List<OfflineMapBean> list = new ArrayList<OfflineMapBean>();
		try {
			OfflineBeanSqlHelper helper = new OfflineBeanSqlHelper(
					new DatabaseContext(mContext, Constant.FLODER_NAME
							+ museumId), museumId + ".db");
			Dao<OfflineMapBean, String> oDao = helper.getOfflineMapDao();
			QueryBuilder<OfflineMapBean, String> builder = oDao.queryBuilder();
			list = builder.query();
			callBack.onGetBeanResponse(list);
		} catch (SQLException e) {
		}

	}

	@Override
	public void getMuseumAreaList(String museumId, int floor,
			GetBeanCallBack<List<MuseumAreaBean>> callBack) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getMapExhibitList(String museumId, String museumAreaId,
			GetBeanCallBack<List<MapExhibitModel>> callBack) {
		List<MapExhibitModel> list = new ArrayList<MapExhibitModel>();
		try {
			OfflineBeanSqlHelper helper = new OfflineBeanSqlHelper(
					new DatabaseContext(mContext, Constant.FLODER_NAME
							+ museumId), museumId + ".db");
			Dao<OfflineExhibitBean, String> oDao = helper
					.getOfflineExhibitDao();
			QueryBuilder<OfflineExhibitBean, String> builder = oDao
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
			callBack.onGetBeanResponse(list);
		} catch (SQLException e) {
		}

	}

	@Override
	public void getBeaconBean(String museumId, String major, String minor,
			GetBeanCallBack<OfflineBeaconBean> callBack) {
		OfflineBeaconBean bean = null;
		try {
			OfflineBeanSqlHelper helper = new OfflineBeanSqlHelper(
					new DatabaseContext(mContext, Constant.FLODER_NAME
							+ museumId), museumId + ".db");
			Dao<OfflineBeaconBean, String> oDao = helper.getOfflineBeaconDao();
			QueryBuilder<OfflineBeaconBean, String> builder = oDao
					.queryBuilder();
			builder.where().eq("major", major).and().eq("minor", minor);
			bean = builder.queryForFirst();
			callBack.onGetBeanResponse(bean);
		} catch (SQLException e) {
		}

	}

	@Override
	public void getBeaconList(String museumId, int floor,
			GetBeanCallBack<List<OfflineBeaconBean>> callBack) {
		List<OfflineBeaconBean> list = null;
		try {
			OfflineBeanSqlHelper helper = new OfflineBeanSqlHelper(
					new DatabaseContext(mContext, Constant.FLODER_NAME
							+ museumId), museumId + ".db");
			Dao<OfflineBeaconBean, String> beaconDao = helper
					.getOfflineBeaconDao();
			QueryBuilder<OfflineBeaconBean, String> builder = beaconDao
					.queryBuilder();
			builder.where().eq("floor", floor);
			list = builder.query();
			callBack.onGetBeanResponse(list);
		} catch (SQLException e) {
		}

	}


}
