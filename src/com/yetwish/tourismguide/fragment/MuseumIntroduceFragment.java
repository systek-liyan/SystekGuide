package com.yetwish.tourismguide.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.yetwish.tourismguide.R;
import com.yetwish.tourismguide.adapter.ExhibitAdapter;
import com.yetwish.tourismguide.info.Exhibit;
import com.yetwish.tourismguide.view.HeaderLayout;

/**
 * 博物馆主页fragment
 * @author yetwish
 */
public class MuseumIntroduceFragment extends Fragment{

	private View rootView;
	private TextView tvTitle;
	private ViewPager viewPager;
	private PagerAdapter pagerAdapter;
	private final static int[] IMAGE_RESOURCES = {
			R.drawable.main_footer_geren_img,R.drawable.main_footer_luntan_img,
			R.drawable.main_footer_retie_img,R.drawable.main_footer_zuijin_img};
	private ArrayList<ImageView> images;	
	
	private final static String introduction = "1977年平谷刘家河出土。敛口，口沿外折，方唇，颈粗短，折肩，深腹，高圈足。颈部饰以两道平行凸弦纹，肩部饰一周目雷纹，其上圆雕等距离三个大卷角羊首，腹部饰以扉棱为鼻的饕餮纹，圈足饰一周对角云雷纹，其上有三个方形小镂孔。此罍带有商代中期的显著特征。其整体造型，纹饰与河南郑州白家庄M3出土的罍较相似。此器造型凝重，纹饰细密，罍肩上的羊首系用分铸法铸造，显示了商代北京地区青铜铸造工艺的高度水平。";
	
	private HeaderLayout header;
	
	private LinearLayout pagerLayout;
	private TextView tvIntroduction;
	
	private ListView lvExhibit;
	private ExhibitAdapter exhibitAdapter;
	private List<Exhibit> exhibits;
	private final static int image = R.drawable.exhibit_icon;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		getImages();
		getExhibitData();
		exhibitAdapter = new ExhibitAdapter(activity,R.layout.fragment_subject_exhibit_list_item,exhibits);
		pagerAdapter = createPagerAdapter();
	}
	
	/**
	 * get 精品的数据
	 */
	private void getExhibitData() {
		exhibits = new ArrayList<Exhibit>();
		int length = 6;
		for(int i = 0;i< length;i++){
			exhibits.add(new Exhibit("八星八箭青花瓷", "展厅2010", "唐朝",
					introduction, getResources().getDrawable(image)));
		}
	}
	
	/**
	 * 获取博物馆图片集，将其放入imageView中
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
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		//加入缓存加载，避免每次切换fragment都要加载一次视图
		if(rootView == null){
			rootView = inflater.inflate(R.layout.fragment_musume_introduce, null);
			initViews();
		}
		ViewGroup parent = (ViewGroup)rootView.getParent();
		if(parent!=null){
			parent.removeView(rootView);
		}
		
		return rootView;
	}

	private void initViews() {
		if(rootView == null) return ;
		//将viewPager嵌套在一个layout下，通过指定该layout的params来决定viewPager的params
		//解决viewPager占满屏幕的问题
		pagerLayout = (LinearLayout) rootView.findViewById(R.id.fragment_museum_pager_content);	
		viewPager = (ViewPager)rootView.findViewById(R.id.fragment_museum_pager);
		DisplayMetrics dm = new DisplayMetrics();        
		this.getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);    	        
		LayoutParams params = new LayoutParams(dm.widthPixels, dm.heightPixels * 2 / 5);
		params.setMargins(20, 20, 20, 0);
		viewPager.setLayoutParams(params);                
		
		//给textView 尾部添加imageView. 但可能不美观，考虑其他方式
		tvIntroduction = (TextView)rootView.findViewById(R.id.fragment_museum_tv_introduction);
		Bitmap bitmap= BitmapFactory.decodeResource(this.getActivity().getResources(), R.drawable.footer_normal_zuijin);  
		ImageSpan imgSpan = new ImageSpan(this.getActivity(),bitmap);  
        SpannableString spanString = new SpannableString("icon");
        spanString.setSpan(imgSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 
        tvIntroduction.append(spanString);
		
        //get listview
        lvExhibit = (ListView)rootView.findViewById(R.id.fragment_museum_lv_exhibit);
        lvExhibit.setAdapter(exhibitAdapter);
        setListViewHeightBasedOnChildren(lvExhibit);
		
        header = (HeaderLayout)rootView.findViewById(R.id.fragment_museum_header);
        header.setTitle("北京博物馆");
		
		viewPager.setAdapter(pagerAdapter);
		viewPager.setOnPageChangeListener(new OnPageChangeListener(	) {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
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
				ImageView image = images.get(position );
				((ViewGroup) container).addView(image,0);
				return image;
				
			}
			
			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager)container).removeView(images.get(position ));
			}
		};
	}

	/**
	 * 解决listView嵌入scrollView中不能正常显示和滚动的问题
	 * 根据listView的子项计算listView的高度，将listView展开
	 * fixme 使用动态加载/数据量较多时会使页面过大 ，listView 采用 pullToRefreshListView 替换 
	 * @param listView
	 */
	private void setListViewHeightBasedOnChildren(ListView listView){
		ListAdapter listAdapter = listView.getAdapter();
		if(listAdapter == null){
			return ;
		}
		int totalHeight = 0;
		for(int i= 0,len = listAdapter.getCount();i<len;i++){
			View listItem = listAdapter.getView(i,null,listView);
			listItem.measure(0,0);
			totalHeight += listItem.getMeasuredHeight();
		}
		
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight +(listView.getDividerHeight()*(listAdapter.getCount()-1));
		listView.setLayoutParams(params);
	}
	
}
