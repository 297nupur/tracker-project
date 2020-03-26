package com.amazonaws.lambda.tracker.parser.sendum.segment;

import java.util.Stack;
import com.amazonaws.lambda.tracker.model.segment.Status;
import com.amazonaws.lambda.tracker.parser.exception.TrackerParsingException;

public class StatusParser extends SegmentParser<Status> {
	private enum Property {
		TIME,
		CAP_REMAINING,
		CAP_FULL,
		VOLTAGE,
		CYCLE,
		TEMPERATURE_F,
		TEMPERATURE_C
	}

	public StatusParser(Stack<String> stack) {
		super(stack);
	}

	@Override
	public Status parse() throws TrackerParsingException {
		Status statusPT300 = new Status();
		Pair pair = fetchPair(stack);
		Property propertyType = parsePropertyType(pair);
		while (propertyType != null) {
			switch (propertyType) {
				case TIME:
					statusPT300.setTime(parseTimestamp(pair.getValue()));
					break;
				case CAP_FULL:
					statusPT300.setCapacityFull(parseInt(pair.getValue()));
					break;
				case CAP_REMAINING:
					statusPT300.setCapacityRemaining(parseInt(pair.getValue()));
					break;
				case CYCLE:
					statusPT300.setCycle(parseInt(pair.getValue()));
					break;
				case VOLTAGE:
					statusPT300.setVoltage(parseInt(pair.getValue()));
					break;
				case TEMPERATURE_C:
					statusPT300.setTemperatureC(parseDouble(pair.getValue()));
					break;
				case TEMPERATURE_F:
					statusPT300.setTemperatureF(parseDouble(pair.getValue()));
					break;
			}
			stack.remove(pair.getOrigin());
			pair = fetchPair(stack);
			propertyType = parsePropertyType(pair);
		}
		return statusPT300;
	}

	private Property parsePropertyType(Pair pair) {
		if (pair == null) {
			return null;
		}
		for(Property property : Property.values()) {
			if (property.name().equals(pair.getKey())) {
				return property;
			}
		}
		return null;
	}
}
