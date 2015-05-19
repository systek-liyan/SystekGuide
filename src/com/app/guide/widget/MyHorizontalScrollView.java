package com.app.guide.widget;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.app.guide.R;
import com.app.guide.adapter.HorizontalScrollViewAdapter;

/**
 * Created by yetwish on 2015-05-16
 */

public class MyHorizontalScrollView extends HorizontalScrollView implements
		View.OnClickListener {

	/**
	 * 屏幕宽度
	 */
	private int mScreenWidth;

	private int mChildWidth;

	private int mChildHeight;

	/**
	 * horizontalScrollView 下的 linearLayout
	 */
	private LinearLayout mContainer;

	/**
	 * 每屏最多显示的View的个数
	 */
	private int mCountOneScreen;

	/**
	 * adapter
	 */
	private HorizontalScrollViewAdapter mAdapter;

	/**
	 * 保存View与位置的键值对
	 */
	private Map<View, Integer> mViewPos = new HashMap<View, Integer>();

	/**
	 * 记录当前选中view 的position
	 */
	private int mCurrentPos;

	/**
	 * 当前第一张图片的下标
	 */
	private int mFirstIndex;

	private CurrentImageChangedListener mItemChangedListener;

	private OnItemClickListener mItemClickListener;

	private DisplayMetrics outMetrics;

	private int mLastItemCounts;

	private int mCurrentClickedItem;
	/**
	 * 标识是否加载了空白view
	 */
	private boolean isLoaded = false;

	public void setCurrentImageChangedListener(
			CurrentImageChangedListener listener) {
		mItemChangedListener = listener;
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		mItemClickListener = listener;
	}

	public MyHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 获取屏幕宽度
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		mScreenWidth = outMetrics.widthPixels;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// 获取linearLayout
		mContainer = (LinearLayout) getChildAt(0);
	}

	/**
	 * 初始化数据和adapter
	 */
	public void initData(HorizontalScrollViewAdapter adapter) {
		mAdapter = adapter;
		mContainer = (LinearLayout) getChildAt(0);
		// 获取适配器的第一个view
		View view = mAdapter.getView(0, null, mContainer);
		mContainer.addView(view);
		// 计算当前view的宽高
		if (mChildWidth == 0 && mChildHeight == 0) {
			mChildWidth = (int) getResources().getDimension(
					R.dimen.gallery_width) + 1;
			// 计算每次加载多少个view
			mCountOneScreen = mScreenWidth / mChildWidth + 2;
			mLastItemCounts = mCountOneScreen;
			Log.w("TAG", "countOneScreen1 " + mCountOneScreen + ","
					+ mChildWidth);
			// 如果adapter中view 的总数比能一屏能加载的少，则把最多能加载数置为view总数
			if (mCountOneScreen > mAdapter.getCount())
				mCountOneScreen = mAdapter.getCount();
			Log.w("TAG", "countOneScreen2 " + mCountOneScreen);
		}
		// 初始化第一屏幕
		initFirstScreenChild(mCountOneScreen);

	}

	/**
	 * 加载第一屏的view
	 */
	public void initFirstScreenChild(int mCountOneScreen) {
		mContainer = (LinearLayout) getChildAt(0);
		mContainer.removeAllViews();
		mViewPos.clear();
		for (int i = 0; i < mCountOneScreen; i++) {
			View view = mAdapter.getView(i, null, mContainer);
			view.setOnClickListener(this);
			if(i == 0) view.setAlpha(1f);
			else view.setAlpha(0.5f);
			mContainer.addView(view);
			mViewPos.put(view, i);
			mCurrentPos = i;
		}
		if (mItemChangedListener != null) {
			notifyCurrentItemChanged();
		}

	}

	private void notifyCurrentItemChanged() {
		for (int i = 0; i < mContainer.getChildCount(); i++) {
			mContainer.getChildAt(i).setAlpha(0.5f);
		}
		Log.w("TAG", "mFirstIndex:" + mFirstIndex + ", current: " + mCurrentPos
				+ "mlast" + mLastItemCounts);
		mItemChangedListener.onCurrentImgChanged(mFirstIndex,
				mContainer.getChildAt(0));
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_MOVE:
			int scrollX = getScrollX();
			// 如果当前scrollX 为view的宽度，加载下一张，移除第一张
			if (scrollX >= mChildWidth) {
				loadNextImage();
			}
			// 如果scrollX = 0 ,加载上一张 移除最后一张
			if (scrollX == 0) {
				loadPreImage();
			}
			break;
		}
		return super.onTouchEvent(ev);
	}

	/**
	 * 加载下一张image 并移除第一张
	 */
	private void loadNextImage() {
		View view;
		if (isLoaded) {
			return;
		}
		// 后面没有了
		if (mFirstIndex >= mAdapter.getCount() - mCountOneScreen
				&& mLastItemCounts > 1) {
			view = new ImageView(getContext());
			view.setLayoutParams(new ViewGroup.LayoutParams(mChildWidth,
					mChildWidth));
			mLastItemCounts--;
			if (mLastItemCounts == 1)
				isLoaded = true;
		} else {
			// 获取下一张图片
			view = mAdapter.getView(++mCurrentPos, null, mContainer);
			view.setOnClickListener(this);
		}
		// 移除第一张图片，并将水平滚动位置置0
		scrollTo(0, 0);
		mViewPos.remove(mContainer.getChildAt(0));
		mContainer.removeViewAt(0);
		mContainer.addView(view);
		mViewPos.put(view, mCurrentPos);
		if(mCurrentPos != mCurrentClickedItem)
			view.setAlpha(0.5f);
		else view.setAlpha(1f);
		// 更新第一张图片的下标
		if (mFirstIndex < mAdapter.getCount() - 1)
			mFirstIndex++;
		if (mItemChangedListener != null) {
			notifyCurrentItemChanged();
		}
	}

	/**
	 * 加载上一张，并移除最后一张
	 */
	private void loadPreImage() {
		// 前面没有了
		if (mFirstIndex == 0) {
			return;
		}
		// 销毁
		else if (mLastItemCounts < mCountOneScreen) {
			mLastItemCounts++;
			isLoaded = false;
		}
		// 获取当前应该显示为第一张图片的下标
		int index = mFirstIndex - 1;
		if (index >= 0) {
			// 移除最后一张
			int oldViewPos = mContainer.getChildCount() - 1;
			mViewPos.remove(mContainer.getChildAt(oldViewPos));
			mContainer.removeViewAt(oldViewPos);
			View view = mAdapter.getView(index, null, mContainer);
			if(index != mCurrentClickedItem)
				view.setAlpha(0.5f);
			else view.setAlpha(1f);
			view.setOnClickListener(this);
			mContainer.addView(view, 0);
			mViewPos.put(view, index);
			// 水平滚动位置向左移动view的宽度个像素
			scrollTo(mChildWidth, 0);
			if (mFirstIndex <= mAdapter.getCount() - mCountOneScreen)
				mCurrentPos--;
			mFirstIndex--;
			if (mItemChangedListener != null) {
				notifyCurrentItemChanged();
			}
		}

	}

	@Override
	public void onClick(View view) {
		if (mItemClickListener != null) {
			for (int i = 0; i < mContainer.getChildCount(); i++) {
				mContainer.getChildAt(i).setAlpha(0.5f);
			}
			view.setAlpha(1f);
		}
		mCurrentClickedItem = mViewPos.get(view);
		mItemClickListener.onItemClick(view, mViewPos.get(view));
		// 点击时 滚动到相应位置 需要load
		int changePosition = mViewPos.get(view) - mFirstIndex;
		for (int i = 0; i < changePosition; i++) {
			loadNextImage();
		}
		if (changePosition > 2)
			smoothScrollBy(mChildWidth * (changePosition - 3), 0);
		else if (changePosition > 1) {
			smoothScrollBy(mChildWidth * (changePosition - 2), 0);
		} else {
			smoothScrollBy(mChildWidth * (changePosition - 1), 0);
		}
		Log.w("TAG", "first" + mFirstIndex + "onclick " + mViewPos.get(view)
				+ "change:" + changePosition + ", by" + mChildWidth
				* (changePosition));

	}

	public interface CurrentImageChangedListener {
		void onCurrentImgChanged(int position, View viewIndicator);
	}

	public interface OnItemClickListener {
		void onItemClick(View view, int position);
	}
	
	
	/** 
	 * TODO pause时是否需要进行回收？如果pause进行回收，则resume时要重新加载 
	 * 回收bitmap
	 */
	public void onDestroy(){
		if(!mViewPos.isEmpty()){
			for(View view: mViewPos.keySet()){
				ImageView iv = (ImageView) view;
				//获取iv中的bitmap
				iv.setDrawingCacheEnabled(true);
				if (iv.getDrawingCache() != null) {
					iv.getDrawingCache().recycle();
				}
				iv.setDrawingCacheEnabled(false);
			}
		}
	}
	
}
