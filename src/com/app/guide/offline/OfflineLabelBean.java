package com.app.guide.offline;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "label")
public class OfflineLabelBean {

	@DatabaseField(id = true)
	private String id;
	@DatabaseField(columnName = "museumId")
	private String museumId;
	@DatabaseField(columnName = "name")
	private String name;
	@DatabaseField(columnName = "lables")
	private String lables;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMuseumId() {
		return museumId;
	}

	public void setMuseumId(String museumId) {
		this.museumId = museumId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String getLables() {
		return lables;
	}

	public void setLables(String lables) {
		this.lables = lables;
	}


	@Override
	public String toString() {
		return "OfflineLabelBean [id=" + id + ", museumId=" + museumId
				+ ", name=" + name + ", labels=" + lables + "]";
	}

}
