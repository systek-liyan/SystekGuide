package com.app.guide.sql;

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

	/**
	 * MuseumBean表的数据访问对象
	 */
	private Dao<MuseumBean, Integer> downloadedDao;
	
	/**
	 * DownloadInfo表的数据访问对象
	 */
	private Dao<DownloadInfo, Integer> infoDao;
	
	/**
	 * DownloadBean表的数据访问对象
	 */
	private Dao<DownloadBean, Integer> beanDao;

	public DownloadManagerHelper(Context context) {
		super(context, "Download", null, 1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 创建数据库
	 */
	@Override
	public void onCreate(SQLiteDatabase arg0, ConnectionSource arg1) {
		// TODO Auto-generated method stub
		try {
			//创建三个表
			TableUtils.createTableIfNotExists(arg1, MuseumBean.class);
			TableUtils.createTableIfNotExists(arg1, DownloadInfo.class);
			TableUtils.createTableIfNotExists(arg1, DownloadBean.class);
		} catch (java.sql.SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 更新数据库
	 */
	@Override
	public void onUpgrade(SQLiteDatabase arg0, ConnectionSource arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub

	}

	/**
	 * 获取MuseumBean表的数据访问对象，用来操作MuseumBean表
	 * @return
	 * @throws SQLException
	 */
	public Dao<MuseumBean, Integer> getDownloadedDao() throws SQLException {
		if (downloadedDao == null) {
			downloadedDao = getDao(MuseumBean.class);
		}
		return downloadedDao;
	}

	/**
	 * 获取DownloadInfo表的数据访问对象，用来操作DownloadInfo表
	 * @return
	 * @throws SQLException
	 */
	public Dao<DownloadInfo, Integer> getInfoDao() throws SQLException {
		if (infoDao == null) {
			infoDao = getDao(DownloadInfo.class);
		}
		return infoDao;
	}

	/**
	 * 获取DownloadBean表的数据访问对象,用来操作DownloadBean表
	 * @return
	 * @throws SQLException
	 */
	public Dao<DownloadBean, Integer> getBeanDao() throws SQLException {
		if (beanDao == null) {
			beanDao = getDao(DownloadBean.class);
		}
		return beanDao;
	}

}
