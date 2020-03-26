package com.amazonaws.lambda.tracker.data.validation.exception;

public class TrackerDataValidationException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4614730367427239329L;

	public TrackerDataValidationException() {}

	public TrackerDataValidationException(String message) {
		super(message);
	}

	public TrackerDataValidationException(String message, Throwable cause) {
		super(message, cause);
	}

	public TrackerDataValidationException(Throwable cause) {
		super(cause);
	}
}
