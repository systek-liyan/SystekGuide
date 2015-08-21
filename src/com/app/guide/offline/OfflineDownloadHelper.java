package com.app.guide.offline;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.guide.Constant;
import com.app.guide.bean.MuseumBean;
import com.app.guide.download.DownloadBean;
import com.app.guide.download.DownloadInfo;
import com.app.guide.sql.DatabaseContext;
import com.app.guide.sql.DownloadManagerHelper;
import com.app.guide.utils.FastJsonArrayRequest;
import com.app.guide.utils.FileUtils;
import com.j256.ormlite.dao.Dao;

/**
 * <pre>
 * 下载数据流向：
 * DownloadListFragment(可扩展ListView子层选择下载某个博物馆) --> GetBeanStrategy.getDownloadBeanList() --> List<DownloadBean>
 * DownloadListFragment.onDownload --> AppService.getDownloadClient()从后台服务获取DownloadClient，一个博物馆一个client
 * DownloadClient.start() -- prepare() 启动 --> OfflineDownloadHelper.download() --> 下载各个Bean记录数据库，下载资源文件列表-->
 * 通过接口onFinishedListener提交给DownloadClient形成下载队列
 * 
 * 下载博物馆离线数据包辅助类，
 * 从服务器获得可下载博物馆的DownloadBean对象，一个博物馆一个
 * 从服务器获取各个Offline的bean类,各Bean类录入数据库
 * 博物馆离线数据(资源文件)：lrc,mp3,jpg, 
 * 资源列表文件通过onFinishedListener回调监听，提交各DownloadClient,通过xUtil下载各个资源文件
 * 
 * 后台服务管理各个下载客户端 @see AppService
 * 下载数据流向 @see OfflineDownloadHelper
 * 可扩展ListView界面选择下载博物馆 @see DownloadListFragment
 * 下载客户端 @see DownloadClient
 * 
 */
public class OfflineDownloadHelper {

	private static final String TAG = OfflineDownloadHelper.class
			.getSimpleName();

	private Context mContext;

	private String museumId;

	private OnFinishedListener onFinishedListener;
	private RequestQueue mQueue;
	private ErrorListener mErrorListener;

	private DownloadBean downloadBean;
	private List<DownloadInfo> infoList;
	private Set<String> mSet;

	/**
	 * 
	 * @param context
	 * @param downloadBean 来自服务器的可下载博物馆对象，在DownloadListFragment中通过网络获得
	 * @param name 博物馆名称
	 */
	public OfflineDownloadHelper(Context context, DownloadBean downloadBean) {
		super();
		this.mContext = context;
		this.downloadBean = downloadBean;
		this.museumId = downloadBean.getMuseumId();
		infoList = new ArrayList<DownloadInfo>();
		mSet = new HashSet<String>();
		mQueue = Volley.newRequestQueue(context);
		
		/**
		 * DiskBasedCache.get: /data/data/com.app.guide/cache/volley/-1784528609-475196202: java.io.FileNotFoundException: /data/data/com.app.guide/cache/volley/-1784528609-475196202: open failed: ENOENT (No such file or directory)
		 * 类似于Cache的错误，volley认为没有找到Cache,此时,error.getMessage()=null
		 * 这里处理将此类错误不下发到DownloadClient,以防误报。
		 */
		mErrorListener = new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.d(TAG,"Volley Error = "+ error.getMessage());
				if (error.getMessage() != null ) {
					if (onFinishedListener != null) {
						onFinishedListener.onFailed(error.getMessage());
					}
				}
			}
		};
	}

	/**
	 * 下载博物馆中的展品信息
	 * 
	 * @param context
	 * @param museumid
	 * @throws SQLException
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	private void downloadExhibit(final Context context, final String museumid)
			throws SQLException, NumberFormatException, IOException {
		String url = Constant.HOST_HEAD
				+ "/api/exhibitService/exhibitList?museumId=" + museumId;
		FastJsonArrayRequest<OfflineExhibitBean> request = new FastJsonArrayRequest<OfflineExhibitBean>(
				url, OfflineExhibitBean.class,
				new Response.Listener<List<OfflineExhibitBean>>() {

					@Override
					public void onResponse(List<OfflineExhibitBean> response) {
						// TODO Auto-generated method stub
						OfflineBeanSqlHelper helper = new OfflineBeanSqlHelper(
								context, museumid + ".db");
						Dao<OfflineExhibitBean, String> exhibitDao;
						try {
							exhibitDao = helper.getOfflineExhibitDao();
							for (OfflineExhibitBean bean : response) {
								// 如果没有多角度图片，用主图作为第一个多角度图片,从0开始播放
								if (bean.getImgsurl() == null || bean.getImgsurl().equals(""))  {
									bean.setImgsurl(bean.getIconurl()+"*0");
								}
								exhibitDao.createIfNotExists(bean);
							}
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}, mErrorListener);
		mQueue.add(request);
	}

	/**
	 * 下载博物馆下的地图信息
	 * 
	 * @param context
	 * @param museumId
	 * @throws SQLException
	 */
	private void downloadMap(final Context context, final String museumId)
			throws SQLException {
		String url = Constant.HOST_HEAD
				+ "/api/museumMapService/museumMapList?museumId=" + museumId;
		FastJsonArrayRequest<OfflineMapBean> request = new FastJsonArrayRequest<OfflineMapBean>(
				url, OfflineMapBean.class,
				new Response.Listener<List<OfflineMapBean>>() {

					@Override
					public void onResponse(List<OfflineMapBean> response) {
						// TODO Auto-generated method stub
						OfflineBeanSqlHelper helper = new OfflineBeanSqlHelper(
								context, museumId + ".db");
						Dao<OfflineMapBean, String> mapDao;
						try {
							mapDao = helper.getOfflineMapDao();
							for (OfflineMapBean bean : response) {
								// addImageDownloadInfo(bean.getImgurl());
								// updateDownloadBean(bean.getFilesize());
								mapDao.createIfNotExists(bean);
							}
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}, mErrorListener);
		mQueue.add(request);
	}

	/**
	 * 下载博物馆数据
	 * 
	 * @param context
	 * @param museumId
	 */
	private void downloadMuseum(final Context context, final String museumId) {
		String url = Constant.HOST_HEAD
				+ "/api/museumService/museumList?museumId=" + museumId;
		FastJsonArrayRequest<OfflineMuseumBean> jsonRequest = new FastJsonArrayRequest<OfflineMuseumBean>(
				url, OfflineMuseumBean.class,
				new Response.Listener<List<OfflineMuseumBean>>() {

					@Override
					public void onResponse(List<OfflineMuseumBean> response) {
						// TODO Auto-generated method stub
						if(response.size() == 0)
							return ;
						OfflineMuseumBean museumBean = response.get(0);
						OfflineBeanSqlHelper helper = new OfflineBeanSqlHelper(
								context, museumId + ".db");
						museumBean.setId(museumId);
						try {
							Dao<OfflineMuseumBean, String> museumDao = helper
									.getOfflineMuseumDao();
							museumDao.createOrUpdate(museumBean);
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// // TODO 路径问题 相对路径和绝对路径
						// if (!museumBean.getImgurl().contains(
						// museumBean.getIconurl())) {
						// // addImageDownloadInfo(museumBean.getIconurl());
						// }
						// String imgsurl[] = museumBean.getImgurl().split(",");
						// for (int i = 0; i < imgsurl.length; i++) {
						// // TODO add image
						// // addImageDownloadInfo(imgsurl[i]);
						// }
						// 更新downloadedBean
						// updateDownloadBean(bean.getFilesize());
						MuseumBean bean = new MuseumBean();
						bean.setAddress(museumBean.getAddress());
						// TODO
						bean.setIconUrl(Constant.getImageDownloadPath(
								Constant.HOST_HEAD + museumBean.getIconurl(),
								museumId));
						bean.setLongitudX(museumBean.getLongtiudex());
						bean.setLongitudY(museumBean.getLongtiudey());
						bean.setName(museumBean.getName());
						bean.setOpentime(museumBean.getOpentime());
						bean.setOpen(true);
						bean.setVersion(museumBean.getVersion());
						bean.setMuseumId(museumId);
						
						// 外部数据库Context
						Context dContext = new DatabaseContext(mContext, Constant.FLODER_NAME);
						DownloadManagerHelper dbHelper = new DownloadManagerHelper(dContext,"Download.db");
						try {
							dbHelper.getDownloadedDao().createOrUpdate(bean);
						} catch (SQLException e) {
							Log.d(TAG,"downloadMuseum(),Access form network error!"+e.toString());
						}
					}
				}, mErrorListener);
		mQueue.add(jsonRequest);
	}

	/**
	 * 下载博物馆信标设备信息
	 * 
	 * @param context
	 * @param museumId
	 */
	private void downloadBeacon(final Context context, final String museumId) {
		String url = Constant.HOST_HEAD
				+ "/api/beaconService/beaconList?museumId=" + museumId;
		FastJsonArrayRequest<OfflineBeaconBean> request = new FastJsonArrayRequest<OfflineBeaconBean>(
				url, OfflineBeaconBean.class,
				new Response.Listener<List<OfflineBeaconBean>>() {

					@Override
					public void onResponse(List<OfflineBeaconBean> response) {
						// TODO Auto-generated method stub
						OfflineBeanSqlHelper helper = new OfflineBeanSqlHelper(
								context, museumId + ".db");
						Dao<OfflineBeaconBean, String> beaconDao;
						try {
							beaconDao = helper.getOfflineBeaconDao();
							for (OfflineBeaconBean bean : response) {
								beaconDao.createOrUpdate(bean);
							}
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}, mErrorListener);
		mQueue.add(request);
	}

	/**
	 * 下载博物馆标签信息
	 * 
	 * @param context
	 * @param museumId
	 * @throws SQLException
	 */
	private void downloadLabel(final Context context, final String museumId)
			throws SQLException {

		String url = Constant.HOST_HEAD
				+ "/api/labelsService/labelsList?museumId=" + museumId;
		FastJsonArrayRequest<OfflineLabelBean> request = new FastJsonArrayRequest<OfflineLabelBean>(
				url, OfflineLabelBean.class,
				new Response.Listener<List<OfflineLabelBean>>() {

					@Override
					public void onResponse(List<OfflineLabelBean> response) {
						OfflineBeanSqlHelper helper = new OfflineBeanSqlHelper(
								context, museumId + ".db");
						Dao<OfflineLabelBean, String> labelDao;
						try {
							labelDao = helper.getOfflineLabelDao();
							for (OfflineLabelBean bean : response) {
								Log.w(TAG, bean.toString());
								labelDao.createOrUpdate(bean);
							}
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}, mErrorListener);
		mQueue.add(request);
	}

	/**
	 * 博物馆下所有资源文件列表
	 * 
	 */
	private void downloadFiles() {

		String url = Constant.HOST_HEAD + "/api/assetsService/assetsList?museumId="
				+ museumId;	
		JsonObjectRequest request = new JsonObjectRequest(url, null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						try {
							// 获取下载大小
							String size = response.getString("size");
							// 设置总的下载大小 (字节)
							downloadBean.setTotal(Long.valueOf(size));
							// 获取url
							JSONArray array = response.getJSONArray("url");
							for (int i = 0; i < array.length(); i++) {
								String url = array.getString(i);
								Log.w(TAG, url.toString());
								// 获取合法的下载地址
								url = Constant.getURL(url);
								// 根据url的不同，创建不同的下载任务。
								if (url.contains("img")) {
									addImageDownloadInfo(url);
								} else if (url.contains("audio")) {
									addAudioDownloadInfo(url);
								} else if (url.contains("lrc")) {
									addLrcDownloadInfo(url);
								}
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						finished();
					}
				}, mErrorListener);
		mQueue.add(request);
	}

	/**
	 * 下载离线数据，各Bean类录入数据库，资源列表文件通过onFinishedListener回调监听，提交各DownloadClient
	 * 
	 * @throws SQLException
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public void download() throws SQLException, NumberFormatException,
			IOException {
		// TODO 删除目录(Guide/museumId)中的所有文件，要做判断，除非重新下载
		FileUtils.deleteDirectory(Constant.FLODER + museumId);
		Context dContext = new DatabaseContext(mContext, Constant.FLODER_NAME
				+ museumId);
		downloadExhibit(dContext, museumId);
		downloadMap(dContext, museumId);
		downloadLabel(dContext, museumId);
		downloadMuseum(dContext, museumId);
		downloadBeacon(dContext, museumId);
		downloadFiles();  // 资源文件列表
	}

	/**
	 * 数据插入数据库后调用该方法，在回调接口中调用DownloadClient的download方法开始下载
	 */
	private void finished() {
		Log.d(TAG, "start download files!");
		if (onFinishedListener != null) {
			onFinishedListener.onSuccess(infoList, downloadBean);
		}
	}

	/**
	 * 添加一个歌词文件下载任务
	 */
	private void addLrcDownloadInfo(String url) {
		if (url == null || url.equals(""))
			return;
		if (mSet.contains(url))
			return;
		if (!url.contains("/"))
			return;
		mSet.add(url);
		DownloadInfo info = new DownloadInfo();
		info.setMuseumId(museumId);
		info.setUrl(url);
		info.setTarget(Constant.getLrcDownloadPath(url, museumId));
		infoList.add(info);
	}

	/**
	 * 添加一个音频文件下载任务
	 * 
	 * @param url
	 */
	private void addAudioDownloadInfo(String url) {
		if (url == null || url.equals("")) {
			return;
		}
		if (mSet.contains(url)) {
			return;
		}
		mSet.add(url);
		DownloadInfo info = new DownloadInfo();
		info.setMuseumId(museumId);
		info.setUrl(url);
		info.setTarget(Constant.getAudioDownloadPath(url, museumId));
		infoList.add(info);
	}

	/**
	 * 添加一个图片下载任务
	 * 
	 * @param url
	 */
	private void addImageDownloadInfo(String url) {
		if (url == null || url.equals("")) {
			return;
		}
		if (mSet.contains(url)) {
			return;
		}
		mSet.add(url);
		DownloadInfo info = new DownloadInfo();
		info.setMuseumId(museumId);
		info.setUrl(url);
		info.setTarget(Constant.getImageDownloadPath(url, museumId));
		infoList.add(info);
	}

//	/**
//	 * 更新DownloadBean的总长度
//	 * 
//	 * @param fileSize
//	 */
//	private void updateDownloadBean(long fileSize) {
//		downloadBean.setTotal(downloadBean.getTotal() + fileSize);
//	}

	public OnFinishedListener getOnFinishedListener() {
		return onFinishedListener;
	}

	public void setOnFinishedListener(OnFinishedListener onFinishedListener) {
		this.onFinishedListener = onFinishedListener;
	}

	public interface OnFinishedListener {
		/** 通过Volley获得各Bean和离线文件（资源）列表; 各Offline的Bean录入数据库，离线文件列表形成，并通过此接口传递 */
		public void onSuccess(List<DownloadInfo> list, DownloadBean downloadBean);
        /** 通过Volley获得各Bean和离线文件（资源）列表出现的错误通过此接口传递 */
		public void onFailed(String msg);
	}

}
