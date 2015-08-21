package com.app.guide.download;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 管理博物馆数据包的下载<br>
 * 
 * 一个DownloadBean对象，表示对应一个下载任务 ，即某一个博物馆的离线数据包
 * 
 * @author joe_c
 *
 */
@DatabaseTable(tableName = "downloadBean")
public class DownloadBean {

	/** 博物馆id */
	@DatabaseField(id = true)
	private String museumId;
	
	/** 博物馆名称 */
	@DatabaseField(columnName = "name")
	private String name;
	
	/** 博物馆所在城市 */
	@DatabaseField(columnName = "city")
	private String city;
	
	/** 当前下载进度 */
	@DatabaseField(columnName = "current", defaultValue = "0")
	private long current;
	
	/** 下载数据的总大小 */
	@DatabaseField(columnName = "total", defaultValue = "0")
	private long total;
	
	/** 表示该下载任务是否已完成 */
	@DatabaseField(columnName = "isCompleted", defaultValue = "false")
	private boolean isCompleted;
	
	/** 表示该下载(更新)完成的时间 */
	@DatabaseField(columnName = "updateDate")
	private Date updateDate; 

	/**
	 * @return 该下载任务是否已完成
	 */
	public boolean isCompleted() {
		return isCompleted;
	}

	/**
	 * 设置下载任务的完成情况
	 * @param isCompleted true:表示已完成； false：表示未完成
	 */
	public void setCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	/**
	 * @return 下载的博物馆id
	 */
	public String getMuseumId() {
		return museumId;
	}

	/**
	 * @param museumId 要下载的博物馆id
	 */
	public void setMuseumId(String museumId) {
		this.museumId = museumId;
	}

	/**
	 * @return 返回当前已下载的字节数 :long 
	 */
	public long getCurrent() {
		return current;
	}

	/**
	 * 设置当前已下载的字节数 
	 * @param current 下载进度：long 
	 */
	public void setCurrent(long current) {
		this.current = current;
	}

	/**
	 * @return 返回该下载任务的总字节数:long
	 */
	public long getTotal() {
		return total;
	}

	/**
	 * 设置该下载任务的总字节数
	 * @param total 总字节数:long
	 */
	public void setTotal(long total) {
		this.total = total;
	}
	
	/**
	 * @return 返回本次下载(更新)完成的时间:Date
	 */
	public Date getUpdateDate() {
		return updateDate;
	}

	/**
	 * 设置本次下载（更新)完成的时间
	 * @param updateDate
	 */
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	/**
	 * @return 下载的博物馆的名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name 要下载的博物馆的名称
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/** 获取博物馆所在城市 */
	public String getCity() {
		return city;
	}

	/**
	 * 设置博物馆所在城市
	 * @param city
	 */
	public void setCity(String city) {
		this.city = city;
	}

	@Override
	public String toString() {
		String Completed = this.isCompleted?"true":"false";
		return "Id,name,city,Completed,current,total="+museumId+","+name+","+city+",isCompleted="+ Completed +
				","+current+","+total;
	}
	
}
