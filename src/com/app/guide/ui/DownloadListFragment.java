package com.app.guide.ui;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.app.guide.beanhelper.GetBeanCallBack;
import com.app.guide.beanhelper.GetBeanHelper;
import com.app.guide.download.DownloadBean;
import com.app.guide.download.DownloadModel;


/**
 * TODO 添加点击 和 已完成 ， 布局
 * @author yetwish
 *
 */
public class DownloadListFragment extends Fragment {

	// private ListView mListView;
	private ExpandableListView mListView;

	/**
	 * 已经下载的
	 */
	private List<DownloadBean> hasDownloaded;

	/**
	 * 可以下载的列表
	 */
	private List<DownloadModel> downloadList;
	
//	private BaseExpandableListAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initData();
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
//		GetBeanHelper.getInstance(getActivity()).getDownloadCompletedBeans(
//				new GetBeanCallBack<List<DownloadBean>>() {
//
//					@Override
//					public void onGetBeanResponse(List<DownloadBean> response) {
//						hasDownloaded = response;
//					}
//				});
//
//		GetBeanHelper.getInstance(getActivity()).getDownloadList(
//				new GetBeanCallBack<List<DownloadModel>>() {
//
//					@Override
//					public void onGetBeanResponse(List<DownloadModel> response) {
//						downloadList = response;
////						//todo == null
////						mAdapter= createExpandableAdapter();
////						mListView.setAdapter(mAdapter);
//					}
//				});
		downloadList = new ArrayList<DownloadModel>();
		DownloadModel model = new DownloadModel();
		model.setCity("北京市");
		model.setMuseumsUrl("1231412123*首都博物馆*10000,92131273911*故宫博物馆*1312312");
		
		downloadList.add(model);
		model = new DownloadModel();
		model.setCity("南京市");
		model.setMuseumsUrl("1231412123123*苏州博物馆*123112");
		downloadList.add(model);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mListView = new ExpandableListView(getActivity());
		mListView.setAdapter(mAdapter);
		return mListView;
	}

	private BaseExpandableListAdapter mAdapter = new BaseExpandableListAdapter() {

			@Override
			public boolean isChildSelectable(int groupPosition,
					int childPosition) {
				// TODO Auto-generated method stub
				return true;
			}

			@Override
			public boolean hasStableIds() {
				// TODO Auto-generated method stub
				return true;
			}

			@Override
			public View getGroupView(int groupPosition, boolean isExpanded,
					View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub
				TextView tv = new TextView(getActivity());
				tv.setText(getGroup(groupPosition).getCity());
				return tv;
			}

			@Override
			public long getGroupId(int groupPosition) {
				// TODO Auto-generated method stub
				return groupPosition;
			}

			@Override
			public int getGroupCount() {
				// TODO Auto-generated method stub
				return downloadList.size();
			}

			@Override
			public DownloadModel getGroup(int groupPosition) {
				// TODO Auto-generated method stub
				return downloadList.get(groupPosition);
			}

			@Override
			public int getChildrenCount(int groupPosition) {
				// TODO Auto-generated method stub
				return getGroup(groupPosition).getMuseumList().size();
			}

			@Override
			public View getChildView(int groupPosition, int childPosition,
					boolean isLastChild, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub
				TextView tv = new TextView(getActivity());
				tv.setText(getChild(groupPosition, childPosition).getName());
				return tv;
			}

			@Override
			public long getChildId(int groupPosition, int childPosition) {
				// TODO Auto-generated method stub
				return childPosition;
			}

			@Override
			public DownloadBean getChild(int groupPosition, int childPosition) {
				// TODO Auto-generated method stub
				return getGroup(groupPosition).getMuseumList().get(childPosition);
			}
	};

	public interface OnTaskAddedListener {

		void onTaskAdded(DownloadBean downloadBean);
	}

}
