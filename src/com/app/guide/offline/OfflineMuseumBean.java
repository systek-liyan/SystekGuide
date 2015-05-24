package com.app.guide.offline;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "museum")
public class OfflineMuseumBean {
	@DatabaseField(generatedId = true, useGetSet = true)
	private int id;
	@DatabaseField(columnName = "name", useGetSet = true)
	private String name;
	@DatabaseField(columnName = "longtiudeX", useGetSet = true)
	private double longtiudeX;
	@DatabaseField(columnName = "longtiudeY", useGetSet = true)
	private double longtiudeY;
	@DatabaseField(columnName = "iconUrl", useGetSet = true)
	private String iconUrl;
	@DatabaseField(columnName = "address", useGetSet = true)
	private String address;
	@DatabaseField(columnName = "opentime", useGetSet = true)
	private String opentime;
	@DatabaseField(columnName = "isOpen")
	private boolean isOpen;
	@DatabaseField(columnName = "textUrl", useGetSet = true)
	private String textUrl;
	@DatabaseField(columnName = "imgList", useGetSet = true)
	private String imgList;
	@DatabaseField(columnName = "audioUrl", useGetSet = true)
	private String audioUrl;
	@DatabaseField(columnName = "floorCount", useGetSet = true)
	private int floorCount;
	@DatabaseField(columnName = "city", useGetSet = true)
	private String city;
	@DatabaseField(columnName = "version", useGetSet = true)
	private int version;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getLongtiudeX() {
		return longtiudeX;
	}

	public void setLongtiudeX(double longtiudeX) {
		this.longtiudeX = longtiudeX;
	}

	public double getLongtiudeY() {
		return longtiudeY;
	}

	public void setLongtiudeY(double longtiudeY) {
		this.longtiudeY = longtiudeY;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
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

	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	public String getTextUrl() {
		return textUrl;
	}

	public void setTextUrl(String textUrl) {
		this.textUrl = textUrl;
	}

	public String getImgList() {
		return imgList;
	}

	public void setImgList(String imgList) {
		this.imgList = imgList;
	}

	public String getAudioUrl() {
		return audioUrl;
	}

	public void setAudioUrl(String audioUrl) {
		this.audioUrl = audioUrl;
	}

	public int getFloorCount() {
		return floorCount;
	}

	public void setFloorCount(int floorCount) {
		this.floorCount = floorCount;
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
