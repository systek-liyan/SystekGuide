package com.app.guide.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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
import com.app.guide.service.AppService;
import com.app.guide.ui.DownloadListFragment.ExListViewAdapter.ChildViewHolder;

/**
 * TODO 添加点击 和 已完成 ， 布局
 * 城市列表，显示各个城市中可以下载的博物馆，供选择下载
 * @author yetwish
 * 
 */
public class DownloadListFragment extends Fragment {
	
	private static String TAG;

	/** 可扩展ListView */
	private ExpandableListView mListView;
	
	/**
	 * 已经下载的
	 */
	private List<String> hasDownloaded = new ArrayList<String>();
	
	/**
	 * 城市、博物馆列表
	 */
	private List<DownloadBean> downloadList;

	/** 资源下载状态 
	 * 存放规则: groupPosition(外层城市)+""+childPosition(内层博物馆) -> state
	 **/
	//private HashMap<String, Integer> stateMap = new HashMap<String, Integer>();

	//private static OnDownloadBeginListener downloadListener;

	private OnToggleListener mToggleListener;

	public void setToggleListener(OnToggleListener listener) {
		mToggleListener = listener;
	}

//	public static void setDownloadListener(OnDownloadBeginListener listener) {
//		downloadListener = listener;
//	}

	// private BaseExpandableListAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = this.getClass().getSimpleName();
		initData();
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		// TODO 下载完成的不在该页面显示
//		/** 在本地数据库中获取所有已下载完成的bean */
//		GetBeanHelper.getInstance(getActivity()).getDownloadCompletedBeans(
//				new GetBeanCallBack<List<DownloadBean>>() {
//					@Override
//					public void onGetBeanResponse(List<DownloadBean> response) {
//						for(DownloadBean bean:response) {
//							hasDownloaded.add(bean.getMuseumId());
//						}
//					}
//				});

		GetBeanHelper.getInstance(getActivity()).getDownloadBeanList(
				new GetBeanCallBack<List<DownloadBean>>() {

					@Override
					public void onGetBeanResponse(List<DownloadBean> response) {
						if (response == null || response.size() == 0) {
							Log.d(TAG,"getDownloadBeanList(),没有数据返回!!!");
							return;
						}
						downloadList = response;
						String city="",lastcity="";
						ExListViewData vd = new ExListViewData();
						for (DownloadBean bean : downloadList) {
							Log.w(TAG,bean.getCity() + ","	+ bean.getName()+","+bean.getTotal());
							city = bean.getCity();
							// 新的城市或第一次循环
							if (!lastcity.equals(city) || lastcity.equals("")) {
								// 非第一次循环，把上一循环的vd添加到exListViewData中, 初始化下一次的vd
								// 第一次循环的vd在for外初始化
								if (!lastcity.equals("")) {
									exListViewData.add(vd);
									vd = new ExListViewData();
								}
								lastcity = city;
								vd.city = city;
								vd.museumList = new ArrayList<DownloadBean>();
								vd.museumList.add(bean);
							}
							else {
								vd.museumList.add(bean);
							}
						}
						// 添加最后一个城市的数据
						if (!lastcity.equals("")) {
							exListViewData.add(vd);
						}
						// 验证
						for(ExListViewData vd1 : exListViewData) {
							Log.w(TAG,"验证 "+vd1.city);
							for (DownloadBean bean : vd1.museumList) Log.w(TAG,bean.getName());
						}
							
						if (mAdapter != null)
							mAdapter.notifyDataSetChanged();
						
						// TODO == null
					}
				});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.frag_download_list, null);
		mListView = (ExpandableListView) view.findViewById(R.id.lv_download_list);
		mListView.setAdapter(mAdapter);
		return view;
	}
	
	class ExListViewData {
		/** 外层(组,城市)*/
		String city; 
		/** 内层(子层,博物馆列表)*/
		List<DownloadBean> museumList; 
	}
	List<ExListViewData> exListViewData = new ArrayList<ExListViewData>();  
	
	public interface OnDownloadListener {

		void onDownload(DownloadBean downloadBean,ChildViewHolder holder);

		void onUpdate();
	}

	public interface OnToggleListener {
		void onToggle();
	}

	////////////////////////////////////////////////////////
	/** 可扩展ListViewAdapter */
	private ExListViewAdapter mAdapter = new ExListViewAdapter(getActivity(),
			exListViewData, new OnDownloadListener() {
				@Override
				public void onDownload(DownloadBean downloadBean,final ChildViewHolder holder) {
					String museumId = downloadBean.getMuseumId();
					DownloadClient client = AppService.getDownloadClient(
							getActivity(), museumId);
					holder.tvState.setText("准备中");
					// stateMap.put(groupPosition + "" +
					// childPosition,STATE_DOWNLOADING);
					try {
						client.start(); // 开始下载
					} catch (Exception e) {
					}
					client.setOnProgressListener(new OnProgressListener() {
						
						@Override
						public void onSuccess() {
							 holder.tvState.setText("已下载");
							 mAdapter.notifyDataSetChanged();
							 
							 holder.ivStart.setEnabled(false);
						}

						@Override
						public void onStart() {
							 holder.tvState.setText("正在下载...");
							 mAdapter.notifyDataSetChanged();
						}

						@Override
						public void onProgress(long total, long current) {
							 double d = current*100.0/total;
							 holder.tvState.setText(String.format("%.2f", d)+"%");
							 mAdapter.notifyDataSetChanged();
						}

						@Override
						public void onFailed(String url, String msg) {
							 holder.tvState.setText("下载失败！");
							 mAdapter.notifyDataSetChanged();
						}
					});

				}

				@Override
				public void onUpdate() {
					// TODO Auto-generated method stub

				}

			});
	
	/** 可扩展ListViewAdapter类 */
	public class ExListViewAdapter extends BaseExpandableListAdapter {
		//private Context mContext;
        private List<ExListViewData> exListViewData;
        private OnDownloadListener mOnDownload;
        //private LayoutInflater inflater;
        
		public ExListViewAdapter(Context context, List<ExListViewData> exListViewData,OnDownloadListener downloadListener) {
			//inflater = ((Activity)context).getLayoutInflater();
			//mContext = context;
			this.exListViewData = exListViewData;
			mOnDownload = downloadListener;
		}

		/** 外层(组,城市)数量*/
		@Override
		public int getGroupCount() {
			return exListViewData.size();
		}

		/** 内层(子层,博物馆列表)列表元素数量 */
		@Override
		public int getChildrenCount(int groupPosition) {
			return exListViewData.get(groupPosition).museumList.size();
		}

		/** 获取外层(组,城市)对象 --城市 */
		@Override
		public String getGroup(int groupPosition) {
			return exListViewData.get(groupPosition).city;
		}

		/** 内层(子层,博物馆列表)列表对象 -- DownloadBean */
		@Override
		public DownloadBean getChild(int groupPosition, int childPosition) {
			return exListViewData.get(groupPosition).museumList.get(childPosition);
		}

		/** 获取外层(组,城市)Id */
		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		/** 内层(子层,博物馆列表)Id */
		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		/** 相同id，可能得到不同的对象，如不同group中的1号child代表不同的对象 */
		@Override
		public boolean hasStableIds() {
			return false; // 相同id,不是(false)相同对象
		}

		/** 外层--城市 View显示内容*/
		@SuppressLint("InflateParams")
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			GroupViewHolder holder = null;
			/** 外层--城市*/
			if (convertView == null) {
				//convertView = LayoutInflater.from(mContext).inflate(R.layout.item_download_group, null);
			    convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_download_group, null);
				holder = new GroupViewHolder();
				holder.tvName = (TextView) convertView
						.findViewById(R.id.tv_download_group_name);
				holder.ivIndicator = (ImageView) convertView
						.findViewById(R.id.iv_download_group_icon);
				convertView.setTag(holder);
			} else {
				holder = (GroupViewHolder) convertView.getTag();
			}
			/** 外层显示城市名 */
			holder.tvName.setText(getGroup(groupPosition));
			if (isExpanded) {
				holder.ivIndicator.setImageResource(R.drawable.arrow_press);
			} else {
				holder.ivIndicator.setImageResource(R.drawable.arrow);
			}
			return convertView;
		}

		/** 内层(子层,博物馆列表) View显示内容 */
		@Override
		public View getChildView(final int groupPosition, final int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			final ChildViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.item_download_child, null);
				holder = new ChildViewHolder();
				/** 博物馆名称 */
				holder.tvName = (TextView) convertView
						.findViewById(R.id.tv_download_child_name);
				/** 下载资源文件大小 */
				holder.tvSize = (TextView) convertView
						.findViewById(R.id.tv_download_child_size);
				/** 开始(下载)  **/
				holder.ivStart = (ImageView) convertView
						.findViewById(R.id.iv_download_child_icon);
				/** 状态 **/
				holder.tvState = (TextView) convertView
						.findViewById(R.id.tv_download_child_state);
				convertView.setTag(holder);
			} else {
				holder = (ChildViewHolder) convertView.getTag();
			}
			// 博物馆名称
			holder.tvName.setText(getChild(groupPosition, childPosition).getName());
			// 资源文件大小
			double size = getChild(groupPosition, childPosition).getTotal()/1024F/1024F;
			holder.tvSize.setText(String.format("%.2fM",size));
			
			// TODO 状态更新，stateMap意义
			// 确定状态 将状态保存在stateMap中，
            // 存放规则: groupPosition(外层城市)+""+childPosition(内层博物馆) -> state
			// 已经下载完的，应该不在此界面出现
			if (hasDownloaded.contains(getChild(groupPosition, childPosition).getMuseumId())) {
				//stateMap.put(groupPosition + "" + childPosition,STATE_DOWNLOADED);
				holder.ivStart.setImageResource(R.drawable.play_btn_pause);
				// TODO update Donwloading 状态 更换图片
				// if(...)
			} else {
				//stateMap.put(groupPosition + "" + childPosition, STATE_DOWNLOADING);
				holder.ivStart.setImageResource(R.drawable.play_btn_play);
			}
			
			
			/** 开始(下载)*/
			holder.ivStart.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					/** 获取当前选中子层博物馆id */
					//final String museumId = getChild(groupPosition, childPosition).getMuseumId();
					/** 获取当前选中的子层博物馆DownloadBean对象*/
					DownloadBean downloadBean = getChild(groupPosition, childPosition);
					/** 下载吧 */
					// TODO 根据状态，下载、暂停、恢复
					mOnDownload.onDownload(downloadBean,holder);
					
					//int state = stateMap.get(groupPosition + "" + childPosition);
					//Log.w(TAG, state + "," + groupPosition + "" + childPosition);
					((ImageView) v).setImageResource(R.drawable.play_btn_pause);
				}
			});
			
			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
		
		/** 外层 */
		class GroupViewHolder {
			/** 城市 */
			TextView tvName;
			/** 展开 */
			ImageView ivIndicator;
		}

		/** 子层  */
		class ChildViewHolder {
			/** 博物馆 */
			TextView tvName;
			/** 资源文件大小 */
			TextView tvSize;
			/** 状态  */
			TextView tvState;
			/** 开始  */
			ImageView ivStart;
		}
	}
}


///** 可扩展视图Adapter*/
//private BaseExpandableListAdapter mAdapter = new BaseExpandableListAdapter() {
//
//	private static final int STATE_DOWNLOAD = 1;
//	private static final int STATE_DOWNLOADING = 2;
//	private static final int STATE_DOWNLOADED = 3;
//	private static final int STATE_UPDATE = 4;
//
//	@Override
//	public boolean isChildSelectable(int groupPosition, int childPosition) {
//		// TODO Auto-generated method stub
//		return true;
//	}
//
//	@Override
//	public boolean hasStableIds() {
//		// TODO Auto-generated method stub
//		return true;
//	}
//
//	@SuppressLint("InflateParams")
//	@Override
//	public View getGroupView(int groupPosition, boolean isExpanded,
//			View convertView, ViewGroup parent) {
//		GroupViewHolder holder = null;
//		/** 外层--城市*/
//		if (convertView == null) {
//			convertView = LayoutInflater.from(getActivity()).inflate(
//					R.layout.item_download_group, null);
//			holder = new GroupViewHolder();
//			holder.tvName = (TextView) convertView
//					.findViewById(R.id.tv_download_group_name);
//			holder.ivIndicator = (ImageView) convertView
//					.findViewById(R.id.iv_download_group_icon);
//			convertView.setTag(holder);
//		} else {
//			holder = (GroupViewHolder) convertView.getTag();
//		}
//		/** 外层显示城市名 */
//		holder.tvName.setText(getGroup(groupPosition).getCity());
//		if (isExpanded) {
//			holder.ivIndicator.setImageResource(R.drawable.arrow_press);
//		} else {
//			holder.ivIndicator.setImageResource(R.drawable.arrow);
//		}
//		return convertView;
//	}
//
//	@Override
//	public long getGroupId(int groupPosition) {
//		// TODO Auto-generated method stub
//		return groupPosition;
//	}
//
//	@Override
//	public int getGroupCount() {
//		// TODO Auto-generated method stub
//		return downloadList.size();
//	}
//
//	@Override
//	public DownloadBean getGroup(int groupPosition) {
//		return downloadList.get(groupPosition);
//	}
//
////	@Override
////	public int getChildrenCount(int groupPosition) {
////		// TODO Auto-generated method stub
////		return getGroup(groupPosition).getMuseumList().size();
////	}
//
//	/** 内层（子层）--博物馆*/
//	@Override
//	public View getChildView(final int groupPosition,
//			final int childPosition, boolean isLastChild, View convertView,
//			ViewGroup parent) {
//		final ChildViewHolder holder;
//		if (convertView == null) {
//			convertView = LayoutInflater.from(getActivity()).inflate(
//					R.layout.item_download_child, null);
//			holder = new ChildViewHolder();
//			holder.tvName = (TextView) convertView
//					.findViewById(R.id.tv_download_child_name);
//			holder.tvSize = (TextView) convertView
//					.findViewById(R.id.tv_download_child_size);
//			holder.ivStart = (ImageView) convertView
//					.findViewById(R.id.iv_download_child_icon);
//			holder.tvState = (TextView) convertView
//					.findViewById(R.id.tv_download_child_state);
//			convertView.setTag(holder);
//		} else {
//			holder = (ChildViewHolder) convertView.getTag();
//		}
//		holder.tvName.setText(getChild(groupPosition, childPosition)
//				.getName());
//		holder.tvSize.setText(getChild(groupPosition, childPosition)
//				.getTotal() + "");
//		// 确定状态 将状态保存在stateMap中，
//        // 存放规则: groupPosition(外层城市)+""+childPosition(内层博物馆) -> state
//		if (hasDownloaded.contains(getChild(groupPosition, childPosition).getMuseumId())) {
//			stateMap.put(groupPosition + "" + childPosition,STATE_DOWNLOADED);
//			holder.ivStart.setImageResource(R.drawable.play_btn_pause);
//			// TODO update Donwloading 状态 更换图片
//			// if(...)
//		} else {
//			stateMap.put(groupPosition + "" + childPosition, STATE_DOWNLOADING);
//			holder.ivStart.setImageResource(R.drawable.play_btn_play);
//		}
//		
//		/** 获取当前选中子层博物馆id */
//		final String museumId = getChild(groupPosition, childPosition).getMuseumId();
//		
//		/** 开始(下载)*/
//		holder.ivStart.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				int state = stateMap.get(groupPosition + "" + childPosition);
//				Log.w(TAG, state + "," + groupPosition + "" + childPosition);
//				((ImageView) v).setImageResource(R.drawable.play_btn_pause);
//				if (state == STATE_DOWNLOAD) {
//					DownloadClient client = AppService.getDownloadClient(getActivity(), museumId);
//					holder.tvState.setText("准备中");
//					stateMap.put(groupPosition + "" + childPosition,STATE_DOWNLOADING);
//					try {
//						client.start();  // 开始下载
//					} catch (Exception e) {
//					}
//					client.setOnProgressListener(new OnProgressListener() {
//
//						@Override
//						public void onSuccess() {
//							holder.tvState.setText("已下载");
//						}
//
//						@Override
//						public void onStart() {
//							holder.tvState.setText("正在下载");
//						}
//
//						@Override
//						public void onProgress(long total, long current) {
//						}
//
//						@Override
//						public void onFailed(String url, String msg) {
//						}
//					});
//					if (downloadListener != null) {
//						// TODO
//						hasDownloaded.add(getChild(groupPosition,childPosition).getMuseumId());
//						downloadListener.onDownload(getChild(groupPosition,childPosition));
//					}
//				} else if (state == STATE_DOWNLOADED
//						|| state == STATE_DOWNLOADING) {
//					if (mToggleListener != null) {
//						mToggleListener.onToggle();
//					}
//				} else {
//					// TODO update
//				}
//			}
//		});
//
//		return convertView;
//	}
//
//	@Override
//	public long getChildId(int groupPosition, int childPosition) {
//		// TODO Auto-generated method stub
//		return childPosition;
//	}
//
//	@Override
//	public DownloadBean getChild(int groupPosition, int childPosition) {
//		// TODO Auto-generated method stub
//		return getGroup(groupPosition).getMuseumList().get(childPosition);
//	}
//
//	/** 外层 */
//	class GroupViewHolder {
//		/** 城市 */
//		TextView tvName;
//		/** 展开 */
//		ImageView ivIndicator;
//	}
//
//	/** 子层  */
//	class ChildViewHolder {
//		/** 博物馆 */
//		TextView tvName;
//		/** 资源文件大小 */
//		TextView tvSize;
//		/** 状态  */
//		TextView tvState;
//		/** 开始  */
//		ImageView ivStart;
//	}
//};
//
//// private OnClickListener mClickListener =
//
//public interface OnDownloadBeginListener {
//
//	void onDownload(DownloadBean downloadBean);
//
//	void onUpdate();
//}
//
//public interface OnToggleListener {
//	void onToggle();
//}
//}
