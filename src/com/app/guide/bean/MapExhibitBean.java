package com.app.guide.bean;

public class MapExhibitBean {
	private int id;		
	private float mapX;
	private float mapY;
	private String name;
	private String address;
	private String iconUrl;
	
	/**
	 * return the id;
	 * @return
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
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
