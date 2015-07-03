package com.app.guide.ui;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import com.app.guide.bean.MuseumDetailBean;
import com.app.guide.offline.GetBeanFromSql;
import com.app.guide.utils.BitmapUtils;
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

	private DisplayMetrics dm;

	private String mMuseumId;

	private MuseumDetailBean mMuseumDetailBean;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// 获取museumId
		mMuseumId = ((AppContext) getActivity().getApplicationContext()).currentMuseumId;
		Log.w("Fragment", mMuseumId + "");
		getScreenHeight();
		// 初始化博物馆数据
		initMuseumData();
		// 获取博物馆精品展品数据
		getExhibitData();

		exhibitAdapter = new ExhibitAdapter(activity, exhibits,
				R.layout.item_exhibit);

	}

	/**
	 * 获取该页面需要显示的博物馆数据
	 */
	private void initMuseumData() {
		try {
			mMuseumDetailBean = GetBeanFromSql.getMuseunDetailBean(
					getActivity(), mMuseumId);
			Log.w("Fragment", mMuseumDetailBean.getName());
			// 初始化图片信息
			initMuseumImages();
			// 初始化音频信息
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void getScreenHeight() {
		dm = new DisplayMetrics();
		this.getActivity().getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
	}

	/**
	 * get 精品的数据
	 */
	private void getExhibitData() {
		page = 0;
		try {
			exhibits = GetBeanFromSql.getExhibitBeans(getActivity(), mMuseumId,
					page);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取博物馆图片集，并将其放入NetworkImageView数组中
	 */
	private void initMuseumImages() {
		museumImages = new ArrayList<NetworkImageView>();
		NetworkImageView networkImageView;
		for (int i = 0; i < mMuseumDetailBean.getImageList().size(); i++) {
			networkImageView = new NetworkImageView(this.getActivity());
			networkImageView.setErrorImageResId(R.drawable.icon);
			networkImageView.setDefaultImageResId(R.drawable.pictures_no);
			networkImageView.setImageUrl(mMuseumDetailBean.getImageList()
					.get(i), BitmapUtils.getImageLoader(this.getActivity()));
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
		for (int i = 0; i < mMuseumDetailBean.getImageList().size(); i++) {
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
		fragHeader.setTitle(mMuseumDetailBean.getName());

		// 获取header layout
		headerLayout = (LinearLayout) mInflater.inflate(
				R.layout.frag_main_header, null);

		// 获取viewPager
		// 解决viewPager占满屏幕的问题,将viewPager嵌套在一个layout下，通过指定该layout的params来决定viewPager的params
		viewPager = (ViewPager) headerLayout.findViewById(R.id.frag_main_pager);
		// 获取屏幕宽高度，并将viewPager的高度设为屏幕高度的2/5
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT, dm.heightPixels * 2 / 5);
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
		tvIntroduction.setText(mMuseumDetailBean.getTextUrl());
		// 获取播放按钮的bitmap
		Bitmap bitmap = BitmapFactory.decodeResource(this.getActivity()
				.getResources(), R.drawable.home_tab_subject_normal_img);
		ImageSpan imgSpan = new ImageSpan(this.getActivity(), bitmap);
		SpannableString spanString = new SpannableString("icon");
		spanString.setSpan(imgSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		tvIntroduction.append(spanString);

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

	private void initListView() {
		lvExhibit.setOnLoadListener(new OnLoadListener() {
			@Override
			public void onLoad() {
				// TODO Auto-generated method stub
				page++;
				loadOnPage();
			}

			@Override
			public void onRetry() {
				// TODO Auto-generated method stub
				loadOnPage();
			}
		});

		lvExhibit.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				((AppContext) getActivity().getApplication()).currentExhibitId = exhibits
						.get(position - 1).getId();
				((AppContext) getActivity().getApplication())
						.setGuideMode(false);
				// 不能使用HomeActivity.mRadioGroup.check(R.id.home_tab_follow);
				// 因为该方法会重复调用onCheckedChanged()方法
				// ，从而导致java.lang.IllegalStateException异常
				// 跳转到follow guide fragment
				((RadioButton) HomeActivity.mRadioGroup
						.findViewById(R.id.home_tab_follow)).setChecked(true);

			}
		});

	}

	private void loadOnPage() {
		List<ExhibitBean> data = null;
		try {
			data = GetBeanFromSql.getExhibitBeans(getActivity(), mMuseumId,
					page);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (data != null) {
			exhibitAdapter.addData(data);
			if (data.size() < Constant.PAGE_COUNT) {
				lvExhibit.setLoadFull();
			}
		} else {
			lvExhibit.setLoadFailed();
		}
		lvExhibit.onLoadComplete();
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
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

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
