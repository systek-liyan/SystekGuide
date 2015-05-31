package com.app.guide.offline;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.Environment;

import com.alibaba.fastjson.JSON;
import com.app.guide.bean.DownloadBean;
import com.app.guide.bean.ImageOption;
import com.app.guide.sql.DatabaseContext;
import com.app.guide.utils.FileUtils;
import com.j256.ormlite.dao.Dao;

/**
 * 下载数据包辅助类，目前只是向数据库中写入数据
 * 
 * @author joe_c
 * 
 */
public class OfflineDownloadHelper {

	private static final String TAG = OfflineDownloadHelper.class
			.getSimpleName();
	private final static String introduction = "1977年平谷刘家河出土。敛口，口沿外折，方唇，颈粗短，折肩，深腹，高圈足。颈部饰以两道平行凸弦纹，肩部饰一周目雷纹，其上圆雕等距离三个大卷角羊首，腹部饰以扉棱为鼻的饕餮纹，圈足饰一周对角云雷纹，其上有三个方形小镂孔。此罍带有商代中期的显著特征。其整体造型，纹饰与河南郑州白家庄M3出土的罍较相似。此器造型凝重，纹饰细密，罍肩上的羊首系用分铸法铸造，显示了商代北京地区青铜铸造工艺的高度水平。";
	private static final String FLODER = "Guide/Test";
	private Context mContext;
	private int museumId;

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

	//
	List<String> audioData = new ArrayList<String>(Arrays.asList(
			"",
			"",
			""
			));
	
	public OfflineDownloadHelper(Context context, int museumId) {
		super();
		this.mContext = context;
		this.museumId = museumId;
	}

	@SuppressWarnings("resource")
	private void downloadExhibit(Context context, int museumid)
			throws SQLException, NumberFormatException, IOException {

		File file = new File(Environment.getExternalStorageDirectory()
				.getAbsoluteFile() + "/Test/position.txt");
		OfflineBeanSqlHelper sqlHelper = new OfflineBeanSqlHelper(context,
				museumid + ".db");
		Dao<OfflineExhibitBean, Integer> exhibitDao;
		exhibitDao = sqlHelper.getOfflineExhibitDao();
		BufferedReader br = new BufferedReader(new FileReader(file));
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
			bean.setMuseumId(1);
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("年代", "唐代");
			map.put("材质", "瓷器");
			map.put("用途", "观赏");
			bean.setLabelJson(JSON.toJSONString(map));
			List<ImageOption> list = new ArrayList<ImageOption>();
			for (int j = 0; j < galleryData.size(); j++) {
				ImageOption option = new ImageOption();
				option.setImgUrl(galleryData.get(j));
				//TODO 
				option.setStartTime(j *12 * 1000);
				list.add(option);
			}
			bean.setImgJson(JSON.toJSONString(list));
			bean.setFloor(1);
			//补充  TODO id 是否应该改为 int
			//bean.setAudioUrl(); 需要设置音频的url
			bean.setrExhibitBeanId(i>0?i:1); 
			bean.setlExhibitBeanId(i+2);
			exhibitDao.createOrUpdate(bean);
			i++;
		}

	}

	private void downloadMap(Context context, int museumId) throws SQLException {
		OfflineBeanSqlHelper helper = new OfflineBeanSqlHelper(context,
				museumId + ".db");
		Dao<OfflineMapBean, Integer> mapDao = helper.getOfflineMapDao();
		OfflineMapBean bean = new OfflineMapBean();
		bean.setFloor(1);
		bean.setMuseumId(1);
		bean.setVersion(0);
		mapDao.createIfNotExists(bean);
	}

	private OfflineMuseumBean downloadMuseum(Context context, int museumId)
			throws SQLException {
		OfflineBeanSqlHelper helper = new OfflineBeanSqlHelper(context,
				museumId + ".db");
		Dao<OfflineMuseumBean, Integer> museumDao = helper
				.getOfflineMuseumDao();
		OfflineMuseumBean bean = new OfflineMuseumBean();
		bean.setAddress("北京市西城区复兴门外大街16号");
		bean.setCity("北京");
		bean.setFloorCount(1);
		bean.setImgList(JSON.toJSONString(galleryData));
		//audio url
		bean.setLongtiudeX(0);
		bean.setLongtiudeY(0);
		bean.setIconUrl("http://img.my.csdn.net/uploads/201407/26/1406383299_1976.jpg");
		bean.setName("山海关博物馆");
		bean.setOpen(true);
		bean.setOpentime("9:00-15:30");
		bean.setVersion(0);
		museumDao.createOrUpdate(bean);
		return bean;
	}

	private void downloadLabel(Context context, int museumId)
			throws SQLException {
		OfflineBeanSqlHelper helper = new OfflineBeanSqlHelper(context,
				museumId + ".db");
		Dao<OfflineLabelBean, Integer> labelDao = helper.getOfflineLabelDao();
		OfflineLabelBean bean1 = new OfflineLabelBean();
		List<String> list = new ArrayList<String>();
		list.add("石器");
		list.add("陶器");
		list.add("瓷器");
		list.add("铜器");
		list.add("玉器");
		list.add("铁器");
		bean1.setName("材质");
		bean1.setLabels(JSON.toJSONString(list));
		bean1.setMuseumId(1);

		OfflineLabelBean bean2 = new OfflineLabelBean();
		list.clear();
		list.add("夏");
		list.add("商");
		list.add("秦汉");
		list.add("三国");
		list.add("隋唐");
		list.add("宋代");
		bean2.setName("年代");
		bean2.setLabels(JSON.toJSONString(list));
		bean2.setMuseumId(1);

		OfflineLabelBean bean3 = new OfflineLabelBean();
		list.clear();
		list.add("日常用具");
		list.add("农耕");
		list.add("雕刻");
		list.add("观赏");
		bean3.setName("用途");
		bean3.setLabels(JSON.toJSONString(list));
		bean3.setMuseumId(1);

		labelDao.createOrUpdate(bean1);
		labelDao.createOrUpdate(bean2);
		labelDao.createOrUpdate(bean3);

	}

	public void download() throws SQLException, NumberFormatException,
			IOException {
		DownloadManagerHelper helper = new DownloadManagerHelper(mContext);
		int count = helper.getDownloadDao().queryBuilder().where()
				.eq("museumId", museumId).query().size();
		if (count > 0) {
			return;
		}
		FileUtils.deleteDirectory(FLODER);
		Context dContext = new DatabaseContext(mContext, FLODER);
		downloadExhibit(dContext, museumId);
		downloadMap(dContext, museumId);
		downloadLabel(dContext, museumId);
		OfflineMuseumBean museumBean = downloadMuseum(dContext, museumId);
		DownloadBean bean = new DownloadBean();
		bean.setAddress(museumBean.getAddress());
		bean.setIconUrl(museumBean.getIconUrl());
		bean.setLongitudX(museumBean.getLongtiudeX());
		bean.setLongitudY(museumBean.getLongtiudeY());
		bean.setName(museumBean.getName());
		bean.setOpentime(museumBean.getOpentime());
		bean.setOpen(true);
		bean.setVersion(museumBean.getVersion());
		bean.setMuseumId(museumId);
		helper.getDownloadDao().createOrUpdate(bean);
	}
}
