package com.app.guide.utils;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.android.volley.toolbox.ImageLoader.ImageCache;

public class ListBitmapCache implements ImageCache {

	private LruCache<String, Bitmap> mCache;
	private String parentDir;

	public ListBitmapCache(Context context) {
		int maxMemory = (int) (Runtime.getRuntime().maxMemory());
		// 使用最大可用内存值的1/8作为缓存的大小
		int maxSize = maxMemory / 8;
		mCache = new LruCache<String, Bitmap>(maxSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getRowBytes() * bitmap.getHeight();
			}

			@Override
			protected void entryRemoved(boolean evicted, String key,
					Bitmap oldValue, Bitmap newValue) {
				// TODO Auto-generated method stub
				super.entryRemoved(evicted, key, oldValue, newValue);
			}
		};
	}

	@Override
	public Bitmap getBitmap(String url, int maxWidth, int maxHeight) {

		Bitmap bitmap = mCache.get(url);
		if (bitmap == null) {
			//TODO ? 
			String path = url.substring(url.indexOf("!") + 1);
			if (path.startsWith("http:/")) {
				path = path.substring(7);
			}
			Log.w("ImageCache", "exist:" + path);
			File file = new File(path);
			if (file.exists()) {
				
				Options decodeOptions = new Options();
				decodeOptions.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(path, decodeOptions);
				decodeOptions.inSampleSize = getBestSamplesize(maxWidth,
						maxHeight, decodeOptions.outWidth,
						decodeOptions.outHeight);
				decodeOptions.inJustDecodeBounds = false;
				Bitmap tempBitmap = BitmapFactory.decodeFile(path,
						decodeOptions);
				if (tempBitmap != null) {
					bitmap = Bitmap.createScaledBitmap(tempBitmap, maxWidth,
							maxHeight, true);
					tempBitmap.recycle();
				}
				if (bitmap != null) {
					mCache.put(url, bitmap);
				}
			}
		}
		return bitmap;
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		mCache.put(url, bitmap);
	}

	private int getBestSamplesize(int viewWidth, int viewHeight,
			int bitmapWidth, int bitmapHeight) {
		double wr = (double) bitmapWidth / viewWidth;
		double hr = (double) bitmapHeight / viewHeight;
		double ratio = Math.max(wr, hr);
		return (int) ratio;
	}

}
