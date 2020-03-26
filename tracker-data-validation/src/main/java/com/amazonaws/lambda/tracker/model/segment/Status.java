package com.amazonaws.lambda.tracker.model.segment;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Status implements Serializable {
	private static final long serialVersionUID = 7229226018713334400L;
	private Integer capacityRemaining;
	private Integer capacityFull;
	private Integer voltage;
	private Integer cycle;
	private Double temperatureF;
	private Double temperatureC;
	private String messageType;
	private boolean isMoving;
	private Integer isClick;
	private Long time;

	public Status() {}
	

	public Status(Long time, int capacityRemaining, int capacityFull, int cycle, int voltage,
			Double temperatureF, Double temperatureC) {
		this.time = time;
		this.capacityRemaining = capacityRemaining;
		this.capacityFull = capacityFull;
		this.voltage = voltage;
		this.cycle = cycle;
		this.temperatureF = temperatureF;
		this.temperatureC = temperatureC;
	}
}
