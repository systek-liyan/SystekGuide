package com.app.guide.offline;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.app.guide.offline.OfflineBeaconBean;
import com.app.guide.offline.OfflineExhibitBean;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * 数据库管理和维护类
 **/
public class OfflineBeanSqlHelper extends OrmLiteSqliteOpenHelper {

	public static final String TAG = OfflineBeanSqlHelper.class.getSimpleName();

	public static int DATABASE_VERSION = 1;
	private Dao<OfflineExhibitBean, Integer> exhibitDao;
	private Dao<OfflineBeaconBean, Integer> beaconDao;

	public OfflineBeanSqlHelper(Context context, String name) {
		super(context, name, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource arg1) {
		try {
			TableUtils.createTableIfNotExists(arg1, OfflineExhibitBean.class);
			TableUtils.createTableIfNotExists(arg1, OfflineBeaconBean.class);
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

	public Dao<OfflineExhibitBean, Integer> getOfflineExhibitDao()
			throws SQLException {
		if (exhibitDao == null) {
			exhibitDao = getDao(OfflineExhibitBean.class);
		}
		return exhibitDao;
	}

	public Dao<OfflineBeaconBean, Integer> getOfflineBeaconDao()
			throws SQLException {
		if (beaconDao == null) {
			beaconDao = getDao(OfflineBeaconBean.class);
		}
		return beaconDao;
	}
}
