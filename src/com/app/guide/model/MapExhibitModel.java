package com.app.guide.model;

/**
 * 地图上展品数据存储类，用于地图导游界面显示地图上的展品 
 * @author yetwish
 *
 */
public class MapExhibitModel {
	
	private String id;	//展品id
	private float mapX; //展品位于地图位置的横坐标
	private float mapY; //展品位于地图位置的纵坐标
	private String name; //展品名
	private String address; //展品展出地址
	private String iconUrl; //展品图标
	
	/**
	 * return the id;
	 * @return
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the mapX
	 */
	public float getMapX() {
		return mapX;
	}
	/**
	 * @param mapX the mapX to set
	 */
	public void setMapX(float mapX) {
		this.mapX = mapX;
	}
	/**
	 * @return the mapY
	 */
	public float getMapY() {
		return mapY;
	}
	/**
	 * @param mapY the mapY to set
	 */
	public void setMapY(float mapY) {
		this.mapY = mapY;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}
	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}
	/**
	 * @return the iconUrl
	 */
	public String getIconUrl() {
		return iconUrl;
	}
	/**
	 * @param iconUrl the iconUrl to set
	 */
	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

}
