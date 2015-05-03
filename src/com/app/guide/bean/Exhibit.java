package com.app.guide.bean;

import android.graphics.drawable.Drawable;

/**
 * 展品信息类
 * @author yetwish
 * @date 2015-4-19
 */
public class Exhibit {
	private String name;// 展品名
	private String address;// 展厅
	private String dynasty;// 展品年代
	private String introduction;// 展品介绍
	private Drawable icon; // 展品图标
	
	public Exhibit(String name, String address, String dynasty,
			String introduction, Drawable icon) {
		this.name = name;
		this.address = address;
		this.dynasty = dynasty;
		this.introduction = introduction;
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDynasty() {
		return dynasty;
	}

	public void setDynasty(String dynasty) {
		this.dynasty = dynasty;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

}
