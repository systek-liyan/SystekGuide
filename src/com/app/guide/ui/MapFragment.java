package com.app.guide.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.guide.R;

/**
 * 地图fragment
 * @author yetwish
 * @date 2015-4-26
 */
public class MapFragment extends Fragment{

	private View rootView;
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
