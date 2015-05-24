package com.app.guide.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "download")
public class DownloadBean {

	@DatabaseField(generatedId = true)
	private int id;
	@DatabaseField(columnName = "museumid")
	private int museumId;
	@DatabaseField(columnName = "name")
	private String name;// 博物馆名称
	@DatabaseField(columnName = "address")
	private String address;// 博物馆地址
	@DatabaseField(columnName = "longitudx")
	private double longitudX;// 表示博物馆纬度坐标
	@DatabaseField(columnName = "longitudy")
	private double longitudY;// 表示博物馆经度坐标
	@DatabaseField(columnName = "opentime")
	private String opentime;// 博物馆开放时间
	@DatabaseField(columnName = "isOpen")
	private boolean isOpen;// 当前博物馆是否开放
	@DatabaseField(columnName = "iconUrl")
	private String iconUrl;// icon的Url地址
	@DatabaseField(columnName = "version")
	private int version;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public double getLongitudX() {
		return longitudX;
	}

	public void setLongitudX(double longitudX) {
		this.longitudX = longitudX;
	}

	public double getLongitudY() {
		return longitudY;
	}

	public void setLongitudY(double longitudY) {
		this.longitudY = longitudY;
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

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the museumId
	 */
	public int getMuseumId() {
		return museumId;
	}

	/**
	 * @param museumId
	 *            the museumId to set
	 */
	public void setMuseumId(int museumId) {
		this.museumId = museumId;
	}
}
