package com.app.guide.adapter;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.app.guide.R;
import com.app.guide.download.DownloadBean;
import com.app.guide.exception.DeleteDownloadingException;
import com.app.guide.offline.OfflineDeleteHelper;

/**
 * 下载完成adapter 
 */
public class DownloadCompletedAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	
	/**
	 * downloadBean 数据列表
	 */
	private List<DownloadBean> data;
	
	private Context mContext;

	public DownloadCompletedAdapter(Context context, List<DownloadBean> data) {
		inflater = LayoutInflater.from(context);
		this.data = data;
		this.mContext = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void remove() {
		data.remove(data.size() - 1);
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_download_completed,
					null);
			viewHolder.name = (TextView) convertView
					.findViewById(R.id.item_download_completed_name);
			viewHolder.delete = (Button) convertView
					.findViewById(R.id.item_download_completed_delete);
			viewHolder.update = (Button) convertView
					.findViewById(R.id.item_download_completed_update);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		DownloadBean bean = data.get(position);
		viewHolder.name.setText(bean.getName());
		final String museumId = data.get(data.size() - 1).getMuseumId();
		//给delete按钮设置点击监听
		viewHolder.delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				OfflineDeleteHelper helper = new OfflineDeleteHelper(mContext,
						museumId);
				try {
					if (helper.deleteMuseum()) {
						Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT)
								.show();
						remove();
					} else {
						Toast.makeText(mContext, "删除失败", Toast.LENGTH_SHORT)
								.show();
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (DeleteDownloadingException e) {
					// 当博物馆正在下载时，无法删除，弹出提示框
					e.printStackTrace();
					Toast.makeText(mContext, "该博物馆正在下载，无法删除",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		return convertView;
	}

	private class ViewHolder {

		private TextView name;
		private Button delete;
		private Button update;
	}

}
