package com.app.guide.adapter;

import java.util.List;

import com.app.guide.R;
import com.app.guide.utils.ImageLoader;

import android.content.Context;
import android.content.res.Resources;
import android.widget.ImageView;

public class HorizontalScrollViewAdapter extends CommonAdapter<String>{

	private ImageLoader mImageLoader;
	private Resources mRes;
	
	public HorizontalScrollViewAdapter(Context context, List<String> data,
			int layoutId) {
		super(context, data, layoutId);
		mRes = context.getResources();
		mImageLoader = ImageLoader.getDefaultInstance(); 
	}

	
	//如何回收bitmap
	@Override
	public void convert(ViewHolder holder, int position) {
		
		ImageView imageView = (ImageView)holder.getView(R.id.item_gallery_iv);
		imageView.setImageResource(R.drawable.icon);
		mImageLoader.loadImage(mData.get(position), imageView, true, 
				(int)mRes.getDimension(R.dimen.gallery_width), (int)mRes.getDimension(R.dimen.gallery_height));
		
	}
	
	//TODO 滑动时不加载， 静止时加载
	

}
