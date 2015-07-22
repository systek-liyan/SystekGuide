package com.app.guide.sql;

import java.io.File;
import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.app.guide.bean.CityBean;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class CityDBManagerHelper extends OrmLiteSqliteOpenHelper{
	

	private Dao<CityBean,Integer> cityDao;
	
	/**
	 * 数据库名称
	 */
	private static final String DB_NAME = "CityName";
	
	/**
	 * 数据库存放路径
	 */
	private static String DB_PATH ;
	
	
	public CityDBManagerHelper(Context context) {
		super(context,DB_NAME, null,1);
		DB_PATH = "/data" + Environment.getDataDirectory().getAbsolutePath()
				+ "/" + context.getPackageName()+"/databases";
	}

	@Override
	public void onCreate(SQLiteDatabase database,
			ConnectionSource connectionSource) {
		try {
			TableUtils.createTableIfNotExists(connectionSource, CityBean.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase database,
			ConnectionSource connectionSource, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	public Dao<CityBean, Integer> getCityDao() throws SQLException {
		if (cityDao == null) {
			cityDao = getDao(CityBean.class);
		}
		return cityDao;
	}
	
	/**
	 * 判断数据库文件是否存在
	 * @return
	 */
	public boolean isDBExists() {
		File file = new File(DB_PATH + "/" + DB_NAME);
		return file.exists();
	}
	
}
