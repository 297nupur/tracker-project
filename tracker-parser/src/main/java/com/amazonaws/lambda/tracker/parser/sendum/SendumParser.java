package com.amazonaws.lambda.tracker.parser.sendum;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import org.json.simple.JSONObject;

import com.amazonaws.lambda.tracker.model.TrackerData;
import com.amazonaws.lambda.tracker.parser.exception.TrackerParsingException;
import com.amazonaws.lambda.tracker.parser.sendum.segment.AlarmParser;
import com.amazonaws.lambda.tracker.parser.sendum.segment.HeaderParser;
import com.amazonaws.lambda.tracker.parser.sendum.segment.NetworkParser;
import com.amazonaws.lambda.tracker.parser.sendum.segment.SensorsParser;
import com.amazonaws.lambda.tracker.parser.sendum.segment.StatusParser;
import com.amazonaws.lambda.tracker.parser.sendum.segment.LocationParser;

public class SendumParser{
	
	private static final String DELIMITER = ",";
	private static final String HEADER_SEGMENT_NAME = "@RESPONSE";
	private static final String LOCATION_SEGMENT_NAME = "LOCATION";
	private static final String STATUS300_SEGMENT_NAME = "STATUS300";
	private static final String ALARM_SEGMENT_NAME = "ALARM";
	private static final String NETWORK_SEGMENT_NAME = "NETWORK";
	private static final String SENSORS_SEGMENT_NAME = "SENSORS";


	public TrackerData parse(JSONObject input) throws TrackerParsingException {
		String source = (String)input.get("body");
		if (!source.startsWith(HEADER_SEGMENT_NAME + DELIMITER)) {
			throw new TrackerParsingException("Malformed SENDUM message: '" + source + "'");
		}
		try {
			Stack<String> stack = retrieveStack((String)input.get("body"), (String)input.get("thingTypeCode"),  (String)input.get("trackerType"), (String)input.get("group") );
			TrackerData report = new TrackerData();
			report.setHeader(new HeaderParser(stack).parse());
			while (!stack.isEmpty()) {
				String element = stack.pop();
				if (LOCATION_SEGMENT_NAME.equals(element)) {
					report.setLocation(new LocationParser(stack).parse());
				} else if (STATUS300_SEGMENT_NAME.equals(element)) {
					report.setStatus(new StatusParser(stack).parse());
				} else if (ALARM_SEGMENT_NAME.equals(element)) {
					report.setAlarm(new AlarmParser(stack).parse());
				} else if (NETWORK_SEGMENT_NAME.equals(element)) {
					report.setNetwork(new NetworkParser(stack).parse());
				} else if (SENSORS_SEGMENT_NAME.equals(element)) {
					report.setSensors(new SensorsParser(stack).parse());
				}
			}
			return report;
		} catch (Exception ex) {
			throw new TrackerParsingException("Exception occurred during SENDUM report parsing", ex);
		}
	}
	

	private Stack<String> retrieveStack(String source, String thingTypeCode, String trackerType, String group) {
		List<String> elementList = Arrays.asList(source.split(DELIMITER));
		Stack<String> elementStack = new Stack<String>();
		Collections.reverse(elementList);
		elementStack.addAll(elementList);
		elementStack.pop();
		elementStack.add(thingTypeCode);
		elementStack.add(trackerType);
		elementStack.add(group);
		return elementStack;
	}
}