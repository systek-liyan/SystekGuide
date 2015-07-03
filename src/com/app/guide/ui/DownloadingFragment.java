package com.app.guide.ui;

import java.sql.SQLException;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.app.guide.R;
import com.app.guide.adapter.DownloadingAdapter;
import com.app.guide.download.DownloadBean;
import com.app.guide.offline.GetBeanFromSql;
import com.app.guide.sql.DownloadManagerHelper;

public class DownloadingFragment extends Fragment {

	private static final String TAG = DownloadingFragment.class.getSimpleName();

	private ListView downloadingLv;
	private DownloadingAdapter adapter;
	private Button addButton;

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.frag_downloading, null);
		downloadingLv = (ListView) view.findViewById(R.id.download_ing_list);
		addButton = (Button) view.findViewById(R.id.download_btn_add);
		Log.w(TAG, "onCreateView");
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		List<DownloadBean> data = null;
		try {
			data = GetBeanFromSql.getDownloadingBeans(getActivity());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		adapter = new DownloadingAdapter(getActivity(), data);
		downloadingLv.setAdapter(adapter);
		addButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String museumId = "fb468fcd9a894dbf8108f9b8bbc88109";
				DownloadBean bean = new DownloadBean();
				bean.setMuseumId(museumId);
				bean.setName("首都博物馆");
				adapter.add(bean);
			}
		});
	}

}
