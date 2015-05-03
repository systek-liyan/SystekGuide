package com.app.guide.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.app.guide.R;

/**
 * 地图fragment
 * @author yetwish
 * @date 2015-4-26
 */
public class MapFragment extends Fragment{

	private View rootView;
	
	private LinearLayout headerLayout;
	private HomeActivity mActivity;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = (HomeActivity)activity;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(rootView == null){
			rootView = inflater.inflate(R.layout.frag_map, null);
		}
		ViewGroup parent = (ViewGroup)rootView.getParent();
		if(parent!=null){
			parent.removeView(rootView);
		}
		return rootView;
	}
	
	
}
