package com.amazonaws.lambda.tracker.parser.sensatag;

import java.util.Optional;
import java.util.function.Function;
import org.json.simple.JSONObject;
import com.amazonaws.lambda.tracker.model.TrackerData;
import com.amazonaws.lambda.tracker.model.segment.Header;
import com.amazonaws.lambda.tracker.model.segment.Location;
import com.amazonaws.lambda.tracker.model.segment.Status;
import com.amazonaws.lambda.tracker.parser.exception.TrackerParsingException;

public class SenatagParser {
	private static final String TEMPERATURE = "temperature";
	private static final String IS_CLICK = "isClick";
	private static final String LATEST_TEMPERATURE = "temperature8";
	private static final String MESSAGE_TYPE = "messageType";
	private static final String IS_MOVING = "isMoving";

	Function<JSONObject, TrackerData> transformToTrackerData = new Function<JSONObject, TrackerData>() {

		public TrackerData apply(JSONObject input) {
			TrackerData parsedSenatagData = new TrackerData();
			JSONObject trackerData = (JSONObject) input.get("body");
			parsedSenatagData.setHeader(new Header((String) trackerData.get("deviceId"),
					(String) input.get("trackerType"), (String) input.get("thingTypeCode"), (String) input.get("group"),
					(Long) trackerData.get("seqNr")));

			Optional<JSONObject> location = Optional.ofNullable((JSONObject) trackerData.get("location"));
			location.ifPresent(loc -> {
				parsedSenatagData.setLocation(new Location((Double) loc.get("latitude"), (Double) loc.get("longitude"),
						parseToString((Double) loc.get("accuracy"))));
			});

			Optional<JSONObject> payload = Optional.ofNullable((JSONObject) trackerData.get("payload"));

			payload.ifPresent(attr -> {
				Status status = new Status();
				if (attr.containsKey(TEMPERATURE)) {
					status.setTemperatureC((Double) attr.get("temperature"));
				}
				if (attr.containsKey(LATEST_TEMPERATURE))
					status.setTemperatureC((Double) attr.get(LATEST_TEMPERATURE));
				if (attr.containsKey(IS_CLICK))
					status.setIsClick(((Long) attr.get(IS_CLICK)).intValue());
				if (attr.containsKey(IS_MOVING))
					status.setMoving((boolean) attr.get(IS_MOVING));
				if (attr.containsKey(MESSAGE_TYPE))
					status.setMessageType((String) attr.get(MESSAGE_TYPE));
				parsedSenatagData.setStatus(status);
			});

			Status status = Optional.ofNullable((Status) parsedSenatagData.getStatus()).orElse((new Status()));
			status.setTime(((Long) trackerData.get("timestamp")) * 1000);
			parsedSenatagData.setStatus(status);
			return parsedSenatagData;
		}
	};

	public TrackerData parse(JSONObject input) throws TrackerParsingException {
		try {
			return transformToTrackerData.apply(input);
		} catch (Exception ex) {
			throw new TrackerParsingException("Exception occurred during Sensatag data parsing", ex);
		}
	}

	private String parseToString(Double value) {
		return String.valueOf(value);
	}
}
