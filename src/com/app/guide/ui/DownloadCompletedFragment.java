package com.app.guide.ui;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.app.guide.R;
import com.app.guide.adapter.DownloadCompletedAdapter;
import com.app.guide.beanhelper.GetBeanCallBack;
import com.app.guide.beanhelper.GetBeanHelper;
import com.app.guide.download.DownloadBean;

public class DownloadCompletedFragment extends Fragment {

	private ListView mListView;
	private DownloadCompletedAdapter adapter;
	private List<DownloadBean> data;

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
		data = new ArrayList<DownloadBean>();
		GetBeanHelper.getInstance(getActivity()).getDownloadCompletedBeans(
				new GetBeanCallBack<List<DownloadBean>>() {
					@Override
					public void onGetBeanResponse(List<DownloadBean> response) {
						data = response;
						if (adapter != null)
							adapter.notifyDataSetChanged();

					}
				});
		adapter = new DownloadCompletedAdapter(getActivity(), data,
				R.layout.item_download_completed);
		mListView.setAdapter(adapter);
	}

}
