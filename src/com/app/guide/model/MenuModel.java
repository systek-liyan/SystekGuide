package com.app.guide.model;

/**
 * 菜单项  数据存储类，用以显示菜单
 * 
 * @author yetwish
 */
public class MenuModel {
	private int iconResId;
	private String title;

	public MenuModel(int iconResId, String title) {
		this.iconResId = iconResId;
		this.title = title;
	}

	public int getIconResId() {
		return iconResId;
	}

	public void setIconResId(int iconResId) {
		this.iconResId = iconResId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
