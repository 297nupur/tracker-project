package com.amazonaws.lambda.tracker.model;

import com.amazonaws.lambda.tracker.model.segment.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrackerData {
	private Header header;
	private Status status;
	private Sensors sensors;
	private Location location;
	private Alarm alarm;
	private Network network;

}
