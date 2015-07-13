package com.app.guide.utils;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NoCache;
import com.android.volley.toolbox.Volley;

/**
 * 图片加载帮助类
 *
 */
public class BitmapUtils {

	/**
	 * 图片加载器对象 
	 */
	private static ImageLoader imageLoader;

	/**
	 * 获取图片加载器对象
	 * @param context
	 * @return
	 */
	public static ImageLoader getImageLoader(Context context) {
		if (imageLoader == null) {
			synchronized (BitmapUtils.class) {
				if (imageLoader == null) {
					RequestQueue mQueue = Volley.newRequestQueue(context,
							new NoCache(), null);
					imageLoader = new ImageLoader(mQueue, new ListBitmapCache(context));
				}
			}
		}
		return imageLoader;
	}
}
