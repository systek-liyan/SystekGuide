package com.yetwish.tourismguide.fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.yetwish.tourismguide.R;
import com.yetwish.tourismguide.adapter.ExhibitAdapter;
import com.yetwish.tourismguide.adapter.GridAdapter.GridItemClickListener;
import com.yetwish.tourismguide.info.Exhibit;
import com.yetwish.tourismguide.view.SelectorView;

/**
 * 专题导游fragment.每有一组selector（筛选标签）就给listView添加一个头部 (selectorView)
 * 主要用到动态加载gridView,以及设置listView悬浮头部，和通过回调接口 传递信息
 * @author yetwish
 * @date 2015-4-21
 */
public class SubjectSelectFragment extends Fragment {

	/**
	 * 根layoutView 
	 */
	private View rootView;

	/**
	 * 存储筛选器的数据 
	 */
	private List<String[]> selectorData;
	
	/**
	 * 存储已筛选的数据
	 */
	private List<String> selectedData;
	
	/**
	 * 存储展品信息
	 */
	private List<Exhibit> exhibits;

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
	private ListView lvExhibits;
	
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
	 * 展品暂用icon资源
	 */
	private final static int image = R.drawable.exhibit_icon;

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

	/**
	 * 展品介绍字符串
	 */
	private final static String introduction = "1977年平谷刘家河出土。敛口，口沿外折，方唇，颈粗短，折肩，深腹，高圈足。颈部饰以两道平行凸弦纹，肩部饰一周目雷纹，其上圆雕等距离三个大卷角羊首，腹部饰以扉棱为鼻的饕餮纹，圈足饰一周对角云雷纹，其上有三个方形小镂孔。此罍带有商代中期的显著特征。其整体造型，纹饰与河南郑州白家庄M3出土的罍较相似。此器造型凝重，纹饰细密，罍肩上的羊首系用分铸法铸造，显示了商代北京地区青铜铸造工艺的高度水平。";
	
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
		//初始化数据
		initData();
	}

	/**
	 * 初始化数据，获得筛选器数据和展品数据，并获得展品列表适配器
	 */
	private void initData() {
		getSelectorData();
		getSelectedData();
		getExhibitData();
		exhibitAdapter = new ExhibitAdapter(getActivity(),
				R.layout.fragment_subject_exhibit_list_item, exhibits);
	}

	/**
	 * get SelectorData;
	 */
	private void getSelectorData() {
		selectorData = new ArrayList<String[]>();
		selectorData.add(new String[] { "材质","石器", "陶器", "铜器", "玉器", "木制品", "铁器" });
		selectorData.add(new String[] { "年代", "夏", "商", "秦汉", "三国" });
		selectorData.add(new String[] { "用途", "日常用具", "农耕", "雕刻" });
	}

	/**
	 * get SelectedData;
	 */
	private void getSelectedData() {
		selectedData = new ArrayList<String>();
		selectedData.add("已选择");
	}

	/**
	 * get ExhibitData;
	 */
	private void getExhibitData() {
		exhibits = new ArrayList<Exhibit>();
		int length = 8;
		for(int i = 0;i< length;i++){
			exhibits.add(new Exhibit("八星八箭青花瓷", "展厅2010", "唐朝",
					introduction, getResources().getDrawable(image)));
		}

	}

	/**
	 * 根据筛选器数据加载筛选器，并将筛选器添加到listView头部，同时记录invisItem
	 */
	private void initSelectorHeader() {
		for (int i = 0; i < selectorData.size(); i++) {
			selectorHeader = new SelectorView(this.getActivity(),
					Arrays.asList(selectorData.get(i)), new SelectorItemListener());
			lvExhibits.addHeaderView(selectorHeader);
			invisItem++;
		}
	}

	/**
	 * 加载已选择view，并将已选择view添加到listView头部，
	 */
	private void initSelectedHeader() {
		selectedHeader = new SelectorView(mContext,selectedData,new SelectedItemListener()); 
		lvExhibits.addHeaderView(selectedHeader);
	}

	/**
	 * 初始化悬浮部分view 
	 */
	private void initInvisView() {
		// invis
		invisView = new SelectorView(this.getActivity(), selectedData, new SelectedItemListener());
		invisLayout = (FrameLayout) rootView
				.findViewById(R.id.fragment_subject_select_invis_layout);
		invisLayout.addView(invisView);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		//缓存加载，避免多次加载
		if (rootView == null) {
			rootView = inflater
					.inflate(R.layout.fragment_subject_sellect, null);
			initViews();
		}
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if (parent != null)
			parent.removeView(rootView);
		return rootView;
	}

	private void initViews() {
		btnFinish = (Button) rootView
				.findViewById(R.id.fragment_subject_select_btn_finish);

		// get exhibit list
		lvExhibits = (ListView) rootView
				.findViewById(R.id.fragment_subject_lv_exhibits);
		initSelectorHeader();//加载筛选器头部
		initSelectedHeader();//加载已选择头部 
		initInvisView();//加载悬浮头部
		lvExhibits.setAdapter(exhibitAdapter);
		lvExhibits.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			//当滚到 已选择view 及底下 时，显示悬浮头部，否则不显示
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
				Toast.makeText(mContext, position + "", Toast.LENGTH_SHORT)
						.show();
			}
		});
	}
	
	/**
	 * 实现 gridAdapter中定义的　itemClicker 接口，通过回调函数获取item的text内容，从而知道用户点击了哪个item
	 * @author yetwish
	 */
	private class SelectorItemListener implements GridItemClickListener {
		@Override
		public void onClick(String itemName) {
			//如果已选择view中不包含该item,则将其筛选标签集（即已选择view）
			if (!selectedData.contains(itemName)) {
				selectedData.add(itemName); //更新data 
				//Toast.makeText(mContext, itemName, Toast.LENGTH_SHORT).show();
				selectedHeader.updateView(selectedData); //更新已选择view 和悬浮头部
				invisView.updateView(selectedData);
			}
		}
	}

	private class SelectedItemListener implements GridItemClickListener {
		@Override
		public void onClick(String itemName) {
			//点击已选择view 中的item, 表示取消该标签的筛选
			if (selectedData.contains(itemName)) {
				selectedData.remove(itemName);//更新data
				selectedHeader.updateView(selectedData);//更新已选择view 和悬浮头部
				invisView.updateView(selectedData);
			}
		}
	}
	
	

}
