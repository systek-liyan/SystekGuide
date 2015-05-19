package com.app.guide.utils;

import java.io.File;
import java.net.URLEncoder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.android.volley.toolbox.ImageLoader.ImageCache;

public class ListBitmapCache implements ImageCache {

	private LruCache<String, Bitmap> mCache;
	private static final String FLODER = Environment
			.getExternalStorageDirectory().getAbsolutePath();

	public ListBitmapCache() {
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
				Log.w("LruCache", "entryRemoved");
			}
			
		};
	}

	@Override
	public Bitmap getBitmap(String url) {
		Bitmap bitmap = mCache.get(url);
		if (bitmap == null) {
			String name = URLEncoder.encode(url);
			File file = new File(FLODER + "/" + name);
			if (file.exists()) {
				bitmap = BitmapFactory.decodeFile(FLODER + "/" + name);
			}
		}
		return bitmap;
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		mCache.put(url, bitmap);
	}
}
