package com.app.guide.ui;

import java.util.ArrayList;
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
import com.app.guide.adapter.DownloadAdapter.OnDownloadCompleteListener;
import com.app.guide.adapter.DownloadAdapter.OnItemDeleteListener;
import com.app.guide.beanhelper.GetBeanCallBack;
import com.app.guide.beanhelper.GetBeanHelper;
import com.app.guide.download.DownloadBean;
import com.app.guide.ui.DownloadListFragment.ExListViewAdapter.ChildViewHolder;
import com.app.guide.ui.DownloadListFragment.OnDownloadListener;

/**
 * 下载管理，显示已经下载完成的博物馆，供更新删除使用
 * 数据来源：
 * （1）从本地数据库Download.db中的downloadBean表中获取已经下载好的博物馆列表，
 * 此部分由调用GetBeanHelper的getDownloadCompletedBeans()完成
 * （2）从服务器获得已经下载好的博物馆是否有更新，基本上是按照offlineDownloadHelper的做法，只是加一个上一次下载更新资源的时间，在downloadBean中获得
 * 下载博物馆的各个Bean，更新数据库，资源文件列表形成队列，通过DownloadClient下载，
 * 具体流程与DownloadListFragment相同
 */
public class DownloadManageFragment extends Fragment implements
		OnItemDeleteListener, OnDownloadListener ,OnDownloadCompleteListener{

	private static String TAG;
	private DownloadAdapter mUpdatingAdapter;

	private DownloadAdapter mDownloadedAdapter;

	private List<DownloadBean> updatingList = new ArrayList<DownloadBean>();

	private List<DownloadBean> downloadCompletedList = new ArrayList<DownloadBean>();

	/**
	 * 正在更新的listview
	 */
	private ListView lvUpdating;

	/**
	 * 已经下载完成的listview
	 */
	private ListView lvDownloadComplete;

	/**
	 * 正在下载标题栏 tv
	 */
	private TextView tvUpdating;

	/**
	 * 下载完成标题栏tv
	 */
	private TextView tvDownloaded;

	/**
	 * 当没有正在更新列表 也没有下载完成列表时显示
	 */
	private TextView tvNoItems;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = this.getClass().getSimpleName();
		
		// DownloadListFragment.setDownloadListener(this);
		initData();
	}

	private void initData() {
		// TODO 获取正在更新的
//		GetBeanHelper.getInstance(getActivity()).getDownloadCompletedBeans(
//				new GetBeanCallBack<List<DownloadBean>>() {
//
//					@Override
//					public void onGetBeanResponse(List<DownloadBean> response) {
//						updatingList = response;
//						// TODO == null
//					}
//				});

		GetBeanHelper.getInstance(getActivity()).getDownloadCompletedBeans(
				new GetBeanCallBack<List<DownloadBean>>() {
					@Override
					public void onGetBeanResponse(List<DownloadBean> response) {
						downloadCompletedList = response;
					}
				});
		
		mUpdatingAdapter = new DownloadAdapter(getActivity(),
				updatingList, R.layout.item_download);
		mDownloadedAdapter = new DownloadAdapter(getActivity(),
				downloadCompletedList, R.layout.item_download);
	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// 初始化View
		View view = inflater.inflate(R.layout.frag_download_manage, null);
		
		lvUpdating = (ListView) view.findViewById(R.id.lv_download_ing);
		lvDownloadComplete = (ListView) view.findViewById(R.id.lv_download_complete);
		tvUpdating = (TextView) view.findViewById(R.id.tv_download_ing);
		tvDownloaded = (TextView) view.findViewById(R.id.tv_download_complete);
		tvNoItems = (TextView) view.findViewById(R.id.tv_download_no_items);
		
		lvUpdating.setAdapter(mUpdatingAdapter);
		lvDownloadComplete.setAdapter(mDownloadedAdapter);
		updateTvVisibility();
		return view;
	}

	private void updateTvVisibility() {
		if (mDownloadedAdapter.getCount() == 0)
			tvDownloaded.setVisibility(View.GONE);
		else
			tvDownloaded.setVisibility(View.VISIBLE);

		if (mUpdatingAdapter.getCount() == 0)
			tvUpdating.setVisibility(View.GONE);
		else
			tvUpdating.setVisibility(View.VISIBLE);

		if (mUpdatingAdapter.getCount() + mDownloadedAdapter.getCount() == 0) {
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
//		// TODO Auto-generated method stub
//		Toast.makeText(getActivity(), "开始下载", Toast.LENGTH_SHORT).show();
//		updatingList.add(downloadBean);
//		mUpdatingAdapter.notifyDataSetChanged();
////		mUpdatingAdapter.startDownload(downloadBean.getMuseumId());
//		updateTvVisibility();
	}

}
