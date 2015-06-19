package com.app.guide.offline;

import java.sql.SQLException;

import android.content.Context;

import com.app.guide.Constant;
import com.app.guide.download.DownloadClient;
import com.app.guide.download.DownloadClient.STATE;
import com.app.guide.exception.DeleteDownloadingException;
import com.app.guide.service.AppService;
import com.app.guide.utils.FileUtils;

/**
 * 删除数据包帮助类
 * 
 * @author joe_c
 *
 */
public class OfflineDeleteHelper {

	private Context mContext;
	private int museumId;

	public OfflineDeleteHelper(Context context, int museumId) {
		super();
		this.mContext = context;
		this.museumId = museumId;
	}

	/**
	 * 删除整个博物馆的数据 
	 * 抛出DeleteDownloadingException表示试图删除正在下载中的数据
	 * @return
	 * @throws SQLException
	 * @throws DeleteDownloadingException
	 */
	public boolean deleteMuseum() throws SQLException,
			DeleteDownloadingException {
		if (AppService.map.containsKey(museumId)) {
			DownloadClient client = AppService.getDownloadClient(mContext,
					museumId);
			if (client.getState() == STATE.PAUSE) {
				client.cancel();
				return true;
			} else if (client.getState() != STATE.NONE) {
				throw new DeleteDownloadingException();
			}
		}
		DownloadManagerHelper helper = new DownloadManagerHelper(mContext);
		int count = helper.getBeanDao().queryBuilder().where()
				.eq("museumId", museumId).and().eq("isCompleted", true).query()
				.size();
		if (count > 0) {
			helper.getBeanDao().deleteById(museumId);
			helper.getDownloadedDao().deleteById(museumId);
			FileUtils.deleteDirectory(Constant.FLODER + museumId);
			return true;
		} else
			return false;
	}

	/**
	 * 删除博物馆下某个展厅的数据
	 * @param areaId
	 */
	public void deleteMuseumArea(int areaId) {

	}

}
