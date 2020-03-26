package com.amazonaws.lambda.tracker.model.segment;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Header implements Serializable {
	private static final long serialVersionUID = -199846041089846764L;
	private String deviceId;
	private String trackerType;
	private String thingTypeCode;
	private String group;
	private String serialNumber;
	//tracker backend data
	private Long seqNr;
	private String psw3KeyHeader;
	private String messageQualifier;
	

	public Header() {}
	
	public Header(String deviceId, String trackerType, String thingTypeCode, String group,  Long seqNr) {
		this.deviceId = deviceId;
		this.seqNr = seqNr;
		this.trackerType = trackerType;
		this.thingTypeCode = thingTypeCode;
		this.group = group;
	}

	public Header(String group, String trackerType, String thingTypeCode, String deviceId, String psw3KeyHeader, String messageQualifier) {
		this.group = group;
		this.trackerType = trackerType;
		this.thingTypeCode = thingTypeCode;
		this.deviceId = deviceId;
		this.psw3KeyHeader = psw3KeyHeader;
		this.messageQualifier = messageQualifier;
	}

}
