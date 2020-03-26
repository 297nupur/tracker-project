package com.amazonaws.lambda.tracker.model.segment;

import java.io.Serializable;

public class Network implements Serializable {
	private static final long serialVersionUID = -5010803049147239396L;
	private int rssi;
	private int sid;

	public Network() {}

	public Network(int rssi, int sid) {
		this.rssi = rssi;
		this.sid = sid;
	}

	public int getRssi() {
		return rssi;
	}

	public void setRssi(int rssi) {
		this.rssi = rssi;
	}

	public int getSid() {
		return sid;
	}

	public void setSid(int sid) {
		this.sid = sid;
	}
}
