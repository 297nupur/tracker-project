package com.amazonaws.lambda.tracker.model.segment;

import java.io.Serializable;

public class Sensors implements Serializable {
	private static final long serialVersionUID = 5064206044460640131L;
	private Integer humidity;
	private Integer pressure;
	private Integer light;
	private Integer tempProbe;
	private Integer irLight;
	private int[] orientation;
	private Boolean gpsJamming;

	public Sensors() {}

	public Sensors(Integer humidity, Integer pressure, Integer light, Integer tempProbe, Integer irLight,
	                    int[] orientation, Boolean gpsJamming) {
		this.humidity = humidity;
		this.pressure = pressure;
		this.light = light;
		this.tempProbe = tempProbe;
		this.irLight = irLight;
		this.orientation = orientation;
		this.gpsJamming = gpsJamming;
	}

	public Integer getHumidity() {
		return humidity;
	}

	public void setHumidity(Integer humidity) {
		this.humidity = humidity;
	}

	public Integer getPressure() {
		return pressure;
	}

	public void setPressure(Integer pressure) {
		this.pressure = pressure;
	}

	public Integer getLight() {
		return light;
	}

	public void setLight(Integer light) {
		this.light = light;
	}

	public Integer getTempProbe() {
		return tempProbe;
	}

	public void setTempProbe(Integer tempProbe) {
		this.tempProbe = tempProbe;
	}

	public Integer getIrLight() {
		return irLight;
	}

	public void setIrLight(Integer irLight) {
		this.irLight = irLight;
	}

	public int[] getOrientation() {
		return orientation;
	}

	public void setOrientation(int[] orientation) {
		this.orientation = orientation;
	}

	public Boolean getGpsJamming() {
		return gpsJamming;
	}

	public void setGpsJamming(Boolean gpsJamming) {
		this.gpsJamming = gpsJamming;
	}
}
