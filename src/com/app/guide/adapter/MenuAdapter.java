package com.app.guide.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.app.guide.R;

public class MenuAdapter extends BaseAdapter {
	private Context mContext;

	private String[] titles = { "城市选择", "下载中心", "设置", "更多", "返回" };// 返回是否需要
	private int[] iconResources = { R.drawable.sliding_menu_city,
			R.drawable.sliding_menu_download, R.drawable.sliding_menu_setting,
			R.drawable.sliding_menu_more, R.drawable.sliding_menu_back };

	public MenuAdapter(Context context) {
		this.mContext = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return titles.length;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return titles[arg0];
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_menu, null, false);
			holder = new ViewHolder();
			holder.tvTitle = (TextView) convertView
					.findViewById(R.id.item_menu_tv_title);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		Drawable drawable = mContext.getResources().getDrawable(iconResources[position]);
		drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
		//给textview左边添加drawable
		holder.tvTitle.setCompoundDrawables(drawable ,null,null,null);//left, top, right, bottom
		holder.tvTitle.setText(titles[position]);
		return convertView;
	}

	private static class ViewHolder {
		private TextView tvTitle;
	}
}
