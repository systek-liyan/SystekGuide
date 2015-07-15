package com.app.guide.ui;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;
import cn.pedant.SweetAlert.SweetAlertDialog;

import com.app.guide.AppContext;
import com.app.guide.Constant;
import com.app.guide.R;
import com.app.guide.adapter.ExhibitAdapter;
import com.app.guide.bean.ExhibitBean;
import com.app.guide.beanhelper.GetBeanFromSql;
import com.app.guide.widget.AutoLoadListView;
import com.app.guide.widget.AutoLoadListView.OnLoadListener;
import com.app.guide.widget.DialogManagerHelper;
import com.app.guide.widget.SearchView;

/**
 * 搜索界面 TODO adapter
 * 
 * @author yetwish
 * 
 */
public class SearchActivity extends BaseActivity implements
		SearchView.SearchViewListener {

	/**
	 * 结果list view
	 */
	private AutoLoadListView lvResults;

	/**
	 * 精品adapter
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
	 * 热搜版数据
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
	 * 结果列表数据
	 */
	private List<ExhibitBean> resultData;

	/**
	 * 搜索view
	 */
	private SearchView searchView;

	/**
	 * 返回按钮
	 */
	private Button btnBack;

	/**
	 * 储存展品列表
	 */
	private List<ExhibitBean> exhibitsList;

	/**
	 * intent 对象
	 */
	private Intent mIntent;

	/**
	 * museum id
	 */
	private String mMuseumId;

	/**
	 * page
	 */
	private int page = 0;

	private List<ExhibitBean> shownResults;

	private SweetAlertDialog pDialog;

	@Override
	@SuppressLint("InlinedApi")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		mIntent = getIntent();
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
	 * TODO 当展品数据量较大时，存太多数据 初始化exhibit数据
	 */
	private void initExhibitData() {
		exhibitsList = new ArrayList<ExhibitBean>();
		try {
			List<ExhibitBean> data = null;
			do {
				data = GetBeanFromSql.getExhibitBeans(this, mMuseumId, page);
				if (data != null) {
					for (int i = 0; i < data.size(); i++) {
						exhibitsList.add(data.get(i));
					}
					page++;
				}
			} while (data != null && data.size() == Constant.PAGE_COUNT);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initData() {
		initExhibitData();
		getHintData();
		getAutoCompleteData("");
		getResultData("");
	}

	/**
	 * TODO 从服务端中获取到数据库 ，再数据库中获得 获取精品数据
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
		// // 获取搜索词
		// String[] words;
		// if (text.contains(" "))
		// words = text.split(" ");
		// else {
		// words = new String[1];
		// words[0] = text;
		// }
		if (resultData == null) {
			// 初始化
			resultData = new ArrayList<ExhibitBean>();
			shownResults = new ArrayList<ExhibitBean>();
		} else {
			resultData.clear();
			shownResults.clear();
			// if ("".equals(text)) {
			// // 当text为空时
			// for (int i = 0, count = 0; i < exhibitsList.size(); i++) {
			// resultData.add(exhibitsList.get(i));
			// if (count < Constant.PAGE_COUNT) {
			// shownResults.add(exhibitsList.get(i));
			// count++;
			// }
			// }
			// } else
			for (int i = 0, count = 0; i < exhibitsList.size(); i++) {
				if (exhibitsList.get(i).getName().contains(text.trim())) {
					resultData.add(exhibitsList.get(i));
					if (count < Constant.PAGE_COUNT) {
						shownResults.add(exhibitsList.get(i));
						count++;
					}
				}
			}
		}
		if (resultAdapter == null) {
			resultAdapter = new ExhibitAdapter(this, shownResults,
					R.layout.item_exhibit);
		} else {
			resultAdapter.notifyDataSetChanged();
		}
	}

	private void initViews() {
		// find views
		lvResults = (AutoLoadListView) findViewById(R.id.search_lv_results);
		searchView = (SearchView) findViewById(R.id.search_view);
		btnBack = (Button) findViewById(R.id.search_btn_back);

		// 给返回按钮设置点击监听
		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

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
				((AppContext) getApplication()).setGuideMode(Constant.GUIDE_MODE_MANUALLY);
				((AppContext) getApplication()).isSelectedInSearch = true;
				finish();

			}
		});
		lvResults.setOnLoadListener(new OnLoadListener() {

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
		for (int i = shownResults.size(); i < shownResults.size()
				+ Constant.PAGE_COUNT
				&& i < resultData.size(); i++) {
			shownResults.add(resultData.get(i));
		}
		if (resultData.size() == 0 || shownResults.size() == resultData.size())
			lvResults.setLoadFull();
		lvResults.onLoadComplete();
	}

	/**
	 * 当 edit text 文本改变时 触发的回调
	 * 
	 * @param text
	 */
	@Override
	public void onRefreshAutoComplete(String text) {
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

	@Override
	public void onTipsItemClick(String text) {

	}

	@Override
	protected boolean isFullScreen() {
		return true;
	}

}
