package com.app.guide.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.app.guide.R;
import com.app.guide.adapter.MenuAdapter;

public class MenuFragment extends Fragment {

	private HomeClick homeClick;
	private View rootView;
	private ListView lvMenu;
	
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
	
	private void initViews() {
		if(rootView== null) return ;
		lvMenu = (ListView)rootView.findViewById(R.id.frag_menu_lv_menu);
		lvMenu.setAdapter(new MenuAdapter(this.getActivity()));
		lvMenu.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				
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
