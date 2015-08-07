package com.app.guide.widget;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.app.guide.R;
import com.app.guide.adapter.GalleryAdapter;
import com.app.guide.ui.FollowGuideFragment.LoadCallBack;
import com.app.guide.utils.ScreenUtils;

/**
 * Created by yetwish on 2015-05-16
 */

public class GalleryView extends HorizontalScrollView implements
		View.OnClickListener {

	private static final String TAG = GalleryView.class.getSimpleName();

	// /**
	// * 屏幕宽度
	// */
	// private int mScreenWidth;

	/**
	 * 图片宽度
	 */
	private int mChildWidth;

	/**
	 * 图片总个数
	 */
	private int mSize;

	/**
	 * 记录是否可以向右加载更多
	 */
	private boolean isRightNoMore = false;

	/**
	 * 记录是否可以向左加载更多
	 */
	private boolean isLeftNoMore = false;

	/**
	 * 判断是否已加载到最后一张
	 */
	private boolean isLast = true;

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
	private GalleryAdapter mAdapter;

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

	/**
	 * 图片点击事件监听者
	 */
	private OnItemClickListener mItemClickListener;

	/**
	 * 当前被选中的图片下标
	 */
	private int mCurrentSelectedItem;

	/**
	 * 判断是否是由歌词联动切换图片
	 */
	private boolean isSetByLyric = false;

	/**
	 * 手指按下时的横坐标
	 */
	private int downX;

	/**
	 * 手指离开时的横坐标
	 */
	private int upX;

	/**
	 * 用于获取onTouchEvent#MotionEvent.ACTION_MOVE的第一次X ——>downX
	 */
	private boolean slidable;

	/**
	 * 判断点击监听时是否需要调用loadNext()、loadPre()
	 */
	private boolean autoLoad = true;

	/**
	 * 设置点击监听时是否需要调用loadNext()、loadPre()
	 * 
	 * @param autoLoad
	 */
	public void setAutoLoad(boolean autoLoad) {
		this.autoLoad = autoLoad;
	}

	/**
	 * 加载更多回调接口 实例
	 */
	private OnLoadingMoreListener mLoadingMoreListener;

	/**
	 * 设置加载更多监听者
	 * 
	 * @param listener
	 */
	public void seOnLoadingMoreListener(OnLoadingMoreListener listener) {
		mLoadingMoreListener = listener;
	}

	/**
	 * 设置图片点击事件监听者
	 * 
	 * @param listener
	 */
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

	public GalleryView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * 设置当前选中item
	 * @param isByLyric 是否是由歌词联动切换图片
	 * @param index
	 */
	public void setCurrentSelectedItem(boolean isByLyric, int index) {
		this.isSetByLyric = isByLyric;
		if (index < 0)
			return;
		if (index > mAdapter.getCount() - 1)
			return;
		// 如何获得view
		for (View view : mViewPos.keySet()) {
			if (mViewPos.get(view).equals(index)) {
				onClick(view);
				break;
			}
		}
		this.isSetByLyric = false;
	}

	/**
	 * 初始化数据和adapter
	 */
	public void initData(GalleryAdapter adapter) {
		mFirstIndex = mCurrentSelectedItem = 0;
		isRightNoMore = false;
		isLeftNoMore = false;
		isLast = true;
		slidable = false;
		mAdapter = adapter;
		mSize = adapter.getCount();
		mContainer = (LinearLayout) getChildAt(0);
		// 获取适配器的第一个view
		View view = mAdapter.getView(0, null, mContainer);
		mContainer.addView(view);
		// 计算当前view的宽高
		mChildWidth = (int) getResources().getDimension(R.dimen.gallery_width) + 1;
		// 计算每次加载多少个view
		mCountOneScreen = ScreenUtils.getPicCount(getContext());
		// 如果adapter中view 的总数比能一屏能加载的少，则把最多能加载数置为view总数
		int count = mCountOneScreen;
		if (mAdapter.getCount() < mCountOneScreen) {
			count = mAdapter.getCount();
			mCountOneScreen = mAdapter.getCount();
			slidable = true;
		}
		// 初始化第一屏幕
		initFirstScreenChild(count);
	}

	/**
	 * 加载第一屏的view
	 */
	private void initFirstScreenChild(int countOneScreen) {
		mContainer = (LinearLayout) getChildAt(0);
		mContainer.removeAllViews();
		mViewPos.clear();
		for (int i = 0; i < countOneScreen; i++) {
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

	}

	/**
	 * 更新mViewPos中各个view与坐标的关系 ，因为从首部插入 所以都需要改变
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

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_MOVE:
			getParent().requestDisallowInterceptTouchEvent(true);
			int scrollX = getScrollX();
			Log.w(TAG,
					mAdapter.getCount() + " ,"
							+ ScreenUtils.getPicCount(getContext()));
			if (slidable) {
				downX = (int) ev.getX();
				Log.w(TAG, downX + " x");
				slidable = false;

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
			Log.w(TAG, (downX - upX) + "," + mChildWidth / 2);
			if (downX - upX >= mChildWidth / 2) {
				Log.w(TAG, "load next");
				loadNextImage();
			}
			if (upX - downX > mChildWidth) {
				loadPreImage();
				Log.w(TAG, "load pre");
			}
			downX = 0;
			upX = 0;
			getParent().requestDisallowInterceptTouchEvent(false);
			slidable = true;
			break;
		}
		return super.onTouchEvent(ev);
	}

	private View view;

	/**
	 * 加载下一张image 并移除第一张
	 */
	private void loadNextImage() {
		view = null;
		// Log.w(TAG, "Load next, " + mLastIndex + "," + mSize + isRightNoMore);
		if (mLastIndex == mSize - 1 && mLoadingMoreListener != null) {
			if (isRightNoMore)
				return; // 后面没有了，不再加载
			mLoadingMoreListener.onRightLoadingMore(new LoadCallBack() {

				@Override
				public void onLoadSuccess(int count) {
					// TODO Auto-generated method stub
					// 获取下一张图片
					view = mAdapter.getView(++mLastIndex, null, mContainer);
					view.setOnClickListener(GalleryView.this);
					mSize += count;
				}

				@Override
				public void onLoadFailed() {
					Toast.makeText(getContext(), "后面没有了", Toast.LENGTH_SHORT)
							.show();
					isRightNoMore = true;
				}
			});
		} else {
			// 获取下一张图片 循环加载
			view = mAdapter.getView(++mLastIndex % mSize, null, mContainer);
			if (mLastIndex - ScreenUtils.getScreenWidth(getContext())
					/ mChildWidth >= mSize
					&& isLast) {
				Toast.makeText(getContext(), "已加载到最后一张", Toast.LENGTH_SHORT)
						.show();
				isLast = false;
			}
			view.setOnClickListener(this);
		}
		if (view == null)
			return;
		// 移除第一张图片，并将水平滚动位置置0
		scrollTo(0, 0);
		mViewPos.remove(mContainer.getChildAt(0));
		mContainer.removeViewAt(0);
		mContainer.addView(view);
		mViewPos.put(view, mLastIndex % mSize);
		if (mLastIndex % mSize != mCurrentSelectedItem)
			view.setAlpha(0.5f);
		else
			view.setAlpha(1f);
		mFirstIndex++;
		mFirstIndex %= mSize;
	}

	/**
	 * 加载上一张，并移除最后一张
	 */
	private void loadPreImage() {
		// Log.w("TAG", "f:" + mFirstIndex + ",l:" + mLastIndex + ",c:"
		// + mCurrentSelectedItem);
		// 前面没有了
		if (isLeftNoMore)
			return;
		if (mFirstIndex == 0 && mLoadingMoreListener != null) {
			// 加载更多
			mLoadingMoreListener.onLeftLoadingMore(new LoadCallBack() {

				@Override
				public void onLoadSuccess(int count) {
					updateIndex(count);
					mSize += count;
				}

				@Override
				public void onLoadFailed() {
					Toast.makeText(getContext(), "前面没有了", Toast.LENGTH_SHORT)
							.show();
					isLeftNoMore = true;
				}
			});
		}
		// 获取当前应该显示为第一张图片的下标
		int index = 0;
		// 解决当图片总数< 一屏加载数 时bug
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
			mLastIndex--;
			mFirstIndex--;
		}
	}

	@Override
	public void onClick(View view) {
		Log.w(TAG, "onClick");
		if (mItemClickListener == null)
			return;
		for (int i = 0; i < mContainer.getChildCount(); i++) {
			mContainer.getChildAt(i).setAlpha(0.5f);
		}
		view.setAlpha(1f);
		mCurrentSelectedItem = mViewPos.get(view);
		mItemClickListener
				.onItemClick(view, mCurrentSelectedItem, isSetByLyric);
		if (!autoLoad) {
			// 将所选项滑动到中间
			smoothScrollTo(mChildWidth * (mCurrentSelectedItem - 1), 0);
		} else {
			// 获取选中的是在屏幕第几个
			int index = getIndexOfViews(view);
			for (int i = 0; i < index; i++) {
				loadNextImage();
			}
			smoothScrollTo(0, 0);
		}
	}

	/**
	 * 获取view当前在屏幕中的位置
	 * 
	 * @param view
	 * @return
	 */
	private int getIndexOfViews(View view) {
		int x = (int) view.getX();
		return x / 300;
	}

	/**
	 * 图片点击监听接口
	 * 
	 * @author yetwish
	 */
	public interface OnItemClickListener {
		void onItemClick(View view, int position, boolean isByLyric);
	}

	/**
	 * 加载更多回调接口
	 * 
	 * @author yetwish
	 */
	public interface OnLoadingMoreListener {

		void onLeftLoadingMore(LoadCallBack callBack);

		void onRightLoadingMore(LoadCallBack callBack);
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