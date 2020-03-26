package com.amazonaws.lambda.tracker.parser.exception;

public class TrackerParsingException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public TrackerParsingException() {}

	public TrackerParsingException(String message) {
		super(message);
	}

	public TrackerParsingException(String message, Throwable cause) {
		super(message, cause);
	}

	public TrackerParsingException(Throwable cause) {
		super(cause);
	}
}
