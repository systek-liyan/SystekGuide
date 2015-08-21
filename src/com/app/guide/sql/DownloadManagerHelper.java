package com.app.guide.sql;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.app.guide.bean.MuseumBean;
import com.app.guide.download.DownloadBean;
import com.app.guide.download.DownloadInfo;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * 管理已下载的离线数据包
 * 数据表：MuseumBean,DownloadInfo,DownloadBean,DownloadModel
 */
public class DownloadManagerHelper extends OrmLiteSqliteOpenHelper {

	public static final String TAG = DownloadManagerHelper.class.getSimpleName();

	public static int DATABASE_VERSION = 1;
	private String db_name;
	
	/**
	 * MuseumBean表的数据访问对象
	 */
	private Dao<MuseumBean, String> downloadedDao;
	
	/**
	 * DownloadInfo表的数据访问对象
	 */
	private Dao<DownloadInfo, String> infoDao;
	
	/**
	 * DownloadBean表的数据访问对象
	 */
	private Dao<DownloadBean, String> beanDao;
	
	/**
	 * 管理已下载的离线数据包，数据表：MuseumBean,DownloadInfo,DownloadBean,DownloadModel
	 * @param context 如果使用DatabaseContext对象，则数据库建立在外部SD卡上;
	 *                否则，DB_PATH = "/data" + Environment.getDataDirectory().getAbsolutePath()+ "/" + context.getPackageName()+"/databases"
	 * @param db_name 数据库名称(建议带后缀.db)
	 */
	public DownloadManagerHelper(Context context,String db_name) {
		super(context, db_name, null, DATABASE_VERSION);
		this.db_name = db_name;
	}

	/**
	 * 创建数据库
	 */
	@Override
	public void onCreate(SQLiteDatabase arg0, ConnectionSource arg1) {
		try {
			//创建四个表
			TableUtils.createTableIfNotExists(arg1, MuseumBean.class);
			TableUtils.createTableIfNotExists(arg1, DownloadInfo.class);
			TableUtils.createTableIfNotExists(arg1, DownloadBean.class);
		} catch (java.sql.SQLException e) {
			Log.d(TAG,"数据库:"+db_name+",创建失败！" + e.toString());
		}
	}

	/**
	 * 更新数据库
	 */
	@Override
	public void onUpgrade(SQLiteDatabase arg0, ConnectionSource arg1, int arg2,
			int arg3) {
		//TODO 目前考虑，升级即是删除guide目录下的数据库，重新生成之。
	}

	/**
	 * 获取MuseumBean表的数据访问对象，用来操作MuseumBean表
	 * 记录已经下载完成的博物馆信息
	 * @return
	 * @throws SQLException
	 */
	public Dao<MuseumBean, String> getDownloadedDao() throws SQLException {
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
	public Dao<DownloadInfo, String> getInfoDao() throws SQLException {
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
	public Dao<DownloadBean, String> getDownloadBeanDao() throws SQLException {
		if (beanDao == null) {
			beanDao = getDao(DownloadBean.class);
		}
		return beanDao;
	}

}
