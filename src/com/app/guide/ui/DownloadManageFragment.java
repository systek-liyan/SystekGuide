package com.app.guide.ui;

import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.app.guide.R;
import com.app.guide.adapter.DownloadAdapter;
import com.app.guide.adapter.DownloadAdapter.OnItemDeletedListener;
import com.app.guide.beanhelper.GetBeanCallBack;
import com.app.guide.beanhelper.GetBeanHelper;
import com.app.guide.download.DownloadBean;

public class DownloadManageFragment extends Fragment implements
		OnItemDeletedListener {

	private static final String TAG = DownloadManageFragment.class
			.getSimpleName();

	private DownloadAdapter mDownloadingAdapter;

	private DownloadAdapter mDownloadedAdapter;
	// private Button addButton;
	private List<DownloadBean> downloadingList;

	private List<DownloadBean> downloadCompletedList;

	/**
	 * 正在下载的listview
	 */
	private ListView lvDownloading;

	/**
	 * 已经下载完成的listview
	 */
	private ListView lvDownloadComplete;

	/**
	 * 正在下载标题栏 tv
	 */
	private TextView tvDownloading;
	
	/**
	 * 下载完成标题栏tv
	 */
	private TextView tvDownloaded;

	/**
	 * 当没有正在下载列表 也没有完成列表时显示
	 */
	private TextView tvNoItems;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initData();
	}

	private void initData() {
		// TODO Auto-generated method stub
		GetBeanHelper.getInstance(getActivity()).getDownloadingBeans(
				new GetBeanCallBack<List<DownloadBean>>() {

					@Override
					public void onGetBeanResponse(List<DownloadBean> response) {
						downloadingList = response;
						mDownloadingAdapter = new DownloadAdapter(
								getActivity(), downloadingList,
								R.layout.item_downloading_lv);
					}
				});

		GetBeanHelper.getInstance(getActivity()).getDownloadCompletedBeans(
				new GetBeanCallBack<List<DownloadBean>>() {

					@Override
					public void onGetBeanResponse(List<DownloadBean> response) {
						downloadCompletedList = response;
						mDownloadedAdapter = new DownloadAdapter(getActivity(),
								downloadCompletedList,R.layout.item_downloading_lv);

					}
				});
	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// 初始化View
		View view = inflater.inflate(R.layout.frag_download_manage, null);
		lvDownloading = (ListView) view.findViewById(R.id.lv_download_ing);
		lvDownloadComplete = (ListView) view
				.findViewById(R.id.lv_download_complete);
		tvDownloading = (TextView) view.findViewById(R.id.tv_download_ing);
		tvDownloaded = (TextView) view.findViewById(R.id.tv_download_complete);
		tvNoItems = (TextView) view.findViewById(R.id.tv_download_no_items);

		return view;
	}

	/**
	 * 当有子项移除时调用该回调
	 */
	@Override
	public void onItemDeletedListener() {
		// 更新tvNoItems的可见性
		updateTvVisibility();

	}

	private void updateTvVisibility() {
		if(mDownloadedAdapter.getCount() == 0)
			tvDownloaded.setVisibility(View.GONE);
		else tvDownloaded.setVisibility(View.VISIBLE);
		
		if(mDownloadingAdapter.getCount() == 0)
			tvDownloading.setVisibility(View.GONE);
		else tvDownloading.setVisibility(View.VISIBLE);
		
		if (mDownloadingAdapter.getCount() + mDownloadedAdapter.getCount() == 0) {
			tvNoItems.setVisibility(View.VISIBLE);
		} else {
			tvNoItems.setVisibility(View.GONE);
		}
	}

	// @Override
	// public void onViewCreated(View view, Bundle savedInstanceState) {
	// // TODO Auto-generated method stub
	// super.onViewCreated(view, savedInstanceState);
	// data = new ArrayList<DownloadBean>();
	// GetBeanHelper.getInstance(getActivity()).getDownloadCompletedBeans(
	// new GetBeanCallBack<List<DownloadBean>>() {
	// @Override
	// public void onGetBeanResponse(List<DownloadBean> response) {
	// data = response;
	// if (adapter != null)
	// adapter.notifyDataSetChanged();
	//
	// }
	// });
	// adapter = new DownloadingAdapter(getActivity(), data,
	// R.layout.item_downloading);
	// downloadingLv.setAdapter(adapter);
	// addButton.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// // TODO Auto-generated method stub
	// String museumId = "fb468fcd9a894dbf8108f9b8bbc88109";
	// DownloadBean bean = new DownloadBean();
	// bean.setMuseumId(museumId);
	// bean.setName("首都博物馆");
	// adapter.add(bean);
	// }
	// });
	// }

}
