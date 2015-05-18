package com.app.guide.bean;

public class MuseumBean {
	
	private String name;//博物馆名称
	private String address;//博物馆地址
	private String opentime;//博物馆开放时间
	private boolean isOpen;//当前博物馆是否开放
	private String imgUrl;//icon的Url地址
	
	
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
	 * @return the opentime
	 */
	public String getOpentime() {
		return opentime;
	}
	/**
	 * @param opentime the opentime to set
	 */
	public void setOpentime(String opentime) {
		this.opentime = opentime;
	}
	/**
	 * @return the isOpen
	 */
	public boolean isOpen() {
		return isOpen;
	}
	/**
	 * @param isOpen the isOpen to set
	 */
	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}
	/**
	 * @return the imgUrl
	 */
	public String getImgUrl() {
		return imgUrl;
	}
	/**
	 * @param imgUrl the imgUrl to set
	 */
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
}
