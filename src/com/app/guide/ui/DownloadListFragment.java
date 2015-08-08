package com.app.guide.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.guide.R;
import com.app.guide.beanhelper.GetBeanCallBack;
import com.app.guide.beanhelper.GetBeanHelper;
import com.app.guide.download.DownloadBean;
import com.app.guide.download.DownloadClient;
import com.app.guide.download.DownloadClient.OnProgressListener;
import com.app.guide.download.DownloadModel;
import com.app.guide.service.AppService;

/**
 * TODO 添加点击 和 已完成 ， 布局
 * 
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

	private List<String> hasDownload;

	/**
	 * 可以下载的列表
	 */
	private List<DownloadModel> downloadList;

	private HashMap<String, Integer> stateMap = new HashMap<String, Integer>();

	private static OnDownloadBeginListener downloadListener;

	private OnToggleListener mToggleListener;

	public void setToggleListener(OnToggleListener listener) {
		mToggleListener = listener;
	}

	public static void setDownloadListener(OnDownloadBeginListener listener) {
		downloadListener = listener;
	}

	// private BaseExpandableListAdapter mAdapter;

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
		downloadList = new ArrayList<DownloadModel>();
		GetBeanHelper.getInstance(getActivity()).getDownloadCompletedBeans(
				new GetBeanCallBack<List<DownloadBean>>() {

					@Override
					public void onGetBeanResponse(List<DownloadBean> response) {
						hasDownloaded = response;
					}
				});

		GetBeanHelper.getInstance(getActivity()).getDownloadList(
				new GetBeanCallBack<List<DownloadModel>>() {

					@Override
					public void onGetBeanResponse(List<DownloadModel> response) {
						downloadList = response;
						if (mAdapter != null)
							mAdapter.notifyDataSetChanged();
						for (DownloadModel model : downloadList) {
							Log.w("TAG",
									model.getCity() + ","
											+ model.getMuseumsUrl());
							for(DownloadBean bean: model.getMuseumList()){
								Log.w("TAG",
										bean.getName()+ ","
												+ bean.getMuseumId()+","+bean.getTotal());
							}
						}
						// todo == null
					}
				});
		//
		// DownloadModel model = new DownloadModel();
		// model.setCity("北京市");
		// model.setMuseumsUrl("fb468fcd9a894dbf8108f9b8bbc88109*首都博物馆*29886152,cd02abd9a00344d88cf78eaf291f7a48*故宫博物馆*141451");
		//
		// downloadList.add(model);
		// model = new DownloadModel();
		// model.setCity("南京市");
		// model.setMuseumsUrl("1231412123123*苏州博物馆*123112");
		// downloadList.add(model);
		//
		 hasDownloaded = new ArrayList<DownloadBean>();
		
		 hasDownload = new ArrayList<String>();
		 for (DownloadBean bean : hasDownloaded) {
		 hasDownload.add(bean.getMuseumId());
		 }

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.frag_download_list, null);
		mListView = (ExpandableListView) view
				.findViewById(R.id.lv_download_list);
		mListView.setAdapter(mAdapter);
		return view;
	}

	private BaseExpandableListAdapter mAdapter = new BaseExpandableListAdapter() {

		private static final int STATE_DOWNLOAD = 1;
		private static final int STATE_DOWNLOADING = 2;
		private static final int STATE_DOWNLOADED = 3;
		private static final int STATE_UPGRATE = 4;

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return true;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			GroupViewHolder holder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.item_download_group, null);
				holder = new GroupViewHolder();
				holder.tvName = (TextView) convertView
						.findViewById(R.id.tv_download_group_name);
				holder.ivIndicator = (ImageView) convertView
						.findViewById(R.id.iv_download_group_icon);
				convertView.setTag(holder);
			} else {
				holder = (GroupViewHolder) convertView.getTag();
			}
			holder.tvName.setText(getGroup(groupPosition).getCity());
			if (isExpanded) {
				holder.ivIndicator.setImageResource(R.drawable.arrow_press);
			} else {
				holder.ivIndicator.setImageResource(R.drawable.arrow);
			}
			return convertView;
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
		public View getChildView(final int groupPosition,
				final int childPosition, boolean isLastChild, View convertView,
				ViewGroup parent) {
			final ChildViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.item_download_child, null);
				holder = new ChildViewHolder();
				holder.tvName = (TextView) convertView
						.findViewById(R.id.tv_download_child_name);
				holder.tvSize = (TextView) convertView
						.findViewById(R.id.tv_download_child_size);
				holder.ivStart = (ImageView) convertView
						.findViewById(R.id.iv_download_child_icon);
				holder.tvState = (TextView) convertView
						.findViewById(R.id.tv_download_child_state);
				convertView.setTag(holder);
			} else {
				holder = (ChildViewHolder) convertView.getTag();
			}
			holder.tvName.setText(getChild(groupPosition, childPosition)
					.getName());
			holder.tvSize.setText(getChild(groupPosition, childPosition)
					.getTotal() + "");
			// 确定状态 将状态保存在stateMap中，存放规则: groupPosition+""+"childPosition" ->
			// state
			if (hasDownload.contains(getChild(groupPosition, childPosition)
					.getMuseumId())) {
				stateMap.put(groupPosition + "" + childPosition,
						STATE_DOWNLOADED);
				holder.ivStart.setImageResource(R.drawable.play_btn_pause);
				// TODO upgrate Donwloading 状态 更换图片
				// if(...)
			} else {
				stateMap.put(groupPosition + "" + childPosition, STATE_DOWNLOAD);
				holder.ivStart.setImageResource(R.drawable.play_btn_play);
			}
			final String museumId = getChild(groupPosition, childPosition)
					.getMuseumId();
			holder.ivStart.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					int state = stateMap
							.get(groupPosition + "" + childPosition);
					Log.w("TAG", state + "," + groupPosition + ""
							+ childPosition);
					((ImageView) v).setImageResource(R.drawable.play_btn_pause);
					if (state == STATE_DOWNLOAD) {
						DownloadClient client = AppService.getDownloadClient(
								getActivity(), museumId);
						holder.tvState.setText("准备中");
						stateMap.put(groupPosition + "" + childPosition,
								STATE_DOWNLOADING);
						try {
							client.start();
						} catch (Exception e) {
						}
						client.setOnProgressListener(new OnProgressListener() {

							@Override
							public void onSuccess() {
								holder.tvState.setText("已下载");
							}

							@Override
							public void onStart() {
								holder.tvState.setText("正在下载");
							}

							@Override
							public void onProgress(long total, long current) {
							}

							@Override
							public void onFailed(String url, String msg) {
							}
						});
						if (downloadListener != null) {
							// TODO
							hasDownload.add(getChild(groupPosition,
									childPosition).getMuseumId());
							downloadListener.onDownload(getChild(groupPosition,
									childPosition));
						}
					} else if (state == STATE_DOWNLOADED
							|| state == STATE_DOWNLOADING) {
						if (mToggleListener != null) {
							mToggleListener.onToggle();
						}
					} else {
						// TODO upgrate
					}
				}
			});

			return convertView;
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

		class GroupViewHolder {
			TextView tvName;
			ImageView ivIndicator;
		}

		class ChildViewHolder {
			TextView tvName;
			TextView tvSize;
			TextView tvState;
			ImageView ivStart;
		}
	};

	// private OnClickListener mClickListener =

	public interface OnDownloadBeginListener {

		void onDownload(DownloadBean downloadBean);

		void onUpgrate();
	}

	public interface OnToggleListener {
		void onToggle();
	}
}
