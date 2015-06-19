package com.app.guide.offline;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.app.guide.bean.MuseumBean;
import com.app.guide.download.DownloadBean;
import com.app.guide.download.DownloadInfo;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * 管理已下载的离线数据包
 * 
 * @author joe_c
 *
 */
public class DownloadManagerHelper extends OrmLiteSqliteOpenHelper {

	private Dao<MuseumBean, Integer> downloadedDao;
	private Dao<DownloadInfo, Integer> infoDao;
	private Dao<DownloadBean, Integer> beanDao;

	public DownloadManagerHelper(Context context) {
		super(context, "Download", null, 1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase arg0, ConnectionSource arg1) {
		// TODO Auto-generated method stub
		try {
			TableUtils.createTableIfNotExists(arg1, MuseumBean.class);
			TableUtils.createTableIfNotExists(arg1, DownloadInfo.class);
			TableUtils.createTableIfNotExists(arg1, DownloadBean.class);
		} catch (java.sql.SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, ConnectionSource arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub

	}

	public Dao<MuseumBean, Integer> getDownloadedDao() throws SQLException {
		if (downloadedDao == null) {
			downloadedDao = getDao(MuseumBean.class);
		}
		return downloadedDao;
	}

	public Dao<DownloadInfo, Integer> getInfoDao() throws SQLException {
		if (infoDao == null) {
			infoDao = getDao(DownloadInfo.class);
		}
		return infoDao;
	}

	public Dao<DownloadBean, Integer> getBeanDao() throws SQLException {
		if (beanDao == null) {
			beanDao = getDao(DownloadBean.class);
		}
		return beanDao;
	}

}
