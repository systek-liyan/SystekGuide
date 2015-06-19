package com.app.guide.sql;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.app.guide.R;

/**
 * 
 * 城市列表数据库帮助类， 提供打开和关闭数据库的方法，打开数据库时，首先检查数据库是否存在，
 * 若不存在，将Project/res/raw目录下的数据库文件拷贝到/data/package_name文件目录之下
 * 若数据库存在，直接调用openOrCreateDatabase方法打开数据库
 * 
 * 修改为自动获得应用程序包名
 * 
 * @author joe_c
 *
 */
public class CityDBManager {
	private final int BUFFER_SIZE = 400000;
	public static final String DB_NAME = "china_city_name.db";
	public static String DB_PATH;
	private Context mContext;
	private SQLiteDatabase database;

	public CityDBManager(Context context) {
		this.mContext = context;
		String PACKAGE_NAME = context.getPackageName();
		DB_PATH = "/data" + Environment.getDataDirectory().getAbsolutePath()
				+ "/" + PACKAGE_NAME;
	}

	public SQLiteDatabase openDateBase() {
		return openDateBase(DB_PATH + "/" + DB_NAME);
	}

	/**
	 * 
	 * 打开数据库
	 * 
	 * @param dbFile
	 * @return
	 */
	private SQLiteDatabase openDateBase(String dbFile) {
		File file = new File(dbFile);
		if (!file.exists()) {// 如果数据库不存在，执行拷贝操作
			InputStream stream = this.mContext.getResources().openRawResource(
					R.raw.china_city_name);
			try {
				FileOutputStream outputStream = new FileOutputStream(dbFile);
				byte[] buffer = new byte[BUFFER_SIZE];
				int count = 0;
				while ((count = stream.read(buffer)) > 0) {
					outputStream.write(buffer, 0, count);
				}
				outputStream.close();
				stream.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		database = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
		return database;
	}

	/**
	 * 关闭数据库
	 */
	public void closeDatabase() {
		if (database != null && database.isOpen()) {
			this.database.close();
		}
	}
}
