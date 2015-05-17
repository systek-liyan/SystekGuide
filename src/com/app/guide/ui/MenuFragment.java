package com.app.guide.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.app.guide.R;
import com.app.guide.adapter.CommonAdapter;
import com.app.guide.adapter.ViewHolder;
import com.app.guide.bean.Menu;

public class MenuFragment extends Fragment {

	private HomeClick homeClick;
	private View rootView;
	private ListView lvMenu;
	private List<Menu> mData;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		initData();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.frag_menu, null);
			
		}
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if (parent != null) {
			parent.removeView(rootView);
		}
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initViews();
	}
	
	private void initData(){
		String[] titles = { "城市选择", "下载中心", "设置", "更多", "返回" };// 返回是否需要
		int[] iconResources = { R.drawable.sliding_menu_city,
				R.drawable.sliding_menu_download, R.drawable.sliding_menu_setting,
				R.drawable.sliding_menu_more, R.drawable.sliding_menu_back };
		mData = new ArrayList<Menu>();
		for(int i =0 ; i < titles.length;i++)
			mData.add(new Menu(iconResources[i],titles[i]));
	}
	
	private void initViews() {
		if(rootView== null) return ;
		lvMenu = (ListView)rootView.findViewById(R.id.frag_menu_lv_menu);
		lvMenu.setAdapter(new CommonAdapter<Menu>(this.getActivity(),mData,R.layout.item_menu) {
			@Override
			public void convert(ViewHolder holder, int position) {
				TextView textView = (TextView)holder.getView(R.id.item_menu_tv_title);
		        textView.setText(mData.get(position).getTitle());
		        Drawable drawable = getActivity().getResources().getDrawable(mData.get(position).getIconResId());
		        drawable.setBounds(0,0,drawable.getMinimumWidth(), drawable.getMinimumHeight());
		        textView.setCompoundDrawables(drawable,null,null,null);
			}
		
		});
		lvMenu.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
			}
			
		});
	}

	public void setHomeClick(HomeClick homeClick) {
		this.homeClick = homeClick;
	}

	
	public interface HomeClick {
		public void home();
	}

}
