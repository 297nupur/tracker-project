package com.amazonaws.lambda.tracker.parser.sendum.segment;

import java.util.Stack;

import com.amazonaws.lambda.tracker.model.segment.Network;
import com.amazonaws.lambda.tracker.parser.exception.TrackerParsingException;

public class NetworkParser extends SegmentParser<Network> {
	private enum Property {
		RSSI,
		SID
	}

	public NetworkParser(Stack<String> stack) {
		super(stack);
	}

	@Override
	public Network parse() throws TrackerParsingException {
		Network network = new Network();
		Pair pair = fetchPair(stack);
		Property propertyType = parsePropertyType(pair);
		while (propertyType != null) {
			switch (propertyType) {
				case RSSI:
					network.setRssi(parseInt(pair.getValue()));
					break;
				case SID:
					network.setSid(parseInt(pair.getValue()));
					break;
			}
			stack.remove(pair.getOrigin());
			pair = fetchPair(stack);
			propertyType = parsePropertyType(pair);
		}
		return network;
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
