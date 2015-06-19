package com.app.guide.utils;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageRequest;
import com.app.guide.Constant;

public class ListBitmapCache implements ImageCache {

	private LruCache<String, Bitmap> mCache;
	private String parentDir;

	public ListBitmapCache(int museumId) {
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
		parentDir = Constant.getImgCacheDir(museumId);
	}

	@Override
	public Bitmap getBitmap(String url, int maxWidth, int maxHeight) {
		Bitmap bitmap = mCache.get(url);
		if (bitmap == null) {
			String path = parentDir
					+ "/"
					+ Constant.getCacheFilename(url.substring(url
							.indexOf("http")));
			File file = new File(path);
			if (file.exists()) {
				Options decodeOptions = new Options();
				decodeOptions.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(path, decodeOptions);
				decodeOptions.inSampleSize = getBestSamplesize(maxWidth,
						maxHeight, decodeOptions.outWidth,
						decodeOptions.outHeight);
				decodeOptions.inJustDecodeBounds = false;
				bitmap = BitmapFactory.decodeFile(path,
						decodeOptions);
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
		Log.w(url, "put");
	}

	private int getBestSamplesize(int viewWidth, int viewHeight,
			int bitmapWidth, int bitmapHeight) {
		double wr = (double) bitmapWidth / viewWidth;
		double hr = (double) bitmapHeight / viewHeight;
		double ratio = Math.max(wr, hr);
		return (int) ratio;
	}

}
