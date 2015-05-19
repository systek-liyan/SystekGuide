package com.app.guide.offline;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.app.guide.bean.DownloadBean;
import com.app.guide.sql.DatabaseContext;
import com.j256.ormlite.dao.Dao;

public class OfflineDownloadHelper {

	private static final String TAG = OfflineDownloadHelper.class
			.getSimpleName();
	private final static String introduction = "1977年平谷刘家河出土。敛口，口沿外折，方唇，颈粗短，折肩，深腹，高圈足。颈部饰以两道平行凸弦纹，肩部饰一周目雷纹，其上圆雕等距离三个大卷角羊首，腹部饰以扉棱为鼻的饕餮纹，圈足饰一周对角云雷纹，其上有三个方形小镂孔。此罍带有商代中期的显著特征。其整体造型，纹饰与河南郑州白家庄M3出土的罍较相似。此器造型凝重，纹饰细密，罍肩上的羊首系用分铸法铸造，显示了商代北京地区青铜铸造工艺的高度水平。";

	public static void downloadExhibit(Context context, String museumid) {

		DownloadManagerHelper helper = new DownloadManagerHelper(context);
		List<String> galleryData = new ArrayList<String>(Arrays.asList(
				"http://img.my.csdn.net/uploads/201407/26/1406383299_1976.jpg",
				"http://img.my.csdn.net/uploads/201407/26/1406383291_6518.jpg",
				"http://img.my.csdn.net/uploads/201407/26/1406383291_8239.jpg",
				"http://img.my.csdn.net/uploads/201407/26/1406383290_9329.jpg",
				"http://img.my.csdn.net/uploads/201407/26/1406383290_1042.jpg",
				"http://img.my.csdn.net/uploads/201407/26/1406383275_3977.jpg",
				"http://img.my.csdn.net/uploads/201407/26/1406383265_8550.jpg",
				"http://img.my.csdn.net/uploads/201407/26/1406383264_3954.jpg",
				"http://img.my.csdn.net/uploads/201407/26/1406383264_4787.jpg",
				"http://img.my.csdn.net/uploads/201407/26/1406383264_8243.jpg",
				"http://img.my.csdn.net/uploads/201407/26/1406383248_3693.jpg"));
		boolean count = false;
		try {
			count = helper.getDownloadDao().countOf() > 0;
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (count) {
			Log.w(TAG, "have_download");
			return;
		}
		Log.w(TAG, "start_download");
		File file = new File(Environment.getExternalStorageDirectory()
				.getAbsoluteFile() + "/Test/position.txt");
		BufferedReader br = null;
		OfflineBeanSqlHelper sqlHelper = new OfflineBeanSqlHelper(
				new DatabaseContext(context, "Test"), museumid + ".db");
		Dao<OfflineExhibitBean, Integer> exhibitDao;
		try {
			exhibitDao = sqlHelper.getOfflineExhibitDao();
			br = new BufferedReader(new FileReader(file));
			String readString;
			int i = 0;
			int size = galleryData.size();
			while ((readString = br.readLine()) != null) {
				String items[] = readString.split(",");// divided to an array
				float mapX = Integer.parseInt(items[0]) / 600.0f;
				float mapY = Integer.parseInt(items[1]) / 865.0f;
				OfflineExhibitBean bean = new OfflineExhibitBean();
				bean.setMapX(mapX);
				bean.setMapY(mapY);
				bean.setAddress("1展厅1号");
				bean.setName("古青花瓷");
				bean.setIntroduce(introduction);
				bean.setIconUrl(galleryData.get(i % size));
				exhibitDao.createOrUpdate(bean);
				i++;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DownloadBean bean = new DownloadBean();
		bean.setMuseumId("test");
		try {
			helper.getDownloadDao().createIfNotExists(bean);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
