package com.app.guide.offline;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "exhibit")
public class OfflineExhibitBean {

	@DatabaseField(generatedId = true, useGetSet = true)
	private int id;
	@DatabaseField(columnName = "name", useGetSet = true)
	private String name;
	@DatabaseField(columnName = "museumId", useGetSet = true)
	private int museumId;
	@DatabaseField(columnName = "beaconId", useGetSet = true)
	private String beaconUId;
	@DatabaseField(columnName = "introduce", useGetSet = true)
	private String introduce;
	@DatabaseField(columnName = "address", useGetSet = true)
	private String address;
	@DatabaseField(columnName = "mapX", useGetSet = true)
	private float mapX;
	@DatabaseField(columnName = "mapY", useGetSet = true)
	private float mapY;
	@DatabaseField(defaultValue = "1", columnName = "floor", useGetSet = true)
	private int floor;
	@DatabaseField(columnName = "iconUrl", useGetSet = true)
	private String iconUrl;
	@DatabaseField(columnName = "imgJson", useGetSet = true)
	private String imgJson;
	@DatabaseField(columnName = "audioUrl", useGetSet = true)
	private String audioUrl;
	@DatabaseField(columnName = "textUrl", useGetSet = true)
	private String textUrl;
	@DatabaseField(columnName = "labelJson", useGetSet = true)
	private String labelJson;
	@DatabaseField(columnName = "isBoutique")
	private boolean isBoutique;
	@DatabaseField(columnName = "lExhibitBeanId")
	private int lExhibitBeanId;
	@DatabaseField(columnName = "rExhibitBeanId")
	private int rExhibitBeanId;
	@DatabaseField(columnName = "version", useGetSet = true)
	private int version;

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
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the museumId
	 */
	public int getMuseumId() {
		return museumId;
	}

	/**
	 * @param museumId
	 *            the museumId to set
	 */
	public void setMuseumId(int museumId) {
		this.museumId = museumId;
	}

	/**
	 * @return the beaconId
	 */
	public String getBeaconUId() {
		return beaconUId;
	}

	/**
	 * @param beaconId
	 *            the beaconId to set
	 */
	public void setBeaconUId(String beaconId) {
		this.beaconUId = beaconId;
	}

	/**
	 * @return the introduce
	 */
	public String getIntroduce() {
		return introduce;
	}

	/**
	 * @param introduce
	 *            the introduce to set
	 */
	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the mapX
	 */
	public float getMapX() {
		return mapX;
	}

	/**
	 * @param mapX
	 *            the mapX to set
	 */
	public void setMapX(float mapX) {
		this.mapX = mapX;
	}

	/**
	 * @return the mapY
	 */
	public float getMapY() {
		return mapY;
	}

	/**
	 * @param mapY
	 *            the mapY to set
	 */
	public void setMapY(float mapY) {
		this.mapY = mapY;
	}

	/**
	 * @return the floor
	 */
	public int getFloor() {
		return floor;
	}

	/**
	 * @param floor
	 *            the floor to set
	 */
	public void setFloor(int floor) {
		this.floor = floor;
	}

	/**
	 * @return the iconUrl
	 */
	public String getIconUrl() {
		return iconUrl;
	}

	/**
	 * @param iconUrl
	 *            the iconUrl to set
	 */
	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	/**
	 * @return the imgJson
	 */
	public String getImgJson() {
		return imgJson;
	}

	/**
	 * @param imgJson
	 *            the imgJson to set
	 */
	public void setImgJson(String imgJson) {
		this.imgJson = imgJson;
	}

	/**
	 * @return the audioUrl
	 */
	public String getAudioUrl() {
		return audioUrl;
	}

	/**
	 * @param audioUrl
	 *            the audioUrl to set
	 */
	public void setAudioUrl(String audioUrl) {
		this.audioUrl = audioUrl;
	}

	/**
	 * @return the textUrl
	 */
	public String getTextUrl() {
		return textUrl;
	}

	/**
	 * @param textUrl
	 *            the textUrl to set
	 */
	public void setTextUrl(String textUrl) {
		this.textUrl = textUrl;
	}

	/**
	 * @return the labelJson
	 */
	public String getLabelJson() {
		return labelJson;
	}

	/**
	 * @param labelJson
	 *            the labelJson to set
	 */
	public void setLabelJson(String labelJson) {
		this.labelJson = labelJson;
	}

	/**
	 * @return the isBoutique
	 */
	public boolean isBoutique() {
		return isBoutique;
	}

	/**
	 * @param isBoutique
	 *            the isBoutique to set
	 */
	public void setBoutique(boolean isBoutique) {
		this.isBoutique = isBoutique;
	}

	public int getlExhibitBeanId() {
		return lExhibitBeanId;
	}

	public void setlExhibitBeanId(int lExhibitBeanId) {
		this.lExhibitBeanId = lExhibitBeanId;
	}

	public int getrExhibitBeanId() {
		return rExhibitBeanId;
	}

	public void setrExhibitBeanId(int rExhibitBeanId) {
		this.rExhibitBeanId = rExhibitBeanId;
	}

	/**
	 * @return the version
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(int version) {
		this.version = version;
	}

}
