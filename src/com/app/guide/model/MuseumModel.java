package com.app.guide.model;

import java.util.List;

/**
 * 博物馆详情数据存储类，用于博物馆简介界面数据显示
 */
public class MuseumModel {
	private String id; // 博物馆id
	private String name; // 博物馆名称
	private List<String> imgsUrl; // 博物馆图集URL列表
	private String audioUrl; // 博物馆音频URL
	private String introduce; // 博物馆简介
	private int floorCount; // 博物馆楼层数

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getImgsUrl() {
		return imgsUrl;
	}

	public void setImgsUrl(List<String> imgsUrl) {
		this.imgsUrl = imgsUrl;
	}

	public String getAudioUrl() {
		return audioUrl;
	}

	public void setAudioUrl(String audioUrl) {
		this.audioUrl = audioUrl;
	}

	public String getIntroduce() {
		return introduce;
	}

	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}

	public int getFloorCount() {
		return floorCount;
	}

	public void setFloorCount(int floorCount) {
		this.floorCount = floorCount;
	}

}
