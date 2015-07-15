package com.app.guide.model;

import java.util.List;

public class LabelModel {

	private String name;
	private List<String> labels;

	public LabelModel(String name, List<String> list) {
		super();
		this.name = name;
		this.labels = list;
	}

	/**
	 * @return the belong
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param belong
	 *            the belong to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the labels
	 */
	public List<String> getLabels() {
		return labels;
	}

	/**
	 * @param labels
	 *            the labels to set
	 */
	public void setLabels(List<String> labels) {
		this.labels = labels;
	}

}
