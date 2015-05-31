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

	private boolean isShowInCenter = false;

	public void setShowInCenter() {
		this.isShowInCenter = true;
	}

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
	private int mLastIndex;

	/**
	 * 当前第一张图片的下标
	 */
	private int mFirstIndex;

	private CurrentImageChangedListener mItemChangedListener;

	private OnItemClickListener mItemClickListener;

	private DisplayMetrics outMetrics;

	private int mAdditionalCount;

	private int mCurrentSelectedItem;
	/**
	 * 标识是否加载了空白view
	 */
	private boolean isLoaded = false;

	private boolean isFirst = false;

	/**
	 * 加载更多回调接口 实例
	 */
	private OnLoadingMoreListener mLoadingMoreListener;

	public void seOnLoadingMoreListener(OnLoadingMoreListener listener) {
		mLoadingMoreListener = listener;
	}

	public void setCurrentImageChangedListener(
			CurrentImageChangedListener listener) {
		mItemChangedListener = listener;
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		mItemClickListener = listener;
	}

	/**
	 * 获取当前选中的图片的坐标
	 * 
	 * @return
	 */
	public int getCurrentSelectedIndex() {
		return mCurrentSelectedItem;
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

	/**
	 * 设置当前选中item
	 * 
	 * @param index
	 */
	public void setCurrentSelectedItem(int index) {
		if (index < 0)
			index = 0;
		if (index > mAdapter.getCount() - 1)
			index = mAdapter.getCount() - 1;
		// 如何获得view
		for (View view : mViewPos.keySet()) {
			if (mViewPos.get(view).equals(index)) {
				onClick(view);
				break;
			}
		}
	}

	/**
	 * 设置到第一项
	 */
	public void setBackToBegin() {
		mFirstIndex = mCurrentSelectedItem = 0;
		mLastIndex = mFirstIndex + mCountOneScreen;
		initFirstScreenChild(mCountOneScreen);
	}

	/**
	 * 初始化数据和adapter
	 */
	public void initData(HorizontalScrollViewAdapter adapter) {
		mFirstIndex = mCurrentSelectedItem = 0;
		mAdapter = adapter;
		mContainer = (LinearLayout) getChildAt(0);
		// 获取适配器的第一个view
		View view = mAdapter.getView(0, null, mContainer);
		mContainer.addView(view);
		// 计算当前view的宽高
		mChildWidth = (int) getResources().getDimension(R.dimen.gallery_width) + 1;
		// 计算每次加载多少个view
		mCountOneScreen = mScreenWidth / mChildWidth + 2;
		mAdditionalCount = mCountOneScreen - 1;
		if (isShowInCenter) {
			mCurrentSelectedItem = 1;
		}
		// 如果adapter中view 的总数比能一屏能加载的少，则把最多能加载数置为view总数
		int count = mCountOneScreen;
		if (mAdapter.getCount() < mCountOneScreen) {
			count = mAdapter.getCount();
			isFirst = true;
			mCountOneScreen = mAdapter.getCount();
		}
		// 初始化第一屏幕
		initFirstScreenChild(count);
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
			if (i == mCurrentSelectedItem)
				view.setAlpha(1f);
			else
				view.setAlpha(0.5f);
			mContainer.addView(view);
			mViewPos.put(view, i);
			mLastIndex = i;
		}
		if (mItemChangedListener != null) {
			notifyCurrentItemChanged();
		}

	}

	/**
	 * 更新坐标
	 * 
	 * @param count
	 */
	private void updateIndex(int count) {
		mCurrentSelectedItem += count;
		mFirstIndex = mFirstIndex + count;
		mLastIndex += count;
		for (View view : mViewPos.keySet()) {
			mViewPos.put(view, mViewPos.get(view) + count);
		}
	}

	/**
	 * 通知当前item改变
	 */
	private void notifyCurrentItemChanged() {
		for (int i = 0; i < mContainer.getChildCount(); i++) {
			mContainer.getChildAt(i).setAlpha(0.5f);
		}
		Log.w("TAG", "mFirstIndex:" + mFirstIndex + ", last: " + mLastIndex
				+ "additional " + mAdditionalCount);
		mCurrentSelectedItem = mFirstIndex;
		mItemChangedListener.onCurrentImgChanged(mFirstIndex,
				mContainer.getChildAt(0));
	}

	private int downX;
	private int upX;

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_MOVE:
			int scrollX = getScrollX();
			if (isShowInCenter
					|| mAdapter.getCount() < (mScreenWidth / mChildWidth + 2)) {
				if (isFirst) {
					downX = (int) ev.getX();
					isFirst = false;
				}
			} else {
				// 如果当前scrollX 为view的宽度，加载下一张，移除第一张
				if (scrollX >= mChildWidth) {
					loadNextImage();
				}
				// 如果scrollX = 0 ,加载上一张 移除最后一张
				if (scrollX == 0) {
					loadPreImage();
				}
			}
			break;

		case MotionEvent.ACTION_UP:
			if (downX == 0)
				break;
			upX = (int) ev.getX();
			if (downX - upX >= mChildWidth / 2) {
				loadNextImage();
			}
			if (upX - downX > mChildWidth) {
				loadPreImage();
			}
			isFirst = true;
			downX = 0;
			upX = 0;
			Log.w("TAG", upX + "," + downX);
			Log.w("TAG", "first :" + mFirstIndex + " last:" + mLastIndex
					+ " current :" + mCurrentSelectedItem);
			break;
		}
		return super.onTouchEvent(ev);
	}

	/**
	 * 加载下一张image 并移除第一张
	 */
	private void loadNextImage() {
		View view = null;
		if (isLoaded) {
			return;
		}
		// 加载到最后一屏
		if (mFirstIndex >= mAdapter.getCount() - mCountOneScreen
				&& mAdditionalCount > 0) {
			if (mLoadingMoreListener != null) {
				int count = mLoadingMoreListener.onRightLoadingMore();
				if (count > 0) {
					// 获取下一张图片
					view = mAdapter.getView(++mLastIndex, null, mContainer);
					view.setOnClickListener(this);
				} else {
					// 后面没有了
				}
			} else {
				view = new ImageView(getContext());
				view.setLayoutParams(new ViewGroup.LayoutParams(
						(int) getContext().getResources().getDimension(
								R.dimen.gallery_width), (int) getContext()
								.getResources().getDimension(
										R.dimen.gallery_height)));
				mAdditionalCount--;
				if (mAdditionalCount == 0)
					isLoaded = true;
			}
		} else {
			// 获取下一张图片
			view = mAdapter.getView(++mLastIndex, null, mContainer);
			view.setOnClickListener(this);
		}
		if (view == null)
			return;
		// 移除第一张图片，并将水平滚动位置置0
		scrollTo(0, 0);
		mViewPos.remove(mContainer.getChildAt(0));
		mContainer.removeViewAt(0);

		mContainer.addView(view);
		mViewPos.put(view, mLastIndex);
		if (mLastIndex != mCurrentSelectedItem)
			view.setAlpha(0.5f);
		else
			view.setAlpha(1f);
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
			if (mLoadingMoreListener != null) {
				// 加载更多
				int count = mLoadingMoreListener.onLeftLoadingMore();
				if (count > 0) {
					updateIndex(count);
				}
			} else
				return;
		}
		// 获取当前应该显示为第一张图片的下标
		int index = 0;
		// 解决当图片总数< 一屏加载数 时bug
		if (mAdapter.getCount() < mScreenWidth / mChildWidth + 2 && isLoaded)
			index = mFirstIndex;
		else
			index = mFirstIndex - 1;
		if (index >= 0) {
			// 移除最后一张
			int oldViewPos = mContainer.getChildCount() - 1;
			mViewPos.remove(mContainer.getChildAt(oldViewPos));
			mContainer.removeViewAt(oldViewPos);
			View view = mAdapter.getView(index, null, mContainer);
			if (index != mCurrentSelectedItem)
				view.setAlpha(0.5f);
			else
				view.setAlpha(1f);
			view.setOnClickListener(this);
			mContainer.addView(view, 0);
			mViewPos.put(view, index);
			// 水平滚动位置向左移动view的宽度个像素
			scrollTo(mChildWidth, 0);
			if (mFirstIndex <= mAdapter.getCount() - mCountOneScreen)
				mLastIndex--;
			// if(!(mAdapter.getCount() < mScreenWidth/ mChildWidth +2 &&
			// mFirstIndex == mAdapter.getCount() -1))
			if (!(mAdapter.getCount() < mScreenWidth / mChildWidth + 2 && isLoaded))
				mFirstIndex--;
			if (mItemChangedListener != null) {
				notifyCurrentItemChanged();
			}
		}
		if (mAdditionalCount < mCountOneScreen) {
			mAdditionalCount++;
			isLoaded = false;
		}
	}

	@Override
	public void onClick(View view) {
		Log.w("TAG", "onClick");
		if (mItemClickListener != null) {
			for (int i = 0; i < mContainer.getChildCount(); i++) {
				mContainer.getChildAt(i).setAlpha(0.5f);
			}
			view.setAlpha(1f);
		}
		int itemCount = mViewPos.get(view) - mCurrentSelectedItem;
		mCurrentSelectedItem = mViewPos.get(view);
		mItemClickListener.onItemClick(view, mViewPos.get(view));
		// 获取选中的是在屏幕第几个
		if (isShowInCenter) {
//			// 只有三个
//			int index = getIndexOfViews(view);
//			switch (index) {
//			case 0:
//				loadPreImage();
//				break;
//			case 1:
//				// 已在中间不做处理
//				break;
//			case 2:
//				loadNextImage();
//				break;
//			}
//			smoothScrollTo(0, 0);
		} else {
			for (int i = 0; i < itemCount; i++) {
				loadNextImage();
			}
			smoothScrollTo(0, 0);

		}
	}

	private int getIndexOfViews(View view) {
		int x = (int) view.getX();
		int index = 0;
		Log.w("TAG", "X: " + x);
		switch (x) {
		case 0:
			index = 0;
			break;
		case 300:
			index = 1;
			break;
		case 600:
			index = 2;
			break;
		}
		return index;
	}

	public interface CurrentImageChangedListener {
		void onCurrentImgChanged(int position, View viewIndicator);
	}

	public interface OnItemClickListener {
		void onItemClick(View view, int position);
	}

	/**
	 * 加载更多回调接口
	 * 
	 * @author yetwish
	 */
	public interface OnLoadingMoreListener {
		int onLeftLoadingMore();

		int onRightLoadingMore();
	}

	/**
	 * 回收bitmap
	 */
	public void destroy() {
		if (!mViewPos.isEmpty()) {
			for (View view : mViewPos.keySet()) {
				ImageView iv = (ImageView) view;
				// 获取iv中的bitmap
				iv.setDrawingCacheEnabled(true);
				if (iv.getDrawingCache() != null) {
					iv.getDrawingCache().recycle();
				}
				iv.setDrawingCacheEnabled(false);
			}
		}
	}

}
