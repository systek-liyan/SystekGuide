package com.app.guide.download;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 指定城市的博物馆列表，表名:downloadModel
 * TODO 待整理，好像还不是表
 */
@DatabaseTable(tableName = "downloadModel")
public class DownloadModel {

	@DatabaseField(columnName = "city")
	private String city;
	@DatabaseField(columnName = "museums_url")
	private String museumsUrl;//musuemId*name*size,
	
	private List<DownloadBean> museumList;
	
	public DownloadModel(){
		this.museumList= new ArrayList<DownloadBean>();
	}
	
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
	
	public String getMuseumsUrl() {
		return museumsUrl;
	}

	public void setMuseumsUrl(String museumsUrl) {
		this.museumsUrl = museumsUrl;
	}
	
	public void setMuseumsUrl(List<DownloadBean> musuemList){
		setMuseumList(musuemList);
		String urls = "";
		for(DownloadBean bean: museumList){
			urls += bean.getMuseumId()+"*"+bean.getName()+"*"+bean.getTotal();
			if(museumList.indexOf(bean)!= musuemList.size()-1)
				urls +=",";
		}
		this.museumsUrl= urls;
		Log.d("TAG", urls);
	}

	public List<DownloadBean> getMuseumList() {
		museumList.clear();
		String museums[] = museumsUrl.split(",");
		DownloadBean bean = null;
		for(int i = 0; i< museums.length;i++){
			String parts[] = museums[i].split("\\*");
			bean = new DownloadBean();
			bean.setMuseumId(parts[0]);
			bean.setName(parts[1]);
			bean.setTotal(Long.parseLong(parts[2]));
			museumList.add(bean);
		}
		return museumList;
	}
	
	private void setMuseumList(List<DownloadBean> museumList){
		this.museumList = museumList;
	}
	
		
}
