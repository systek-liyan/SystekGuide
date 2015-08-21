package com.app.guide.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.guide.R;

/**
 * 
 * 上拉加载更多ListView
 * 
 * @author acer
 * 
 */
public class AutoLoadListView extends ListView implements OnScrollListener {

	private static final String TAG = AutoLoadListView.class.getSimpleName();
	
	private LayoutInflater inflater;
	/** 在本类表示的ListView的末尾添加此view */
	private View footer;

	/** 加载失败(暂无数据)  */
	private TextView loadFail;
	/** 已加载全部 */
	private TextView loadFull;
	/** 加载中 */
	private TextView loadIng;
	/** 加载中，进度条 */
	private ProgressBar progressBar;

	/** 判断是否正在加载 */
	private boolean isLoading = false;
	
	/** 加载监听，调用者实现 */
	private OnLoadListener mOnLoadListener;
	/** 滑动监听, 内部使用 */
	private OnScrollListener mOnScrollListener;

	public AutoLoadListView(Context context) {
		super(context);
		initView(context);
	}

	public AutoLoadListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public AutoLoadListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	// 初始化组件
	@SuppressLint("InflateParams")
	private void initView(Context context) {
		inflater = LayoutInflater.from(context);
		footer = inflater.inflate(R.layout.listview_footer, null);
		loadFull = (TextView) footer.findViewById(R.id.footer_loadFull);
		loadFail = (TextView) footer.findViewById(R.id.footer_noData);  
		
		/** 点击加载失败(暂无数据) */
		loadFail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG,"点击加载失败");
				setloading();
				if (mOnLoadListener != null) {
					mOnLoadListener.onRetry();
				}
			}
		});
		loadIng = (TextView) footer.findViewById(R.id.footer_more);
		progressBar = (ProgressBar) footer.findViewById(R.id.footer_loading);
		
		//设置footer不可点击，因此loadFail.setOnClickListener()无效
		addFooterView(footer,null,false);
		setOnScrollListener(this);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (this.mOnScrollListener != null) {
			mOnScrollListener.onScroll(view, firstVisibleItem,
					visibleItemCount, totalItemCount);
		}
	}

	/**
	 * 滑动ListView，如果isLoading=false,并且能够看见最后一行,
	 * 执行mOnLoadListener.onLoad();   即继续加载。
	 * @param scrollState The current scroll state. One of SCROLL_STATE_TOUCH_SCROLL or SCROLL_STATE_IDLE.
	 */
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		Log.d(TAG,"onScrollStateChanged(),scrollState="+scrollState);
		if (!isLoading && view.getLastVisiblePosition() == view.getCount() - 1) {
			isLoading = true;
			setloading();
			if (mOnLoadListener != null) {
				mOnLoadListener.onLoad();
			}
		}
		if (this.mOnScrollListener != null) {
			mOnScrollListener.onScrollStateChanged(view, scrollState);
		}
	}

	/** 用于加载更多结束后的回调 */
	public void onLoadComplete() {
		isLoading = false;
	}

	/** 加载失败时footer显示 */
	public void setLoadFailed() {
		loadIng.setVisibility(View.GONE);
		loadFull.setVisibility(GONE);
		progressBar.setVisibility(GONE);
		loadFail.setVisibility(VISIBLE);
	}

	/** 加载完成时footer显示  */
	public void setLoadFull() {
		loadIng.setVisibility(View.GONE);
		loadFull.setVisibility(VISIBLE);
		progressBar.setVisibility(GONE);
		loadFail.setVisibility(GONE);
	}

	/** 正在加载时footer显示 */
	private void setloading() {
		loadIng.setVisibility(View.VISIBLE);
		loadFull.setVisibility(GONE);
		progressBar.setVisibility(VISIBLE);
		loadFail.setVisibility(GONE);
	}

	/** 加载更多监听  */
	public void setOnLoadListener(OnLoadListener onLoadListener) {
		mOnLoadListener = onLoadListener;
	}

	public void setOnMyScrollListener(OnScrollListener onScrollListener) {
		this.mOnScrollListener = onScrollListener;
	}

	/** 加载更多监听接口  */
	public interface OnLoadListener {
		/** 执行加载更多的方法 */
		public void onLoad();
		/** 执行加载失败时的重新加载 */
		public void onRetry();
	}

}
