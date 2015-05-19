package com.app.guide.adapter;

import java.util.List;

import com.app.guide.R;
import com.app.guide.bean.MapExhibitBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MapExhibitAdapter extends BaseAdapter {

	private List<MapExhibitBean> data;
	private Context mContext;

	public MapExhibitAdapter(Context context, List<MapExhibitBean> data) {
		super();
		this.mContext = context;
		this.data = data;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public MapExhibitBean getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_map_exhibit, null);
			holder.name = (TextView) convertView
					.findViewById(R.id.item_map_exhibit_name);
			holder.address = (TextView) convertView
					.findViewById(R.id.item_map_exhibit_address);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		MapExhibitBean bean = data.get(position);
		holder.name.setText(bean.getName());
		holder.address.setText(bean.getAddress());
		return convertView;
	}

	private class ViewHolder {
		TextView name;
		TextView address;
	}

}
