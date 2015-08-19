package com.app.guide.offline;

import java.sql.SQLException;

import android.content.Context;

import com.app.guide.Constant;
import com.app.guide.bean.MuseumBean;
import com.app.guide.download.DownloadBean;
import com.app.guide.download.DownloadClient;
import com.app.guide.download.DownloadClient.STATE;
import com.app.guide.exception.DeleteDownloadingException;
import com.app.guide.service.AppService;
import com.app.guide.sql.DatabaseContext;
import com.app.guide.sql.DownloadManagerHelper;
import com.app.guide.utils.FileUtils;
import com.j256.ormlite.stmt.DeleteBuilder;

/**
 * 删除数据包帮助类
 * 
 * @author joe_c
 *
 */
public class OfflineDeleteHelper {

	private Context mContext;
	private String museumId;

	public OfflineDeleteHelper(Context context, String museumId) {
		super();
		this.mContext = context;
		this.museumId = museumId;
	}

	/**
	 * 删除整个博物馆的数据 抛出DeleteDownloadingException表示试图删除正在下载中的数据
	 * 
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
		
		// 外部数据库Context
		Context dContext = new DatabaseContext(mContext, Constant.FLODER_NAME);
		DownloadManagerHelper helper = new DownloadManagerHelper(dContext,"Download.db");
		
		int count = helper.getDownloadBeanDao().queryBuilder().where()
				.eq("museumId", museumId).and().eq("isCompleted", true).query()
				.size();
		if (count > 0) {
			DeleteBuilder<DownloadBean, String> deleteBuilder = helper
					.getDownloadBeanDao().deleteBuilder();
			deleteBuilder.where().eq("museumId", museumId);
			deleteBuilder.delete();
			DeleteBuilder<MuseumBean, String> deleteBuilder2 = helper
					.getDownloadedDao().deleteBuilder();
			deleteBuilder2.where().eq("museumId", museumId);
			deleteBuilder2.delete();
			FileUtils.deleteDirectory(Constant.FLODER + museumId);
			return true;
		} else
			return false;
	}

	/**
	 * 删除博物馆下某个展厅的数据
	 * 
	 * @param areaId
	 */
	public void deleteMuseumArea(int areaId) {

	}

}
