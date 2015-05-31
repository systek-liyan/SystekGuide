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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.app.guide.AppContext;
import com.app.guide.Constant;
import com.app.guide.R;
import com.app.guide.adapter.ExhibitAdapter;
import com.app.guide.bean.ExhibitBean;
import com.app.guide.offline.GetBeanFromSql;
import com.app.guide.utils.BitmapUtils;
import com.app.guide.widget.AutoLoadListView;
import com.app.guide.widget.AutoLoadListView.OnLoadListener;
import com.app.guide.widget.HeaderLayout;

/**
 * 博物馆主页fragment
 * 
 * 修改为上拉加载更多以及数据库访问方式
 * 
 * @author yetwish
 */
public class MuseumIntroduceFragment extends Fragment {

	private TextView tvTitle;
	private ViewPager viewPager;

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
			"http://img.my.csdn.net/uploads/201407/26/1406383248_3693.jpg" };

	private LayoutInflater mInflater;
	private LinearLayout headerLayout;
	private HeaderLayout fragHeader;
	/**
	 * store the imageView which shows the pictures of museum
	 */
	private ArrayList<NetworkImageView> images;

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

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		getScreenHeight();
		// 获取数据
		getImages();
		getExhibitData();
		exhibitAdapter = new ExhibitAdapter(activity, exhibits,
				R.layout.item_exhibit);
		
		
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
			exhibits = GetBeanFromSql.getExhibitBeans(getActivity(),
					((AppContext) getActivity().getApplication()).museumId,
					page);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 获取博物馆图片集，并将其放入imageView数组中
	 */
	private void getImages() {
		images = new ArrayList<NetworkImageView>();
		NetworkImageView networkImageView;
		for (int i = 0; i < urls.length; i++) {
			networkImageView = new NetworkImageView(this.getActivity());
			networkImageView.setErrorImageResId(R.drawable.icon);
			networkImageView.setDefaultImageResId(R.drawable.pictures_no);
			networkImageView.setImageUrl(urls[i],
					BitmapUtils.getImageLoader(this.getActivity()));
			images.add(networkImageView);
		}
	}

	/**
	 * 将提示小圆圈装入imageView数组中
	 */
	private void getTips() {
		tips = new ArrayList<ImageView>();
		ImageView imageView;
		for (int i = 0; i < urls.length; i++) {
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
	 * 将viewPager和其他view 作为header加入到listView中，使整个页面可滚动，且能复用list_item
	 */
	@SuppressLint("InflateParams")
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		fragHeader = (HeaderLayout) view.findViewById(R.id.frag_header_main);
		fragHeader.setTitle("XX博物馆");

		// 获取header layout
		headerLayout = (LinearLayout) mInflater.inflate(
				R.layout.frag_main_header, null);

		// 获取viewPager
		// 解决viewPager占满屏幕的问题,将viewPager嵌套在一个layout下，通过指定该layout的params来决定viewPager的params
		viewPager = (ViewPager) headerLayout.findViewById(R.id.frag_main_pager);
		// 获取屏幕宽高度，并将viewPager的高度设为屏幕高度的2/5
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, dm.heightPixels * 2 / 5);
		//params.setMargins(0, 0, 0, 0);
		viewPager.setLayoutParams(params);
		
		//TODO viewPager 宽度 无法占满？ 如何解决？
		Toast.makeText(this.getActivity(), "width " + params.width,
				Toast.LENGTH_SHORT).show();

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

		// 解决slidingMenu和viewPager 滑动冲突
		HomeActivity.sm.addIgnoredView(viewPager);
	}
	
	private void loadOnPage(){
		List<ExhibitBean> data = null;
		try {
			data = GetBeanFromSql
					.getExhibitBeans(getActivity(),
							((AppContext) getActivity()
									.getApplication()).museumId, page);
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
				return images.size();
			}

			@Override
			public Object instantiateItem(View container, int position) {
				NetworkImageView image = images.get(position);
				((ViewGroup) container).addView(image, 0);
				return image;

			}

			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager) container).removeView(images.get(position));
			}
		};
	}

}
