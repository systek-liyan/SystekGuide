package com.app.guide;

import android.os.Environment;

public class Constant {

	public static final int PAGE_COUNT = 20;
	
	public static final String EXTRA_MUSEUM_ID = "museumId";
	
	public static final String EXTRA_EXHIBIT_ID = "exhibitId";
	
	public static final String ROOT_SDCARD = Environment
			.getExternalStorageDirectory().getAbsolutePath();

}
