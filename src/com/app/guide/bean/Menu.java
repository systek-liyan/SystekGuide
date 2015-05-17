package com.app.guide.bean;

public class Menu {
	private int iconResId;
	private String title;

	public Menu(int iconResId, String title) {
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
