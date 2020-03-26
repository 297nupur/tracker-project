package com.amazonaws.lambda.tracker.parser.sendum.segment;

import java.util.Stack;

import com.amazonaws.lambda.tracker.model.segment.Location;
import com.amazonaws.lambda.tracker.model.segment.LocationFixType;
import com.amazonaws.lambda.tracker.parser.exception.TrackerParsingException;

public class LocationParser extends SegmentParser<Location> {
	public LocationParser(Stack<String> stack) {
		super(stack);
	}

	@Override
	public Location parse() throws TrackerParsingException {
		String firstEntry = stack.pop();
		if ("ERROR".equals(firstEntry)) {
			return new Location(
				parseInt(stack.pop()),
				parseTimestamp(stack.pop()));
		} else {
			return new Location(
				parseCoordinate(firstEntry),
				parseCoordinate(stack.pop()),
				parseTimestamp(stack.pop()),
				stack.pop() + ";" + stack.pop(),
				parseLocationFixType(stack.pop()));
		}
	}

	private LocationFixType parseLocationFixType(String value) throws TrackerParsingException {
		try {
			return LocationFixType.valueOf(value);
		} catch (IllegalArgumentException ex) {
			throw new TrackerParsingException("Could not parse LocationFixType '" + value + "'", ex);
		}
	}
}
