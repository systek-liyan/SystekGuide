package com.app.guide.offline;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "beacon")
public class OfflineBeaconBean {

	@DatabaseField(generatedId = true, useGetSet = true)
	private int id;
	@DatabaseField(columnName = "uuid", useGetSet = true)
	private String uuid;
	@DatabaseField(columnName = "personX", useGetSet = true)
	private float personX;
	@DatabaseField(columnName = "personY", useGetSet = true)
	private float personY;
	@DatabaseField(columnName = "type", useGetSet = true)
	private int type;

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
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

	/**
	 * @return the personX
	 */
	public float getPersonX() {
		return personX;
	}

	/**
	 * @param personX
	 *            the personX to set
	 */
	public void setPersonX(float personX) {
		this.personX = personX;
	}

	/**
	 * @return the personY
	 */
	public float getPersonY() {
		return personY;
	}

	/**
	 * @param personY
	 *            the personY to set
	 */
	public void setPersonY(float personY) {
		this.personY = personY;
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
