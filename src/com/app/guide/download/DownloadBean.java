package com.app.guide.download;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 管理数据包的下载
 * @author joe_c
 *
 */
@DatabaseTable(tableName = "downloadBean")
public class DownloadBean {

	@DatabaseField(id = true)
	private String museumId;
	@DatabaseField(columnName = "current")
	private long current;//当前下载进度
	@DatabaseField(columnName = "total")
	private long total;//下载数据的总大小
	@DatabaseField(columnName = "name")
	private String name;//博物馆名称
	@DatabaseField(columnName = "isCompleted", defaultValue = "false")
	private boolean isCompleted;

	public boolean isCompleted() {
		return isCompleted;
	}

	public void setCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	public String getMuseumId() {
		return museumId;
	}

	public void setMuseumId(String museumId) {
		this.museumId = museumId;
	}

	public long getCurrent() {
		return current;
	}

	public void setCurrent(long current) {
		this.current = current;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
