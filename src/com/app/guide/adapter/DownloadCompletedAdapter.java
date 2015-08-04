package com.app.guide.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.guide.R;
import com.app.guide.download.DownloadBean;

/**
 * 下载完成adapter
 */
public class DownloadCompletedAdapter extends CommonAdapter<DownloadBean> {

	public DownloadCompletedAdapter(Context context, List<DownloadBean> data,
			int layoutId) {
		super(context, data, layoutId);
	}

	public void remove(int position) {
		mData.remove(position);
		notifyDataSetChanged();
	}

	@Override
	public void convert(ViewHolder holder,final int position) {
		holder.setTvText(R.id.tv_download_citylist_name, getItem(position).getName());

//		final String museumId = getItem(position).getMuseumId();
		ImageView ivDownload = holder.getView(R.id.iv_downloadable);
		//判断是否已下载
		holder.setTvText(R.id.tv_download_size, getItem(position).getTotal()+"");
		
		ivDownload.setOnClickListener(mClickListener);
		//		// 给删除按钮添加点击监听
//		Button btnDelete = holder.getView(R.id.item_download_completed_delete);
//
//		btnDelete.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				OfflineDeleteHelper helper = new OfflineDeleteHelper(mContext,
//						museumId);
//				try {
//					if (helper.deleteMuseum()) {
//						Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT)
//								.show();
//						remove(position);
//					} else {
//						Toast.makeText(mContext, "删除失败", Toast.LENGTH_SHORT)
//								.show();
//					}
//				} catch (SQLException e) {
//					e.printStackTrace();
//				} catch (DeleteDownloadingException e) {
//					// 当博物馆正在下载时，无法删除，弹出提示框
//					e.printStackTrace();
//					Toast.makeText(mContext, "该博物馆正在下载，无法删除",
//							Toast.LENGTH_SHORT).show();
//				}
//			}
//		});
//
//		// 给更新按钮添加点击监听
//		Button btnUpdate = holder.getView(R.id.item_download_completed_update);
//
//		btnUpdate.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Toast.makeText(mContext, "点击了更新按钮", Toast.LENGTH_SHORT).show();
//			}
//		});
	}
	
	private OnClickListener mClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Toast.makeText(mContext, "开始下载", Toast.LENGTH_SHORT).show();
			
		}
	};

}
