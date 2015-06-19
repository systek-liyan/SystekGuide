package com.app.guide.download;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 记录下载的单个文件的信息
 * @author joe_c
 *
 */
@DatabaseTable(tableName = "downloadInfo")
public class DownloadInfo {
	
	@DatabaseField(id = true)
	private String url;
	@DatabaseField(columnName = "target")
	private String target;//下载文件的存储位置
	@DatabaseField(columnName = "museumId")
	private int museumId;
	
	public int getMuseumId() {
		return museumId;
	}

	public void setMuseumId(int museumId) {
		this.museumId = museumId;
	}

	public DownloadInfo(){
		super();
	}
	
	public DownloadInfo(String url, String target) {
		super();
		this.url = url;
		this.target = target;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	
}
