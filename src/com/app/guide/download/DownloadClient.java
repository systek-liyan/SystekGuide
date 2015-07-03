package com.app.guide.download;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.app.guide.Constant;
import com.app.guide.bean.MuseumBean;
import com.app.guide.offline.OfflineDownloadHelper;
import com.app.guide.offline.OfflineDownloadHelper.OnFinishedListener;
import com.app.guide.service.AppService;
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
 * 
 * 使用xUtils框架进行单个博物馆的下载任务，对博物馆离线数据的所有文件形成一个队列依次进行下载
 * 
 * @author joe_c
 *
 */
public class DownloadClient {

	public enum STATE {
		PREPARE, DOWNLOADING, NONE, PAUSE, CANCELED
	}

	private static final String TAG = DownloadClient.class.getSimpleName();
	private static final int TRY_TIME = 3;

	private Context mContext;
	private String museumId;
	private Dao<DownloadInfo, Integer> infoDao;
	private Dao<DownloadBean, Integer> beanDao;
	private DownloadBean downloadBean;

	private BlockingQueue<DownloadInfo> queue;
	private HttpUtils utils;
	private HttpHandler<File> handler;
	private RequestCallBack<File> callBack;
	private STATE state = STATE.NONE;
	private int tryTime = 0;

	private OnProgressListener onProgressListener;

	public DownloadClient(Context context, String museumId) {
		mContext = context;
		this.museumId = museumId;
		queue = new LinkedBlockingQueue<DownloadInfo>();
		utils = new HttpUtils();
		callBack = new RequestCallBack<File>() {

			@Override
			public void onSuccess(ResponseInfo<File> responseInfo) {
				// TODO Auto-generated method stub
				downloadOnceCompleted(responseInfo.contentLength);
				Log.w(TAG, "success once:" );
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				// TODO Auto-generated method stub
				Log.w(TAG, "onFailure once:" );
				if (msg.equals("downloaded")) {
					downloadOnceCompleted(FileUtils.getFileSize(queue.peek()
							.getTarget()));
					return;
				}
				tryTime++;
				if (tryTime == TRY_TIME && onProgressListener != null) {
					onProgressListener.onFailed(queue.peek().getUrl(), msg);
					return;
				}
				downloadNext();
			}

			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				// TODO Auto-generated method stub
				super.onLoading(total, current, isUploading);
				Log.w(TAG, "Loading");
				if (onProgressListener != null) {
					onProgressListener.onProgress(downloadBean.getTotal(),
							(downloadBean.getCurrent() + current));
				}
			}

		};
		DownloadManagerHelper helper = new DownloadManagerHelper(mContext);
		try {
			infoDao = helper.getInfoDao();
			beanDao = helper.getBeanDao();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 每次下载成功一个文件后调用此方法。 更新DownloadBean和DownloadInfo表
	 * 
	 * @param length
	 */
	private void downloadOnceCompleted(long length) {
		DownloadInfo deleteInfo = queue.poll();
		tryTime = 0;
		try {
			downloadBean.setCurrent(downloadBean.getCurrent() + length);
			beanDao.createOrUpdate(downloadBean);
			infoDao.delete(deleteInfo);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (queue.size() != 0) {
			downloadNext();
		} else {
			state = STATE.NONE;
			downloadBean.setCompleted(true);
			try {
				beanDao.createOrUpdate(downloadBean);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (onProgressListener != null) {
				onProgressListener.onSuccess();
			}
			AppService.remove(museumId);
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
			infoDao.createOrUpdate(info);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File file = new File(info.getTarget());
		if (file.exists()) {
			file.delete();
		}
	}

	/**
	 * 开始下载，自动判断是否为第一次开始下载
	 * 
	 * @throws SQLException
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	public void start() throws SQLException, NumberFormatException, IOException {
		if (state == STATE.NONE) {
			if (prepare()) {
				if(onProgressListener != null){
					onProgressListener.onStart();
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
	 * @return
	 * @throws SQLException
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public boolean prepare() throws SQLException, NumberFormatException,
			IOException {
		state = STATE.PREPARE;
		Log.w(TAG, "prepare");
		downloadBean = beanDao.queryBuilder().where().eq("museumId", museumId)
				.queryForFirst();
		if (downloadBean == null) {
			OfflineDownloadHelper helper = new OfflineDownloadHelper(mContext,
					museumId);
			helper.setOnFinishedListener(new OnFinishedListener() {

				@Override
				public void onFailed(String msg) {
					// TODO Auto-generated method stub
					if (onProgressListener != null) {
						onProgressListener.onFailed("no start", msg);
					}
				}

				@Override
				public void onSuccess(List<DownloadInfo> list, DownloadBean bean) {
					// TODO Auto-generated method stub
					downloadBean = bean;
					try {
						beanDao.createOrUpdate(downloadBean);
						addTask(list);
						if(onProgressListener != null){
							onProgressListener.onStart();
						}
						Log.w(TAG, "data download completed");
						downloadNext();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			helper.download();
			return false;
		} else {
			List<DownloadInfo> list = infoDao.queryBuilder().where()
					.eq("museumId", downloadBean.getMuseumId()).query();
			addTask(list);
			return true;
		}
	}

	private void addTask(List<DownloadInfo> list) throws SQLException {
		if (list.size() == 0) {
			return;
		}
		queue.add(list.get(0));
		for (int i = 1; i < list.size(); i++) {
			addTask(list.get(i));
		}
		beanDao.createOrUpdate(downloadBean);
		list.clear();
	}

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
	 * 下载队列中的系一个任务
	 */
	private void downloadNext() {
		if (queue.size() != 0) {
			DownloadInfo info = queue.peek();
			download(info);
		}
	}

	/**
	 * 暂停方法
	 */
	public void pause() {
		if (handler != null && state == STATE.DOWNLOADING) {
			state = STATE.PAUSE;
			handler.cancel();
		}
	}

	/**
	 * 恢复下载方法
	 */
	public void resume() {
		if (state == STATE.PAUSE) {
			state = STATE.DOWNLOADING;
			downloadNext();
		}
	}

	/**
	 * 取消方法
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
				DownloadManagerHelper helper = new DownloadManagerHelper(
						mContext);
				DeleteBuilder<MuseumBean, Integer> deleteBuilder = helper
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

	public OnProgressListener getOnProgressListener() {
		return onProgressListener;
	}

	public void setOnProgressListener(OnProgressListener onProgressListener) {
		this.onProgressListener = onProgressListener;
	}

	public interface OnProgressListener {
		public void onStart();

		public void onProgress(long total, long current);

		public void onSuccess();

		public void onFailed(String url, String msg);
	}

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
					if (onProgressListener != null) {
						onProgressListener.onStart();
					}
				}
			} else {
				if (onProgressListener != null) {
					onProgressListener.onFailed(null, "该数据包已经下载完成！");
				}
			}
		}
	}

}
