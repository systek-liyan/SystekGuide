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

	/**
	 * 内存缓存
	 */
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
					/**
					 * Creates a new bitmap, scaled from an existing bitmap, when possible. 
					 * If the specified width and height are the same as the current width and height of 
					 * the source bitmap, the source bitmap is returned and no new bitmap is created.
					 */
					bitmap = Bitmap.createScaledBitmap(tempBitmap, maxWidth,
							maxHeight, true);
					Log.d("ListBitmapCache","maxWidth,maxHeight="+maxWidth+","+maxHeight);
					Log.d("ListBitmapCache","bitmap,tempBitmap="+bitmap+","+tempBitmap);
					
					/**
					 * Given another bitmap, return true if it has the same dimensions, config, and pixel data as this bitmap. 
					 * If any of those differ, return false. If other is null, return false.
					 */
					if (bitmap.sameAs(tempBitmap)) {
						Log.d("ListBitmapCache","bitmap,tempBitmap is same! Cannot be recycled!!");
					}
					else {
						Log.d("ListBitmapCache","bitmap,tempBitmap is Not same! Can be recycled!!");
						tempBitmap.recycle();
					}
					// 源程序在此没有判断直接回收，导致crash
					//tempBitmap.recycle();
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

	/**
	 * 获取最合适的图片缩放比例
	 * 
	 * @param viewWidth
	 * @param viewHeight
	 * @param bitmapWidth
	 * @param bitmapHeight
	 * @return
	 */
	private int getBestSamplesize(int viewWidth, int viewHeight,
			int bitmapWidth, int bitmapHeight) {
		double wr = (double) bitmapWidth / viewWidth;
		double hr = (double) bitmapHeight / viewHeight;
		double ratio = Math.max(wr, hr);
		return (int) ratio;
	}

}
