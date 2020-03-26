package com.amazonaws.lambda.tracker.model.segment;

public enum LocationFixType {
	/** MS-Assisted */
	MSAL,
	/** MS-Based */
	MSBL,
	/** Autonomous */
	AUTO,
	/**
	 * Not documented
	 * TODO: Ask SENDUM
	 **/
	SKYHOOK
}
