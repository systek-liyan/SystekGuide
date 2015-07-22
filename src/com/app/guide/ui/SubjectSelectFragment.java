package com.app.guide.ui;

import java.util.ArrayList;
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
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import cn.pedant.SweetAlert.SweetAlertDialog;

import com.app.guide.AppContext;
import com.app.guide.Constant;
import com.app.guide.R;
import com.app.guide.adapter.ExhibitAdapter;
import com.app.guide.adapter.GridAdapter.GridItemClickListener;
import com.app.guide.bean.ExhibitBean;
import com.app.guide.beanhelper.GetBeanCallBack;
import com.app.guide.beanhelper.GetBeanHelper;
import com.app.guide.model.LabelModel;
import com.app.guide.widget.AutoLoadListView;
import com.app.guide.widget.AutoLoadListView.OnLoadListener;
import com.app.guide.widget.DialogManagerHelper;
import com.app.guide.widget.LabelView;

/**
 * 专题导游fragment.每有一组selector（筛选标签）就给listView添加一个头部 (selectorView)
 * 主要用到动态加载gridView,以及设置listView悬浮头部，和通过回调接口 传递信息
 * 
 * 
 * 修改为上拉加载更多以及数据库访问方式
 * 
 * @author yetwish
 * @date 2015-4-21
 */
public class SubjectSelectFragment extends Fragment {

	private static final String TAG = SubjectSelectFragment.class
			.getSimpleName();

	/**
	 * 存储标签视图的数据,修改为数据库加载方式
	 */
	private List<LabelModel> labelBeans;

	/**
	 * 存储已筛选的数据
	 */
	private List<String> selectedData;

	/**
	 * 存储展品信息
	 */
	private List<ExhibitBean> exhibits;

	/**
	 * 选中的展品列表
	 */
	private List<ExhibitBean> selectedExhibits;

	/**
	 * 显示的展品列表 （用以分页加载）
	 */
	private List<ExhibitBean> shownExhibits;

	/**
	 * 悬浮头部view
	 */
	private LabelView invisView;

	/**
	 * 悬浮头部layout
	 */
	private FrameLayout invisLayout;

	/**
	 * 展品列表
	 */
	private AutoLoadListView lvExhibits;

	/**
	 * 展品列表适配器
	 */
	private ExhibitAdapter exhibitAdapter;

	/**
	 * 完成按钮
	 */
	private Button btnFinish;

	/**
	 * 记录listView的哪一个头部是悬浮部分
	 */
	private int invisItem = 0;

	/**
	 * 存储标签视图view
	 */
	private LabelView selectorHeader;

	/**
	 * 存储已选择view
	 */
	private LabelView selectedHeader;

	/**
	 * context对象
	 */
	private Context mContext;

	/**
	 * 记录分页加载 当前加载的页数
	 */
	private int page;

	/**
	 * 当前博物馆id
	 */
	private String mMuseumId;

	/**
	 * 存储已选中的标签项的列表
	 */
	private List<Button> selectedViews;

	/**
	 * 加载对话框
	 */
	private SweetAlertDialog pDialog;

	/**
	 * 已选择的标签组的点击监听接口
	 */
	private SelectedItemListener mSelectedListener;

	private SelectorItemListener mSelectorListener;

	/**
	 * 判断是否已加载完数据
	 */
	private boolean isCompleted = false;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
		mMuseumId = ((AppContext) activity.getApplication()).currentMuseumId;
		// 加载数据时耗费时间较长
		pDialog = new DialogManagerHelper(mContext).showLoadingProgressDialog();
		// 初始化数据
		initData();
		pDialog.dismiss();
		pDialog = null;
		// 初始化两个点击监听器
		mSelectedListener = new SelectedItemListener();
		mSelectorListener = new SelectorItemListener();
	}

	/**
	 * 加载数据时 弹出对话框 初始化数据，获得标签视图数据和展品数据，并获得展品列表适配器
	 */
	private void initData() {
		// 初始化当前博物馆下的所有标签的数据
		initSelectorData();
		// 初始化已选择标签列表
		selectedData = new ArrayList<String>();
		selectedViews = new ArrayList<Button>();
		// 初始化展品列表
		initExhibitData();
		// 获取展品列表adapter
		exhibitAdapter = new ExhibitAdapter(getActivity(), shownExhibits,
				R.layout.item_exhibit);
	}

	/**
	 * 从数据库中 获取当前博物馆下的所有的标签数据;
	 */
	private void initSelectorData() {
		GetBeanHelper.getInstance(mContext).getLabelList(mMuseumId,
				new GetBeanCallBack<List<LabelModel>>() {

					@Override
					public void onGetBeanResponse(List<LabelModel> response) {
						labelBeans = response;

					}
				});
	}

	/**
	 * 从数据库中获取当前博物馆下所有展品的列表，初始化已选择展品列表和展示展品列表
	 */
	private void initExhibitData() {
		exhibits = new ArrayList<ExhibitBean>();
		page = 0;
		do { // 加载当前博物馆下的所有展品，用以匹配筛选
			GetBeanHelper.getInstance(mContext).getExhibitList(mMuseumId, page,
					new GetBeanCallBack<List<ExhibitBean>>() {

						@Override
						public void onGetBeanResponse(List<ExhibitBean> response) {
							if (response != null) {
								exhibits.addAll(response);
								page++;
							}
							if (response == null ||response.size() < Constant.PAGE_COUNT) {
								// 加载完成
								notifyLoadExhibitListCompleted();
							}

						}
					});
		} while (!isCompleted);

	}

	private void notifyLoadExhibitListCompleted() {
		isCompleted = true;
		//初始化已选择展品列表
		selectedExhibits = new ArrayList<ExhibitBean>(exhibits);
		//初始化展示展品列表
		shownExhibits = new ArrayList<ExhibitBean>();
		for (int i = 0; i < Constant.PAGE_COUNT
				&& i < selectedExhibits.size(); i++) {
			shownExhibits.add(selectedExhibits.get(i));
		}
		page = 0;
	}

	/**
	 * 根据标签组数据加载标签视图（labelView），并将其添加到listView头部，同时记录标签视图的个数
	 */
	private void initSelectorHeader() {
		for (int i = 0; i < labelBeans.size(); i++) {
			selectorHeader = new LabelView(this.getActivity(),
					labelBeans.get(i));
			selectorHeader.setClickListener(mSelectorListener);
			lvExhibits.addHeaderView(selectorHeader);
			invisItem++;
		}
	}

	/**
	 * 加载已选择view，并将已选择view添加到listView头部，
	 */
	private void initSelectedHeader() {
		LabelModel bean = new LabelModel("已选择", selectedData);
		selectedHeader = new LabelView(mContext, bean);
		selectedHeader.setClickListener(mSelectedListener);

		lvExhibits.addHeaderView(selectedHeader);
	}

	/**
	 * 初始化悬浮部分view
	 */
	private void initInvisLayout() {
		LabelModel bean = new LabelModel("已选择", selectedData);
		invisView = new LabelView(mContext, bean);
		selectedHeader.setClickListener(mSelectedListener);
		invisLayout.addView(invisView);
	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.frag_subject_sellect, null);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		btnFinish = (Button) view
				.findViewById(R.id.frag_subject_select_btn_finish);
		btnFinish.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 获取selectedExhibit的id列表
				String ids = "";
				for (int i = 0; i < selectedExhibits.size(); i++) {
					if (i == selectedExhibits.size() - 1)
						ids += selectedExhibits.get(i).getId();
					else
						ids += selectedExhibits.get(i).getId() + ",";
				}
				Log.w(TAG, ids);
				// TODO 似乎有些小问题
				((AppContext) getActivity().getApplication()).exhibitsIds = ids;
				((RadioButton) HomeActivity.mRadioGroup
						.findViewById(R.id.home_tab_map)).setChecked(true);
				// Toast.makeText(mContext, "完成筛选，跳转到map", Toast.LENGTH_SHORT)
				// .show();
			}
		});
		// 获取展品列表view
		lvExhibits = (AutoLoadListView) view
				.findViewById(R.id.frag_subject_lv_exhibits);
		initSelectorHeader();// 加载标签视图头部
		initSelectedHeader();// 加载已选择标签头部
		invisLayout = (FrameLayout) view
				.findViewById(R.id.frag_subject_select_invis_layout);
		initInvisLayout();// 加载悬浮头部
		lvExhibits.setAdapter(exhibitAdapter);
		lvExhibits.setOnMyScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			// 当滚到 已选择view 及底下 时，显示悬浮头部，否则不显示
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (firstVisibleItem >= invisItem) {
					invisView.setVisibility(View.VISIBLE);
				} else {

					invisView.setVisibility(View.GONE);
				}
			}
		});
		// 给展品列表设置子项点击监听
		lvExhibits.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// 更新全局变量中的当前展品ID
				((AppContext) getActivity().getApplication()).currentExhibitId = exhibits
						.get(position - invisItem - 1).getId();
				// 更新全局变量的导航模式为手动导航
				((AppContext) getActivity().getApplication())
						.setGuideMode(Constant.GUIDE_MODE_MANUALLY);
				// 跳转至随行导游界面
				RadioButton btn = (RadioButton) HomeActivity.mRadioGroup
						.findViewById(R.id.home_tab_follow);
				btn.setChecked(true);
				btn.setEnabled(true);
			}
		});
		// 设置上拉加载更多
		lvExhibits.setOnLoadListener(new OnLoadListener() {

			/**
			 * 加载时回调该方法
			 */
			@Override
			public void onLoad() {
				// TODO Auto-generated method stub
				page++;
				loadOnPage();
			}

			/**
			 * 加载失败，重试时回调该方法
			 */
			@Override
			public void onRetry() {
				loadOnPage();
			}
		});
	}

	/**
	 * 加载一页数据，添加到显示展品列表中，如果一次加载展品个数<理应加载数，则表示已经加载完了，调用setLoadFull()方法
	 */
	private void loadOnPage() {
		for (int i = shownExhibits.size(); i < selectedExhibits.size()
				+ Constant.PAGE_COUNT
				&& i < selectedExhibits.size(); i++) {
			shownExhibits.add(selectedExhibits.get(i));
		}
		if (selectedExhibits.size() == 0
				|| shownExhibits.size() == selectedExhibits.size())
			lvExhibits.setLoadFull();
		lvExhibits.onLoadComplete();
	}

	/**
	 * 根据已选择标签 更新展品列表
	 */
	private void updateSelectResult() {
		int j = 0;
		selectedExhibits.clear();
		for (int i = 0; i < exhibits.size(); i++) {
			// 匹配
			for (j = 0; j < selectedData.size(); j++) {
				if (!exhibits.get(i).getLabels().contains(selectedData.get(j))) {
					break;
				}
			}
			if (j == selectedData.size()) {
				// 表示该展品匹配
				selectedExhibits.add(exhibits.get(i));
			}
		}
		shownExhibits.clear();
		loadOnPage();
	}

	/**
	 * 实现 gridAdapter中定义的　itemClicker 接口，通过回调函数获取item的text内容，从而知道用户点击了哪个item
	 * 
	 * @author yetwish
	 */
	private class SelectorItemListener implements GridItemClickListener {

		@Override
		public void onClick(View view) {
			// 如果已选择view中不包含该item,则将其筛选标签集（即已选择view）
			Button btnItem = (Button) view;
			String itemName = btnItem.getText().toString();
			if (!selectedData.contains(itemName)) {
				// 设置不可点击
				btnItem.setBackgroundColor(getResources().getColor(
						R.color.darkgray));
				btnItem.setClickable(false);
				// 将该view加到selectedViews中
				selectedViews.add(btnItem);
				selectedData.add(itemName); // 更新data
				// Toast.makeText(mContext, itemName,
				// Toast.LENGTH_SHORT).show();
				selectedHeader.notifyDataSetChanged();// 通知数据已更新
				invisView.notifyDataSetChanged();
				// 根据selected data筛选list view
				updateSelectResult();
				exhibitAdapter.notifyDataSetChanged();
			}
		}

	}

	private class SelectedItemListener implements GridItemClickListener {

		@Override
		public void onClick(View view) {
			Button btnItem = (Button) view;
			String itemName = btnItem.getText().toString();
			// 点击已选择view 中的item, 表示取消该标签的筛选
			if (selectedData.contains(itemName)) {
				for (Button item : selectedViews) {
					if (item.getText().toString().equals(itemName)) {
						item.setClickable(true);
						item.setBackgroundColor(getResources().getColor(
								R.color.btn_pressed_color));
						selectedData.remove(item);
						break;
					}
				}
				btnItem.setClickable(true);
				selectedData.remove(itemName);// 更新data
				selectedHeader.notifyDataSetChanged();
				// updateView(selectedData);// 更新已选择view 和悬浮头部
				invisView.notifyDataSetChanged();
				// updateView(selectedData);
				updateSelectResult();
				exhibitAdapter.notifyDataSetChanged();
			}
			// 重绘视图，使得item可点击 TODO 在切换的期间到底做了什么
			selectedHeader.invalidate();
			if (invisView.getVisibility() == View.VISIBLE) {
				invisView.invalidate();
			}

		}
	}

}
