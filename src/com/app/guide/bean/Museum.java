package com.app.guide.bean;

public class Museum {
	private String name;
	private int iconResId;
	private String address;
	private boolean isClose;
	private String time;

	public Museum(String name, int iconResId, String address, boolean isClose,
			String time) {
		this.name = name;
		this.iconResId = iconResId;
		this.address = address;
		this.isClose = isClose;
		this.time = time;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIconResId() {
		return iconResId;
	}

	public void setIconResId(int iconResId) {
		this.iconResId = iconResId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public boolean isClose() {
		return isClose;
	}

	public void setClose(boolean isClose) {
		this.isClose = isClose;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}
