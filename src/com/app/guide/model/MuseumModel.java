package com.app.guide.model;

import java.util.List;

/**
 * 博物馆详情数据存储类，用于博物馆简介界面数据显示
 */
public class MuseumModel {
	private String id; //博物馆id
	private String name;  //博物馆名称
	private List<String> imgsUrl;	//博物馆图集URL列表
	private String audioUrl;        //博物馆音频URL
	private String introduce;		//博物馆简介
	private int floorCount;			//博物馆楼层数
	
	

	public List<String> getImageList() {
		return imgsUrl;
	}

	public void setImageList(List<String> imageList) {
		this.imgsUrl = imageList;
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
		return introduce;
	}

	public void setTextUrl(String textUrl) {
		this.introduce = textUrl;
	}


}
