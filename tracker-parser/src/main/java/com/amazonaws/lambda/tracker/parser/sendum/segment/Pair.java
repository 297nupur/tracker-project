package com.amazonaws.lambda.tracker.parser.sendum.segment;

public class Pair {
	private String key;
	private String value;
	private String origin;

	public Pair(String source) {
		origin = source;
		String[] split = origin.split("=");
		this.key = split[0];
		this.value = split.length == 1 ? null : split[1];
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
