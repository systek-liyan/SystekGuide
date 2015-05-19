package com.app.guide.bean;

/**
 * 
 * @author yetwish
 * 
 */
public class ImageBean {
	private String imgUrl;
	private int startTime;

	public ImageBean(){}
	
	public ImageBean(String imgUrl, int startTime) {
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
