package com.app.guide.offline;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "label")
public class OfflineLabelBean {

	@DatabaseField(generatedId = true)
	private int id;
	@DatabaseField(columnName = "museumId")
	private int museumId;
	@DatabaseField(columnName = "name")
	private String name;
	@DatabaseField(columnName = "lables")
	private String labels;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMuseumId() {
		return museumId;
	}

	public void setMuseumId(int museumId) {
		this.museumId = museumId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabels() {
		return labels;
	}

	public void setLabels(String labels) {
		this.labels = labels;
	}

}
