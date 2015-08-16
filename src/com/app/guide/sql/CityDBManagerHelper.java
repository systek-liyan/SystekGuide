package com.app.guide.sql;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.app.guide.bean.CityBean;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * 城市列表数据库管理维护类，数据表CityBean
 */
public class CityDBManagerHelper extends OrmLiteSqliteOpenHelper{
	
	public static final String TAG = CityDBManagerHelper.class.getSimpleName();

	public static int DATABASE_VERSION = 1;
	private String db_name;

	private Dao<CityBean,String> cityDao;
	
	/**
	 * 城市列表数据库管理维护类，数据表CityBean
	 * @param context 如果使用DatabaseContext对象，则数据库建立在外部SD卡上
	 *                否则，DB_PATH = "/data" + Environment.getDataDirectory().getAbsolutePath()+ "/" + context.getPackageName()+"/databases"
	 * @param db_name 数据库名称(建议带后缀.db)
	 */
	public CityDBManagerHelper(Context context, String db_name) {
		super(context,db_name, null,DATABASE_VERSION);
		this.db_name=db_name;
	}

	@Override
	public void onCreate(SQLiteDatabase database,
			ConnectionSource connectionSource) {
		try {
			TableUtils.createTableIfNotExists(connectionSource, CityBean.class);
		} catch (SQLException e) {
			Log.d(TAG,"数据库:"+db_name+",创建失败！" + e.toString());
		}
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase database,
			ConnectionSource connectionSource, int oldVersion, int newVersion) {
		// TODO 目前考虑，升级即是删除sdk目录下的数据库，重新生成之。
	}
	
	/** 获取CityBean对象 */
	public Dao<CityBean,String> getCityDao() throws SQLException {
		if (cityDao == null) {
			cityDao = getDao(CityBean.class);
		}
		return cityDao;
	}
}
