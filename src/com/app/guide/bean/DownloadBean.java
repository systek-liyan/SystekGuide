package com.app.guide.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="download")
public class DownloadBean {
	
	@DatabaseField(generatedId = true, columnName = "id")
	private int id;
	@DatabaseField(columnName = "museumid")
	private String museumId;
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the museumId
	 */
	public String getMuseumId() {
		return museumId;
	}
	/**
	 * @param museumId the museumId to set
	 */
	public void setMuseumId(String museumId) {
		this.museumId = museumId;
	}
}
