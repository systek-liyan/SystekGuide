package com.app.guide.adapter;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ClearCacheRequest;
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

	private Map<String,View> viewMap;
	private Map<String,DownloadClient> clientMap;
	
	private OnItemDeleteListener mItemDeleteListener;
	
	private OnDownloadCompleteListener mDownloadCompleteListener;
	
	public void setItemDeleteListener(OnItemDeleteListener listener){
		this.mItemDeleteListener = listener;
	}
	
	public void setDownloadCompleteListener(OnDownloadCompleteListener listener){
		this.mDownloadCompleteListener = listener;
	}
	
	public DownloadAdapter(Context context, List<DownloadBean> data,
			int layoutId) {
		super(context, data, layoutId);
		progressMap = new HashMap<Integer, ProgressBar>();
//		clientMap = new HashMap<String, DownloadClient>();
//		for(DownloadBean bean : mData){
//			clientMap.put(bean.getMuseumId(), AppService.getDownloadClient(mContext,bean.getMuseumId()));
//		}
		viewMap = new HashMap<String, View>();
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
//		for(DownloadBean bean : mData){
//			clientMap.put(bean.getMuseumId(), AppService.getDownloadClient(mContext,bean.getMuseumId()));
//		}
	}
	
	public void remove(int position) {
		this.mData.remove(position);
		notifyDataSetChanged();
	}
	
//	public void startDownload(String museumId){
//		DownloadClient client = clientMap.get(museumId);
//		try {
//			client.start();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} catch (NumberFormatException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	@Override
	public void convert(ViewHolder holder, final int position) {
		viewMap.put(getItem(position).getMuseumId(), holder.getConvertView());
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
		Log.w("TAG", museumId);
		final DownloadClient client = AppService.getDownloadClient(mContext,
				museumId);
		client.setOnProgressListener(new OnProgressListener() {

			int progress ;
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				Toast.makeText(mContext, getItem(position).getName() + "下载完成",
						Toast.LENGTH_SHORT).show();
				if(mDownloadCompleteListener != null){
					mDownloadCompleteListener.onDownloadComplete(getItem(position));
				}
				remove(position);
			}

			@Override
			public void onStart() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgress(long total, long current) {
				Log.w("TAG", current+","+total+"adapter");
				progress = (int)(current*100/total);
				progressMap.get(0).setProgress(progress);
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

		holder.getConvertView().setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(mContext, "删除", Toast.LENGTH_SHORT).show();
				if(mItemDeleteListener!=null){
					mItemDeleteListener.onItemDeleted(getItem(position));
				}
				remove(position);
				return true;
			}
		});

	}

//	private OnLongClickListener mLongClickListener = ;
	public interface OnDownloadCompleteListener{
		void onDownloadComplete(DownloadBean bean);
	}
	
	public interface OnItemDeleteListener{
		
		void onItemDeleted(DownloadBean bean);
	}

}
