package com.app.guide.utils;

import com.app.guide.R;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * 屏幕相关帮助类，用以获取屏幕尺寸等
 * 
 * @author yetwish
 * 
 */
public class ScreenUtils {

	private ScreenUtils() {
		throw new UnsupportedOperationException("cannot be instantiated");
	}

	private static DisplayMetrics dm = null;
	
	/**
	 * 当前设备一屏加载的图片数
	 */
	private static int picCount = -1;
	
	/**
	 * 获取最大加载图片大小
	 * @return
	 */
	public static int getPicCount(Context context){
		if(picCount == -1){
			int width = getScreenWidth(context);
			picCount = width / (int)context.getResources().getDimension(R.dimen.gallery_width) +2;
		}
		return picCount;
	}

	/**
	 * 获得屏幕高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenWidth(Context context) {
		comfirmDMNotNull(context);
		return dm.widthPixels;
	}

	/**
	 * 获得屏幕宽度
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenHeight(Context context) {
		comfirmDMNotNull(context);
		return dm.heightPixels;
	}
	
	/**
	 * 获取屏幕展示矩阵
	 * @param context
	 * @return
	 */
	public static DisplayMetrics getDisplayMetrics(Context context){
		comfirmDMNotNull(context);
		return dm;
	}
	
	/**
	 * 获取屏幕像素比
	 * @param context
	 * @return
	 */
	public static float getScreenDensity(Context context){
		comfirmDMNotNull(context);
		return dm.density;
	}

	/**
	 * 保证dm不为null
	 * @param context
	 */
	private static void comfirmDMNotNull(Context context){
		if (dm == null) {
			WindowManager wm = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
			dm = new DisplayMetrics();
			wm.getDefaultDisplay().getMetrics(dm);
		}
	}
	
}
