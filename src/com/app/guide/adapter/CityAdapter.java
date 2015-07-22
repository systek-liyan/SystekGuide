package com.app.guide.adapter;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.app.guide.R;
import com.app.guide.bean.CityBean;

/**
 * 城市列表Adapter
 */
public class CityAdapter extends CommonAdapter<CityBean> {

	/**
	 * 存储每个字母的首个城市在城市列表中的位置
	 */
	private HashMap<String, Integer> alphaIndexer;

	public CityAdapter(Context context, List<CityBean> data, int layoutId) {
		super(context, data, layoutId);
		alphaIndexer = new HashMap<String, Integer>();
		// 匹配每一个CityModel的alpha,如果不等于前一个CityModel的NameSort，证明该城市是某一字母的第一个城市，
		// 则将该CityModel的position加入到Map中
		for (int i = 0;  i < mData.size(); i++) {
			String currentStr = mData.get(i).getAlpha();
			String previewStr = (i - 1) >= 0 ? mData.get(i - 1).getAlpha()
					: " ";
			if (!previewStr.equals(currentStr)) {
				String name = mData.get(i).getAlpha();
				alphaIndexer.put(name, i);
			}
		}
	}

	public HashMap<String, Integer> getCityMap() {
		return alphaIndexer;
	}

	@Override
	public void convert(ViewHolder holder, int position) {
		holder.setTvText(R.id.item_city_name, getItem(position).getName());
		TextView alpha = holder.getView(R.id.item_city_alpha);
		String currentStr = getItem(position).getAlpha();
		if (position == alphaIndexer.get(currentStr)) {
			alpha.setVisibility(View.VISIBLE);
			alpha.setText(currentStr);
		} else {
			alpha.setVisibility(View.GONE);
		}

	}

}
