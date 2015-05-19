package com.app.guide.adapter;

import java.util.List;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.app.guide.R;
import com.app.guide.utils.BitmapUtils;

import android.content.Context;
import android.content.res.Resources;

public class HorizontalScrollViewAdapter extends CommonAdapter<String> {

	private ImageLoader mImageLoader;
	private Resources mRes;

	public HorizontalScrollViewAdapter(Context context, List<String> data,
			int layoutId) {
		super(context, data, layoutId);
		mRes = context.getResources();
		mImageLoader = BitmapUtils.getImageLoader(context);
	}

	// 如何回收bitmap
	@Override
	public void convert(ViewHolder holder, int position) {

		NetworkImageView imageView = (NetworkImageView) holder
				.getView(R.id.item_gallery_iv);
		imageView.setDefaultImageResId(R.drawable.ic_launcher);
		imageView.setErrorImageResId(R.drawable.ic_launcher);
		imageView.setImageUrl(mData.get(position), mImageLoader);

	}

}
