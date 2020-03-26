package com.amazonaws.lambda.tracker.parser.sendum.segment;

import java.util.Stack;

import com.amazonaws.lambda.tracker.model.segment.Sensors;
import com.amazonaws.lambda.tracker.parser.exception.TrackerParsingException;

public class SensorsParser extends SegmentParser<Sensors> {
	private enum Property {
		HUMIDITY, PRESSURE, LIGHT, TEMPPROBE, IRLIGHT, ORIENTATION, GPSJAMMING,
	}

	private Pair pair = null;

	public SensorsParser(Stack<String> stack) {
		super(stack);
	}

	@Override
	public Sensors parse() throws TrackerParsingException {
		Sensors sensorsPT300 = new Sensors();
		pair = fetchPair(stack);
		Property propertyType = parsePropertyType(pair);
		while (propertyType != null) {
			switch (propertyType) {
			case HUMIDITY:
				sensorsPT300.setHumidity(parseInt(pair.getValue()));
				break;
			case PRESSURE:
				sensorsPT300.setPressure(parseInt(pair.getValue()));
				break;
			case TEMPPROBE:
				sensorsPT300.setTempProbe(parseInt(pair.getValue()));
				break;
			case LIGHT:
				sensorsPT300.setLight(parseInt(pair.getValue()));
				break;
			case IRLIGHT:
				sensorsPT300.setIrLight(parseInt(pair.getValue()));
				break;
			case ORIENTATION:
				sensorsPT300.setOrientation(parseOrientation(stack));
				break;
			case GPSJAMMING:
				sensorsPT300.setGpsJamming(parseBoolean(pair.getValue()));
				break;
			}
			stack.remove(pair.getOrigin());
			pair = fetchPair(stack);
			propertyType = parsePropertyType(pair);
		}
		return sensorsPT300;
	}

	private int[] parseOrientation(Stack<String> stack) throws TrackerParsingException {
		int[] orientation = new int[3];
		orientation[0] = parseInt(pair.getValue());
		stack.remove(pair.getOrigin());
		pair = fetchPair(stack);
		orientation[1] = parseInt(pair.getKey());
		stack.remove(pair.getOrigin());
		pair = fetchPair(stack);
		orientation[2] = parseInt(pair.getKey());
		return orientation;
	}

	private Property parsePropertyType(Pair pair) {
		if (pair == null) {
			return null;
		}
		for (Property property : Property.values()) {
			if (property.name().equals(pair.getKey())) {
				return property;
			}
		}
		return null;
	}
}
