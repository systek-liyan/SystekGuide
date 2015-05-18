package com.app.guide.bean;

import java.util.List;

public class ExhibitBean {
	private String name;// 展品名
	private String address;// 展厅
	private String dynasty;// 展品年代
	private String introduction;// 展品介绍
	private List<String> labelList;//该展品拥有的标签
	private String imgUrl;// 图标的Url地址

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
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
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the dynasty
	 */
	public String getDynasty() {
		return dynasty;
	}

	/**
	 * @param dynasty
	 *            the dynasty to set
	 */
	public void setDynasty(String dynasty) {
		this.dynasty = dynasty;
	}

	/**
	 * @return the introduction
	 */
	public String getIntroduction() {
		return introduction;
	}

	/**
	 * @param introduction
	 *            the introduction to set
	 */
	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	/**
	 * @return the imgUrl
	 */
	public String getImgUrl() {
		return imgUrl;
	}

	/**
	 * @param imgUrl
	 *            the imgUrl to set
	 */
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public List<String> getLabelList() {
		return labelList;
	}

	public void setLabelList(List<String> labelList) {
		this.labelList = labelList;
	}
}
