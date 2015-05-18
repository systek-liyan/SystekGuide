package com.app.guide.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


public class OfflineSqlHelper {

	private String sql_path, sql_name;
	private Context mContext;

	public OfflineSqlHelper(Context context, String path, String name) {
		mContext = context;
		sql_path = path;
		sql_name = name;
	}
	
	private SQLiteDatabase getSqLiteDatabase(){
		
		DatabaseContext dbContext = new DatabaseContext(mContext, sql_path);
		SdCardDBHelper helper = new SdCardDBHelper(dbContext, sql_name);
		return helper.getWritableDatabase();
	}

}
