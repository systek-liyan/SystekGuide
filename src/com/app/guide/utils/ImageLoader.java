package com.app.guide.utils;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

/**
 * 通过信号量维护一个线程池， 解决并发问题，加载图片时判断本地是否有缓存，有则直接从本地获取，没有则新建一个线程去加载图片并将图片加入缓存
 * 加载图片帮助类，实现图片压缩，异步加载 、缓存等
 * 
 * @author yetwish
 * 
 */
@SuppressLint({ "NewApi"})
public class ImageLoader {

	private static final String TAG = "ImageLoader";
	
	/**
	 * 使用LruCache 实现图片缓存
	 */
	private LruCache<String, Bitmap> mMemoryCache;

	/**
	 * 维护一个线程池，处理并发
	 */
	private ExecutorService mThreadPool;

	/**
	 * 默认线程数为3
	 */
	private final static int DEFAULT_THREAD_COUNT = 1;

	/**
	 * 任务队列
	 */
	private LinkedList<Runnable> mTaskQueue;

	/**
	 * 后台轮询线程
	 */
	private Thread mPoolThread;
	private Handler mPoolThreadHandler;

	private static final int MSG_ADD_TASK = 0x110;

	/**
	 * UI handler
	 */
	private Handler mUIHandler;

	/**
	 * 信号量 维护并发问题
	 */
	private Semaphore mSemaphorePoolThreadHandler = new Semaphore(0); // TODO
	private Semaphore mSemaphoreThreadPool;

	/**
	 * 判断是否开启硬件缓存
	 */
	private boolean isDiskCacheEnable = true;

	public static ImageLoader getInstance(int threadCount) {
		if (instance == null) {
			synchronized (ImageLoader.class) {
				if (instance == null) {
					instance = new ImageLoader(threadCount);
				}
			}
		}
		return instance;
	}

	/**
	 * 获得默认的loader 默认维护一个线程数为1 的线程池
	 * 
	 * @return
	 */
	public static ImageLoader getDefaultInstance() {
		return getInstance(DEFAULT_THREAD_COUNT);
	}

	/**
	 * singleton
	 */
	private static ImageLoader instance;

	private ImageLoader(int threadCount) {
		init(threadCount);
	}

	private void init(int threadCount) {
		// 初始化后台轮询线程
		initBgThread();
		// 初始化LruCache
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheSize = maxMemory / 8;
		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getByteCount();
			}
		};
		// 初始化线程池
		mThreadPool = Executors.newFixedThreadPool(threadCount);
		// 初始化任务队列
		mTaskQueue = new LinkedList<Runnable>();
		// 初始化信号量
		mSemaphoreThreadPool = new Semaphore(threadCount);
	}

	/**
	 * 初始化后台轮询线程
	 */
	@SuppressLint("HandlerLeak")
	private void initBgThread() {
		mPoolThread = new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				// 后台线程handler 维护一个线程队列，每次从线程队列中取出一个线程进行执行。
				mPoolThreadHandler = new Handler() {
					@Override
					public void handleMessage(Message msg) {
//						switch (msg.what) {
//						case MSG_ADD_TASK:
							mThreadPool.execute(getTask());
							Log.w(TAG,"execute");
							try {
								// 阻塞方法，获取许可， 如果没许可，则线程等待。直到有其他线程释放许可。
								mSemaphoreThreadPool.acquire();
							} catch (InterruptedException e) {
							}
//							break;
//						}
					}
				};
				// 释放一个信号量
				mSemaphorePoolThreadHandler.release();
				Looper.loop();
			}
		};
		mPoolThread.start();
	}

	/**
	 * 加载图片
	 * 
	 * @param url
	 */
	@SuppressLint("HandlerLeak")
	public void loadImage(String url, final ImageView imageView,
			final boolean isFromNet, int width, int height) {
		imageView.setTag(url);
		if (mUIHandler == null) {
			mUIHandler = new Handler() {
				// 当后台加载好图片后，会调用ui线程的handler发送包含图片信息的消息，于是在ui
				// handler中处理发来的图片消息，将它们显示出来
				@Override
				public void handleMessage(Message msg) {
					// 获取得到的图片 ,并设置图片
					ImageHolder holder = (ImageHolder) msg.obj;
					ImageView imageView = holder.imageView;
					String url = holder.url;
					// 判断存储路径是否匹配，
					if (url.equals(imageView.getTag().toString())) {
						Bitmap bitmap = holder.bitmap;
						imageView.setImageBitmap(bitmap);
					}
				}
			};
		}
		// 判断这张图片是否存在缓存
		Bitmap bitmap = getBitmapFromLruCache(url);
		Log.w(TAG, (bitmap == null )+"");
		if (bitmap != null) {
			// 存在缓存 则像UI线程发送图片消息
			refreshBitmap(url, imageView, bitmap);
		} else {
			// 否则 新建一个任务 加载图片
			addTask(buildTask(url, imageView, isFromNet, width, height));
		}
	}

	private Runnable buildTask(final String url, final ImageView imageView,
			final boolean isFromNet, final int width, final int height) {
		return new Runnable() {
			@Override
			public void run() {
				Log.w(TAG,"run  build");
				Bitmap bitmap = null;
				if (isFromNet) {
					// 从网上加载图片
					File file = getDiskCacheDir(imageView.getContext(),
							md5(url));
					if (file.exists())// 如果在缓存文件中发现
					{
						Log.e(TAG, "find image :" + url + " in disk cache ."+file.getAbsolutePath());
						bitmap = loadImageFromLocal(file.getAbsolutePath(),
								imageView, width, height);
					} else {
						if (isDiskCacheEnable)// 检测是否开启硬盘缓存
						{
							Log.w(TAG,"download");
							boolean downloadState = DownloadImgUtils
									.downloadImgByUrl(url, file);
							if (downloadState)// 如果下载成功
							{
								Log.w(TAG,"download success");
								bitmap = loadImageFromLocal(
										file.getAbsolutePath(), imageView,
										width, height);
								Log.e(TAG,
										"download image :" + url
												+ " to disk cache . path is "
												+ file.getAbsolutePath());
							}
						} else {
							Log.e(TAG, "load image :" + url + " to memory.");
							// 直接从本地加载
							bitmap = loadImageFromLocal(url, imageView, width, height);
						}
					}
				} else {// 从本地加载图片
					bitmap = loadImageFromLocal(url, imageView, width, height);
				}
				// 把图片加入到缓存
				addBitmapToLruCache(url, bitmap);
				// 给ui线程发消息 更新bitmap
				refreshBitmap(url, imageView, bitmap);
				// 加载完图片，将许可释放掉
				mSemaphoreThreadPool.release();

			}
		};
	}

	/**
	 * 从本地中加载图片 根据imageView 的宽高对图片进行压缩
	 */
	private Bitmap loadImageFromLocal(String url, ImageView imageView,
			int width, int height) {

		return decodeSampleBitmapFromurl(url, width, height);
	}

	/**
	 * 利用签名辅助类，将字符串字节数组
	 * 
	 * @param str
	 * @return
	 */
	public String md5(String str) {
		byte[] digest = null;
		try {
			MessageDigest md = MessageDigest.getInstance("md5");
			digest = md.digest(str.getBytes());
			return bytes2hex02(digest);

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 方式二
	 * 
	 * @param bytes
	 * @return
	 */
	public String bytes2hex02(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		String tmp = null;
		for (byte b : bytes) {
			// 将每个字节与0xFF进行与运算，然后转化为10进制，然后借助于Integer再转化为16进制
			tmp = Integer.toHexString(0xFF & b);
			if (tmp.length() == 1)// 每个字节8为，转为16进制标志，2个16进制位
			{
				tmp = "0" + tmp;
			}
			sb.append(tmp);
		}

		return sb.toString();

	}

	/**
	 * 添加线程到线程队列
	 * 
	 * @param runnable
	 */
	private synchronized void addTask(Runnable runnable) {
		mTaskQueue.add(runnable);
		try {
			if (mPoolThreadHandler == null)
				mSemaphorePoolThreadHandler.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Log.w(TAG,"add task");
		mPoolThreadHandler.sendEmptyMessage(MSG_ADD_TASK);
	}

	/**
	 * 从任务队列取出一个方法
	 */
	private Runnable getTask() {
		return mTaskQueue.removeFirst();
	}

	/**
	 * 获得缓存图片的地址
	 * 
	 * @param context
	 * @param uniqueName
	 * @return
	 */
	public File getDiskCacheDir(Context context, String uniqueName) {
		String cacheurl;
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			cacheurl = context.getExternalCacheDir().getPath();
		} else {
			cacheurl = context.getCacheDir().getPath();
		}
		return new File(cacheurl + File.separator + uniqueName);
	}

	/**
	 * 给UI线程发送holder对象，更新imageView 中的bitmap
	 */
	private void refreshBitmap(String url, ImageView imageView, Bitmap bitmap) {
		Message msg = Message.obtain();
		ImageHolder holder = new ImageHolder();
		holder.url = url;
		holder.imageView = imageView;
		holder.bitmap = bitmap;
		msg.obj = holder;
		mUIHandler.sendMessage(msg);

	}

	private Bitmap getBitmapFromLruCache(String url) {
		return mMemoryCache.get(url);
	}

	/**
	 * 将图片加入LruCache
	 * 
	 * @param url
	 * @param bm
	 */
	protected void addBitmapToLruCache(String url, Bitmap bitmap) {
		if (getBitmapFromLruCache(url) == null) {
			if (bitmap != null) {
				mMemoryCache.put(url, bitmap);
			}
		}
	}

	/**
	 * 从本地文件中压缩并获取bitmap
	 */
	public Bitmap decodeSampleBitmapFromurl(String url, int decodeWidth,
			int decodeHeight) {

		final BitmapFactory.Options options = new BitmapFactory.Options();
		// set inJustDecodeBounds = true to check dimensions;
		// 未真正解码图片，不占内存，只是获得图片的尺寸信息
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(url, options);
		// 计算要解码的图片的size
		options.inSampleSize = calculateInSampeSize(options, decodeWidth,
				decodeHeight);
		// 根据计算出来的size解码图片
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(url, options);
	}

	/**
	 * 图片压缩解码 从系统资源中解码成bitmap对象 ，并进行压缩
	 * 
	 * @param res
	 *            , Resource
	 * @param resId
	 *            , 图片的resId
	 * @param decodeWidth
	 *            , 解码后图片的宽度
	 * @param decodeHeight
	 *            , 解码后图片的高度
	 * @return Bitmap, 解码后图片的bitmap 对象
	 */
	public Bitmap decodeSampleBitmapFromResource(Resources res, int resId,
			int decodeWidth, int decodeHeight) {

		final BitmapFactory.Options options = new BitmapFactory.Options();
		// set inJustDecodeBounds = true to check dimensions;
		// 未真正解码图片，不占内存，只是获得图片的尺寸信息
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);
		// 计算要解码的图片的size
		options.inSampleSize = calculateInSampeSize(options, decodeWidth,
				decodeHeight);
		// 根据计算出来的size解码图片
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

	/**
	 * 获取要缩放的尺寸
	 */
	public int calculateInSampeSize(Options options, int reqWidth, int reqHeight) {
		// 获取图片的原始大小
		final int originWidth = options.outWidth;
		final int originHeight = options.outHeight;
		int inSampleSize = 1;
		if (originHeight > reqHeight || originWidth > reqWidth) {
			// 计算缩放倍率
			final int heightRadio = Math.round(originHeight * 1.0f / reqHeight);
			final int widthRadio = Math.round(originWidth * 1.0f / reqWidth);
			inSampleSize = heightRadio > widthRadio ? heightRadio : widthRadio;
		}
		return inSampleSize;
	}

	private class ImageHolder {
		Bitmap bitmap;
		ImageView imageView;
		String url;
	}
	
	/**
	 * 取消所有正在下载或等待下载的任务。
	 */
	public void cancelAllTasks() {
		
	}

}
