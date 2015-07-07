package com.app.guide.bean;

/**
 * 列表 展品信息
 */
public class ExhibitBean {
	private String id;  //展品id
	private String name;// 展品名
	private String address;//展品所在位置（展厅）
	private String introduction;// 展品介绍
	private String imgUrl;// 图标的Url地址
	private String labels; // 标签

	public ExhibitBean(String id, String name, String address,
			String introduction, String imgUrl, String labels) {
		this.id = id;
		this.name = name;
		this.address = address;
		this.introduction = introduction;
		this.imgUrl = imgUrl;
		this.labels = labels;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

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

	/**
	 * @return the labels
	 */
	public String getLabels() {
		return labels;
	}

	/**
	 * @param labels
	 *            the labels to set
	 */
	public void setLabels(String labels) {
		this.labels = labels;
	}

}
