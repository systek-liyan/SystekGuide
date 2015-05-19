package com.app.guide.offline;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.app.guide.bean.DownloadBean;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DownloadManagerHelper extends OrmLiteSqliteOpenHelper {

	private Dao<DownloadBean, Integer> downloaDao;

	public DownloadManagerHelper(Context context) {
		super(context, "Download", null, 1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase arg0, ConnectionSource arg1) {
		// TODO Auto-generated method stub
		try {
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

	public Dao<DownloadBean, Integer> getDownloadDao() throws SQLException {
		if (downloaDao == null) {
			downloaDao = getDao(DownloadBean.class);
		}
		return downloaDao;
	}

}
