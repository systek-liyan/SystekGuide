package com.app.guide.bean;

/**
 * 存储城市列表的信息
 * @author joe_c
 *
 */
public class CityModel {
	private String CityName;//城市名称
	private String NameSort;//城市排序

	public String getCityName() {
		return CityName;
	}

	public void setCityName(String cityName) {
		CityName = cityName;
	}

	public String getNameSort() {
		return NameSort;
	}

	public void setNameSort(String nameSort) {
		NameSort = nameSort;
	}

}
