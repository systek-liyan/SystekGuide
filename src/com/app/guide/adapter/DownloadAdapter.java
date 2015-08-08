package com.app.guide.adapter;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
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
public class DownloadAdapter extends CommonAdapter<DownloadBean> {

	/**
	 * 存储每一项的progressBar， 复用导致无法更新item中的progress，所以要提取出来
	 */
	private Map<Integer, ProgressBar> progressMap;

	public DownloadAdapter(Context context, List<DownloadBean> data,
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

		holder.setTvText(R.id.tv_download_museum_name,
				getItem(position).getName()).setTvText(R.id.tv_download_size,
				getItem(position).getTotal() + "");
		final TextView tvMsg = holder.getView(R.id.tv_download_msg);
		final ImageView ivStart = holder.getView(R.id.iv_download_start);
		ProgressBar pb = holder.getView(R.id.pb_downloading);
		if (!progressMap.containsKey(pb)) {
			progressMap.put(position, pb);
		}

		tvMsg.setText("");
		String museumId = getItem(position).getMuseumId();
		final DownloadClient client = AppService.getDownloadClient(mContext,
				museumId);
		client.setOnProgressListener(new OnProgressListener() {

			int progress ;
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				Toast.makeText(mContext, getItem(position).getName() + "下载完成",
						Toast.LENGTH_SHORT).show();
				remove(position);
			}

			@Override
			public void onStart() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgress(long total, long current) {
				progress = (int)(current/total)*100;
				progressMap.get(position).setProgress(progress);
				tvMsg.setText("正在下载"+progress+"%");
				
			}

			@Override
			public void onFailed(String url, String msg) {
				Toast.makeText(mContext, "请检查网络状态", Toast.LENGTH_SHORT).show();
			}
		});

		ivStart.setOnClickListener(new OnClickListener() {

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
					ivStart.setImageResource(R.drawable.play_btn_pause);
				} else if (client.getState() == STATE.DOWNLOADING) {
					client.pause();
					ivStart.setImageResource(R.drawable.play_btn_play);
				} else if (client.getState() == STATE.PAUSE) {
					client.resume();
					ivStart.setImageResource(R.drawable.play_btn_pause);
				}
			}
		});

		holder.getConvertView().setOnLongClickListener(mLongClickListener);

	}

	private OnLongClickListener mLongClickListener = new OnLongClickListener() {

		@Override
		public boolean onLongClick(View v) {
			// TODO Auto-generated method stub
			Toast.makeText(mContext, "删除", Toast.LENGTH_SHORT).show();
			return true;
		}
	};
	
	
	public interface OnItemDeletedListener{
		
		void onItemDeletedListener();
	}

	// //设置下载项 的名字（博物馆名）
	// holder.setTvText(R.id.item_downloading_name,
	// getItem(position).getName());
	// //获取显示提示语的tv
	// final TextView tvMsg = holder.getView(R.id.item_downloading_msg);
	// //获取开始按钮
	// final Button btnStart = holder.getView(R.id.item_downloading_btn_start);
	// //获取博物馆id
	// String museumId = getItem(position).getMuseumId();
	// //根据获取的博物馆id通过AppService取得一个DownloadClient 用以下载
	// final DownloadClient client = AppService.getDownloadClient(mContext,
	// museumId);
	// //给downloadClient设置下载进度监听
	// client.setOnProgressListener(new OnProgressListener() {
	// @Override
	// public void onSuccess() {
	// Toast.makeText(mContext,
	// client.getDownloadBean().getName() + "下载完成",
	// Toast.LENGTH_SHORT).show();
	// // 移除view
	// remove(position);
	// }
	//
	// @Override
	// public void onStart() {
	// // 开始下载 TODO 设置进度
	// tvMsg.setText("正在下载...");
	// }
	//
	// @Override
	// public void onProgress(long total, long current) {
	// ProgressBar progressBar = progressMap.get(position);
	// if (progressBar != null) {
	// progressBar.setProgress((int) (current * 100 / total));
	// }
	// Log.w("Adapter", "total:" + total + "  current:" + current);
	// }
	//
	// @Override
	// public void onFailed(String url, String msg) {
	// // 下载失败 TODO
	// Toast.makeText(mContext, "请检查网络状态", Toast.LENGTH_SHORT).show();
	// }
	// });
	// //给开始按钮设置点击监听
	// btnStart.setOnClickListener(new OnClickListener() {
	// @Override
	// public void onClick(View v) {
	// if (client.getState() == STATE.NONE) {
	// tvMsg.setText("准备中...");
	// try {
	// client.start();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// } catch (NumberFormatException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// btnStart.setText("暂停");
	// } else if (client.getState() == STATE.DOWNLOADING) {
	// client.pause();
	// btnStart.setText("开始");
	// } else if (client.getState() == STATE.PAUSE) {
	// client.resume();
	// btnStart.setText("暂停");
	// }
	// }
	// });
	// //获取下载进度条，并添加到Map中。不将progressBar放入holder，是因为复用之后无法更新进度
	// //TODO查查看是否可以复用
	// ProgressBar progress = (ProgressBar) holder.getConvertView()
	// .findViewById(R.id.item_downloading_progress);
	// if (!progressMap.containsKey(position)) {
	// progress.setMax(100);
	// progressMap.put(position, progress);
	// }
	// }
}
