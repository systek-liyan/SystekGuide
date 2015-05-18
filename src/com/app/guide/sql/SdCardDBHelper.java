package com.app.guide.sql;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 数据库管理和维护类
 **/
public class SdCardDBHelper extends SQLiteOpenHelper {

	public static final String TAG = SdCardDBHelper.class.getSimpleName();

	/**
	 * 数据库版本
	 **/
	public static int DATABASE_VERSION = 1;

	/**
	 * 构造函数
	 * 
	 * @param context
	 *            上下文环境
	 **/
	public SdCardDBHelper(Context context, String name) {
		super(context, name, null, DATABASE_VERSION);
	}

	/**
	 * 创建数据库时触发，创建离线存储所需要的数据库表
	 * 
	 * @param db
	 **/
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.e(TAG, "开始创建数据库表");
		try {
			// 创建用户表(user)
			db.execSQL("create table if not exists user"
					+ "(_id integer primary key autoincrement,name varchar(20),password varchar(20),role varchar(10),updateTime varchar(20))");
		} catch (SQLException se) {
			se.printStackTrace();
			Log.e(TAG, "创建离线所需数据库表失败");
		}
	}

	/**
	 * 更新数据库时触发，
	 * 
	 * @param db
	 * @param oldVersion
	 * @param newVersion
	 **/
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// db.execSQL("ALTER TABLE person ADD COLUMN other STRING");
	}
}
