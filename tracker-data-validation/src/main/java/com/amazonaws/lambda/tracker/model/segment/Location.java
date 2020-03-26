package com.amazonaws.lambda.tracker.model.segment;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Location implements Serializable {
	private static final long serialVersionUID = -1120046338600415208L;
	private Double latitude;
	private Double longitude;
	private Long fixTimestamp;
	private LocationFixType fixType;
	private Integer errorNumber;
	private Double accuracy;
	private String timeZone;
	private String range;

	public Location() {
	}

	public Location(double latitude, double longitude, Long fixTimestamp, String range, LocationFixType fixType) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.fixTimestamp = fixTimestamp;
		this.range = range;
		this.fixType = fixType;
	}

	public Location(Double latitude, Double longitude, Double accuracy) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.accuracy = accuracy;
	}

	public Location(Integer errorNumber, Long fixTimestamp) {
		this.errorNumber = errorNumber;
		this.fixTimestamp = fixTimestamp;
	}

}
