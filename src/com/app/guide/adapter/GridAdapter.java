package com.app.guide.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.app.guide.R;

/**
 * gridView adapter 
 * @author yetwish
 */
public class GridAdapter extends CommonAdapter<String>{

	private GridItemClickListener mItemClickListener;
	
	private MyClickListener mClickListener;
	
	
	public GridAdapter(Context context, List<String> data, int layoutId) {
		super(context, data, layoutId);
		mClickListener = new MyClickListener();
	}
	
	public void setItemClickListener(GridItemClickListener listener){
		this.mItemClickListener = listener;
	} 

	/**
	 * 替换整个数组
	 * @param items
	 */
	public void replaceAll(List<String> data){
		this.mData= data;
		notifyDataSetChanged();
	}


	@Override
	public void convert(ViewHolder holder, int position) {
		Button btn = holder.getView(R.id.item_label_grid_btn);
		btn.setText(getItem(position));
		//设置点击监听
		btn.setOnClickListener(mClickListener);
	}
	
	private class MyClickListener implements OnClickListener{

		@Override
		public void onClick(View view) {
			if(mItemClickListener != null){
				mItemClickListener.onClick(view);
			}
		}
		
	} 
	

	/**
	 * 回调接口，用来响应点击事件，客户端实现具体的事件处理
	 * 
	 * @author yetwish
	 */
	public interface GridItemClickListener {
		
		/**
		 * 当标签项被点击时，回调刚方法
		 * @param view
		 */
		public void onClick(View view);
	}

}
