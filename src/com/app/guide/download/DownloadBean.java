package com.app.guide.download;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 管理数据包的下载<br>
 * 
 * 一个DownloadBean表示对应一个下载任务 ： 某一个博物馆的离线数据
 * 
 * @author joe_c
 *
 */
@DatabaseTable(tableName = "downloadBean")
public class DownloadBean {

	@DatabaseField(id = true)
	private String museumId;//博物馆id
	@DatabaseField(columnName = "current")
	private long current;//当前下载进度
	@DatabaseField(columnName = "total")
	private long total;//下载数据的总大小
	@DatabaseField(columnName = "name")
	private String name;//博物馆名称
	@DatabaseField(columnName = "isCompleted", defaultValue = "false")
	private boolean isCompleted; //表示该下载任务是否已完成

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

}
