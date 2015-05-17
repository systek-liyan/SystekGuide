package com.app.guide.ui;

import java.util.ArrayList;
import java.util.List;

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
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.app.guide.R;
import com.app.guide.adapter.ExhibitAdapter;
import com.app.guide.bean.Exhibit;
import com.app.guide.widget.HeaderLayout;

/**
 * 博物馆主页fragment
 * @author yetwish
 */
public class MuseumIntroduceFragment extends Fragment{

	private View rootView;
	private TextView tvTitle;
	private ViewPager viewPager;
	private final static int[] IMAGE_RESOURCES = {R.drawable.home_tab_main_normal_img,R.drawable.home_tab_follow_normal_img,R.drawable.home_tab_subject_normal_img};
	private LayoutInflater mInflater;
	private LinearLayout headerLayout;
	private HeaderLayout fragHeader;
	/**
	 * store the imageView which shows the pictures of museum
	 */
	private ArrayList<ImageView> images; 	
	
	/**
	 * store the imageview which shows the circles
	 */
	private ArrayList<ImageView> tips;
	private LinearLayout tipsGroup;
	
	private final static String introduction = "1977年平谷刘家河出土。敛口，口沿外折，方唇，颈粗短，折肩，深腹，高圈足。颈部饰以两道平行凸弦纹，肩部饰一周目雷纹，其上圆雕等距离三个大卷角羊首，腹部饰以扉棱为鼻的饕餮纹，圈足饰一周对角云雷纹，其上有三个方形小镂孔。此罍带有商代中期的显著特征。其整体造型，纹饰与河南郑州白家庄M3出土的罍较相似。此器造型凝重，纹饰细密，罍肩上的羊首系用分铸法铸造，显示了商代北京地区青铜铸造工艺的高度水平。";
	
	
	private TextView tvIntroduction;
	
	private ListView lvExhibit;
	private ExhibitAdapter exhibitAdapter;
	private List<Exhibit> exhibits;
	private final static int image = R.drawable.exhibit_icon;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		//获取数据
		getImages();
		getExhibitData();
		exhibitAdapter = new ExhibitAdapter(activity,exhibits,R.layout.item_exhibit);
	}
	
	/**
	 * get 精品的数据
	 */
	private void getExhibitData() {
		exhibits = new ArrayList<Exhibit>();
		int length = 10;
		for(int i = 0;i< length;i++){
			exhibits.add(new Exhibit("八星八箭青花瓷", "展厅2010", "唐朝",
					introduction, getResources().getDrawable(image)));
		}
	}
	
	/**
	 * 获取博物馆图片集，并将其放入imageView数组中
	 */
	private void getImages() {
		images = new ArrayList<ImageView>();
		ImageView imageView;
		for(int i =0;i<IMAGE_RESOURCES.length;i++){
			imageView = new ImageView(this.getActivity());
			imageView.setBackgroundResource(IMAGE_RESOURCES[i]);
			images.add(imageView);
		}
	}
	
	/**
	 * 将提示小圆圈装入imageView数组中
	 */
	private void getTips(){
		tips = new ArrayList<ImageView>();
		ImageView imageView ;
		for(int i = 0; i< IMAGE_RESOURCES.length; i++){
			imageView = new ImageView(this.getActivity());
			imageView.setLayoutParams(new LayoutParams(10,10));
			tips.add(imageView);
			if(i == 0)
				imageView.setBackgroundResource(R.drawable.page_indicator_focused);
			else 
				imageView.setBackgroundResource(R.drawable.page_indicator_unfocused);
			LinearLayout.LayoutParams layoutParams = new LayoutParams(new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
			layoutParams.leftMargin = 5;
			layoutParams.rightMargin = 5;
			tipsGroup.addView(imageView,layoutParams);
		}
	}
	
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container,  Bundle savedInstanceState) {
		//加入缓存加载，避免每次切换fragment都要加载一次视图
		if(rootView == null){
			rootView = inflater.inflate(R.layout.frag_main, null);
			mInflater = inflater;
			initViews();
		}
		ViewGroup parent = (ViewGroup)rootView.getParent();
		if(parent!=null){
			parent.removeView(rootView);
		}
		
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		
	}
	
	/**
	 * 将viewPager和其他view 作为header加入到listView中，使整个页面可滚动，且能复用list_item
	 */
	private void initViews() {
		if(rootView == null) return ;
		//获取frag header
		fragHeader = (HeaderLayout)rootView.findViewById(R.id.frag_header_main);
		fragHeader.setTitle("XX博物馆");
		
		//获取header layout
		headerLayout = (LinearLayout)mInflater.inflate(R.layout.frag_main_header, null);
		
		//获取viewPager
		//解决viewPager占满屏幕的问题,将viewPager嵌套在一个layout下，通过指定该layout的params来决定viewPager的params
		viewPager = (ViewPager)headerLayout.findViewById(R.id.frag_main_pager);
		//获取屏幕宽高度，并将viewPager的高度设为屏幕高度的2/5
		DisplayMetrics dm = new DisplayMetrics();        
		this.getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);    	        
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(dm.widthPixels, dm.heightPixels * 2 / 5);
		params.setMargins(20, 20, 20, 20);
		viewPager.setLayoutParams(params);       
		
		//加载提示小点点
		tipsGroup = (LinearLayout)headerLayout.findViewById(R.id.frag_main_pager_tips_group);
		getTips();

		//给viewPager设置adapter
		viewPager.setAdapter(createPagerAdapter());
		
		//给viewPager设置pageChangeListener，联动tipsGroup
		viewPager.setOnPageChangeListener(createPagerChangedListener());
		
		//获取博物馆简介textView,并给textView尾部添加imageView(播放键)
		tvIntroduction = (TextView)headerLayout.findViewById(R.id.frag_main_tv_introduction);
		//获取播放按钮的bitmap
		Bitmap bitmap= BitmapFactory.decodeResource(this.getActivity().getResources(), R.drawable.home_tab_subject_normal_img);  
		ImageSpan imgSpan = new ImageSpan(this.getActivity(),bitmap);  
        SpannableString spanString = new SpannableString("icon");
        spanString.setSpan(imgSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 
        tvIntroduction.append(spanString);
		
        //获取listview
        lvExhibit = (ListView)rootView.findViewById(R.id.frag_main_lv_exhibit);
		//添加header
		lvExhibit.addHeaderView(headerLayout);
		//设置adapter
		lvExhibit.setAdapter(exhibitAdapter);
		
		//解决slidingMenu和viewPager 滑动冲突
        HomeActivity.sm.addIgnoredView(viewPager);
	}
	
	private OnPageChangeListener createPagerChangedListener(){
		return new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				//更新小圆圈的状态
				for(int i = 0 ;i < tips.size();i++){
					if(position == i)
						tips.get(i).setBackgroundResource(R.drawable.page_indicator_focused);
					else 
						tips.get(i).setBackgroundResource(R.drawable.page_indicator_unfocused);
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
	 * @return pagerAdapter
	 */
	private PagerAdapter createPagerAdapter(){
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
				ImageView image = images.get(position);
				((ViewGroup) container).addView(image,0);
				return image;
				
			}
			
			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager)container).removeView(images.get(position));
			}
		};
	}
	
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		rootView = null;
	}
	
	
}
