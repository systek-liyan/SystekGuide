package com.app.guide.ui;

import java.sql.SQLException;
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
import android.widget.Toast;

import cn.pedant.SweetAlert.SweetAlertDialog;

import com.app.guide.AppContext;
import com.app.guide.Constant;
import com.app.guide.R;
import com.app.guide.adapter.ExhibitAdapter;
import com.app.guide.adapter.GridAdapter.GridItemClickListener;
import com.app.guide.bean.ExhibitBean;
import com.app.guide.bean.LabelBean;
import com.app.guide.offline.GetBeanFromSql;
import com.app.guide.widget.AutoLoadListView;
import com.app.guide.widget.AutoLoadListView.OnLoadListener;
import com.app.guide.widget.DialogManagerHelper;
import com.app.guide.widget.SelectorView;

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

	/**
	 * 存储筛选器的数据,修改为数据库加载方式
	 */
	private List<LabelBean> labelBeans;
	/**
	 * 存储已筛选的数据
	 */
	private List<String> selectedData;

	/**
	 * 存储展品信息
	 */
	private List<ExhibitBean> exhibits;

	/**
	 * 选中的展品列表Id
	 */
	private List<ExhibitBean> selectedExhibits;

	private List<ExhibitBean> shownExhibits;

	/**
	 * 悬浮头部view
	 */
	private SelectorView invisView;

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
	 * 存储筛选器view
	 */
	private SelectorView selectorHeader;

	/**
	 * 存储已选择view
	 */
	private SelectorView selectedHeader;

	/**
	 * context对象
	 */
	private Context mContext;

	private int page;

	private String mMuseumId;

	private List<Button> selectedViews = new ArrayList<Button>();

	private SweetAlertDialog pDialog;

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
	}

	/**
	 * 加载数据时 弹出对话框 初始化数据，获得筛选器数据和展品数据，并获得展品列表适配器
	 */
	private void initData() {
		getSelectorData();
		getSelectedData();
		getExhibitData();
		exhibitAdapter = new ExhibitAdapter(getActivity(), shownExhibits,
				R.layout.item_exhibit);
	}

	/**
	 * get SelectorData;
	 */
	private void getSelectorData() {
		try {
			labelBeans = GetBeanFromSql.getLabelBeans(mContext, mMuseumId);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * get SelectedData;
	 */
	private void getSelectedData() {
		selectedData = new ArrayList<String>();
	}

	/**
	 * TODO 采用异步加载？ get ExhibitData;
	 */
	private void getExhibitData() {
		exhibits = new ArrayList<ExhibitBean>();
		page = 0;
		try {
			List<ExhibitBean> data = null;
			do {
				data = GetBeanFromSql
						.getExhibitBeans(mContext, mMuseumId, page);
				if (data != null) {
					for (int i = 0; i < data.size(); i++) {
						exhibits.add(data.get(i));
					}
					page++;
				}
			} while (data != null && data.size() == Constant.PAGE_COUNT);
			selectedExhibits = new ArrayList<ExhibitBean>(exhibits);
			shownExhibits = new ArrayList<ExhibitBean>();
			for (int i = 0; i < Constant.PAGE_COUNT
					&& i < selectedExhibits.size(); i++) {
				shownExhibits.add(selectedExhibits.get(i));
			}
			page = 0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 根据筛选器数据加载筛选器，并将筛选器添加到listView头部，同时记录invisItem
	 */
	private void initSelectorHeader() {
		for (int i = 0; i < labelBeans.size(); i++) {
			selectorHeader = new SelectorView(this.getActivity(),
					labelBeans.get(i), new SelectorItemListener());
			lvExhibits.addHeaderView(selectorHeader);
			invisItem++;
		}
	}

	/**
	 * 加载已选择view，并将已选择view添加到listView头部，
	 */
	private void initSelectedHeader() {
		LabelBean bean = new LabelBean("已选择", selectedData);
		selectedHeader = new SelectorView(mContext, bean,
				new SelectedItemListener());
		lvExhibits.addHeaderView(selectedHeader);
	}

	/**
	 * 初始化悬浮部分view
	 */
	private void initInvisLayout() {
		// invis
		LabelBean bean = new LabelBean("已选择", selectedData);
		invisView = new SelectorView(mContext, bean, new SelectedItemListener());
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
				Log.w("TAG", ids);
				((AppContext) getActivity().getApplication()).exhibitsIdList = ids;
				((RadioButton) HomeActivity.mRadioGroup
						.findViewById(R.id.home_tab_map)).setChecked(true);
				Toast.makeText(mContext, "完成筛选，跳转到map", Toast.LENGTH_SHORT)
						.show();
			}
		});
		// get exhibit list
		lvExhibits = (AutoLoadListView) view
				.findViewById(R.id.frag_subject_lv_exhibits);
		initSelectorHeader();// 加载筛选器头部
		initSelectedHeader();// 加载已选择头部
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
		lvExhibits.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				((AppContext) getActivity().getApplication()).currentExhibitId = exhibits
						.get(position - invisItem - 1).getId();
				((AppContext) getActivity().getApplication())
						.setGuideMode(false);
				RadioButton btn = (RadioButton) HomeActivity.mRadioGroup
						.findViewById(R.id.home_tab_follow);
				btn.setChecked(true);
				btn.setEnabled(true);
			}
		});
		lvExhibits.setOnLoadListener(new OnLoadListener() {

			@Override
			public void onLoad() {
				// TODO Auto-generated method stub
				page++;
				loadOnPage();
			}

			@Override
			public void onRetry() {
				// TODO Auto-generated method stub
				loadOnPage();
			}
		});
	}

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
	 * 实现 gridAdapter中定义的　itemClicker 接口，通过回调函数获取item的text内容，从而知道用户点击了哪个item
	 * 
	 * @author yetwish
	 */
	private class SelectorItemListener implements GridItemClickListener {
		@Override
		public void onClick(Button btnItem) {
			// 如果已选择view中不包含该item,则将其筛选标签集（即已选择view）
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
				selectedHeader.updateView(selectedData); // 更新已选择view 和悬浮头部
				invisView.updateView(selectedData);
				// 根据selected data筛选list view
				updateSelectResult();
				exhibitAdapter.notifyDataSetChanged();
			}
		}

	}

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

	private class SelectedItemListener implements GridItemClickListener {
		@Override
		public void onClick(Button btnItem) {
			String itemName = btnItem.getText().toString();
			// 点击已选择view 中的item, 表示取消该标签的筛选
			if (selectedData.contains(itemName)) {
				// TODO 匹配 ，设置可点击
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
				selectedHeader.updateView(selectedData);// 更新已选择view 和悬浮头部
				invisView.updateView(selectedData);
				updateSelectResult();
				exhibitAdapter.notifyDataSetChanged();
			}
		}
	}

}
