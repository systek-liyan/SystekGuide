package com.app.guide.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "cityBean")
public class CityBean {
	@DatabaseField( id=true )
	private String name;//城市名称
	@DatabaseField(columnName = "alpha")
	private String alpha;//城市名称首字母

	public CityBean(){
		
	}
			
	public CityBean(String name, String alpha) {
		super();
		this.name = name;
		this.alpha = alpha;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlpha() {
		return alpha;
	}

	public void setAlpha(String alpha) {
		this.alpha = alpha;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		return "CityBean,name:"+this.name+",alpha:"+this.alpha;
	}

}
