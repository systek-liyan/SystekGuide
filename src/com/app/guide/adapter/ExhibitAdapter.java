package com.app.guide.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.guide.R;
import com.app.guide.bean.Exhibit;

/**
 * 展品listView adapter
 * @author yetwish
 */
public class ExhibitAdapter extends ArrayAdapter<Exhibit>{

	private int resourceId;

	public ExhibitAdapter(Context context, int resource, List<Exhibit> objects) {
		super(context, resource, objects);
		this.resourceId = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(convertView == null){
			Log.w("list_item", "new convertView");
			convertView = LayoutInflater.from(getContext()).inflate(resourceId, null,false);
			viewHolder = new ViewHolder();
			bindView(viewHolder, convertView);
		}else{
			viewHolder = (ViewHolder)convertView.getTag();
		}
		Exhibit exhibit = getItem(position);
		setViewContent(viewHolder, exhibit);
		return convertView;
	}
	
	private static class ViewHolder{
		ImageView ivIcon;
		TextView tvName;
		TextView tvAddress;
		TextView tvDynasty;
		TextView tvIntroduction;
		
	}
	
	private void setViewContent(ViewHolder viewHolder, Exhibit exhibit){
		viewHolder.ivIcon.setImageDrawable(exhibit.getIcon());
		viewHolder.tvName.setText(exhibit.getName());
		viewHolder.tvAddress.setText(exhibit.getAddress());
		viewHolder.tvDynasty.setText(exhibit.getDynasty());
		viewHolder.tvIntroduction.setText(exhibit.getIntroduction());
	}
	
	private void bindView(ViewHolder viewHolder,View convertView){
		viewHolder.ivIcon = (ImageView)convertView.findViewById(R.id.exhibit_list_item_iv_icon);
		viewHolder.tvName = (TextView)convertView.findViewById(R.id.exhibit_list_item_tv_name);
		viewHolder.tvAddress = (TextView)convertView.findViewById(R.id.exhibit_list_item_tv_address);
		viewHolder.tvDynasty = (TextView)convertView.findViewById(R.id.exhibit_list_item_tv_dynasty);
		viewHolder.tvIntroduction = (TextView)convertView.findViewById(R.id.exhibit_list_item_tv_introduction);
		convertView.setTag(viewHolder);
		
	}
	
}
