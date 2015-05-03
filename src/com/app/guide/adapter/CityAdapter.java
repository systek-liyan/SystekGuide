package com.app.guide.adapter;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.app.guide.R;
import com.app.guide.bean.CityModel;

public class CityAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<CityModel> list;
	private HashMap<String, Integer> alphaIndexer;
	private String[] sections;

	public CityAdapter(Context context, List<CityModel> list) {

		this.inflater = LayoutInflater.from(context);
		this.list = list;
		alphaIndexer = new HashMap<String, Integer>();
		sections = new String[list.size()];

		for (int i = 0; i < list.size(); i++) {
			String currentStr = list.get(i).getNameSort();
			String previewStr = (i - 1) >= 0 ? list.get(i - 1).getNameSort()
					: " ";
			if (!previewStr.equals(currentStr)) {
				String name = list.get(i).getNameSort();
				alphaIndexer.put(name, i);
				sections[i] = name;
			}
		}

	}

	public HashMap<String, Integer> getCityMap(){
		return alphaIndexer;
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_city, null);
			holder = new ViewHolder();
			holder.alpha = (TextView) convertView
					.findViewById(R.id.item_city_alpha);
			holder.name = (TextView) convertView
					.findViewById(R.id.item_city_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.name.setText(list.get(position).getCityName());
		String currentStr = list.get(position).getNameSort();
		String previewStr = (position - 1) >= 0 ? list.get(position - 1)
				.getNameSort() : " ";
		if (!previewStr.equals(currentStr)) {
			holder.alpha.setVisibility(View.VISIBLE);
			holder.alpha.setText(currentStr);
		} else {
			holder.alpha.setVisibility(View.GONE);
		}
		return convertView;
	}

	private class ViewHolder {
		TextView alpha;
		TextView name;
	}

}
