package com.app.guide.adapter;

import java.util.List;

import android.content.Context;

import com.app.guide.R;
import com.app.guide.bean.Exhibit;

/**
 * 展品listView adapter
 * @author yetwish
 */
public class ExhibitAdapter extends CommonAdapter<Exhibit>{

	public ExhibitAdapter(Context context, List<Exhibit> data, int layoutId) {
		super(context, data, layoutId);
	}

	@Override
	public void convert(ViewHolder holder, int position) {
		holder.setImageResource(R.id.item_exhibit_iv_icon, R.drawable.exhibit_icon)
				.setText(R.id.item_exhibit_tv_name, mData.get(position).getName())
				.setText(R.id.item_exhibit_tv_address, mData.get(position).getAddress())
				.setText(R.id.item_exhibit_tv_dynasty, mData.get(position).getDynasty())
				.setText(R.id.item_exhibit_tv_introduction, mData.get(position).getIntroduction());
				
	}
	
}
