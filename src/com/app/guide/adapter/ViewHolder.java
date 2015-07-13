package com.app.guide.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.app.guide.R;
import com.app.guide.utils.BitmapUtils;

/**
 * 高复用ViewHolder。用以简化代码。<br>
 * 可根据项目后期需要给该类添加更多方法，用以给ViewHolder设置组件值。<br>
 * Created by yetwish on 2015-05-11
 */

public class ViewHolder {

	/**
	 * 用以存储holder中的组件与id的对应关系，相当与hashMap的优化
	 */
	private SparseArray<View> mViews;
	
	/**
	 * 列表项的view，用以复用
	 */
	private View mConvertView;
	
	/**
	 * 表示某一项在列表中所在的位置，因为使用了复用，所以滑动时其实是显示了复用的view，所以位置需实时更新
	 */
	@SuppressWarnings("unused")
	private int mPosition;

	/**
	 * init holder
	 */
	public ViewHolder(Context context, int layoutId, ViewGroup parent,
			int position) {
		mConvertView = LayoutInflater.from(context).inflate(layoutId, parent,
				false);
		mViews = new SparseArray<View>();
		mPosition = position;
		mConvertView.setTag(this);
	}

	/**
	 * 获取viewHolder
	 */
	public static ViewHolder getHolder(Context context, View convertView,
			int layoutId, ViewGroup parent, int position) {
		if (convertView == null) {
			return new ViewHolder(context, layoutId, parent, position);
		} else {
			ViewHolder holder = (ViewHolder) convertView.getTag();
			holder.mPosition = position;
			return holder;
		}
	}

	public View getConvertView() {
		return mConvertView;
	}

	/**
	 * 获取某一个组件
	 */
	@SuppressWarnings("unchecked")
	public <T extends View> T getView(int viewId) {
		View view = mViews.get(viewId);
		if (view == null) {
			view = mConvertView.findViewById(viewId);
			mViews.put(viewId, view);
		}
		return (T) view;
	}

	/**
	 * 给TextView设置文本，返回viewHolder对象，用以链式编程
	 */
	public ViewHolder setTvText(int viewId, String text) {
		TextView tv = getView(viewId);
		tv.setText(text);
		return this;
	}
	
	/**
	 * 给Button设置文本，返回viewHolder对象，用以链式编程
	 */
	public ViewHolder setBtnText(int viewId,String text){
		Button btn = getView(viewId);
		btn.setText(text);
		return this;
	}

	/**
	 * 给ImageView设置图片资源Resource
	 */
	public ViewHolder setImageResource(int viewId, int resId) {
		NetworkImageView iv = getView(viewId);
		iv.setImageResource(resId);
		return this;
	}

	/**
	 * 给ImageView设置图片bitmap
	 */
	public ViewHolder setImageBitmap(int viewId, Context context, String url) {
		NetworkImageView imageView = getView(viewId);
		imageView.setDefaultImageResId(R.drawable.ic_launcher);
		imageView.setErrorImageResId(R.drawable.ic_launcher);
		ImageLoader imageLoader = BitmapUtils.getImageLoader(context);
		imageView.setImageUrl(url, imageLoader);
		return this;
	}
}
