package com.app.guide.ui;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
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
	
	///////////////////////// 搜索结果 ListView+data+adapter
	/**
	 * 搜索结果list view
	 */
	private ListView lvResults;
	/**
	 * 搜索结果列表数据
	 */
	private List<ExhibitBean> resultData;
	/**
	 * 结果adapter
	 */
	private ExhibitAdapter resultAdapter;

	//////////////////////////////////////////////////
	/**
	 * 自定义的搜索view
	 */
	private SearchView searchView;

    ///////////////////////// 自定义搜索SearchView 提示框 data+adapter
	/**
	 * 热搜版数据（推荐精品）
	 */
	private List<String> hintData;
	/**
	 * 热搜版adapter
	 */
	private ArrayAdapter<String> hintAdapter;

    ///////////////////////// 自定义搜索SearchView 提示框 data+adapter
	/**
	 * 搜索过程中自动补全数据
	 */
	private List<String> autoCompleteData;
	/**
	 * 自动补全adapter
	 */
	private ArrayAdapter<String> autoCompleteAdapter;

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
	 * museum id
	 */
	private String mMuseumId;

	@Override
	@SuppressLint("InlinedApi")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		mMuseumId = ((AppContext) getApplicationContext()).currentMuseumId;
		initViews();
	}
	
	private void initViews() {
		// 搜索结果
		lvResults = (ListView) findViewById(R.id.search_lv_results);
		
		// 自定义SearchView应该包含两个结构：输入栏+弹出框。
		searchView = (SearchView) findViewById(R.id.search_view);	
		// 设置搜索监听回调
		searchView.setSearchViewListener(this);
		
		// 从服务端中获取到数据库 ，再从数据库中获得 获取精品推荐数据(热度搜索)
		getHintData();
		// 设置热度adapter,精品推荐，热度搜索
		searchView.setTipsHintAdapter(hintAdapter);

		// 自动补全提示框：数据+adapter
		autoCompleteData = new ArrayList<String>(mHintSize);
		autoCompleteAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, autoCompleteData);
		searchView.setAutoCompleteAdapter(autoCompleteAdapter);
		
		// 搜索结果ListView: 数据+adapter
		resultData = new ArrayList<ExhibitBean>();
		resultAdapter = new ExhibitAdapter(this, resultData,R.layout.item_exhibit);

		lvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long l) {
				// 跳转到随身导游界面
				((AppContext) getApplication()).currentExhibitId = resultData
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
	 * TODO 从服务端中获取到数据库 ，再从数据库中获得 获取精品推荐数据(热度搜索)
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
	 * 当有文字输入时:
	 * 在提示框显示自动补全的茶品名称，最多mHintSize个
	 * 填充resultData，提供搜索结果展品
	 */
	private void getAutoCompleteData(String text) {
		// 清除上次匹配的数据
		autoCompleteData.clear();
		resultData.clear();
		GetBeanHelper.getInstance(this).getExhibitList_name(mMuseumId, text.trim(),
				new GetBeanCallBack<List<ExhibitBean>>() {
					@Override
					public void onGetBeanResponse(List<ExhibitBean> response) {
						if (response == null) return;
						int count = 0;
						for (ExhibitBean exhibit : response) {
						   resultData.add(exhibit);
						   if (count < mHintSize) {
						       autoCompleteData.add(exhibit.getName());
						   }
						   count++;
						}
					}
			
		});
			
		// 通知提示框数据改变
    	autoCompleteAdapter.notifyDataSetChanged();
    	// 通知搜索结果数据改变，在点击软键盘的serarch按键时触发，见onSearch()
		// resultAdapter.notifyDataSetChanged();
	}

	/**
	 * 当 edit text 文本改变时 触发的回调,自动匹配展品名称
	 * 
	 * @param text
	 */
	@Override
	public void onAutoRefreshComplete(String text) {
		getAutoCompleteData(text);
	}

	/**
	 * 点击软键盘的搜索键时edit text触发的回调
	 * 
	 * @param text
	 */
	@Override
	public void onSearch(String text) {
		// 在onAutoRefreshComplete(text)中，已经填充了lvResults,这里仅使用即可。
		lvResults.setVisibility(View.VISIBLE);
		// 第一次获取结果 还未配置适配器
		if (lvResults.getAdapter() == null) {
			// 获取搜索数据 设置适配器
			lvResults.setAdapter(resultAdapter);
		} else {
			// 更新搜索数据
			resultAdapter.notifyDataSetChanged();
		}
		// Toast.makeText(this, "完成搜索", Toast.LENGTH_SHORT).show();
		// 隐藏软键盘
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}
}
