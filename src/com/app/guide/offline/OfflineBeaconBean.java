package com.app.guide.offline;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "beacon")
public class OfflineBeaconBean {

	@DatabaseField(id = true, useGetSet = true)
	private String id;
	@DatabaseField(columnName = "uuid", useGetSet = true)
	private String uuid;
	@DatabaseField(columnName = "personX", useGetSet = true)
	private float personx;
	@DatabaseField(columnName = "personY", useGetSet = true)
	private float persony;
	@DatabaseField(columnName = "type", useGetSet = true)
	private int type;
	@DatabaseField(columnName = "major")
	private String major;
	@DatabaseField(columnName = "minor")
	private String minor;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the uuid
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * @param uuid
	 *            the uuid to set
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}


	public float getPersonx() {
		return personx;
	}

	public void setPersonx(float personx) {
		this.personx = personx;
	}

	public float getPersony() {
		return persony;
	}

	public void setPersony(float persony) {
		this.persony = persony;
	}

	public String getMajor() {
		return major;
	}

	public void setMajor(String major) {
		this.major = major;
	}

	public String getMinor() {
		return minor;
	}

	public void setMinor(String minor) {
		this.minor = minor;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

}
