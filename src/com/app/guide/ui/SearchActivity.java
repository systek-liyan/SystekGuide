package com.app.guide.ui;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import cn.pedant.SweetAlert.SweetAlertDialog;

import com.app.guide.AppContext;
import com.app.guide.Constant;
import com.app.guide.R;
import com.app.guide.adapter.ExhibitAdapter;
import com.app.guide.bean.ExhibitBean;
import com.app.guide.beanhelper.GetBeanCallBack;
import com.app.guide.beanhelper.GetBeanHelper;
import com.app.guide.widget.AutoLoadListView;
import com.app.guide.widget.AutoLoadListView.OnLoadListener;
import com.app.guide.widget.DialogManagerHelper;
import com.app.guide.widget.SearchView;

/**
 * 搜索界面主要分为两个部分：SearchView（自定义搜索View）+搜索结果列表(ListView)。
 * 
 * 自定义SearchView应该包含两个结构：输入栏+弹出框。
 * 弹出框包含两个功能——用于自动补全和热门搜索推荐，所以应分为两个列表：自动补全列表和热门搜索列表
 * 提示框的数据与输入框输入的文本是实时联动的，随着文本输入内容，弹出框显示与之匹配的展品名称,最多显示DEFAULT_HINT_SIZE个
 * 在输入框为空时，点击输入框，弹出框显示热门搜索列表。
 * 点击软键盘搜索键，显示搜索结果
 * 输入栏最右端，在有输入内容时，显示'X',代表清除输入内容
 * 
 */
public class SearchActivity extends BaseActivity implements
		SearchView.SearchViewListener {
	
	private static final String TAG = SearchActivity.class.getSimpleName();
	
	/**
	 * 搜索结果显示list view
	 */
	//private AutoLoadListView lvResults;
	private ListView lvResults;


	/**
	 * 推荐精品adapter
	 */
	private ArrayAdapter<String> hintAdapter;

	/**
	 * 自动补全adapter
	 */
	private ArrayAdapter<String> autoCompleteAdapter;

	/**
	 * 结果adapter
	 */
	private ExhibitAdapter resultAdapter;

	/**
	 * 热搜版数据（推荐精品）
	 */
	private List<String> hintData;

	/**
	 * 默认提示列表的数据项个数
	 */
	private static int DEFAULT_HINT_SIZE = 6;

	/**
	 * 提示列表的数据项个数
	 */
	private static int mHintSize = DEFAULT_HINT_SIZE;

	/**
	 * 设置提示列表数据项个数
	 * 
	 * @param hintSize
	 */
	public static void setHintSize(int hintSize) {
		mHintSize = hintSize;
	}

	/**
	 * 搜索过程中自动补全数据
	 */
	private List<String> autoCompleteData;

	/**
	 * 搜索结果列表数据
	 */
	private List<ExhibitBean> resultData;

	/**
	 * 自定义的搜索view
	 */
	private SearchView searchView;

	/**
	 * 返回按钮,在SearchView中已经捕获，不用在此处理
	 */
	// private Button btnBack;

	/**
	 * 储存展品列表,目前是博物馆的所有展品
	 */
	private List<ExhibitBean> exhibitsList;

	/**
	 * museum id
	 */
	private String mMuseumId;

	/**
	 * page=0,1,2... 代表第1,2,3...页
	 */
	private int page = 0;

	/** 存储当前显示的搜索结果 */
	private List<ExhibitBean> shownResults;

	private SweetAlertDialog pDialog;

	private boolean isCompleted = false;

	@Override
	@SuppressLint("InlinedApi")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		mMuseumId = ((AppContext) getApplicationContext()).currentMuseumId;
		// 加载数据时耗费时间较长
		pDialog = new DialogManagerHelper(this).showLoadingProgressDialog();
		// 初始化数据
		initData();
		pDialog.dismiss();
		pDialog = null;
		initViews();
	}

	/**
	 * 当展品数据量较大时，存太多数据 初始化exhibit数据
	 * 目前，将博物馆所有展品存入exhibitsList中
	 */
	private void initExhibitData() {
		exhibitsList = new ArrayList<ExhibitBean>();
			do {
				 GetBeanHelper.getInstance(this).getExhibitList(mMuseumId, page, new GetBeanCallBack<List<ExhibitBean>>() {
					
					@Override
					public void onGetBeanResponse(List<ExhibitBean> response) {
						Log.d(TAG,"reponse.size,page="+response.size()+","+page);
						if(response !=null){
							exhibitsList.addAll(response);
							page++;
						}
						if (response == null ||response.size() < Constant.PAGE_COUNT) {
							// 加载完成
							isCompleted = true;
							Log.d(TAG,"加载完成,共"+page+"页");
						}
					}
				});
			} while (!isCompleted );
	}

	private void initData() {
		initExhibitData();
		getHintData();
		getAutoCompleteData("");
		getResultData("");
	}

	/**
	 * TODO 从服务端中获取到数据库 ，再从数据库中获得 获取精品推荐数据
	 */
	private void getHintData() {
		hintData = new ArrayList<String>(mHintSize);
		for (int i = 0; i < mHintSize; i++) {
			hintData.add("分类/精品" + (i + 1));
		}
		hintAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, hintData);
	}

	/**
	 * 获取所有的exhibitBean 匹配每个的name 获取自动补全data 和adapter
	 */
	private void getAutoCompleteData(String text) {
		if (autoCompleteData == null) {
			// 初始化
			autoCompleteData = new ArrayList<String>(mHintSize);
		} else {
			// 根据text 获取auto data
			autoCompleteData.clear();
			for (int i = 0, count = 0; i < exhibitsList.size()
					&& count < mHintSize; i++) {
				if (exhibitsList.get(i).getName().contains(text.trim())) {
					autoCompleteData.add(exhibitsList.get(i).getName());
					count++;
				}
			}
		}
		if (autoCompleteAdapter == null) {
			autoCompleteAdapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, autoCompleteData);
		} else {
			autoCompleteAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 根据 搜索name 获取result data 从数据库中获取 获取搜索结果data
	 */
	private void getResultData(String text) {
		if (resultData == null) {
			// 初始化
			resultData = new ArrayList<ExhibitBean>();
			shownResults = new ArrayList<ExhibitBean>();
		} else {
			resultData.clear();  // 存储搜索结果
			shownResults.clear(); // 存储当前将要显示的搜索结果
			for (int i = 0, count = 0; i < exhibitsList.size(); i++) {
				if (exhibitsList.get(i).getName().contains(text.trim())) {
					resultData.add(exhibitsList.get(i));
					if (count < Constant.PAGE_COUNT) {
						shownResults.add(exhibitsList.get(i)); // 添加一页的显示数据
						count++;
					}
				}
			}
		}
		if (resultAdapter == null) {
			resultAdapter = new ExhibitAdapter(this, shownResults,
					R.layout.item_exhibit);
		}
		resultAdapter.notifyDataSetChanged();
	}

	private void initViews() {
		// find views
		lvResults = (ListView) findViewById(R.id.search_lv_results);
		searchView = (SearchView) findViewById(R.id.search_view);

		// 设置搜索监听回调
		searchView.setSearchViewListener(this);

		// 设置热度adapter
		searchView.setTipsHintAdapter(hintAdapter);

		// 设置自动补全adapter
		searchView.setAutoCompleteAdapter(autoCompleteAdapter);

		lvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long l) {
				// 跳转到随身导游界面
				((AppContext) getApplication()).currentExhibitId = shownResults
						.get(position).getId();
				// 目前，认为这样的选择展品，自动变为手动选择
				((AppContext) getApplication()).setGuideMode(Constant.GUIDE_MODE_MANUALLY);
				// 表示是通过搜索选择的展品
				((AppContext) getApplication()).isSelectedInSearch = true;
				finish();

			}
		});
	}

	/**
	 * 当 edit text 文本改变时 触发的回调
	 * 
	 * @param text
	 */
	@Override
	public void onAutoRefreshComplete(String text) {
		getAutoCompleteData(text);
	}

	/**
	 * 点击搜索键时edit text触发的回调
	 * 
	 * @param text
	 */
	@Override
	public void onSearch(String text) {
		getResultData(text);
		lvResults.setVisibility(View.VISIBLE);
		// 第一次获取结果 还未配置适配器
		if (lvResults.getAdapter() == null) {
			// 获取搜索数据 设置适配器
			lvResults.setAdapter(resultAdapter);
		} else {
			// 更新搜索数据
			resultAdapter.notifyDataSetChanged();
		}
		Toast.makeText(this, "完成搜索", Toast.LENGTH_SHORT).show();
		// 隐藏软键盘
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}
}
