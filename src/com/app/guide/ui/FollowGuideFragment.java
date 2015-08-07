package com.app.guide.ui;

import java.util.ArrayList;
import java.util.List;

import org.altbeacon.beacon.Beacon;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import cn.pedant.SweetAlert.SweetAlertDialog;

import com.android.volley.toolbox.NetworkImageView;
import com.app.guide.AppContext;
import com.app.guide.AppContext.OnGuideModeChangedListener;
import com.app.guide.Constant;
import com.app.guide.R;
import com.app.guide.adapter.GalleryAdapter;
import com.app.guide.beanhelper.GetBeanCallBack;
import com.app.guide.beanhelper.GetBeanHelper;
import com.app.guide.model.ExhibitModel;
import com.app.guide.model.ImageModel;
import com.app.guide.offline.OfflineBeaconBean;
import com.app.guide.ui.HomeActivity.onBeaconSearcherListener;
import com.app.guide.utils.BitmapUtils;
import com.app.guide.utils.ScreenUtils;
import com.app.guide.widget.DialogManagerHelper;
import com.app.guide.widget.GalleryView;
import com.app.guide.widget.GalleryView.OnItemClickListener;
import com.app.guide.widget.GalleryView.OnLoadingMoreListener;
import com.app.guide.widget.LyricView;
import com.app.guide.widget.LyricView.onProgressChangedListener;
import com.app.guide.widget.TopBar;

import edu.xidian.NearestBeacon.NearestBeacon;

/**
 * 两种进入该界面的方式，底部导航进入(前提：必须开启自动导航) 和选择某一展品进入<br>
 * 随身导游页面，包含与Beacon相关的操作，根据停留时间获取最近的beacon，并让相应的音频文件自动播放，并显示文字<br>
 * 自动导航时 手动选择展品 优先播放完选择的展品，再返回自动导航
 * 
 * TODO 监听 电话 设置声音 TODO 博物馆选择界面 对话框
 * 
 * @author yetwish
 * @date 2015-4-25
 * 
 */
public class FollowGuideFragment extends Fragment implements
		onBeaconSearcherListener, OnGuideModeChangedListener {

	private static final String TAG = FollowGuideFragment.class.getSimpleName();

	/**
	 * 常量，表示加载更多操作成功
	 */
	private static final int LOAD_SUCCESS = 1;

	/**
	 * 常量，播放进度改变时 传递的msg的标识数
	 */
	private static final int MSG_PROGRESS_CHANGED = 0x200;

	/**
	 * 常量，（beacon切换使得）展品改变时 传递的msg的标识数
	 */
	private static final int MSG_EXHIBIT_CHANGED = 0x201;

	/**
	 * 常量，播放完成时 传递的msg的标识数
	 */
	private static final int MSG_MEDIA_PLAY_COMPLETE = 0x202;

	/**
	 * 上下文对象
	 */
	private Context mContext;

	/**
	 * 标题栏
	 */
	private TopBar fragHeader;

	/**
	 * the view which show the lyric.
	 */
	private LyricView mLyricView;

	/**
	 * the parent layout of lyric view
	 */
	private FrameLayout mLyricContainer;

	/**
	 * 歌词背景
	 */
	private NetworkImageView ivLyricBg;

	/**
	 * the height of the lyric view.
	 */
	private int mLyricHeight;

	/**
	 * the button that control playing the media.
	 */
	private ImageView ivStart;

	/**
	 * 上一个进度iv
	 */
	private ImageView ivPre;

	/**
	 * 下一个进度iv
	 */
	private ImageView ivNext;

	/**
	 * 播放进度条
	 */
	private ProgressBar pbMusic;

	/**
	 * 多角度展示图片tv
	 */
	private TextView tvTitlePics;

	/**
	 * 展品列表图片tv
	 */
	private TextView tvTitleExhibits;

	/**
	 * 多角度展示图片tv的展开iv
	 */
	private ImageView ivPicExpand;

	/**
	 * 展品列表图片tv的展开iv
	 */
	private ImageView ivExhibitExpand;

	/**
	 * 多角度展示图片gallery view
	 */
	private GalleryView mPicGallery;

	/**
	 * 展品展示图片gallery view
	 */
	private GalleryView mExhibitGallery;

	/**
	 * 存储多角度展示图片gallery的数据
	 */
	private List<ImageModel> mGalleryList = null;

	/**
	 * 存储邻近展品列表图片数据
	 */
	private List<ImageModel> mExhibitImages = new ArrayList<ImageModel>();

	/**
	 * gallery adapter, 负责多角度展示图片
	 */
	private GalleryAdapter mGalleryAdapter;

	/**
	 * exhibit Adapter，负责邻近展品列表
	 */
	private GalleryAdapter mExhibitAdapter;

	/**
	 * 标识多角度图片列表是否展开
	 */
	private boolean picFlag = true;

	/**
	 * 标识展品列表图片是否展开
	 */
	private boolean exhibitFlag = false;

	/**
	 * 当前显示的展品
	 */
	private ExhibitModel mCurrentExhibit;

	/**
	 * 邻近的展品列表
	 */
	private List<ExhibitModel> mExhibitsList = new ArrayList<ExhibitModel>();

	/**
	 * 当前exhibit id
	 */
	private String mCurrentExhibitId;

	/**
	 * 当前博物馆的id
	 */
	private String mMuseumId;

	/**
	 * click listener
	 */
	private MyClickListener mClickListener;

	/**
	 * 标记手动选择展品列表中的展品为当前展品,不用区分了
	 */
	//private boolean isChosed = false;

	/**
	 * 加载对话框
	 */
	private SweetAlertDialog pDialog;

	/**
	 * 对话框helper对象
	 */
	private DialogManagerHelper mDialogHelper;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_PROGRESS_CHANGED:
				int progress = msg.arg1;
				// 更新progressBar 和 图集
				pbMusic.setProgress(progress);
				// 匹配图集的时间
				int index = -1;
				for (int i = 0; i < mGalleryList.size(); i++) {
					if (progress >= mGalleryList.get(i).getStartTime()) {
						index = i;
					}
				}
				if (index != mPicGallery.getCurrentSelectedIndex()) {
					mPicGallery.setCurrentSelectedItem(true, index);
				}
				break;
			case MSG_MEDIA_PLAY_COMPLETE:
				// 由于在LyricView中把播放器设为循环播放，因此，应该没有此事件发生；除非给播放器一个不存在的
				Toast.makeText(getActivity(), "播放结束", Toast.LENGTH_LONG).show();
//				// 判断是否有手动选择展品，有则将isChosed标志位置为False
//				if (isChosed)
//					isChosed = false;
//				else {
//					// TODO 没有则循环播放当前展品
//					String id = mCurrentExhibitId;
//					mCurrentExhibitId = null;
//					changeCurrentExhibitById(id);
//				}
				break;
			case MSG_EXHIBIT_CHANGED:
				// beacon切换时，获取新beacon的展品列表
				String beaconId = (String) msg.obj;
				// 获取展品列表
				GetBeanHelper.getInstance(mContext).getExhibitModelsByBeaconId(
						mMuseumId, beaconId,
						new GetBeanCallBack<List<ExhibitModel>>() {
							@Override
							public void onGetBeanResponse(
									List<ExhibitModel> response) {
								// 在beacon没有管理展品的情况下，不更新数据
								if (response.size() == 0)
									return;
								mExhibitsList = response;
								// 该beaconId代表的第一个展品成为当前展品
								changeCurrentExhibitById(mExhibitsList.get(0).getId());
								
								// 判断是否有不小于两个展品，有则显示第二个（中间那个），否则显示唯一的一个
//								if (mExhibitsList.size() >= 2)		
//									updateCurrentExhibit(mExhibitsList.get(1));
//								else
//									updateCurrentExhibit(mExhibitsList.get(0));
//																
//								// 保证一开始有X个或以上个展品在列表中 ，X为一屏展示图片数
//								confirmExhibitListAvailable();
//								updateGalleryAdapter();
//								setPicGalleryVisibility(true);
//								updateExhibitAdapter();
//								notifyStartPlaying();
							}
						});
				break;

			}
		}
	};

	/**
	 * init beaconSearcher————get BeaconSearcher instance , set minStayTime
	 * ,openSearcher and setBeaconRangingListener
	 */
	@Override
	public void onAttach(Activity activity) {	
		super.onAttach(activity);
		Log.d(TAG,"onAttach()");
		mContext = activity;
		// 获取intent
		mMuseumId = ((AppContext) getActivity().getApplication()).currentMuseumId;
		// 获取exhibit id
		mCurrentExhibitId = ((AppContext) getActivity().getApplication()).currentExhibitId;
		
		mDialogHelper = new DialogManagerHelper(activity);
		
		// 根据exhibit id 获取数据,避免自动随行导游时，此时还没有自动定位到的展品
		if (mCurrentExhibitId != null) {  
			pDialog = mDialogHelper.showLoadingProgressDialog();  // 显示正在加载
		    initData(mCurrentExhibitId);
		}
		// initGalleryData();
		// 初始化clickListener
		mClickListener = new MyClickListener();

		HomeActivity.setBeaconLocateType(NearestBeacon.GET_EXHIBIT_BEACON);

		((AppContext) getActivity().getApplication()).addGuideModeChangedListener(this);
	}

	/**
	 * 根据展品id 更新、加载数据
	 * 
	 * @param id
	 */
	private void initData(String id) {
		// 判断是否是从点击展品跳转过来的
		if (id != null) {
			// 开始手动模式
			// 根据传入的展品id，获取选中的展品信息
			GetBeanHelper.getInstance(mContext).getExhibitModel(mMuseumId, id,
					new GetBeanCallBack<ExhibitModel>() {
						@Override
						public void onGetBeanResponse(ExhibitModel response) {
							// TODO Auto-generated method stub
							if (response != null) {
								updateCurrentExhibit(response);
								// 重置两个列表
								mExhibitsList.clear();
								mExhibitImages.clear();
								// 获取左右展品
								mExhibitsList.add(mCurrentExhibit);
								mExhibitImages.add(new ImageModel(
										mCurrentExhibit.getIconUrl(), 0));
								// 保证一开始有X个或以上个展品在列表中 ，X为一屏展示图片数
								confirmExhibitListAvailable();
								// 初始化标题
								if (mCurrentExhibit != null
										&& fragHeader != null) {
									fragHeader.setTitle(mCurrentExhibit
											.getName());
								}
							}
							// 取消显示正在加载对话框
							if (pDialog != null && pDialog.isShowing()) {
								pDialog.dismiss();
								pDialog = null;
							}
						}
					});
		}
	}

	/**
	 * 加载某一展品的右展品
	 * 
	 * @param mExhibit
	 * @return
	 */
	private void loadNextExhibit(ExhibitModel mExhibit,
			final LoadCallBack callBack) {

		// 根据传入的展品id，获取选中的展品信息
		GetBeanHelper.getInstance(mContext).getExhibitModel(mMuseumId,
				mExhibit.getrExhibitBeanId(),
				new GetBeanCallBack<ExhibitModel>() {

					@Override
					public void onGetBeanResponse(ExhibitModel response) {
						// TODO Auto-generated method stub
						if (response != null) {
							mExhibitsList.add(response);
							// 将展品图片加入展品图片列表
							mExhibitImages.add(new ImageModel(response
									.getIconUrl(), 0));
							if (mExhibitAdapter != null) {
								mExhibitAdapter.notifyDataSetChanged();
							}
							if (callBack != null) {
								callBack.onLoadSuccess(LOAD_SUCCESS);
							}
						}
						else if (callBack != null) {
							callBack.onLoadFailed();
						}

					}
				});
	}

	/**
	 * 加载某一展品的左展品
	 * 
	 * @param mExhibit
	 * @return
	 */
	private void loadPreExhibit(ExhibitModel mExhibit,
			final LoadCallBack callBack) {
		// 根据传入的展品id，获取选中的展品信息
		GetBeanHelper.getInstance(mContext).getExhibitModel(mMuseumId,
				mExhibit.getlExhibitBeanId(),
				new GetBeanCallBack<ExhibitModel>() {
					@Override
					public void onGetBeanResponse(ExhibitModel response) {
						if (response != null) {
							// 将展品加入展品列表
							mExhibitsList.add(0, response);
							// 将展品图片加入展品图片列表
							mExhibitImages.add(0,
									new ImageModel(response.getIconUrl(), 0));
							if (mExhibitAdapter != null) {
								mExhibitAdapter.notifyDataSetChanged();
							}
							if (callBack != null) {
								callBack.onLoadSuccess(LOAD_SUCCESS);
							}
						}
						else if (callBack != null) {
							callBack.onLoadFailed();
						}

					}
				});
	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.frag_follow_guide, null);
		Log.d(TAG,"onCreateView()");
		return view;

	}

	/**
	 * init views
	 */
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		Log.d(TAG,"onViewCreated");
		// init fragment header
		fragHeader = (TopBar) view.findViewById(R.id.frag_header_follow_guide);
		if (mCurrentExhibit != null)
			fragHeader.setTitle(mCurrentExhibit.getName());
		else
			fragHeader.setTitle("");
		fragHeader.setSearchingVisible(false);

		// find Views
		mLyricView = (LyricView) view
				.findViewById(R.id.frag_follow_guide_lyricview);

		ivLyricBg = (NetworkImageView) view
				.findViewById(R.id.frag_follow_guide_iv_bg);

		mLyricContainer = (FrameLayout) view
				.findViewById(R.id.frag_follow_guide_lyric_container);

		ivStart = (ImageView) view
				.findViewById(R.id.frag_follow_guide_iv_start);

		ivPre = (ImageView) view.findViewById(R.id.frag_follow_guide_iv_pre);

		ivNext = (ImageView) view.findViewById(R.id.frag_follow_guide_iv_next);

		pbMusic = (ProgressBar) view
				.findViewById(R.id.frag_follow_guide_progressbar);

		tvTitlePics = (TextView) view
				.findViewById(R.id.frag_follow_guide_tv_title_pics);

		tvTitleExhibits = (TextView) view
				.findViewById(R.id.frag_follow_guide_tv_title_exhibits);

		ivPicExpand = (ImageView) view
				.findViewById(R.id.frag_follow_guide_iv_gallery_expand);

		ivExhibitExpand = (ImageView) view
				.findViewById(R.id.frag_follow_guide_iv_exhibit_expand);

		mPicGallery = (GalleryView) view
				.findViewById(R.id.frag_follow_guide_pic_gallery_container);

		mExhibitGallery = (GalleryView) view
				.findViewById(R.id.frag_follow_guide_exhibit_gallery_container);

		ivStart.setOnClickListener(mClickListener);
		ivPre.setOnClickListener(mClickListener);
		ivNext.setOnClickListener(mClickListener);

		ivStart.setClickable(false);
		ivPre.setClickable(false);
		ivNext.setClickable(false);

		initLyricView();

		initGalleryViews();
		// 如果当前已有展品信息，则加载gallery
		if (mCurrentExhibit != null) {
			notifyStartPlaying();
		}

	}

	private void initLyricView() {
		// 设置监听
		mLyricView.setProgressChangedListener(new onProgressChangedListener() {

			@Override
			public void onProgressChanged(int progress) {
				Message msg = Message.obtain();
				msg.what = MSG_PROGRESS_CHANGED;
				msg.arg1 = progress;
				mHandler.sendMessage(msg);
			}

			@Override
			public void onMediaPlayCompleted() {
				mHandler.sendEmptyMessage(MSG_MEDIA_PLAY_COMPLETE);
			}
		});

		// 设置lyricView height
		mLyricContainer.post(new Runnable() {
			@Override
			public void run() {

				int titleHeight = (int) ((tvTitlePics.getHeight()
						+ tvTitleExhibits.getHeight() + 20) * ScreenUtils
						.getScreenDensity(mContext));
				Log.w(TAG, titleHeight + " title");
				int playBarHeight = (int) (getResources().getDimension(
						R.dimen.progressbar_height)
						+ getResources().getDimension(R.dimen.icon_height) + 20 * ScreenUtils
						.getScreenDensity(mContext));

				Log.w(TAG, playBarHeight + " play bar");

				int headerHeight = (int) getResources().getDimension(
						R.dimen.frag_header_height);

				mLyricHeight = ScreenUtils.getScreenHeight(mContext)
						- titleHeight - playBarHeight - headerHeight - 45;

				mLyricContainer.setLayoutParams(new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT, mLyricHeight));

				Log.w(TAG, mLyricHeight + " lyric");
			}
		});
	}

	private void initGalleryViews() {

		/**
		 * 处理与slidingMenu滚动冲突
		 */
		HomeActivity.getMenu().addIgnoredView(mPicGallery);

		HomeActivity.getMenu().addIgnoredView(mExhibitGallery);

		mPicGallery.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(View view, int position, boolean isByLyric) {
				ivLyricBg.setImageUrl(mGalleryList.get(position).getImgUrl(),
						BitmapUtils.getImageLoader(mContext));
				if (!isByLyric) {
					mLyricView.setCurrentPosition(mGalleryList.get(position)
							.getStartTime());
				}
			}

		});

		/**
		 * 设置tvPic可点击
		 */
		tvTitlePics.setOnClickListener(mClickListener);
		ivPicExpand.setOnClickListener(mClickListener);

		if (mCurrentExhibit != null) {
			updateGalleryAdapter();
			setPicGalleryVisibility(true);
		}
		// 设置切换时不自动加载
		mExhibitGallery.setAutoLoad(false);

		mExhibitGallery.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(View view, int position, boolean isByLyric) {
				// 切换展品
				changeCurrentExhibitById(mExhibitsList.get(position).getId());
				// 将手动选择切换标记位置为true
//				isChosed = true;
			}
		});

		// 设置加载更多监听
		mExhibitGallery.seOnLoadingMoreListener(new OnLoadingMoreListener() {
			@Override
			public void onRightLoadingMore(LoadCallBack callBack) {
				loadNextExhibit(mExhibitsList.get(mExhibitsList.size() - 1),
						callBack);
			}

			@Override
			public void onLeftLoadingMore(LoadCallBack callBack) {
				loadPreExhibit(mExhibitsList.get(0), callBack);
			}
		});
		if (mCurrentExhibit != null) {
			updateExhibitAdapter();
		}
		/**
		 * 设置tvExhibit 可点击
		 */
		tvTitleExhibits.setOnClickListener(mClickListener);
		ivExhibitExpand.setOnClickListener(mClickListener);
	}

	/**
	 * when the fragment is on the top of the stack ,start ranging the beacon
	 */
	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG,"onResume()");
		// 自动随行导游
		if (((AppContext) getActivity().getApplication()).getGuideMode()) {
			// 判断是否需要弹出对话框,第一次由博物馆主页按随行导游按键进入
			if (pDialog == null)
				pDialog = mDialogHelper.showSearchingProgressDialog();
			// 设置beacon监听
			HomeActivity.setBeaconSearcherListener(this);
		} 
		else { // 手动由博物馆主页选择展品
			HomeActivity.setBeaconSearcherListener(null);
			// 从AppContext中获取全局exhibit id 可能为null
			changeCurrentExhibitById(((AppContext) getActivity().getApplication()).currentExhibitId);
		}
			
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.d(TAG,"onStop()");
		// 移出监听
		((AppContext) getActivity().getApplication())
				.removeGuideModeListener(this);
		HomeActivity.setBeaconSearcherListener(null);
	}

	/**
	 * when the fragment is destroy , close the beaconSearcher,and release
	 * resources
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG,"onDestroy()");
		if (mLyricView != null) {
			mLyricView.destroy();
		}
		if (mPicGallery != null) {
			mPicGallery.destroy();
		}
		if (mExhibitGallery != null) {
			mExhibitGallery.destroy();
		}
		clear();
	}

	private void clear() {
		mCurrentExhibit = null;
		mCurrentExhibitId = null;
		((AppContext) getActivity().getApplication()).currentExhibitId = null;
		if (mDialogHelper != null) {
			mDialogHelper = null;
		}
		if (pDialog != null)
			pDialog = null;
	}

	/**
	 * 更新当前展品数据数据，即更新gallery列表和mCurrentExhibit、mCurrentExhibitId
	 * 
	 * @param exhibit
	 */
	private void updateCurrentExhibit(ExhibitModel exhibit) {
		if (exhibit == null)
			return;
		mCurrentExhibit = exhibit;
		mCurrentExhibitId = exhibit.getId();
		// 更新appContext中的id
		((AppContext) getActivity().getApplication()).currentExhibitId = exhibit
				.getId();
		// 获取展品多角度图片数据
		mGalleryList = mCurrentExhibit.getImgList();
	}

	/**
	 * 更新多角度展示列表adapter: galleryAdapter
	 */
	private void updateGalleryAdapter() {
		mGalleryAdapter = null;
		mGalleryAdapter = new GalleryAdapter(mContext, mGalleryList,
				R.layout.item_gallery);
		mPicGallery.initData(mGalleryAdapter);
		mPicGallery.setCurrentSelectedItem(false, 0);
	}

	/**
	 * 更新展品列表adapter : exhibitAdapter
	 */
	private void updateExhibitAdapter() {
		mExhibitAdapter = null;
		mExhibitAdapter = new GalleryAdapter(mContext, mExhibitImages,
				R.layout.item_gallery);
		mExhibitGallery.initData(mExhibitAdapter);
		mExhibitGallery.setCurrentSelectedItem(false,
				mExhibitsList.indexOf(mCurrentExhibit));

	}

	/**
	 * 根据exhibit id 切换当前展品
	 * 
	 * @param exhibitId
	 */
	private void changeCurrentExhibitById(String exhibitId) {
		// 排除id = null及与当前id相等的情况
		if (exhibitId != null && !exhibitId.equals(mCurrentExhibitId)) {
			if (mLyricView != null)
				mLyricView.stop();
			initData(exhibitId);
			if (mCurrentExhibit != null) {
				// 重新加载数据
				updateGalleryAdapter();
				updateExhibitAdapter();
				notifyStartPlaying();
			}
		}
	}

	/**
	 * 根据beaconId切换当前展品
	 * 
	 * @param beaconId
	 */
	private void changeCurrentExhibitByBeaconId(String beaconId) {
//		isChosed = false;
		// 当前有展品，并且与当前展品不同
		if (mCurrentExhibit != null
				&& !mCurrentExhibit.getBeaconUId().equals(beaconId)) {
			Message msg = Message.obtain();
			msg.what = MSG_EXHIBIT_CHANGED;
			msg.obj = beaconId;
			mHandler.sendMessage(msg);
		} 
		else if (mCurrentExhibit == null) { // 当前无展品，第一次自动定位
			Message msg = Message.obtain();
			msg.what = MSG_EXHIBIT_CHANGED;
			msg.obj = beaconId;
			mHandler.sendMessage(msg);
		}
	}

	/**
	 * 保证展品列表可用，在当前条件下是保证展品列表size>=一屏展示图片数，或不可加载更多（即没有左右展品的情况下）
	 */
	private void confirmExhibitListAvailable() {
		if (mExhibitsList.size() >= ScreenUtils.getPicCount(mContext))
			return;
		// 先向左加载一个,保证该列表的第一个展品处于“中间”位置
		loadPreExhibit(mExhibitsList.get(0), null);
		// 如果未达到一屏展示图片数，向右加载更多
		for (int i = mExhibitsList.size(); i < ScreenUtils
				.getPicCount(mContext); i++) {
			loadNextExhibit(mExhibitsList.get(mExhibitsList.size() - 1), null);
		}
		// 如果加载完还是没有达到一屏展示图片数，则向左加载
		for (int i = mExhibitsList.size(); i < ScreenUtils
				.getPicCount(mContext); i++) {
			loadPreExhibit(mExhibitsList.get(0), null);
		}
		for (int i = 0; i < mExhibitsList.size(); i++) {
			Log.w(TAG, mExhibitsList.get(i).getName());
		}
		// 如果还未达到，证明已经不能加载更多了。
	}

	/**
	 * 开始播放
	 */
	private void notifyStartPlaying() {
		if (mCurrentExhibit == null) {
			Toast.makeText(mContext, "无当前展品", Toast.LENGTH_SHORT).show();
			return;
		}
		if (mLyricView == null) {
			Toast.makeText(mContext, "无当前歌词", Toast.LENGTH_SHORT).show();
			return;
		}
		// 准备音频文件
		mLyricView.prepare(Constant.getAudioDownloadPath(
				mCurrentExhibit.getAudioUrl(), mMuseumId), Constant
				.getLrcDownloadPath(mCurrentExhibit.getTextUrl(), mMuseumId));
		// 设置progressBar的最大值
		pbMusic.setMax(mLyricView.getDuration());
		// 开始播放
		mLyricView.start();
		ivStart.setImageResource(R.drawable.play_btn_pause);
		ivStart.setClickable(true);
		ivPre.setClickable(true);
		ivNext.setClickable(true);
	}

	/**
	 * 设置多角度图片gallery可见性
	 * 
	 * @param visibility
	 *            true:可见；false:不可见
	 */
	private void setPicGalleryVisibility(boolean visibility) {
		if (visibility) {
			mPicGallery.setVisibility(View.VISIBLE);
			ivPicExpand.setImageResource(R.drawable.title_normal);
		} else {
			mPicGallery.setVisibility(View.GONE);
			ivPicExpand.setImageResource(R.drawable.title_expanded);
		}
	}

	/**
	 * 设置展品列表图片gallery可见性
	 * 
	 * @param visibility
	 *            true:可见；false:不可见
	 */
	private void setExhibitGalleryVisibility(boolean visibility) {
		if (visibility) {
			mExhibitGallery.setVisibility(View.VISIBLE);
			ivExhibitExpand.setImageResource(R.drawable.title_normal);
		} else {
			mExhibitGallery.setVisibility(View.GONE);
			ivExhibitExpand.setImageResource(R.drawable.title_expanded);

		}
	}

	/**
	 * custom clickListener
	 * 
	 * @author yetwish
	 * @date 2015-4-25
	 */
	private class MyClickListener implements OnClickListener {
		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.frag_follow_guide_iv_start:
				// TODO Auto-generated method stub
				if (mLyricView.isPlaying()) {
					mLyricView.pause();
					ivStart.setImageResource(R.drawable.play_btn_play);
				} else {
					mLyricView.start();
					ivStart.setImageResource(R.drawable.play_btn_pause);
				}
				break;
			case R.id.frag_follow_guide_iv_pre:
				if (mCurrentExhibit != null) {
					int index = mPicGallery.getCurrentSelectedIndex();
					mPicGallery.setCurrentSelectedItem(false, index - 1);
				}
				break;
			case R.id.frag_follow_guide_iv_next:
				if (mCurrentExhibit != null) {
					int index = mPicGallery.getCurrentSelectedIndex();
					mPicGallery.setCurrentSelectedItem(false, index + 1);
				}
				break;
			case R.id.frag_follow_guide_tv_title_pics:
			case R.id.frag_follow_guide_iv_gallery_expand:
				// 展开多角度列表
				if (!picFlag) {
					// gvGalleryPics.setVisibility(View.VISIBLE);
					setPicGalleryVisibility(true);
					picFlag = true;

				} else {
					setPicGalleryVisibility(false);
					picFlag = false;
				}
				break;
			case R.id.frag_follow_guide_tv_title_exhibits:
			case R.id.frag_follow_guide_iv_exhibit_expand:
				if (!exhibitFlag) {
					setExhibitGalleryVisibility(true);
					exhibitFlag = true;
				} else {
					setExhibitGalleryVisibility(false);
					exhibitFlag = false;
				}
				break;

			}
		}
	}

	/**
	 * 当传入的beacon与当前beacon不同时，则证明已经进入其他展品区域，则自动播放其他展品 ？还是弹出转换？
	 */
	@Override
	public void onNearestBeaconDiscovered(int type, Beacon beacon) {
		// 取消显示搜索beacon对话框
		if (pDialog != null && pDialog.isShowing()) {
			pDialog.dismiss();
		}
		
		if (beacon == null)// 不做处理
			return;
		
		Log.d(TAG,"nearst beacon major,minor="+beacon.getId2()+","+beacon.getId3());
		
		// 根据搜索到的beacon的major
		// minor获取数据库中匹配的beaconBean,以获取其id，用于检索该beacon中的exhibit
		GetBeanHelper.getInstance(mContext).getBeaconBean(mMuseumId,
				beacon.getId2().toString(), beacon.getId3().toString(),
				new GetBeanCallBack<OfflineBeaconBean>() {

					@Override
					public void onGetBeanResponse(OfflineBeaconBean response) {
						if (response != null) {
							changeCurrentExhibitByBeaconId(response.getId());
						}
					}
				});

	}

	@Override
	public void onGuideModeChanged(boolean isAutoGuide) {
		if (isAutoGuide) {
			HomeActivity.getMenu().toggle();
			RadioButton rb = (RadioButton) HomeActivity.mRadioGroup
					.findViewById(R.id.home_tab_follow);
			rb.setChecked(true);
			// 显示搜索beacon
			pDialog = mDialogHelper.showSearchingProgressDialog();
			// 设置beacon监听
			HomeActivity.setBeaconSearcherListener(this);
		}
		else {
			// 显示正在加载展品
			pDialog = mDialogHelper.showLoadingProgressDialog();
		}
	}

	public interface LoadCallBack {

		void onLoadSuccess(int count);

		void onLoadFailed();
	}
}
