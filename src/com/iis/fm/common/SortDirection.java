package com.iis.fm.common;


public enum SortDirection {
	/**
     * Ascending order
     */
    ASC,
    /**
     * Descending order
     */
    DESC;
    
    public static SortDirection fromString(String name) {
		if (ASC.name().equalsIgnoreCase(name)) {
			return ASC;
		} else if (DESC.name().equalsIgnoreCase(name)) {
			return DESC;
		}

		return null;
	}
}
