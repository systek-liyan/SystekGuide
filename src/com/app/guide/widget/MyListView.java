package com.app.guide.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.guide.R;

public class MyListView extends ListView {

	//private static final String TAG = MyListView.class.getSimpleName();

	private LinearLayout mFooter;
	private Context mContext;

	/**
	 * footer文字
	 */
	private TextView tvTips;

	/**
	 * 进度条
	 */
	private ProgressBar pbLoading;


	private enum State {
		READY_TO_LOAD, LOADING
	}

	private State mState = State.READY_TO_LOAD;

	private FooterLoadingMoreListener mLoadMoreListener;

	/**
	 * 若需要用到加载更多的功能，则设置加载更多接口，不设置则不需要该功能
	 * 
	 * @param listener
	 */
	public void setLoadingMoreListener(FooterLoadingMoreListener listener) {
		this.mLoadMoreListener = listener;
	}

	public MyListView(Context context) {
		super(context);
	}

	public MyListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		initFooter();
	}

	@SuppressLint("InflateParams")
	private void initFooter() {

		mFooter = (LinearLayout) LayoutInflater.from(mContext).inflate(
				R.layout.listview_footer, null);

		tvTips = (TextView) mFooter.findViewById(R.id.list_footer_tv);

		pbLoading = (ProgressBar) mFooter.findViewById(R.id.list_footer_pb);

		mFooter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				changeFooterByState(State.LOADING);
				mLoadMoreListener.onLoadingMore();
			}
		});

		this.addFooterView(mFooter);
	}


	private void changeFooterByState(State state) {
		if (mState != state) {

			switch (state) {
			case READY_TO_LOAD:
				tvTips.setText(mContext.getResources().getString(
						R.string.to_load_more));
				pbLoading.setVisibility(View.GONE);
				break;

			case LOADING:
				tvTips.setText(mContext.getResources().getString(
						R.string.loading_more));
				pbLoading.setVisibility(View.VISIBLE);
				break;
			}
			mState = state;
		}
	}

	/**
	 * 需要在外部调用该API
	 */
	public void onLoadingComplete() {
		changeFooterByState(State.READY_TO_LOAD);
	}

	/**
	 * 加载更多 回调接口
	 * 
	 * @author yetwish
	 */
	public interface FooterLoadingMoreListener {

		void onLoadingMore();

	}

}
