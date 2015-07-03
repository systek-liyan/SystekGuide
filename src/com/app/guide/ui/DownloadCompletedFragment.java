package com.app.guide.ui;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.app.guide.adapter.DownloadCompletedAdapter;
import com.app.guide.download.DownloadBean;
import com.app.guide.download.DownloadInfo;
import com.app.guide.offline.GetBeanFromSql;

public class DownloadCompletedFragment extends Fragment {

	private ListView mListView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mListView = new ListView(getActivity());
		return mListView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		List<DownloadBean> data = new ArrayList<DownloadBean>();
		try {
			data = GetBeanFromSql.getDownloadCompletedBeans(getActivity());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DownloadCompletedAdapter adapter = new DownloadCompletedAdapter(
				getActivity(), data);
		mListView.setAdapter(adapter);
	}

}
