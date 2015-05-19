package com.app.guide.utils;

import android.content.Context;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.app.guide.R;

public class BitmapUtils {

	private static ImageLoader imageLoader;

	public BitmapUtils(Context context) {
		RequestQueue mQueue = Volley.newRequestQueue(context);
		imageLoader = new ImageLoader(mQueue, new ListBitmapCache());
	}

	public void loadListBitmap(Context context, ImageView imageView, String url) {

		ImageListener listener = ImageLoader.getImageListener(imageView,
				R.drawable.ic_launcher, R.drawable.ic_launcher);
		imageLoader.get(url, listener);
	}

	public static ImageLoader getImageLoader(Context context) {

		if (imageLoader == null) {
			synchronized (BitmapUtils.class) {
				if (imageLoader == null) {
					RequestQueue mQueue = Volley.newRequestQueue(context);
					imageLoader = new ImageLoader(mQueue,
							new ListBitmapCache());
				}
			}
		}
		return imageLoader;
	}
}
