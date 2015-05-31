package com.app.guide.bean;

import java.util.List;

/**
 * 用以显示在museumIntroduction 页面上
 */
public class MuseumDetailBean {
	
	private String name; 
	private String audioUrl;
	private String textUrl;
	
	private List<String> imageList;	

	public List<String> getImageList() {
		return imageList;
	}

	public void setImageList(List<String> imageList) {
		this.imageList = imageList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAudioUrl() {
		return audioUrl;
	}

	public void setAudioUrl(String audioUrl) {
		this.audioUrl = audioUrl;
	}

	public String getTextUrl() {
		return textUrl;
	}

	public void setTextUrl(String textUrl) {
		this.textUrl = textUrl;
	}


}
