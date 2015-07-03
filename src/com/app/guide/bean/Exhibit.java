package com.app.guide.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 展品信息类,用于随行导游界面数据显示
 */
public class Exhibit {

	private String id;
	private String name;// 展品名
	private String beaconUId;
	private String iconUrl;
	private List<ImageOption> imgList;
	private String audioUrl;
	private String textUrl;
	private String labels;
	private String lExhibitBeanId;
	private String rExhibitBeanId;

	public Exhibit() {
		super();
	}

	public Exhibit(String id, String name, String beaconUId, String iconUrl,
			ArrayList<ImageOption> imgList, String audioUrl, String textUrl,
			String labels, String lExhibitBeanId,
			String rExhibitBeanId) {
		this.id = id;
		this.name = name;
		this.beaconUId = beaconUId;
		this.iconUrl = iconUrl;
		this.imgList = imgList;
		this.audioUrl = audioUrl;
		this.textUrl = textUrl;
		this.labels = labels;
		this.lExhibitBeanId = lExhibitBeanId;
		this.rExhibitBeanId = rExhibitBeanId;
	}

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

	public String getBeaconUId() {
		return beaconUId;
	}

	public void setBeaconUId(String beaconUId) {
		this.beaconUId = beaconUId;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public List<ImageOption> getImgList() {
		return imgList;
	}

	public void setImgList(List<ImageOption> imgList) {
		this.imgList = imgList;
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

	public String getLabels() {
		return labels;
	}

	public void setLabels(String labels) {
		this.labels = labels;
	}

	public String getlExhibitBeanId() {
		return lExhibitBeanId;
	}

	public void setlExhibitBeanId(String lExhibitBeanId) {
		this.lExhibitBeanId = lExhibitBeanId;
	}

	public String getrExhibitBeanId() {
		return rExhibitBeanId;
	}

	public void setrExhibitBeanId(String rExhibitBeanId) {
		this.rExhibitBeanId = rExhibitBeanId;
	}
}
