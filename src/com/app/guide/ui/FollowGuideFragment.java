package com.app.guide.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.app.guide.R;
import com.app.guide.adapter.HorizontalScrollViewAdapter;
import com.app.guide.utils.BitmapUtils;
import com.app.guide.widget.HeaderLayout;
import com.app.guide.widget.LyricView;
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

	/**
	 * Store the layoutView of this fragment,by which we can get all other views
	 * of the fragment.
	 */
	private View rootView;

	private HeaderLayout fragHeader;

	/**
	 * the view which show the lyric.
	 */
	private LyricView mLyricView;

	private NetworkImageView ivLyricBg;// 改歌词背景为NetworkImageView

	private int lyricHeight;

	/**
	 * the button that control playing the media.
	 */
	private ImageView ivStart;

	/**
	 * the image button that can select media resource
	 */
	private ImageView ivBack;

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
	private List<String> galleryData;

	private boolean picFlag = false;
	private boolean exhibitFlag = false;

	private DisplayMetrics dm;

	/**
	 * click listener
	 */
	private MyClickListener listener;

	/**
	 * store BeaconSearcher instance, we use it to range beacon,and get the
	 * minBeacon from it.
	 */
	private BeaconSearcher beaconSearcher;

	/**
	 * defined several file path of media resources
	 */
	private static final String FILE_PATH = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ "/TourismGuide/LyricSync/";
	private static final String[] MP3_NAMES = { "shanqiu.mp3", "libai.mp3",
			"mote.mp3" };
	private static final String[] LYRIC_NAMES = { "shanqiu.lrc", "libai.lrc",
			"mote.lrc" };

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

	private void initGalleryData() {

		galleryData = new ArrayList<String>(Arrays.asList(
				"http://img.my.csdn.net/uploads/201407/26/1406383299_1976.jpg",
				"http://img.my.csdn.net/uploads/201407/26/1406383291_6518.jpg",
				"http://img.my.csdn.net/uploads/201407/26/1406383291_8239.jpg",
				"http://img.my.csdn.net/uploads/201407/26/1406383290_9329.jpg",
				"http://img.my.csdn.net/uploads/201407/26/1406383290_1042.jpg",
				"http://img.my.csdn.net/uploads/201407/26/1406383275_3977.jpg",
				"http://img.my.csdn.net/uploads/201407/26/1406383265_8550.jpg",
				"http://img.my.csdn.net/uploads/201407/26/1406383264_3954.jpg",
				"http://img.my.csdn.net/uploads/201407/26/1406383264_4787.jpg",
				"http://img.my.csdn.net/uploads/201407/26/1406383264_8243.jpg",
				"http://img.my.csdn.net/uploads/201407/26/1406383248_3693.jpg"));
	}

	/**
	 * get RootView and init View .
	 */
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.frag_follow_guide, null);
			initViews();
		}
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if (parent != null) {
			parent.removeView(rootView);
		}
		return rootView;

	}

	/**
	 * init view, find all other views by rootView.
	 */
	private void initViews() {
		if (rootView == null)
			return;
		// 获取屏幕信息
		dm = new DisplayMetrics();
		WindowManager manager = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		manager.getDefaultDisplay().getMetrics(dm);

		// init fragment header
		fragHeader = (HeaderLayout) rootView
				.findViewById(R.id.frag_header_follow_guide);
		fragHeader.setTitle("青铜绝唱——莲鹤方壶");
		fragHeader.setSearchingVisible(false);
		// initViews
		mLyricView = (LyricView) rootView
				.findViewById(R.id.frag_follow_guide_lyricview);
		ivLyricBg = (NetworkImageView) rootView
				.findViewById(R.id.frag_follow_guide_iv_bg);
		ivStart = (ImageView) rootView
				.findViewById(R.id.frag_follow_guide_iv_start);
		ivBack = (ImageView) rootView
				.findViewById(R.id.frag_follow_guide_iv_back);
		tvTitlePics = (TextView) rootView
				.findViewById(R.id.frag_follow_guide_tv_title_pics);
		tvTitleExhibits = (TextView) rootView
				.findViewById(R.id.frag_follow_guide_tv_title_exhibits);

		// 设置height
		mLyricView.post(new Runnable() {
			@Override
			public void run() {
				/**
				 * TODO
				 */
				int titleHeight = (int) ((tvTitlePics.getHeight()
						+ tvTitleExhibits.getHeight() + 20) * dm.density);
				int playBarHeight = (int) ((ivStart.getHeight() + 20) * dm.density);
				lyricHeight = dm.heightPixels - titleHeight - playBarHeight
						- 20 * 2;
				Toast.makeText(
						mContext,
						"Height: " + titleHeight + ",playbar height"
								+ playBarHeight, Toast.LENGTH_SHORT).show();
				mLyricView.setLayoutParams(new FrameLayout.LayoutParams(
						FrameLayout.LayoutParams.MATCH_PARENT, lyricHeight));

			}
		});

		initGallery();

		listener = new MyClickListener();

		ivStart.setBackgroundResource(R.drawable.play_btn_play);
		ivStart.setOnClickListener(listener);
		ivBack.setOnClickListener(listener);

		tvTitleExhibits.setOnClickListener(listener);
		tvTitlePics.setOnClickListener(listener);

	}

	private void initGallery() {
		mPicGallery = (MyHorizontalScrollView) rootView
				.findViewById(R.id.frag_follow_guide_pic_gallery_container);
		mExhibitGallery = (MyHorizontalScrollView) rootView
				.findViewById(R.id.frag_follow_guide_exhibit_gallery_container);

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
						Log.w("TAG", "INIT");
						ivLyricBg.setErrorImageResId(R.drawable.icon);
						ivLyricBg.setDefaultImageResId(R.drawable.icon);
						ivLyricBg.setImageUrl(galleryData.get(position),
								BitmapUtils.getImageLoader(mContext));
						viewIndicator.setAlpha(1f);

					}
				});

		mPicGallery.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(View view, int position) {
				ivLyricBg.setImageUrl(galleryData.get(position),
						BitmapUtils.getImageLoader(mContext));
				view.setBackgroundColor(Color.parseColor("#AA024DA4"));
			}
		});

		mPicGallery.initData(new HorizontalScrollViewAdapter(mContext,
				galleryData, R.layout.item_gallery));

		mExhibitGallery.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(View view, int position) {
				ivLyricBg.setImageUrl(galleryData.get(position),
						BitmapUtils.getImageLoader(mContext));
				view.setAlpha(1f);
				view.setBackgroundColor(Color.parseColor("#AA024DA4"));
			}
		});
		mExhibitGallery
				.setCurrentImageChangedListener(new CurrentImageChangedListener() {

					@Override
					public void onCurrentImgChanged(int position,
							View viewIndicator) {
						// TODO Auto-generated method stub
					}
				});

		mExhibitGallery.initData(new HorizontalScrollViewAdapter(mContext,
				galleryData, R.layout.item_gallery));

	}

	/**
	 * 添加判断beacon
	 */
	@Override
	public void onRangeIn(Beacon beacon) {

		Toast.makeText(this.getActivity(), "RANGEIN" + beacon.getName(),
				Toast.LENGTH_SHORT).show();
		if (beacon.getName().contains("50")) {
			current = 0;
			mLyricView.prepare(FILE_PATH
					+ MP3_NAMES[current % MP3_NAMES.length], FILE_PATH
					+ LYRIC_NAMES[current % MP3_NAMES.length]);

			mLyricView.start();
		} else if (beacon.getName().contains("44")) {
			current = 1;
			mLyricView.prepare(FILE_PATH
					+ MP3_NAMES[current % MP3_NAMES.length], FILE_PATH
					+ LYRIC_NAMES[current % MP3_NAMES.length]);

			mLyricView.start();
		}
		ivStart.setBackgroundResource(R.drawable.play_btn_pause_prs);
		ivStart.setClickable(true);
		ivBack.setClickable(true);
	}

	@Override
	public void onRangeOut(Beacon beacon) {
		Toast.makeText(this.getActivity(), "RANGEOUT" + beacon.getName(),
				Toast.LENGTH_SHORT).show();
		if (beacon.getName().contains("140")) {
			mLyricView.stop();
		}
		ivStart.setBackgroundResource(R.drawable.play_btn_play);
		ivStart.setClickable(false);
		ivBack.setClickable(false);
	}

	/**
	 * when the fragment is on the top of the stack ,start ranging the beacon
	 */
	@Override
	public void onResume() {
		super.onResume();
		rootView.scrollTo(0, 0);
		// TODO
		if (beaconSearcher.prepareBluetooth())
			beaconSearcher.startRanging();
		mLyricView.prepare(FILE_PATH + MP3_NAMES[current % MP3_NAMES.length],
				FILE_PATH + LYRIC_NAMES[current % MP3_NAMES.length]);

	}

	/**
	 * when the fragment is not on the top of the stack , stop ranging the
	 * beacon
	 */
	@Override
	public void onPause() {
		super.onPause();

		mLyricView.stop();
		ivStart.setBackgroundResource(R.drawable.play_btn_play);
		beaconSearcher.stopRanging();
	}

	/**
	 * destroy the fragment, close the beaconSearcher.
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		beaconSearcher.closeSearcher();
		rootView = null;
		mExhibitGallery.onDestroy();
		mPicGallery.onDestroy();
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
					ivStart.setBackgroundResource(R.drawable.play_btn_pause_prs);
				}
				break;
			case R.id.frag_follow_guide_iv_back:
				current++;
				if (mLyricView.isPlaying()) {
					mLyricView.pause();
					ivStart.setClickable(false);
				}
				mLyricView.prepare(FILE_PATH
						+ MP3_NAMES[current % MP3_NAMES.length], FILE_PATH
						+ LYRIC_NAMES[current % MP3_NAMES.length]);
				mLyricView.start();
				ivStart.setClickable(true);
				ivStart.setBackgroundResource(R.drawable.play_btn_pause_prs);
				break;
			case R.id.frag_follow_guide_iv_next:
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
