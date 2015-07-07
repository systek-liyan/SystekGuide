package com.app.guide.bean;

public class ImageOption {

	private String imgUrl; // 图片URL
	private int startTime; // 开始显示该图片的音频播放进度（时间）

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
