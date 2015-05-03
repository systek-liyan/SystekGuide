package com.app.guide.bean;

import java.util.ArrayList;

public class LabelBean {

	private String belong;
	private ArrayList<String> labels;
	
	public LabelBean(String belong, ArrayList<String> labels) {
		super();
		this.belong = belong;
		this.labels = labels;
	}
	/**
	 * @return the belong
	 */
	public String getBelong() {
		return belong;
	}
	/**
	 * @param belong the belong to set
	 */
	public void setBelong(String belong) {
		this.belong = belong;
	}
	/**
	 * @return the labels
	 */
	public ArrayList<String> getLabels() {
		return labels;
	}
	/**
	 * @param labels the labels to set
	 */
	public void setLabels(ArrayList<String> labels) {
		this.labels = labels;
	}
	
	
}
