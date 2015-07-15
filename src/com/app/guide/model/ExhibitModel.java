package com.app.guide.model;

import java.util.ArrayList;
import java.util.List;


/**
 * 展品信息类,用于随行导游界面数据显示
 */
public class ExhibitModel {

	private String id;  //展品id
	private String name;// 展品名
	private String beaconId; //展品所属beacon的id
	private String iconUrl;   //展品图标Url
	private List<ImageModel> imgsUrl; //展品多图片列表
	private String audioUrl;  //展品音频 url
	private String textUrl;   //展品简介
	private String labels;    //展品拥有的标签，以“，”隔开
	private String lExhibitBeanId; //左边展品id
	private String rExhibitBeanId; //右边展品id

	public ExhibitModel() {
		super();
	}

	public ExhibitModel(String id, String name, String beaconUId, String iconUrl,
			ArrayList<ImageModel> imgList, String audioUrl, String textUrl,
			String labels, String lExhibitBeanId,
			String rExhibitBeanId) {
		this.id = id;
		this.name = name;
		this.beaconId = beaconUId;
		this.iconUrl = iconUrl;
		this.imgsUrl = imgList;
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
		return beaconId;
	}

	public void setBeaconUId(String beaconUId) {
		this.beaconId = beaconUId;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public List<ImageModel> getImgList() {
		return imgsUrl;
	}

	public void setImgList(List<ImageModel> imgList) {
		this.imgsUrl = imgList;
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
