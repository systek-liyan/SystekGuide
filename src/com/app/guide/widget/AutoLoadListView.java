package com.app.guide.widget;

import com.app.guide.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 
 * 上拉加载更多ListView
 * 
 * @author acer
 * 
 */
public class AutoLoadListView extends ListView implements OnScrollListener {
	private static final String TAG = "AutoLoadListView";

	private LayoutInflater inflater;
	private View footer;

	private TextView loadFail;
	private TextView loadFull;
	private TextView loadIng;
	private ProgressBar progressBar;

	private boolean isLoading;// 判断是否正在加载

	private OnLoadListener mOnLoadListener;
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
	private void initView(Context context) {
		inflater = LayoutInflater.from(context);
		footer = inflater.inflate(R.layout.listview_footer, null);
		loadFull = (TextView) footer.findViewById(R.id.footer_loadFull);
		loadFail = (TextView) footer.findViewById(R.id.footer_noData);
		loadIng = (TextView) footer.findViewById(R.id.footer_more);
		progressBar = (ProgressBar) footer.findViewById(R.id.footer_loading);
		addFooterView(footer);
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

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
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

	// 用于加载更多结束后的回调
	public void onLoadComplete() {
		isLoading = false;
	}

	// 加载失败时footer显示
	public void setLoadFailed() {
		loadIng.setVisibility(View.GONE);
		loadFull.setVisibility(GONE);
		progressBar.setVisibility(GONE);
		loadFail.setVisibility(VISIBLE);
	}

	// 加载完成时footer显示
	public void setLoadFull() {
		loadIng.setVisibility(View.GONE);
		loadFull.setVisibility(VISIBLE);
		progressBar.setVisibility(GONE);
		loadFail.setVisibility(GONE);
	}

	// 正在加载时footer显示
	private void setloading() {
		loadIng.setVisibility(View.VISIBLE);
		loadFull.setVisibility(GONE);
		progressBar.setVisibility(VISIBLE);
		loadFail.setVisibility(GONE);
	}

	// 加载更多监听
	public void setOnLoadListener(OnLoadListener onLoadListener) {
		mOnLoadListener = onLoadListener;
	}

	public void setOnMyScrollListener(OnScrollListener onScrollListener) {
		this.mOnScrollListener = onScrollListener;
	}

	public interface OnLoadListener {
		public void onLoad();
	}

}
