package com.app.guide.adapter;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
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
public class DownloadingAdapter extends CommonAdapter<DownloadBean> {

	/**
	 * 存储每一项的progressBar， 复用导致无法更新item中的progress，所以要提取出来
	 */
	private Map<Integer, ProgressBar> progressMap;

	public DownloadingAdapter(Context context, List<DownloadBean> data,
			int layoutId) {
		super(context, data, layoutId);
		progressMap = new HashMap<Integer, ProgressBar>();
	}

	public void add(DownloadBean downloadBean) {
		// TODO Auto-generated method stub
		this.mData.add(downloadBean);
		notifyDataSetChanged();
	}

	public void remove(int position) {
		this.mData.remove(position);
		notifyDataSetChanged();
	}

	@Override
	public void convert(ViewHolder holder, final int position) {
		//设置下载项 的名字（博物馆名）
		holder.setTvText(R.id.item_downloading_name, getItem(position).getName());
		//获取显示提示语的tv
		final TextView tvMsg = holder.getView(R.id.item_downloading_msg);
		//获取开始按钮
		final Button btnStart = holder.getView(R.id.item_downloading_btn_start);
		//获取博物馆id
		String museumId = getItem(position).getMuseumId();
		//根据获取的博物馆id通过AppService取得一个DownloadClient  用以下载
		final DownloadClient client = AppService.getDownloadClient(mContext,
				museumId);
		//给downloadClient设置下载进度监听
		client.setOnProgressListener(new OnProgressListener() {
			@Override
			public void onSuccess() {
				Toast.makeText(mContext,
						client.getDownloadBean().getName() + "下载完成",
						Toast.LENGTH_SHORT).show();
				// 移除view
				remove(position);
			}

			@Override
			public void onStart() {
				// 开始下载 TODO 设置进度
				tvMsg.setText("正在下载...");
			}

			@Override
			public void onProgress(long total, long current) {
				ProgressBar progressBar = progressMap.get(position);
				if (progressBar != null) {
					progressBar.setProgress((int) (current * 100 / total));
				}
				Log.w("Adapter", "total:" + total + "  current:" + current);
			}

			@Override
			public void onFailed(String url, String msg) {
				// 下载失败 TODO
				Toast.makeText(mContext, "请检查网络状态", Toast.LENGTH_SHORT).show();
			}
		});
		//给开始按钮设置点击监听
		btnStart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (client.getState() == STATE.NONE) {
					tvMsg.setText("准备中...");
					try {
						client.start();
					} catch (SQLException e) {
						e.printStackTrace();
					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					btnStart.setText("暂停");
				} else if (client.getState() == STATE.DOWNLOADING) {
					client.pause();
					btnStart.setText("开始");
				} else if (client.getState() == STATE.PAUSE) {
					client.resume();
					btnStart.setText("暂停");
				}
			}
		});
		//获取下载进度条，并添加到Map中。不将progressBar放入holder，是因为复用之后无法更新进度 
		//TODO查查看是否可以复用
		ProgressBar progress = (ProgressBar) holder.getConvertView()
				.findViewById(R.id.item_downloading_progress);
		if (!progressMap.containsKey(position)) {
			progress.setMax(100);
			progressMap.put(position, progress);
		}
	}
}
