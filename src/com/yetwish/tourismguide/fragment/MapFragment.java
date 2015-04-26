package com.yetwish.tourismguide.fragment;

import com.yetwish.tourismguide.R;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 地图fragment
 * @author yetwish
 * @date 2015-4-26
 */
public class MapFragment extends Fragment{

	private View rootView;
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if(rootView == null){
			rootView = inflater.inflate(R.layout.fragment_map, null);
		}
		ViewGroup parent = (ViewGroup)rootView.getParent();
		if(parent!=null){
			parent.removeView(rootView);
		}
		return rootView;
	}
}
