package com.iis.fm.common;

import java.util.Map;

public class SortInfo {
	private final String property;

	private final SortDirection direction;

	public SortInfo(String property, SortDirection direction) {
		this.property = property;
		this.direction = direction;
	}

	/**
	 * @return the property/field on which to sort
	 */
	public String getProperty() {
		return property;
	}

	public SortDirection getDirection() {
		return direction;
	}

	public static SortInfo create(Map<String, Object> jsonData) {
		String property = (String) jsonData.get("property");
		String direction = (String) jsonData.get("direction");

		SortInfo sortInfo = new SortInfo(property, SortDirection.fromString(direction));

		return sortInfo;
	}

	@Override
	public String toString() {
		return "SortInfo [property=" + property + ", direction=" + direction + "]";
	}
}
