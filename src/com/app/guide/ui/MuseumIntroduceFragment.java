package com.app.guide.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.app.guide.AppContext;
import com.app.guide.Constant;
import com.app.guide.R;
import com.app.guide.adapter.ExhibitAdapter;
import com.app.guide.bean.ExhibitBean;
import com.app.guide.beanhelper.GetBeanCallBack;
import com.app.guide.beanhelper.GetBeanHelper;
import com.app.guide.model.MuseumModel;
import com.app.guide.utils.BitmapUtils;
import com.app.guide.utils.ScreenUtils;
import com.app.guide.widget.AutoLoadListView;
import com.app.guide.widget.AutoLoadListView.OnLoadListener;
import com.app.guide.widget.TopBar;

/**
 * 博物馆主页fragment 从博物馆选择页中获取博物馆的id
 * 
 * 修改为上拉加载更多以及数据库访问方式
 * 
 * @author yetwish
 */
public class MuseumIntroduceFragment extends Fragment {

	private ViewPager viewPager;

	private LayoutInflater mInflater;
	private LinearLayout headerLayout;
	private TopBar fragHeader;
	/**
	 * store the imageView which shows the pictures of museum
	 */
	private ArrayList<NetworkImageView> museumImages;

	/**
	 * store the imageview which shows the circles
	 */
	private ArrayList<ImageView> tips;
	private LinearLayout tipsGroup;

	private TextView tvIntroduction;

	private AutoLoadListView lvExhibit;
	private ExhibitAdapter exhibitAdapter;
	private List<ExhibitBean> exhibits;
	private int page;

	private String mMuseumId;

	private MuseumModel mMuseumModel;

	private ImageView ivPlay;

	private MediaPlayer mPlayer;

	//private static final String URL = "http://192.168.0.104:8080/daoyou/userfiles/1/files/baoli/audio/baochao.wav";

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// 获取museumId
		mMuseumId = ((AppContext) getActivity().getApplicationContext()).currentMuseumId;
		Log.w("Fragment", mMuseumId + "");
		// 初始化博物馆数据
		initMuseumData();
		// 获取博物馆精品展品数据
		getExhibitData();

		exhibitAdapter = new ExhibitAdapter(activity, exhibits,
				R.layout.item_exhibit);

	}

	/**
	 * 准备播放音乐
	 */
	private void prepareAudio() {
		if (mPlayer != null) {
			if (mPlayer.isPlaying()) {
				mPlayer.stop();
			}
			mPlayer.reset();
		} else {
			mPlayer = new MediaPlayer();
			mPlayer.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					ivPlay.setImageResource(R.drawable.home_tab_subject_normal_img);
				}
			});
		}
		try {
			// TODO 改为fromDB or fromNetwork
			// djt modify
			//mPlayer.setDataSource(URL);
			mPlayer.setDataSource(Constant.getAudioDownloadPath(mMuseumModel.getAudioUrl(),mMuseumId));
			
			mPlayer.prepare();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取该页面需要显示的博物馆数据
	 */
	private void initMuseumData() {
		GetBeanHelper.getInstance(getActivity()).getMuseumModel(mMuseumId,
				new GetBeanCallBack<MuseumModel>() {

					@Override
					public void onGetBeanResponse(MuseumModel response) {
						mMuseumModel = response;
						((AppContext) getActivity().getApplication()).floorCount = response
								.getFloorCount();
						// 初始化图片信息
						initMuseumImages();
						// 初始化音频信息
					}
				});
	}

	/**
	 * get 精品的数据
	 */
	private void getExhibitData() {
		page = 0;
		GetBeanHelper.getInstance(getActivity()).getExhibitList(mMuseumId, 2,
				page, new GetBeanCallBack<List<ExhibitBean>>() {

					@Override
					public void onGetBeanResponse(List<ExhibitBean> response) {
						exhibits = response;

					}
				});
	}

	/**
	 * 获取博物馆图片集，并将其放入NetworkImageView数组中
	 */
	private void initMuseumImages() {
		museumImages = new ArrayList<NetworkImageView>();
		NetworkImageView networkImageView;
		for (int i = 0; i < mMuseumModel.getImgsUrl().size(); i++) {
			networkImageView = new NetworkImageView(this.getActivity());
			networkImageView.setErrorImageResId(R.drawable.icon);
			networkImageView.setDefaultImageResId(R.drawable.picture_no);
			networkImageView.setImageUrl(mMuseumModel.getImgsUrl().get(i),
					BitmapUtils.getImageLoader(this.getActivity()));
			networkImageView.setScaleType(ScaleType.CENTER_CROP);
			museumImages.add(networkImageView);
		}
	}

	/**
	 * 将提示小圆圈装入imageView数组中
	 */
	private void getTips() {
		tips = new ArrayList<ImageView>();
		ImageView imageView;
		for (int i = 0; i < mMuseumModel.getImgsUrl().size(); i++) {
			imageView = new ImageView(this.getActivity());
			imageView.setLayoutParams(new LayoutParams(10, 10));
			tips.add(imageView);
			if (i == 0)
				imageView
						.setBackgroundResource(R.drawable.page_indicator_focused);
			else
				imageView
						.setBackgroundResource(R.drawable.page_indicator_unfocused);
			LinearLayout.LayoutParams layoutParams = new LayoutParams(
					new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));
			layoutParams.leftMargin = 5;
			layoutParams.rightMargin = 5;
			tipsGroup.addView(imageView, layoutParams);
		}
	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.frag_main, null);
		mInflater = inflater;
		return view;
	}

	/**
	 * 作为header加入到listView中，使整个页面可滚动，且能复用list_item
	 */
	@SuppressLint("InflateParams")
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		fragHeader = (TopBar) view.findViewById(R.id.frag_header_main);
		// 设置博物馆名字
		fragHeader.setTitle(mMuseumModel.getName());

		// 获取header layout
		headerLayout = (LinearLayout) mInflater.inflate(
				R.layout.frag_main_header, null);

		// 获取viewPager
		// 解决viewPager占满屏幕的问题,将viewPager嵌套在一个layout下，通过指定该layout的params来决定viewPager的params
		viewPager = (ViewPager) headerLayout.findViewById(R.id.frag_main_pager);
		// 获取屏幕宽高度，并将viewPager的高度设为屏幕高度的2/5
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				ScreenUtils.getScreenHeight(getActivity()) * 2 / 5);
		// params.setMargins(20, 20, 20, 0);
		viewPager.setLayoutParams(params);

		// 加载提示小点点
		tipsGroup = (LinearLayout) headerLayout
				.findViewById(R.id.frag_main_pager_tips_group);
		getTips();

		// 给viewPager设置adapter
		viewPager.setAdapter(createPagerAdapter());

		// 给viewPager设置pageChangeListener，联动tipsGroup
		viewPager.setOnPageChangeListener(createPagerChangedListener());

		// 获取博物馆简介textView,并给textView尾部添加imageView(播放键)
		tvIntroduction = (TextView) headerLayout
				.findViewById(R.id.frag_main_tv_introduction);
		// 设置博物馆简介
		// 解析html文本
		Spanned text = Html.fromHtml(mMuseumModel.getIntroduce());
		tvIntroduction.setText(text);
		ivPlay = (ImageView) headerLayout.findViewById(R.id.frag_main_iv_play);

		ivPlay.setClickable(true);
		ivPlay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mPlayer == null)
					return;
				if (mPlayer.isPlaying()) {
					mPlayer.pause();
					ivPlay.setImageResource(R.drawable.home_tab_subject_normal_img);
				} else {
					mPlayer.start();
					ivPlay.setImageResource(R.drawable.home_tab_subject_pressed_img);
				}
			}
		});

		// // 获取播放按钮的bitmap
		// Bitmap bitmap = BitmapFactory.decodeResource(this.getActivity()
		// .getResources(), R.drawable.home_tab_subject_normal_img);
		// ImageSpan imgSpan = new ImageSpan(this.getActivity(), bitmap);
		// SpannableString spanString = new SpannableString("icon");
		// spanString.setSpan(imgSpan, 0, 4,
		// Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		// tvIntroduction.append(spanString);

		// 获取listview
		lvExhibit = (AutoLoadListView) view
				.findViewById(R.id.frag_main_lv_exhibit);
		// 添加header
		lvExhibit.addHeaderView(headerLayout);
		// 设置adapter
		lvExhibit.setAdapter(exhibitAdapter);
		initListView();
		
		// 解决slidingMenu和viewPager 滑动冲突
		HomeActivity.getMenu().addIgnoredView(viewPager);
	}

	@Override
	public void onResume() {
		super.onResume();
		prepareAudio();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mPlayer != null) {
			mPlayer.stop();
			ivPlay.setImageResource(R.drawable.home_tab_subject_normal_img);
		}
	}

	private void initListView() {
		lvExhibit.setOnLoadListener(new OnLoadListener() {
			@Override
			public void onLoad() {
				page++;
				loadOnPage();
			}

			@Override
			public void onRetry() {
				loadOnPage();
			}
		});

		lvExhibit.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				((AppContext) getActivity().getApplication()).currentExhibitId = exhibits
						.get(position - 1).getId();
				
				// 目前，认为这样的选择展品，自动变为手动选择
				((AppContext) getActivity().getApplication())
						.setGuideMode(Constant.GUIDE_MODE_MANUALLY);
				
				// 不能使用HomeActivity.mRadioGroup.check(R.id.home_tab_follow);
				// 因为该方法会重复调用onCheckedChanged()方法
				// ，从而导致java.lang.IllegalStateException异常
				// 跳转到follow guide fragment
				RadioButton btn = (RadioButton) HomeActivity.mRadioGroup
						.findViewById(R.id.home_tab_follow);
				btn.setChecked(true);
				btn.setEnabled(true);
			}
		});

	}

	private void loadOnPage() {
		GetBeanHelper.getInstance(getActivity()).getExhibitList(mMuseumId, 2,
				page, new GetBeanCallBack<List<ExhibitBean>>() {

					@Override
					public void onGetBeanResponse(List<ExhibitBean> response) {
						if (response.size() != 0) {
							exhibits.addAll(response);
							if (response.size() < Constant.PAGE_COUNT) {
								lvExhibit.setLoadFull();
							}
						} 
                        ////////////////////// 认为已经下载全部了
						else {
							//lvExhibit.setLoadFailed();
							lvExhibit.setLoadFull();
						}
						lvExhibit.onLoadComplete();
					}
				});
	
		// 必须通知数据改变，否则，当正在加载时，点击展品，切换到新的页面，导致在后台线程改变这个页面的ListView数据，从而使app停止。
		// 应该在此通知，即在ui线程改变adapter中的数据
		exhibitAdapter.notifyDataSetChanged();
	}

	private OnPageChangeListener createPagerChangedListener() {
		return new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// 更新小圆圈的状态
				for (int i = 0; i < tips.size(); i++) {
					if (position == i)
						tips.get(i).setBackgroundResource(
								R.drawable.page_indicator_focused);
					else
						tips.get(i).setBackgroundResource(
								R.drawable.page_indicator_unfocused);
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Autogenerated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Autogenerated method stub

			}
		};
	}

	/**
	 * 获取viewPager adapter
	 * 
	 * @return pagerAdapter
	 */
	private PagerAdapter createPagerAdapter() {
		return new PagerAdapter() {

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return museumImages.size();
			}

			@Override
			public Object instantiateItem(View container, int position) {
				NetworkImageView image = museumImages.get(position);
				((ViewGroup) container).addView(image, 0);
				return image;

			}

			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager) container).removeView(museumImages.get(position));
			}
		};
	}

}
