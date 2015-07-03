package com.app.guide.utils;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NoCache;
import com.android.volley.toolbox.Volley;

public class BitmapUtils {

	private static ImageLoader imageLoader;

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
