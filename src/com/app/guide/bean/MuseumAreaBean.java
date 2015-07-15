package com.app.guide.bean;

/**
 * 展厅 数据存储类
 * @author yetwish
 *
 */
public class MuseumAreaBean {

	private String museumId; //所属的博物馆id
	private int floor; 		 //所在的楼层
	
	public MuseumAreaBean(){
		
	}
	
	public MuseumAreaBean(String museumId, int floor) {
		super();
		this.museumId = museumId;
		this.floor = floor;
	}

	public String getMuseumId() {
		return museumId;
	}

	public void setMuseumId(String museumId) {
		this.museumId = museumId;
	}

	public int getFloor() {
		return floor;
	}

	public void setFloor(int floor) {
		this.floor = floor;
	}
	
		
	
		
}
