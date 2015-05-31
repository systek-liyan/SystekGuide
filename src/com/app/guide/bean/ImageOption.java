package com.app.guide.bean;

public class ImageOption {

	private String imgUrl;
	private int startTime; // 为实现方便将endTime 改为了startTime

	public ImageOption() {
	}

	public ImageOption(String imgUrl, int startTime) {
		this.imgUrl = imgUrl;
		this.startTime = startTime;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

}
