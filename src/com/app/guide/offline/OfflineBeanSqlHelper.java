package com.app.guide.offline;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * 离线数据库管理和维护类
 * 数据表：OfflineExhibitBean，OfflineBeaconBean，OfflineMuseumBean，OfflineLabelBean
 */
public class OfflineBeanSqlHelper extends OrmLiteSqliteOpenHelper {

	public static final String TAG = OfflineBeanSqlHelper.class.getSimpleName();

	public static int DATABASE_VERSION = 1;
	private String db_name;
	private Dao<OfflineExhibitBean, String> exhibitDao;
	private Dao<OfflineBeaconBean, String> beaconDao;
	private Dao<OfflineMapBean, String> mapDao;
	private Dao<OfflineMuseumBean, String> museumDao;
	private Dao<OfflineLabelBean, String> labelDao;

	/**
	 * 离线数据库管理和维护类，数据表：OfflineExhibitBean，OfflineBeaconBean，OfflineMuseumBean，OfflineLabelBean
	 * @param context 如果使用DatabaseContext对象，则数据库建立在外部SD卡上;
	 *                否则，DB_PATH = "/data" + Environment.getDataDirectory().getAbsolutePath()+ "/" + context.getPackageName()+"/databases"
	 * @param db_name 数据库名称(建议带后缀.db)
	 */
	public OfflineBeanSqlHelper(Context context, String db_name) {
		super(context, db_name, null, DATABASE_VERSION);
		this.db_name=db_name;
	}

	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource arg1) {
		try {
			TableUtils.createTableIfNotExists(arg1, OfflineExhibitBean.class);
			TableUtils.createTableIfNotExists(arg1, OfflineBeaconBean.class);
			TableUtils.createTableIfNotExists(arg1, OfflineMapBean.class);
			TableUtils.createTableIfNotExists(arg1, OfflineMuseumBean.class);
			TableUtils.createTableIfNotExists(arg1, OfflineLabelBean.class);
		} catch (java.sql.SQLException e) {
			Log.d(TAG,"数据库:"+db_name+",创建失败！" + e.toString());
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, ConnectionSource arg1, int arg2,
			int arg3) {
		// TODO 目前考虑，升级即是删除sdk目录下的数据库，重新生成之。
	}

	/** 获取OfflineExhibitBean对象 */
	public Dao<OfflineExhibitBean, String> getOfflineExhibitDao()
			throws SQLException {
		if (exhibitDao == null) {
			exhibitDao = getDao(OfflineExhibitBean.class);
		}
		return exhibitDao;
	}

	/** 获取OfflineBeaconBean对象 */
	public Dao<OfflineBeaconBean, String> getOfflineBeaconDao()
			throws SQLException {
		if (beaconDao == null) {
			beaconDao = getDao(OfflineBeaconBean.class);
		}
		return beaconDao;
	}

	/** 获取OfflineMapBean对象 */
	public Dao<OfflineMapBean, String> getOfflineMapDao() throws SQLException {
		if (mapDao == null) {
			mapDao = getDao(OfflineMapBean.class);
		}
		return mapDao;
	}

	/** 获取OfflineMuseumBean对象 */
	public Dao<OfflineMuseumBean, String> getOfflineMuseumDao()
			throws SQLException {
		if (museumDao == null) {
			museumDao = getDao(OfflineMuseumBean.class);
		}
		return museumDao;
	}

	/** 获取OfflineLabelBean对象 */
	public Dao<OfflineLabelBean, String> getOfflineLabelDao()
			throws SQLException {
		if (labelDao == null) {
			labelDao = getDao(OfflineLabelBean.class);
		}
		return labelDao;
	}
}
