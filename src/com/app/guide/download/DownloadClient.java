package com.app.guide.download;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.app.guide.Constant;
import com.app.guide.bean.MuseumBean;
import com.app.guide.offline.OfflineDownloadHelper;
import com.app.guide.offline.OfflineDownloadHelper.OnFinishedListener;
import com.app.guide.service.AppService;
import com.app.guide.sql.DatabaseContext;
import com.app.guide.sql.DownloadManagerHelper;
import com.app.guide.utils.FileUtils;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

/**
 * <pre>
 * 使用xUtils框架进行单个博物馆的下载任务，对博物馆离线数据的所有文件形成一个队列依次进行下载<br>
 * 离线文件（资源）列表由OfflineDownloadHelper提供
 * 一个下载任务将会开启一个DownloadClient(下载客户端),对应一个DownloadBean<br>
 * 所有的下载客户端由AppService统一管理
 * 本博物馆的资源文件形成一个下载队列，记录在Download.db的downloadinfo表中，每下载完成一个文件，从表中删除一项，直至下载完成，本表清空。
 * 首先记录数据库，是因为本次由于某种原因（如暂停，关机，开机），下次下载时读取数据库，下载以前的文件。
 * 考虑到这种情况，每次下载完队列中的一个文件，删除downloadinfo表中的对应项时，也要更新DownloadBean中的current，以便下次开机下载使用。
 * 最后一个文件下载完成，记录DownloadBean的updateDate字段，供下次更新用。
 * @see #downloadOnceCompleted(long)
 * 
 * 后台服务管理各个下载客户端 @see AppService
 * 下载数据流向 @see OfflineDownloadHelper
 * 可扩展ListView界面选择下载博物馆 @see DownloadListFragment
 * 下载客户端 @see DownloadClient
 * @author joe_c
 *
 */
public class DownloadClient {
	
	private static String TAG;

	/**
	 * 表示下载的5种状态
	 */
	public enum STATE {
		PREPARE, DOWNLOADING, NONE, PAUSE, CANCELED
	}
	
	/**
	 * 下载失败重试次数
	 */
	private static final int TRY_TIME = 3;

	/**
	 * 上下文对象
	 */
	private Context mContext;
	
	/**
	 * 要下载的博物馆Id 
	 */
	private String museumId;
	
	/**
	 * DownloadInfo表的数据访问对象，用于访问、操作downloadInfo表中的数据
	 */
	private Dao<DownloadInfo, String> infoDao;
	
	/**
	 * DownloadBean表的数据访问对象，用于访问、操作DownloadBean表中的数据
	 */
	private Dao<DownloadBean, String> beanDao;
	
	/**
	 * 该downloadClient对应的downloadBean,一对一的关系 
	 */
	private DownloadBean downloadBean;

	/**
	 * 阻塞队列,下载队列,用来存储所有的下载项
	 */
	private BlockingQueue<DownloadInfo> queue;
	
	/**
	 * HttpUtils for xUtil 
	 */
	private HttpUtils utils;
	
	/**
	 * HttpHandler<File> for xUtil
	 */
	private HttpHandler<File> handler;
	
	/**
	 * RequestCallBack<File> for xUtil
	 * 每下载一个文件的回调，onSuccess(),onFailure(),onLoading()
	 */
	private RequestCallBack<File> callBack;
	
	/**
	 * 表示当前下载状态，默认为none
	 */
	private STATE state = STATE.NONE;
	
	/**
	 * 尝试下载次数，当下载失败时会自动重试，当总共尝试此时=3次时，实现下载失败
	 */
	private int tryTime = 0;

	/**
	 * 内部定义的进度监听接口的对象，实现对downloadClient的下载进度的监听
	 */
	private OnProgressListener mProgressListener;
	
	public DownloadClient(Context context, String museumId) {
		TAG = this.getClass().getSimpleName();
		
		mContext = context;
		this.museumId = museumId;
		Log.d(TAG, "DownloadClient(),museumId=" + museumId);
		
		// 下载队列
		queue = new LinkedBlockingQueue<DownloadInfo>();
		utils = new HttpUtils();
		// 每次下载一个文件的回调
		callBack = new RequestCallBack<File>() {

			// 某个文件，下载成功时，回调该方法
			@Override
			public void onSuccess(ResponseInfo<File> responseInfo) {
				Log.w(TAG, "onSuccess once.[" + queue.peek().getUrl() + "]" );
				// 调用下载成功的方法
				downloadOnceCompleted(FileUtils.getFileSize(queue.peek().getTarget()));		
			}

			// 某个文件，下载失败时，回调该方法
			@Override
			public void onFailure(HttpException error, String msg) {
				Log.w(TAG, "onFailure once.[" + queue.peek().getUrl()+ "]" );
				//如果已经下载成功，则调用下载成功方法，否则将tryTime++重新下载，
				//如果tryTime达到3次，则调用ProgressListener#onFailed()方法
				if (msg.equals("downloaded")) {
					downloadOnceCompleted(FileUtils.getFileSize(queue.peek().getTarget()));
					return;
				}
				tryTime++;
				if (tryTime == TRY_TIME && mProgressListener != null) {
					mProgressListener.onFailed(queue.peek().getUrl(), msg);
					return;
				}
				// 下载队列中的第一项，此时，该失败文件就是第一项。
				downloadNext(); 
			}

			// 某个文件，正在下载时，回调该方法
			// 注意，这里的total和current是针对当前下载的单个文件
			@Override
			public void onLoading(long total, final long current, boolean isUploading) {
				super.onLoading(total, current, isUploading);
				Log.w(TAG, "Loading");
				// 在主线程,更新进度条
				if (mProgressListener != null) {
					Log.w(TAG, downloadBean.getName()+",下载: " + ((downloadBean.getCurrent() + current)*1.0/downloadBean.getTotal())*100+"%");
					mainHandler.post(new Runnable() {						
						@Override
						public void run() {
							mProgressListener.onProgress(downloadBean.getTotal(),
									(downloadBean.getCurrent() + current));
						}
					});
				}
			}

		};
		
		// 外部数据库Context
		Context dContext = new DatabaseContext(mContext, Constant.FLODER_NAME);
		DownloadManagerHelper dbHelper = new DownloadManagerHelper(dContext,"Download.db");
					
		try {
			infoDao = dbHelper.getInfoDao();
			beanDao = dbHelper.getDownloadBeanDao();
		} catch (SQLException e) {
			Log.d(TAG,"访问数据库Download.db出错,"+e.toString());
		}
	}
	
	/** 主线程的Handler  */
	private Handler mainHandler = new Handler(Looper.getMainLooper());

	/**
	 * 每次下载成功一个文件后调用此方法。 
	 * 更新DownloadBean的整个下载队列当前下载的长度;
	 * 删除DownloadInfo表中的对应文件记录。
	 * 并重置尝试次数，更新下载队列，将队列中的第一项移除。<br>
	 * 移除后若队列为空，则表示下载队列已全部完成，则调用ProgressListener#onSuccess()方法
	 *  @param length 文件长度
	 */
	private void downloadOnceCompleted(long length) {
		// 提取队列中的第一项，并从队列中移除
		DownloadInfo deleteInfo = queue.poll();
		tryTime = 0;
		try {
			// 整个下载队列当前下载的长度
			downloadBean.setCurrent(downloadBean.getCurrent() + length);
			beanDao.createOrUpdate(downloadBean);
			// 删除DownloadInfo表中的对应文件记录。
			infoDao.delete(deleteInfo);
		} catch (SQLException e) {
			Log.d(TAG,"操作数据库Download.db错误,"+e.toString());
		}
		if (queue.size() != 0) {
			// 下载队列的第一个任务
			downloadNext();
		} 
		else { // 整个队列下载完成
			//更新下载状态
			state = STATE.NONE;
			// 在Download.db的downloadBean表中标记下载完成
			downloadBean.setCompleted(true);
			downloadBean.setUpdateDate(new Date());
					
            // 更新downloadBean数据库记录，标记已经完成
			try {
				beanDao.createOrUpdate(downloadBean);
			} catch (SQLException e) {
				Log.d(TAG,"操作数据库Download.db错误,"+e.toString());
			}
			if (mProgressListener != null) {
				mProgressListener.onSuccess();
			}
			//在AppService中，移除该博物馆下载任务
			AppService.remove(museumId);
			
			Log.w(TAG, "博物馆下载完成," + downloadBean.toString());
		}
	}

	/**
	 * 返回下载状态
	 * 
	 * @return
	 */
	public STATE getState() {
		return state;
	}

	/**
	 * 添加下载任务，会将其记录在数据库中
	 * 
	 * @param info
	 */
	public void addTask(DownloadInfo info) {
		queue.add(info);
		try {
			//每添加一个下载任务，则创建一条downloadInfo记录
			infoDao.createOrUpdate(info);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//获取下载任务对应的物理存储位置
		File file = new File(info.getTarget());
		if (file.exists()) {
			file.delete();
		}
	}

	/**
	 * 开始下载，自动判断是否为第一次开始下载,否则，恢复下载
	 * 
	 * @throws SQLException
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	public void start() throws SQLException, NumberFormatException, IOException {
		// 问题出在这条语句，此时downloadBean是null
		// 引起DownloadListFragment中的OnDownloadListener产生java.lang.NullPointerException
		/////////////Log.w(TAG, "博物馆开始下载," + downloadBean.toString());
		if (state == STATE.NONE) {
			if (prepare()) {
				if(mProgressListener != null){
					mProgressListener.onStart();
				}
				downloadNext();
			}
		} else if (state == STATE.PAUSE) {
			resume();
		}
	}

	/**
	 * 下载准备方法，首先访问数据库中是否存在之前的下载任务
	 * 
	 * @return false 调用OfflineDownloadHelper的download(),使其开始工作,使用Volley下载各个Bean并记录数据库，下载资源文件列表，该列表供这里使用，形成下载队列
	 *         True 从Download.db中的downinfo中取出以前没有下载完的本博物馆资源文件
	 * @throws SQLException
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public boolean prepare() throws SQLException, NumberFormatException,
			IOException {
		state = STATE.PREPARE;
		Log.w(TAG, "prepare");
		//判断数据库中downloadBean表中是否有待下载的博物馆的记录
		downloadBean = beanDao.queryBuilder().where().eq("museumId", museumId).queryForFirst();
		Log.w(TAG, "博物馆准备下载," + downloadBean.toString());
		// 新下载
		if ((downloadBean.isCompleted() == false) && (downloadBean.getCurrent() == 0)) { 
			//如果要下载，则调用offlineDownloadHelper,并准备开始下载，为helper对象设置下载状态监听接口OnFinishedListener
			//当helper完成所有的下载接口（服务端API）的访问，且成功生成一个下载列表时，会回调OnFinishedListener#onSuccess()方法
			//否则（不成功的情况下）,会回调OnFinishedListener#onFailed()方法
			
		    OfflineDownloadHelper helper = new OfflineDownloadHelper(mContext,downloadBean);
		    
			helper.setOnFinishedListener(new OnFinishedListener() {

				@Override
				public void onFailed(String msg) {
					Log.d(TAG,"offlineDownloadHelper通过Volley获取各Offline的Bean及离线(资源)文件列表失败！");
					// 调用ProgressListener#onFailed()方法
					if (mProgressListener != null) {
						mProgressListener.onFailed("no start", msg);
					}
				}

				@Override
				public void onSuccess(List<DownloadInfo> list, DownloadBean bean) {
					Log.d(TAG,"offlineDownloadHelper通过Volley获取各Offline的Bean及离线(资源)文件列表成功！");
					downloadBean = bean;
					try {
						//创建或更新downloadBean
						beanDao.createOrUpdate(downloadBean);
						//并将offlineDownloadHelper中生成的downloadInfo列表添加到下载队列中
						addTask(list);
						Log.w(TAG, "下载任务队列生成成功！");
						//开始下载队列的第一个
						downloadNext();
						//调用ProgressListener#onStart()方法
						if(mProgressListener != null){
							mProgressListener.onStart();
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			// 调用OfflineDownloadHelper#download()方法，使其开始工作
			// 使用Volley下载各个Bean并记录数据库，下载资源文件列表，该列表供这里使用，形成下载队列
			helper.download();
			return false;
		} 
		// 之前未下载
		else if ((downloadBean.isCompleted() == false) && (downloadBean.getCurrent() != downloadBean.getTotal()))
		{   // 将之前未下载的要下载
			//如果数据库中已经存在 要下载的博物馆的记录了，从数据库中获取该博物馆的所有downloadInfo记录
			//一个DownloadInfo记录表示一个未下载完成的下载项，并将其添加到下载队列中。
			List<DownloadInfo> list = infoDao.queryBuilder().where().eq("museumId", downloadBean.getMuseumId()).query();
			addTask(list);
			Log.w(TAG, "上次未下载的资源文件加入队列" + downloadBean.toString());
		}
		return true;  
	}

	/**
	 * 将downloadInfo列表添加到下载队列中。
	 * 
	 * @param list
	 * @throws SQLException
	 */
	private void addTask(List<DownloadInfo> list) throws SQLException {
		if (list.size() == 0) {
			return;
		}
		queue.add(list.get(0));
		for (int i = 1; i < list.size(); i++) {
			addTask(list.get(i));
		}
		// 没有意义，downloadBean下载前来源于本地数据库，下载过程中，每下载完一个文件，更新当前大小，更新此表
		// beanDao.createOrUpdate(downloadBean);  
		list.clear();
	}

	/**
	 * @return 当前下载客户端对应的downloadBean
	 */
	public DownloadBean getDownloadBean() {
		return downloadBean;
	}

	/**
	 * 调用xUtils中的下载方法进行单个文件的下载
	 * 
	 * @param info
	 */
	private void download(DownloadInfo info) {
		state = STATE.DOWNLOADING;
		handler = utils.download(info.getUrl(), info.getTarget(), true, false,
				callBack);
	}

	/**
	 * 下载队列中的第一个任务
	 */
	private void downloadNext() {
		if (queue.size() != 0) {
			DownloadInfo info = queue.peek();
			download(info);
		}
	}

	/**
	 * 暂停下载
	 */
	public void pause() {
		if (handler != null && state == STATE.DOWNLOADING) {
			state = STATE.PAUSE;
			handler.cancel();
		}
	}

	/**
	 * 恢复下载
	 */
	public void resume() {
		if (state == STATE.PAUSE) {
			state = STATE.DOWNLOADING;
			downloadNext();
		}
	}

	/**
	 * 取消下载，移出数据库中的记录，以及删除已经下载的文件
	 */
	public void cancel() {
		if (state == STATE.DOWNLOADING) {
			pause();
		}
		if (state != STATE.NONE) {
			state = STATE.CANCELED;
			try {
				beanDao.delete(downloadBean);
				while (queue.size() != 0) {
					infoDao.delete(queue.poll());
				}
			
				// 外部数据库Context
				Context dContext = new DatabaseContext(mContext, Constant.FLODER_NAME);
				DownloadManagerHelper helper = new DownloadManagerHelper(dContext,"Download.db");
				
				DeleteBuilder<MuseumBean, String> deleteBuilder = helper
						.getDownloadedDao().deleteBuilder();
				deleteBuilder.where().eq("id", museumId);
				deleteBuilder.delete();
				FileUtils.deleteDirectory(Constant.FLODER + museumId);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			state = STATE.NONE;
			AppService.remove(museumId);
		}
	}

	/**
	 * 设置下载进度监听器
	 * @return
	 */
	public void setOnProgressListener(OnProgressListener onProgressListener) {
		this.mProgressListener = onProgressListener;
	}

	/**
	 * 下载进度监听接口 ，用以监听下载进度的变化
	 * 下载博物馆离线资源文件列表的状态信息
	 */
	public interface OnProgressListener {
		
		/**
		 * 开始
		 */
		public void onStart();

		/**
		 * 下载进度更新时调用该回调，用以更新进度条显示
		 * 
		 * @param total
		 * @param current
		 */
		public void onProgress(long total, long current);

		/**
		 * 下载完成时 调用该回调
		 */
		public void onSuccess();

		/**
		 * 下载失败时 调用该回调
		 * @param url
		 * @param msg
		 */
		public void onFailed(String url, String msg);
	}
	
	/*
	private class PrepareTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				return prepare();
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result) {
				if (queue.size() > 0) {
					download(queue.peek());
					if (mProgressListener != null) {
						mProgressListener.onStart();
					}
				}
			} else {
				if (mProgressListener != null) {
					mProgressListener.onFailed(null, "该数据包已经下载完成！");
				}
			}
		}
	}
	*/
}
