package com.app.guide.download;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 记录下载的单个文件的信息<br>
 * 一个downloadInfo对应一个文件下载任务，该bean储存了文件下载的url和存放位置target,以及所属的museumId
 * 
 * @author joe_c
 * 
 */
@DatabaseTable(tableName = "downloadInfo")
public class DownloadInfo {

	/** 下载文件的源url */
	@DatabaseField(id = true)
	private String url;
	
	/** 下载文件的存储位置(SD卡中)  */
	@DatabaseField(columnName = "target")
	private String target;
	
	/** 下载文件所属的博物馆id */
	@DatabaseField(columnName = "museumId")
	private String museumId;
	
	public DownloadInfo() {
		super();
	}

	public DownloadInfo(String url, String target) {
		super();
		this.url = url;
		this.target = target;
	}

	/**
	 * @return 返回该下载任务的url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * 设置下载任务的url
	 * @param url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return 返回所要下载的文件的在硬盘上的存放位置
	 */
	public String getTarget() {
		return target;
	}

	/**
	 * 设置所要下载的文件的在硬盘上的存放位置
	 * @param target
	 */
	public void setTarget(String target) {
		this.target = target;
	}

	/**
	 * @return 该下载任务所属的博物馆Id
	 */
	public String getMuseumId() {
		return museumId;
	}

	/**
	 * 设置该下载任务所属的博物馆Id
	 * @param museumId
	 */
	public void setMuseumId(String museumId) {
		this.museumId = museumId;
	}

}
