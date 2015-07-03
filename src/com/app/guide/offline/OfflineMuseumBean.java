package com.app.guide.offline;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "museum")
public class OfflineMuseumBean {
	@DatabaseField(id = true, useGetSet = true)
	private String id;
	@DatabaseField(columnName = "name", useGetSet = true)
	private String name;
	@DatabaseField(columnName = "longtiudeX", useGetSet = true)
	private double longtiudex;
	@DatabaseField(columnName = "longtiudeY", useGetSet = true)
	private double longtiudey;
	@DatabaseField(columnName = "iconUrl", useGetSet = true)
	private String iconurl;
	@DatabaseField(columnName = "address", useGetSet = true)
	private String address;
	@DatabaseField(columnName = "opentime", useGetSet = true)
	private String opentime;
	@DatabaseField(columnName = "textUrl", useGetSet = true)
	private String texturl;
	@DatabaseField(columnName = "imgurl", useGetSet = true)
	private String imgurl;
	@DatabaseField(columnName = "audioUrl", useGetSet = true)
	private String audiourl;
	@DatabaseField(columnName = "floorCount", useGetSet = true)
	private int floorcount;
	@DatabaseField(columnName = "city", useGetSet = true)
	private String city;
	@DatabaseField(columnName = "version", useGetSet = true)
	private int version;
//	@DatabaseField(columnName = "filesize", useGetSet = true)
//	private long filesize;
//	
//	public long getFilesize() {
//		return filesize;
//	}
//	public void setFilesize(long filesize) {
//		this.filesize = filesize;
//	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getLongtiudex() {
		return longtiudex;
	}
	public void setLongtiudex(double longtiudex) {
		this.longtiudex = longtiudex;
	}
	public double getLongtiudey() {
		return longtiudey;
	}
	public void setLongtiudey(double longtiudey) {
		this.longtiudey = longtiudey;
	}
	public String getIconurl() {
		return iconurl;
	}
	public void setIconurl(String iconurl) {
		this.iconurl = iconurl;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getOpentime() {
		return opentime;
	}
	public void setOpentime(String opentime) {
		this.opentime = opentime;
	}
	public String getTexturl() {
		return texturl;
	}
	public void setTexturl(String texturl) {
		this.texturl = texturl;
	}
	public String getImgurl() {
		return imgurl;
	}
	public void setImgurl(String imgurls) {
		this.imgurl = imgurls;
	}
	public String getAudiourl() {
		return audiourl;
	}
	public void setAudiourl(String audiourl) {
		this.audiourl = audiourl;
	}
	public int getFloorcount() {
		return floorcount;
	}
	public void setFloorcount(int floorcount) {
		this.floorcount = floorcount;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
}
