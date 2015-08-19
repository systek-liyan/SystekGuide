package com.app.guide.ui;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.guide.R;
import com.app.guide.adapter.DownloadAdapter;
import com.app.guide.adapter.DownloadAdapter.OnDownloadCompleteListener;
import com.app.guide.adapter.DownloadAdapter.OnItemDeleteListener;
import com.app.guide.beanhelper.GetBeanCallBack;
import com.app.guide.beanhelper.GetBeanHelper;
import com.app.guide.download.DownloadBean;
import com.app.guide.ui.DownloadListFragment.ExListViewAdapter.ChildViewHolder;
import com.app.guide.ui.DownloadListFragment.OnDownloadListener;

/**
 * 下载管理，目前显示正在下载和已经下载完成的博物馆
 * @author Administrator
 *
 */
public class DownloadManageFragment extends Fragment implements
		OnItemDeleteListener, OnDownloadListener ,OnDownloadCompleteListener{

	private static String TAG;
	private DownloadAdapter mDownloadingAdapter;

	private DownloadAdapter mDownloadedAdapter;

	private List<DownloadBean> downloadingList = new ArrayList<DownloadBean>();

	private List<DownloadBean> downloadCompletedList = new ArrayList<DownloadBean>();

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
		super.onCreate(savedInstanceState);
		TAG = this.getClass().getSimpleName();
		
		//DownloadListFragment.setDownloadListener(this);
		initData();
	}

	private void initData() {
		// TODO Auto-generated method stub
		GetBeanHelper.getInstance(getActivity()).getDownloadBeanList(
				new GetBeanCallBack<List<DownloadBean>>() {

					@Override
					public void onGetBeanResponse(List<DownloadBean> response) {
						downloadingList = response;
						// TODO == null
					}
				});

		GetBeanHelper.getInstance(getActivity()).getDownloadCompletedBeans(
				new GetBeanCallBack<List<DownloadBean>>() {

					@Override
					public void onGetBeanResponse(List<DownloadBean> response) {
						downloadCompletedList = response;

					}
				});
		mDownloadingAdapter = new DownloadAdapter(getActivity(),
				downloadingList, R.layout.item_download);
		mDownloadedAdapter = new DownloadAdapter(getActivity(),
				downloadCompletedList, R.layout.item_download);
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
		lvDownloading.setAdapter(mDownloadingAdapter);
		lvDownloadComplete.setAdapter(mDownloadedAdapter);
		updateTvVisibility();
		return view;
	}

	private void updateTvVisibility() {
		if (mDownloadedAdapter.getCount() == 0)
			tvDownloaded.setVisibility(View.GONE);
		else
			tvDownloaded.setVisibility(View.VISIBLE);

		if (mDownloadingAdapter.getCount() == 0)
			tvDownloading.setVisibility(View.GONE);
		else
			tvDownloading.setVisibility(View.VISIBLE);

		if (mDownloadingAdapter.getCount() + mDownloadedAdapter.getCount() == 0) {
			tvNoItems.setVisibility(View.VISIBLE);
		} else {
			tvNoItems.setVisibility(View.GONE);
		}
	}

	@Override
	public void onUpdate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onItemDeleted(DownloadBean bean) {
		// TODO Auto-generated method stub
		updateTvVisibility();
	}

	@Override
	public void onDownloadComplete(DownloadBean bean) {
		// TODO Auto-generated method stub
		downloadCompletedList.add(bean);
		mDownloadedAdapter.notifyDataSetChanged();
	}

	@Override
	public void onDownload(DownloadBean downloadBean, ChildViewHolder holder) {
		// TODO Auto-generated method stub
		Toast.makeText(getActivity(), "开始下载", Toast.LENGTH_SHORT).show();
		downloadingList.add(downloadBean);
		mDownloadingAdapter.notifyDataSetChanged();
//		mDownloadingAdapter.startDownload(downloadBean.getMuseumId());
		updateTvVisibility();
	}

}
