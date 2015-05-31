package com.app.guide.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.app.guide.R;
import com.app.guide.utils.BitmapUtils;

/**
 * Created by yetwish on 2015-05-11
 */

public class ViewHolder {

	private SparseArray<View> mViews;
	private View mConvertView;
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
	 * get view
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
	 * set text
	 */
	public ViewHolder setText(int viewId, String text) {
		TextView tv = getView(viewId);
		tv.setText(text);
		return this;
	}

	/**
	 * set image res
	 */
	public ViewHolder setImageResource(int viewId, int resId) {
		NetworkImageView iv = getView(viewId);
		iv.setImageResource(resId);
		return this;
	}

	/**
	 * set image bitmap
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
