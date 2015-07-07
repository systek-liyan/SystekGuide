package com.app.guide.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.app.guide.R;

/**
 * gridView adapter 
 * @author yetwish
 */
public class GridAdapter extends BaseAdapter{

	private Context mContext;
	private List<String> items;
	private GridItemClickListener itemListener;
	public GridAdapter(Context context,List<String> items,GridItemClickListener listener){
		this.mContext = context;
		this.items = items;
		this.itemListener = listener;
		
	}
	
	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public String getItem(int index) {
		return items.get(index);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int position, View converView, ViewGroup root) {
		Button btnItem;
		if(converView == null){
			converView = LayoutInflater.from(mContext).inflate(R.layout.item_label_grid_view, root,false);
			btnItem = (Button) converView.findViewById(R.id.item_label_grid_btn);
			converView.setTag(btnItem);
		}else{
			btnItem = (Button)converView.getTag();
		}
		btnItem.setText(getItem(position));
		btnItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Button button = (Button)view;
				if(itemListener!=null){
					//响应点击事件，并将子控件(button)的text 传递出去
					itemListener.onClick(button);
				}
			}
		});
		return converView;
	}
	
	
	/**
	 * 更新数据
	 * @param items
	 */
	public void refreshData(List<String> items){
		this.items = items;
		notifyDataSetChanged();
	}

	/**
	 * 回调接口，用来响应点击事件，并将子控件的参数传递给父控件
	 * @author yetwish
	 */
	public interface GridItemClickListener{
		public void onClick(Button btnItem);
	}
}
