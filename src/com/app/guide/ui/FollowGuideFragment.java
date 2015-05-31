package com.app.guide.ui;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.altbeacon.beacon.Beacon;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.app.guide.Constant;
import com.app.guide.R;
import com.app.guide.adapter.HorizontalScrollViewAdapter;
import com.app.guide.bean.Exhibit;
import com.app.guide.bean.ImageOption;
import com.app.guide.offline.GetBeanFromSql;
import com.app.guide.utils.BitmapUtils;
import com.app.guide.widget.HeaderLayout;
import com.app.guide.widget.LyricView;
import com.app.guide.widget.LyricView.onProgressChangedListener;
import com.app.guide.widget.MyHorizontalScrollView;
import com.app.guide.widget.MyHorizontalScrollView.CurrentImageChangedListener;
import com.app.guide.widget.MyHorizontalScrollView.OnItemClickListener;
import com.app.guide.widget.MyHorizontalScrollView.OnLoadingMoreListener;

import edu.xidian.NearestBeacon.BeaconSearcher;
import edu.xidian.NearestBeacon.BeaconSearcher.OnNearestBeaconListener;
import edu.xidian.NearestBeacon.NearestBeacon;

/**
 * 两种进入该界面的方式，底部导航进入 和选择某一展品进入
 * 随身导游页面，包含与Beacon相关的操作，根据停留时间获取最近的beacon，并让相应的音频文件自动播放，并显示文字
 * 
 * 自动和手动的切换  跳转  手动 自动
 * 放到侧边栏 checkBox
 * 
 * map 点击展品 跳转
 * 
 * 设置 距离
 * 
 * 搜索 根据名称做匹配
 * 
 * 
 * 需要加载的数据有 当前展品的数据 如名称，audio数据 ,gallery 数据, 左右两展品的数据， TODO 更多展品的数据从哪来？
 * 根据当前所在beaconId 获取一组exhibit? TODO 加入加载更多
 * 
 * @author yetwish
 * @date 2015-4-25
 */
public class FollowGuideFragment extends Fragment implements
		OnNearestBeaconListener {

	private static final String TAG = FollowGuideFragment.class.getSimpleName();

	/**
	 * 上下文对象
	 */
	private Context mContext;

	/**
	 * intent 对象，用以传递数据
	 */
	private Intent mIntent;

	/**
	 * 存储屏幕像素信息
	 */
	private DisplayMetrics dm;

	/**
	 * 标题栏
	 */
	private HeaderLayout fragHeader;

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
	 * 播放进度条
	 */
	private ProgressBar pbMusic;

	/**
	 * 多角度图片tv
	 */
	private TextView tvTitlePics;

	/**
	 * 展品图片tv
	 */
	private TextView tvTitleExhibits;

	/**
	 * 多角度图片gallery view
	 */
	private MyHorizontalScrollView mPicGallery;

	/**
	 * 展品图片gallery view
	 */
	private MyHorizontalScrollView mExhibitGallery;

	/**
	 * 存储多角度图片gallery的数据
	 */
	private List<ImageOption> mGalleryList = null;

	/**
	 * 存储展品图片数据
	 */
	private List<ImageOption> mExhibitImages = new ArrayList<ImageOption>();

	/**
	 * gallery adapter
	 */
	private HorizontalScrollViewAdapter mGalleryAdapter;

	/**
	 * exhibit Adapter;
	 */
	private HorizontalScrollViewAdapter mExhibitAdapter;

	/**
	 * 标识多角度图片列表是否展开
	 */
	private boolean picFlag = false;

	/**
	 * 标识展品图片列表是否展开
	 */
	private boolean exhibitFlag = false;

	/**
	 * 当前显示的展品
	 */
	private Exhibit mCurrentExhibit;

	/**
	 * 附近的展品1
	 */
	private Exhibit lExhibit;

	/**
	 * 附近的展品2
	 */
	private Exhibit rExhibit;

	/**
	 * 附近的展品列表
	 */
	private List<Exhibit> mExhibitsList = new ArrayList<Exhibit>();

	/**
	 * 当前exhibit id
	 */
	private int mCurrentExhibitId;

	/**
	 * 当前博物馆的id
	 */
	private int mMuseumId;

	/**
	 * click listener
	 */
	private MyClickListener mClickListener;

	/**
	 * store BeaconSearcher instance, we use it to range beacon,and get the
	 * minBeacon from it.
	 */
	private BeaconSearcher beaconSearcher;

	private boolean isFirst = true;

	/**
	 * defined several file path of media resources
	 */
	private static final String FILE_PATH = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ "/TourismGuide/LyricSync/";
	private static final String[] MP3_NAMES = { "3201.mp3", "3203.mp3",
			"3205.mp3" };
	private static final String[] LYRIC_NAMES = { "3201.lrc", "3203.lrc",
			"3205.lrc" };

	private static final int MSG_PROGRESS_CHANGED = 0x200;
	private static final int MSG_BEACON_CHANGED = 0x201;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_PROGRESS_CHANGED:
				int progress = msg.arg1;
				// 更新progressBar 和 图集
				pbMusic.setProgress(progress);
				// 匹配图集的时间
				int index = 0;
				for (int i = 0; i < mGalleryList.size(); i++) {
					if (progress >= mGalleryList.get(i).getStartTime()) {
//						Log.w(TAG, mGalleryList.get(i).getStartTime() + "," + i
//								+ " start time");
						index = i;
					}
				}
				if (index != mPicGallery.getCurrentSelectedIndex() || isFirst) {
					Log.w("TAG", "SET START TIME" + " index "+index +" current "+mPicGallery.getCurrentSelectedIndex());
					//mGalleryList.get(index).setStartTime(progress + 120);
					mPicGallery.setCurrentSelectedItem(index);
					isFirst = false;
				}
				break;
			case MSG_BEACON_CHANGED:
				// 若当前beacon改变了
//				notifyCurrentExhibitChanged();
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
		mContext = activity;
		// 获取屏幕信息
		getScreenDisplayMetrix();
		// 初始化beacon 搜索器
		// initBeaconSearcher(activity);
		// 获取intent
		mIntent = activity.getIntent();
		// 获取museum id
		mMuseumId = mIntent.getIntExtra(Constant.EXTRA_MUSEUM_ID, 1);
		// 获取exhibit id
		mCurrentExhibitId = mIntent.getIntExtra(Constant.EXTRA_EXHIBIT_ID, -1);
		// 获取之后更新id
		if (mCurrentExhibitId != -1) {
			mIntent.putExtra(Constant.EXTRA_EXHIBIT_ID, mCurrentExhibitId);
		}
		// 根据exhibit id 获取数据
		initData(mCurrentExhibitId);
		// initGalleryData();
		// 初始化clickListener
		mClickListener = new MyClickListener();

	}

	/**
	 * 获取屏幕信息
	 */
	private void getScreenDisplayMetrix() {
		dm = new DisplayMetrics();
		WindowManager manager = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		manager.getDefaultDisplay().getMetrics(dm);
	}

	private void initData(int id) {
		// 判断是否是从点击展品跳转过来的
		if (id != -1) {
			// 开始手动模式
			// 根据传入的展品id，获取选中的展品信息
			try {
				mCurrentExhibit = GetBeanFromSql.getExhibit(mContext,
						mMuseumId, id);
				mCurrentExhibitId = id;
				if (mCurrentExhibit != null) {
					// 获取展品图片数据
					mGalleryList = mCurrentExhibit.getImgList();
					// 获取展品图片数据
					lExhibit = GetBeanFromSql.getExhibit(mContext, mMuseumId,
							mCurrentExhibit.getlExhibitBeanId());
					rExhibit = GetBeanFromSql.getExhibit(mContext, mMuseumId,
							mCurrentExhibit.getrExhibitBeanId());
					// 将展品添加到展品数组中
					mExhibitsList.clear();
					Log.w(TAG, "clear done");
					mExhibitsList.add(lExhibit);
					mExhibitsList.add(mCurrentExhibit);
					mExhibitsList.add(rExhibit);
					// 初始化 列表  获取展品icon
					for(int i = 0 ;i < mExhibitsList.size(); i++){
						mExhibitImages.add(new ImageOption(mExhibitsList.get(i).getIconUrl(),0));
					}
				}
				// 获取audio数据
				

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {// 是从导航键跳转过来的，开启随身导航模式（自动）
			// 开启BeaconSearcher
			// initBeaconSearcher(getActivity());
		}

	}

	/**
	 * 初始化beacon 搜索器
	 * 
	 * @param activity
	 */
	private void initBeaconSearcher(Activity activity) {

		beaconSearcher = BeaconSearcher.getInstance(activity);
		// 设定用于展品定位的最小停留时间(ms)
		beaconSearcher.setMin_stay_milliseconds(5000);
		// 设定用于展品定位的最小距离(m)
		beaconSearcher.setExhibit_distance(5.0);
		// 设置获取距离最近的beacon类型
		// NearestBeacon.GET_EXHIBIT_BEACON：游客定位beacon。可以不用设置上述的最小停留时间和最小距离
		// NearestBeacon.GET_EXHIBIT_BEACON：展品定位beacon
		beaconSearcher.setNearestBeaconType(NearestBeacon.GET_EXHIBIT_BEACON);
		// 设置beacon监听器
		beaconSearcher.setNearestBeaconListener(this);
		// 当蓝牙打开时，打开beacon搜索器，开始搜索距离最近的Beacon
		if (beaconSearcher.checkBLEEnable())
			beaconSearcher.openSearcher();
	}

	/**
	 * 打开蓝牙请求回调方法
	 */
	public void onBluetoothResult(int requestCode, int resultCode) {
		if (beaconSearcher.onBluetoothResult(requestCode, resultCode))
			beaconSearcher.openSearcher();
	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.frag_follow_guide, null);
		return view;

	}

	/**
	 * init views
	 */
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		// init fragment header
		fragHeader = (HeaderLayout) view
				.findViewById(R.id.frag_header_follow_guide);
		if (mCurrentExhibit != null) {
			fragHeader.setTitle(mCurrentExhibit.getName());
		} else
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

		pbMusic = (ProgressBar) view
				.findViewById(R.id.frag_follow_guide_progressbar);

		tvTitlePics = (TextView) view
				.findViewById(R.id.frag_follow_guide_tv_title_pics);

		tvTitleExhibits = (TextView) view
				.findViewById(R.id.frag_follow_guide_tv_title_exhibits);

		mPicGallery = (MyHorizontalScrollView) view
				.findViewById(R.id.frag_follow_guide_pic_gallery_container);

		mExhibitGallery = (MyHorizontalScrollView) view
				.findViewById(R.id.frag_follow_guide_exhibit_gallery_container);

		ivStart.setOnClickListener(mClickListener);

		ivStart.setClickable(false);

		initLyricView();

		// 如果当前已有展品信息，则加载gallery
		if (mCurrentExhibit != null) {
			initGalleryViews();
			notifyStartPlaying();
		}

		// notifyViewchange();

	}

	/**
	 * when the fragment is on the top of the stack ,start ranging the beacon
	 */
	@Override
	public void onResume() {
		super.onResume();
		// 从intent中获取exhibit id
		int id = mIntent.getIntExtra(Constant.EXTRA_EXHIBIT_ID,
				mCurrentExhibitId);
		if (id != mCurrentExhibitId) {
			Log.w(TAG, "id changed" + "id: "+id+"currentId : "+ mCurrentExhibitId);
			// 当前展品与选中的展品不同
			initData(id);
			// TODO
			// 通知audio 改变 gallery 改变
			if (mCurrentExhibit != null) {
				//需要把之前的注销掉
				initGalleryViews();
				notifyStartPlaying();
			}
		}
	}

	/**
	 * when the fragment is destroy , close the beaconSearcher,and release
	 * resources
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (beaconSearcher != null) {
			beaconSearcher.closeSearcher();
		}
		if (mLyricView != null) {
			mLyricView.destroy();
		}
		if (mPicGallery != null) {
			mPicGallery.destroy();
		}
		if (mExhibitGallery != null) {
			mExhibitGallery.destroy();
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
		});

		// TODO 设置height 并没有起到作用
		mLyricContainer.post(new Runnable() {
			@Override
			public void run() {

				int titleHeight = (int) ((tvTitlePics.getHeight()
						+ tvTitleExhibits.getHeight() + 20) * dm.density);
				Log.w(TAG, titleHeight + " title");
				int playBarHeight = (int) (getResources().getDimension(
						R.dimen.progressbar_height)
						+ getResources().getDimension(R.dimen.icon_height) + 20 * dm.density);

				Log.w(TAG, playBarHeight + " play bar");

				int headerHeight = (int) getResources().getDimension(
						R.dimen.frag_header_height);

				mLyricHeight = dm.heightPixels - titleHeight - playBarHeight
						- headerHeight;

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
		HomeActivity.sm.addIgnoredView(mPicGallery);

		HomeActivity.sm.addIgnoredView(mExhibitGallery);

		mPicGallery
				.setCurrentImageChangedListener(new CurrentImageChangedListener() {

					@Override
					public void onCurrentImgChanged(int position,
							View viewIndicator) {
						ivLyricBg.setErrorImageResId(R.drawable.icon);
						ivLyricBg.setDefaultImageResId(R.drawable.icon);
						ivLyricBg.setImageUrl(mGalleryList.get(position)
								.getImgUrl(), BitmapUtils
								.getImageLoader(mContext));
						viewIndicator.setAlpha(1f);
						mLyricView.setCurrentPosition(mGalleryList
								.get(position).getStartTime());

					}
				});

		mPicGallery.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(View view, int position) {
				ivLyricBg.setImageUrl(mGalleryList.get(position).getImgUrl(),
						BitmapUtils.getImageLoader(mContext));
				mLyricView.setCurrentPosition(mGalleryList.get(position)
						.getStartTime());
			}
		});

		mGalleryAdapter = new HorizontalScrollViewAdapter(mContext,
				mGalleryList, R.layout.item_gallery);
		mPicGallery.initData(mGalleryAdapter);

		mPicGallery.setCurrentSelectedItem(0);
		
		/**
		 * 设置tvPic可点击
		 */
		tvTitlePics.setOnClickListener(mClickListener);

		
		mExhibitGallery.setShowInCenter();
		
		// TODO 设置第一张
		mExhibitGallery.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {
				//changeCurrentExhibitById(position);
//				// 将多角度图片设置为首张
//				mPicGallery.setBackToBegin();
				ivLyricBg.setImageUrl(mExhibitImages.get(position).getImgUrl(),
						BitmapUtils.getImageLoader(mContext));
			}
		});
		mExhibitGallery.seOnLoadingMoreListener(new OnLoadingMoreListener() {
			@Override
			public int onRightLoadingMore() {
				// TODO Auto-generated method stub
				mExhibitImages.add(mGalleryList.get(4));
				return 1;
			}
			
			@Override
			public int onLeftLoadingMore() {
				// TODO Auto-generated method stub
				mExhibitImages.add(0,mGalleryList.get(6));
				return 1;
			}
		});
		
		mExhibitAdapter = new HorizontalScrollViewAdapter(mContext,
				mExhibitImages, R.layout.item_gallery);
		
		mExhibitGallery.initData(mExhibitAdapter);

		/**
		 * 设置tvExhibit 可点击
		 */
		tvTitleExhibits.setOnClickListener(mClickListener);

	}

	/**
	 * 当传入的beacon与当前beacon不同时，则证明已经进入其他展品区域，则自动播放其他展品 ？还是弹出转换？
	 */
	@Override
	public void getNearestBeacon(int type, Beacon beacon) {
		if (beacon == null)// 不做处理
			return;
		Log.w("BeaconSearcher", beacon.getId2().toString());
		if (beacon.getId2().toString().contains("40")) {
			changeCurrentExhibitById(1);
		} else if (beacon.getId2().toString().contains("44")) {
			changeCurrentExhibitById(2);
		}
	}

	private void changeCurrentExhibitById(int i) {
		if (mCurrentExhibitId != i) {
			mCurrentExhibitId = i;
			mHandler.sendEmptyMessage(MSG_BEACON_CHANGED);
		}
	}

	/**
	 * 当前展品改变时
	 */
	private void notifyCurrentExhibitChanged() {
		try {
			// 根据当前id 获取展品数据
			mCurrentExhibit = GetBeanFromSql.getExhibit(mContext, mMuseumId,
					mCurrentExhibitId);
			// 各控件加载数据
			// gallery
			mGalleryList = mCurrentExhibit.getImgList();
			// // 加载附近的展品
			// exhibitList.clear();
			// // TODO
			// lExhibit = GetBeanFromSql.getExhibit(mContext, mMuseumId,
			// mCurrentExhibitId + 2);
			// rExhibit = GetBeanFromSql.getExhibit(mContext, mMuseumId,
			// mCurrentExhibitId + 3);
			// // 存入列表中
			// exhibitList.add(mCurrentExhibit);
			// exhibitList.add(lExhibit);
			// exhibitList.add(rExhibit);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 开始播放
	 */
	private void notifyStartPlaying() {
		if (mCurrentExhibitId == -1)
			return;
		// 准备音频文件
		mLyricView.prepare(FILE_PATH
				+ MP3_NAMES[mCurrentExhibitId % MP3_NAMES.length], FILE_PATH
				+ LYRIC_NAMES[mCurrentExhibitId % MP3_NAMES.length]);
		// 设置progressBar的最大值
		pbMusic.setMax(mLyricView.getDuration());
		// 开始播放
		mLyricView.start();
		ivStart.setImageResource(R.drawable.play_btn_pause);
		ivStart.setClickable(true);
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
			case R.id.frag_follow_guide_tv_title_pics:
				// 展开多角度列表
				if (!picFlag) {
					// gvGalleryPics.setVisibility(View.VISIBLE);
					mPicGallery.setVisibility(View.VISIBLE);
					picFlag = true;
				} else {
					mPicGallery.setVisibility(View.GONE);
					// gvGalleryPics.setVisibility(View.GONE);
					picFlag = false;
				}
				break;
			case R.id.frag_follow_guide_tv_title_exhibits:
				if (!exhibitFlag) {
					mExhibitGallery.setVisibility(View.VISIBLE);
					// gvGalleryExhibits.setVisibility(View.VISIBLE);
					exhibitFlag = true;
				} else {
					mExhibitGallery.setVisibility(View.GONE);
					// gvGalleryExhibits.setVisibility(View.GONE);
					exhibitFlag = false;
				}
				break;

			}
		}
	}
}
