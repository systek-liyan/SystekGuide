package com.app.guide.offline;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "exhibit")
public class OfflineExhibitBean {

	@DatabaseField(id = true, useGetSet = true)
	private String id;
	@DatabaseField(columnName = "name", useGetSet = true)
	private String name;
	@DatabaseField(columnName = "museumId", useGetSet = true)
	private String museumId;
	@DatabaseField(columnName = "beaconId", useGetSet = true)
	private String beaconId;
	@DatabaseField(columnName = "introduce", useGetSet = true)
	private String introduce;
	@DatabaseField(columnName = "address", useGetSet = true)
	private String address;
	@DatabaseField(columnName = "mapx", useGetSet = true)
	private float mapx;
	@DatabaseField(columnName = "mapy", useGetSet = true)
	private float mapy;
	@DatabaseField(defaultValue = "1", columnName = "floor", useGetSet = true)
	private int floor;
	@DatabaseField(columnName = "iconurl", useGetSet = true)
	private String iconurl;
	@DatabaseField(columnName = "imgsurl", useGetSet = true)
	private String imgsurl;
	@DatabaseField(columnName = "audiourl", useGetSet = true)
	private String audiourl;
	@DatabaseField(columnName = "texturl", useGetSet = true)
	private String texturl;
	@DatabaseField(columnName = "label", useGetSet = true)
	private String labels;
	@DatabaseField(columnName = "lexhibit")
	private String lexhibit;
	@DatabaseField(columnName = "rexhibit")
	private String rexhibit;
	@DatabaseField(columnName = "version", useGetSet = true)
	private int version;
	@DatabaseField(columnName = "priority")
	private int priority;
//	@DatabaseField(columnName = "filesize")
//	private long filesize;
//
//	public long getFilesize() {
//		return filesize;
//	}
//
//	public void setFilesize(long filesize) {
//		this.filesize = filesize;
//	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMuseumId() {
		return museumId;
	}

	public void setMuseumId(String museumId) {
		this.museumId = museumId;
	}

	public String getBeaconId() {
		return beaconId;
	}

	public void setBeaconId(String beaconId) {
		this.beaconId = beaconId;
	}

	public String getIntroduce() {
		return introduce;
	}

	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public float getMapx() {
		return mapx;
	}

	public void setMapx(float mapx) {
		this.mapx = mapx;
	}

	public float getMapy() {
		return mapy;
	}

	public void setMapy(float mapy) {
		this.mapy = mapy;
	}

	public int getFloor() {
		return floor;
	}

	public void setFloor(int floor) {
		this.floor = floor;
	}

	public String getIconurl() {
		return iconurl;
	}

	public void setIconurl(String iconurl) {
		this.iconurl = iconurl;
	}

	public String getImgsurl() {
		return imgsurl;
	}

	public void setImgsurl(String imgsurl) {
		this.imgsurl = imgsurl;
	}

	public String getAudiourl() {
		return audiourl;
	}

	public void setAudiourl(String audiourl) {
		this.audiourl = audiourl;
	}

	public String getTexturl() {
		return texturl;
	}

	public void setTexturl(String texturl) {
		this.texturl = texturl;
	}

	public String getLabels() {
		return labels;
	}

	public void setLabels(String labels) {
		this.labels = labels;
	}

	public String getLexhibit() {
		return lexhibit;
	}

	public void setLexhibit(String lexhibit) {
		this.lexhibit = lexhibit;
	}

	public String getRexhibit() {
		return rexhibit;
	}

	public void setRexhibit(String rexhibit) {
		this.rexhibit = rexhibit;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

}
