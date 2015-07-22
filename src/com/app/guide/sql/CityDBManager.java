//package com.app.guide.sql;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.os.Environment;
//
//import com.app.guide.model.CityModel;
//
///**
// * 
// * 城市列表数据库帮助类， 提供打开和关闭数据库的方法，打开数据库时，首先检查数据库是否存在，
// * 若不存在，将Project/res/raw目录下的数据库文件拷贝到/data/package_name文件目录之下
// * 若数据库存在，直接调用openOrCreateDatabase方法打开数据库
// * 
// * 修改为自动获得应用程序包名
// * 
// * @author joe_c
// * 
// */
//public class CityDBManager {
//	
//	/**
//	 * 数据库名称
//	 */
//	private static final String DB_NAME = "china_city_name.db";
//	
//	/**
//	 * 数据库路径
//	 */
//	private static String DB_PATH;
//	
//	/**
//	 * 上下文对象 
//	 */
//	private Context mContext;
//	
//	/**
//	 * 数据库对象 ，用于操作数据库
//	 */
//	private SQLiteDatabase db;
//	
//	/**
//	 * 数据库帮助类 用以创建和获取数据库对象
//	 */
//	private CityDBManagerHelper dbHelper;
//
//	public CityDBManager(Context context) {
//		this.mContext = context;
//		String PACKAGE_NAME = context.getPackageName();
//		DB_PATH = "/data" + Environment.getDataDirectory().getAbsolutePath()
//				+ "/" + PACKAGE_NAME;
//	}
//
//	/**
//	 * 创建数据库
//	 * @param cityList
//	 */
//	public void createDB(List<CityModel> cityList) {
//		dbHelper = new CityDBManagerHelper(mContext, DB_PATH + "/" + DB_NAME, null, 1);
//		db = dbHelper.getWritableDatabase();
//		for (CityModel city : cityList) {
//			db.execSQL(
//					"insert into china_city_name(name, alpha) values(?,?)",
//					new String[] { city.getName(), city.getAlpha() });
//		}
//		db.close();
//	}
//
//	/**
//	 * 获取从数据库中城市列表
//	 * @return
//	 */
//	public List<CityModel> getCityListFromDB() {
//		List<CityModel> cityList = new ArrayList<CityModel>();
//		if (!isDBExists())
//			return cityList;
//		db = SQLiteDatabase
//				.openDatabase(DB_PATH + "/" + DB_NAME, null, 1);
//		Cursor cursor = db.rawQuery(
//				"SELECT * FROM china_city_name ", null);
//		for (int i = 0; i < cursor.getCount(); i++) {
//			cursor.moveToPosition(i);
//			CityModel cityModel = new CityModel();
//			cityModel.setName(cursor.getString(cursor
//					.getColumnIndex("name")));
//			cityModel.setAlpha(cursor.getString(cursor
//					.getColumnIndex("alpha")));
//			cityList.add(cityModel);
//		}
//		db.close();
//		return cityList;
//	}
//
//	/**
//	 * 判断数据库文件是否存在
//	 * @return
//	 */
//	public boolean isDBExists() {
//		File file = new File(DB_PATH + "/" + DB_NAME);
//		return file.exists();
//	}
//}
