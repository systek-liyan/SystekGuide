package com.app.guide.offline;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "map")
public class OfflineMapBean {

	@DatabaseField(generatedId = true)
	private int id;
	@DatabaseField(columnName = "museumId", useGetSet = true)
	private String museumId;
	@DatabaseField(columnName = "imgUrl", useGetSet = true)
	private String imgUrl;
	@DatabaseField(columnName = "floor", useGetSet = true)
	private int floor;
	@DatabaseField(columnName = "version", useGetSet = true)
	private int version;
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
	/**
	 * @return the imgUrl
	 */
	public String getImgUrl() {
		return imgUrl;
	}
	/**
	 * @param imgUrl the imgUrl to set
	 */
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	/**
	 * @return the floor
	 */
	public int getFloor() {
		return floor;
	}
	/**
	 * @param floor the floor to set
	 */
	public void setFloor(int floor) {
		this.floor = floor;
	}
	/**
	 * @return the version
	 */
	public int getVersion() {
		return version;
	}
	/**
	 * @param version the version to set
	 */
	public void setVersion(int version) {
		this.version = version;
	}

}
