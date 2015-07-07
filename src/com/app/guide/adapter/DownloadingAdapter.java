package com.app.guide.adapter;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.guide.R;
import com.app.guide.download.DownloadBean;
import com.app.guide.download.DownloadClient;
import com.app.guide.download.DownloadClient.OnProgressListener;
import com.app.guide.download.DownloadClient.STATE;
import com.app.guide.service.AppService;

/**
 * 正在下载列表adapter 
 *
 */
public class DownloadingAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context mContext;
	
	/**
	 * 正在下载数据列表
	 */
	private List<DownloadBean> data;
	
	/**
	 * 存储每一项的progressBar， 复用导致无法更新item中的progress，所以要提取出来
	 */
	private Map<Integer, ProgressBar> progressMap;

	public DownloadingAdapter(Context context, List<DownloadBean> data) {
		// TODO Auto-generated constructor stub
		this.data = data;
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
		progressMap = new HashMap<Integer, ProgressBar>();
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
		return position;
	}

	public void add(DownloadBean downloadBean) {
		// TODO Auto-generated method stub
		this.data.add(downloadBean);
		notifyDataSetChanged();
	}

	public void remove(int position) {
		// TODO Auto-generated method stub
		this.data.remove(position);
		notifyDataSetChanged();
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_downloading, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView
					.findViewById(R.id.item_downloading_name);
			holder.message = (TextView) convertView
					.findViewById(R.id.item_downloading_msg);
			holder.start = (Button) convertView
					.findViewById(R.id.item_downloading_btn_start);
			ProgressBar progress = (ProgressBar) convertView
					.findViewById(R.id.item_downloading_progress);
			if (!progressMap.containsKey(position)) {
				progress.setMax(100);
				progressMap.put(position, progress);
			}
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String museumId = data.get(position).getMuseumId();
		holder.name.setText(data.get(position).getName());

		final DownloadClient client = AppService.getDownloadClient(mContext,
				museumId);
		client.setOnProgressListener(new OnProgressListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				Toast.makeText(mContext,
						client.getDownloadBean().getName() + "下载完成",
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				holder.message.setText("正在下载...");
			}

			@Override
			public void onProgress(long total, long current) {
				// TODO Auto-generated method stub
				ProgressBar progressBar = progressMap.get(position);
				if (progressBar != null) {
					progressBar.setProgress((int) (current * 100 / total));
				}
				Log.w("Adapter", "total:" + total + "  current:" + current);
			}

			@Override
			public void onFailed(String url, String msg) {
				// TODO Auto-generated method stub

			}
		});
		holder.start.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (client.getState() == STATE.NONE) {
					holder.message.setText("准备中...");
					try {
						client.start();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					holder.start.setText("暂停");
				} else if (client.getState() == STATE.DOWNLOADING) {
					client.pause();
					holder.start.setText("开始");
				} else if (client.getState() == STATE.PAUSE) {
					client.resume();
					holder.start.setText("暂停");
				}
			}
		});
		return convertView;
	}

	private class ViewHolder {
		private TextView name;
		private TextView message;
		private Button start;
	}
}
