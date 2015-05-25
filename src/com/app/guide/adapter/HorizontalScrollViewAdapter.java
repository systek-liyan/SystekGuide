package com.app.guide.adapter;

import java.util.List;

import android.content.Context;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.app.guide.R;
import com.app.guide.bean.ImageBean;
import com.app.guide.utils.BitmapUtils;

public class HorizontalScrollViewAdapter extends CommonAdapter<ImageBean> {

	private ImageLoader mImageLoader;

	public HorizontalScrollViewAdapter(Context context, List<ImageBean> data,
			int layoutId) {
		super(context, data, layoutId);
		mImageLoader = BitmapUtils.getImageLoader(context);
	}

	// 如何回收bitmap
	@Override
	public void convert(ViewHolder holder, int position) {

		NetworkImageView imageView = (NetworkImageView) holder
				.getView(R.id.item_gallery_iv);
		imageView.setDefaultImageResId(R.drawable.ic_launcher);
		imageView.setErrorImageResId(R.drawable.ic_launcher);
		imageView.setImageUrl(mData.get(position).getImgUrl(), mImageLoader);

	}

}
