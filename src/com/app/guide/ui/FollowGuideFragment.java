package com.app.guide.ui;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.guide.R;
import com.app.guide.adapter.HorizontalScrollViewAdapter;
import com.app.guide.bean.ImageBean;
import com.app.guide.utils.ImageLoader;
import com.app.guide.widget.HeaderLayout;
import com.app.guide.widget.LyricView;
import com.app.guide.widget.LyricView.onProgressChangedListener;
import com.app.guide.widget.MyHorizontalScrollView;
import com.app.guide.widget.MyHorizontalScrollView.CurrentImageChangedListener;
import com.app.guide.widget.MyHorizontalScrollView.OnItemClickListener;
import com.estimote.sdk.Beacon;
import com.yetwish.libs.BeaconSearcher;
import com.yetwish.libs.BeaconSearcher.OnRangingListener;
import com.yetwish.libs.distance.DistanceUtils;

/**
 * 随身导游页面，包含与Beacon相关的操作，根据停留时间获取最近的beacon，并让相应的音频文件自动播放，并显示文字
 * 
 * @author yetwish
 * @date 2015-4-25
 */
public class FollowGuideFragment extends Fragment implements OnRangingListener {

	private HeaderLayout fragHeader;

	/**
	 * the view which show the lyric.
	 */
	private LyricView mLyricView;

	private ImageView ivLyricBg;

	private int lyricHeight;

	private int lyricWidth;

	/**
	 * the button that control playing the media.
	 */
	private ImageView ivStart;
	
	private ProgressBar pbMusic;


	/**
	 * store the resource of current playing
	 */
	private int current = 0;

	private Context mContext;

	private TextView tvTitlePics;
	private TextView tvTitleExhibits;

	private MyHorizontalScrollView mPicGallery;

	private MyHorizontalScrollView mExhibitGallery;

	/**
	 * store image resources for gallery
	 */
	private List<ImageBean> galleryData;

	private boolean picFlag = false;
	private boolean exhibitFlag = false;

	private DisplayMetrics dm;

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
	private static final String[] MP3_NAMES = { "3201.mp3","shanqiu.mp3", "libai.mp3",
			"mote.mp3" };
	private static final String[] LYRIC_NAMES = { "3201.lrc", "shanqiu.lrc", "libai.lrc",
			"mote.lrc" };

	private static final int MSG_PROGRESS_CHANGED = 0x200;
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_PROGRESS_CHANGED:
				int progress = msg.arg1;
				//更新progressBar 和 图集
				pbMusic.setProgress(progress);
				//匹配图集的时间
				int index = 0;
				for(int i = 0 ;i < startTime.length;i++){
					if(progress >= startTime[i])
						index = i;
				}
				if(index != mPicGallery.getCurrentSelectedIndex() || isFirst){
					galleryData.get(index).setStartTime(progress+125);
					mPicGallery.setCurrentSelectedItem(index);
					isFirst=false;
				}
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
		beaconSearcher = BeaconSearcher.getInstance(activity);
		// 设置最小停留时间
		BeaconSearcher.setMinEnterStayTime(6);
		BeaconSearcher.setMinChangeStayTime(4);
		beaconSearcher.openSearcher();
		beaconSearcher.setBeaconRangingListener(this);
		DistanceUtils.setBroadcastDistance(2.0);

		initGalleryData();

	}

	private static String[] urls = {
			"http://img.my.csdn.net/uploads/201407/26/1406383299_1976.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383291_6518.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383291_8239.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383290_9329.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383290_1042.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383275_3977.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383264_3954.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383264_4787.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383264_8243.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383248_3693.jpg"
	};
	/**
	 * msec
	 */
	private int[] startTime ;
		
	
	private void initGalleryData() {
		startTime = new int[urls.length];
		for(int i = 0 ; i<startTime.length;i++){
			startTime[i] = i*12*1000;
		}
		galleryData = new ArrayList<ImageBean>();
		for(int i = 0 ; i<urls.length;i++){
			galleryData.add(new ImageBean(urls[i],startTime[i]));
		}
	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.frag_follow_guide, null);
		return view;

	}
	
	private void initLyricView(){

		//设置监听
		mLyricView.setProgressChangedListener(new onProgressChangedListener() {
			
			@Override
			public void onProgressChanged(int progress) {
				Message msg = Message.obtain();
				msg.what = MSG_PROGRESS_CHANGED;
				msg.arg1 = progress;
				mHandler.sendMessage(msg);
			}
		});
		
		// 设置height
		mLyricView.post(new Runnable() {
			@Override
			public void run() {
				/**
				 * TODO
				 */
				lyricWidth = mLyricView.getWidth();
				int titleHeight = (int) ((tvTitlePics.getHeight()
						+ tvTitleExhibits.getHeight() + 20) * dm.density);
				int playBarHeight = (int) ((ivStart.getHeight() + 20) * dm.density);
				lyricHeight = dm.heightPixels - titleHeight - playBarHeight
						- 20 * 2;
//				Toast.makeText(
//						mContext,
//						"Height: " + titleHeight + ",playbar height"
//								+ playBarHeight, Toast.LENGTH_SHORT).show();
				mLyricView.setLayoutParams(new FrameLayout.LayoutParams(
						FrameLayout.LayoutParams.MATCH_PARENT, lyricHeight));

			}
		});
	}

	/**
	 * init views
	 */
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		// 获取屏幕信息
		dm = new DisplayMetrics();
		WindowManager manager = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		manager.getDefaultDisplay().getMetrics(dm);

		// init fragment header
		fragHeader = (HeaderLayout) view
				.findViewById(R.id.frag_header_follow_guide);
		fragHeader.setTitle("青铜绝唱——莲鹤方壶");
		fragHeader.setSearchingVisible(false);
		// initViews
		mLyricView = (LyricView) view
				.findViewById(R.id.frag_follow_guide_lyricview);
		ivLyricBg = (ImageView) view.findViewById(R.id.frag_follow_guide_iv_bg);
		
		ivStart = (ImageView) view
				.findViewById(R.id.frag_follow_guide_iv_start);
		
		pbMusic = (ProgressBar) view.findViewById(R.id.frag_follow_guide_progressbar);
		
		initLyricView();
		
		tvTitlePics = (TextView) view
				.findViewById(R.id.frag_follow_guide_tv_title_pics);
		tvTitleExhibits = (TextView) view
				.findViewById(R.id.frag_follow_guide_tv_title_exhibits);

		mPicGallery = (MyHorizontalScrollView) view
				.findViewById(R.id.frag_follow_guide_pic_gallery_container);
		mExhibitGallery = (MyHorizontalScrollView) view
				.findViewById(R.id.frag_follow_guide_exhibit_gallery_container);


		initGallery();

		mClickListener = new MyClickListener();

		ivStart.setBackgroundResource(R.drawable.play_btn_play);
		ivStart.setOnClickListener(mClickListener);

		tvTitleExhibits.setOnClickListener(mClickListener);
		tvTitlePics.setOnClickListener(mClickListener);
		
		notifyStartPlaying();
	}

	private void initGallery() {

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
						ImageLoader.getDefaultInstance().loadImage(
								galleryData.get(position).getImgUrl(), ivLyricBg, true,
								lyricWidth, lyricHeight);
						mLyricView.setCurrentPosition(galleryData.get(position).getStartTime());
						viewIndicator.setAlpha(1f);

					}
				});

		mPicGallery.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(View view, int position) {
				ImageLoader.getDefaultInstance().loadImage(
						galleryData.get(position).getImgUrl(), ivLyricBg, true, lyricWidth,
						lyricHeight);
				mLyricView.setCurrentPosition(galleryData.get(position).getStartTime());
				
			}
		});

		mPicGallery.initData(new HorizontalScrollViewAdapter(mContext,
				galleryData, R.layout.item_gallery));

		mExhibitGallery.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(View view, int position) {
				ImageLoader.getDefaultInstance().loadImage(
						galleryData.get(position).getImgUrl(), ivLyricBg, true, lyricWidth,
						lyricHeight);
				
			}
		});

		mExhibitGallery.initData(new HorizontalScrollViewAdapter(mContext,
				galleryData, R.layout.item_gallery));

		// ImageLoader.getDefaultInstance().loadImage(
		// galleryData.get(0), ivLyricBg, true,
		// lyricWidth, lyricHeight);
	}
	
	private void notifyStartPlaying(){
		mLyricView.prepare(FILE_PATH
				+ MP3_NAMES[current % MP3_NAMES.length], FILE_PATH
				+ LYRIC_NAMES[current % MP3_NAMES.length]);

		mLyricView.start();
		
		pbMusic.setMax(mLyricView.getDuration());
		
		ivStart.setBackgroundResource(R.drawable.play_btn_pause);
	}

	/**
	 * 添加判断beacon
	 */
	public void onRangeIn(Beacon beacon) {

		Toast.makeText(this.getActivity(), "RANGEIN" + beacon.getName(),
				Toast.LENGTH_SHORT).show();
		if (beacon.getName().contains("40")) {
			current = 0;
			notifyStartPlaying();
		} else if (beacon.getName().contains("44")) {
			current = 1;
			notifyStartPlaying();
		}
		ivStart.setBackgroundResource(R.drawable.play_btn_pause);
		ivStart.setClickable(true);
	}

	public void onRangeOut(Beacon beacon) {
		Toast.makeText(this.getActivity(), "RANGEOUT" + beacon.getName(),
				Toast.LENGTH_SHORT).show();
		if (beacon.getName().contains("140")) {
			mLyricView.stop();
		}
		ivStart.setBackgroundResource(R.drawable.play_btn_play);
		ivStart.setClickable(false);
	}

	/**
	 * when the fragment is on the top of the stack ,start ranging the beacon
	 */
	@Override
	public void onResume() {
		super.onResume();
		// TODO
		// if (beaconSearcher.prepareBluetooth())
		// beaconSearcher.startRanging();
		
	}

	/**
	 * when the fragment is not on the top of the stack , stop ranging the
	 * beacon
	 */
	@Override
	public void onPause() {
		super.onPause();

//		mLyricView.stop();
//		ivStart.setBackgroundResource(R.drawable.play_btn_play);
//		beaconSearcher.stopRanging();
	}

	/**
	 * destroy the fragment, close the beaconSearcher.
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		beaconSearcher.closeSearcher();
	}

	/**
	 * when the fragment calling destroy view, release all the resource that all
	 * the view hold.
	 */
	@Override
	public void onDestroyView() {
		super.onDestroyView();

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
					ivStart.setBackgroundResource(R.drawable.play_btn_play);
				} else {
					mLyricView.start();
					ivStart.setBackgroundResource(R.drawable.play_btn_pause);
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

	public void onBluetoothResult(int requestCode, int resultCode) {
		if (beaconSearcher.onBluetoothResult(requestCode, resultCode))
			beaconSearcher.startRanging();
	}

}
