package com.app.guide.bean;

import java.util.ArrayList;
import java.util.List;

public class LabelBean {

	private String name;
	private List<String> labels;

	public LabelBean(String name, List<String> list) {
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
