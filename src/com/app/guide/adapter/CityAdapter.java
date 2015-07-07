package com.app.guide.adapter;

import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.app.guide.R;
import com.app.guide.bean.CityModel;

/**
 * TODO 简化 
 *
 */
public class CityAdapter extends BaseAdapter {
	
	private LayoutInflater inflater;
	
	/**
	 * 所有城市的列表，一个CityModel中包含一个name,和一个nameSort:表示该城市首字母是哪个 
	 */
	private List<CityModel> list;
	
	/**
	 * 存储每个字母的首个城市在城市列表中的位置
	 */
	private HashMap<String, Integer> alphaIndexer;
	

	public CityAdapter(Context context, List<CityModel> list) {

		this.inflater = LayoutInflater.from(context);
		this.list = list;
		alphaIndexer = new HashMap<String, Integer>();
		//匹配每一个CityModel的NameSort,如果不等于前一个CityModel的NameSort，证明该城市是某一字母的第一个城市，
		//则将该CityModel的position加入到Map中
		for (int i = 0; i < list.size(); i++) {
			String currentStr = list.get(i).getNameSort();
			String previewStr = (i - 1) >= 0 ? list.get(i - 1).getNameSort()
					: " ";
			if (!previewStr.equals(currentStr)) {
				String name = list.get(i).getNameSort();
				alphaIndexer.put(name, i);
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

	@SuppressLint("InflateParams") @Override
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
		//TODO 判断map中是否有该name即可
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
