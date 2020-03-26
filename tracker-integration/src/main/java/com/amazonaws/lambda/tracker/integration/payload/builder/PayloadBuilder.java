package com.amazonaws.lambda.tracker.integration.payload.builder;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;
import org.json.simple.JSONObject;
import com.amazonaws.lambda.tracker.model.TrackerData;
import com.amazonaws.lambda.tracker.model.segment.Location;
import com.amazonaws.lambda.tracker.model.segment.Sensors;
import com.amazonaws.lambda.tracker.model.segment.Status;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PayloadBuilder {
	public static Function<String, JSONObject> transformToVizixData = new Function<String, JSONObject>() {

		public JSONObject apply(String trackerData) {
			ObjectMapper mapper = new ObjectMapper();
			JSONObject payload = new JSONObject();
			TrackerData tracker;
			try {
				tracker = mapper.readValue(trackerData, TrackerData.class);
				JSONObject udfs = new JSONObject();
				payload.put("group", tracker.getHeader().getGroup());
				payload.put("serialNumber", tracker.getHeader().getSerialNumber());
				payload.put("thingTypeCode", tracker.getHeader().getThingTypeCode());
				payload.put("udfs", udfs);
				
				JSONObject uri = new JSONObject();
				uri.put("value","IOT Tracker");
				udfs.put("bizStepUri", uri);
				udfs.put("readPointUri", uri);

				Optional<Location> location = Optional.ofNullable(tracker.getLocation());
				setLocationUDFS(location, udfs);

				Optional<Status> status = Optional.ofNullable(tracker.getStatus());
				setStatusUDFS(status,udfs);
				
				Optional<Sensors> sensors = Optional.ofNullable(tracker.getSensors());
				setSensorsUDF(sensors,udfs);
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return payload;
		}
	};

	private static void setLocationUDFS(Optional<Location> location, JSONObject udfs) {
		location.map(Location::getLongitude).ifPresent(lon -> {
			location.map(Location::getLatitude).ifPresent(lat -> {
				JSONObject loc = new JSONObject();
				loc.put("value", lon + ";" + lat + ";0.0");
				udfs.put("location", loc);
			});
		});

		location.map(Location::getTimeZone).ifPresent(timeZone -> {
			JSONObject tZone = new JSONObject();
			tZone.put("value", timeZone);
			udfs.put("trackerTimezone", tZone);
		});

		location.map(Location::getRange).ifPresent(range -> {
			JSONObject tRange = new JSONObject();
			tRange.put("value", range);
			udfs.put("trackerRange", tRange);
		});

		location.map(Location::getFixTimestamp).ifPresent(fixTimestamp -> {
			JSONObject fixTimestampO = new JSONObject();
			fixTimestampO.put("value", fixTimestamp);
			udfs.put("trackerLastGpsFix", fixTimestampO);
		});

	}

	private static void setStatusUDFS(Optional<Status> status, JSONObject udfs) {

		status.map(Status::getTime).ifPresent(time -> {
			JSONObject timeO = new JSONObject();
			timeO.put("value", String.valueOf(time));
			udfs.put("lastDetectTime", timeO);
			udfs.put("lastLocateTime", timeO);
		});

		status.map(Status::getCapacityRemaining).ifPresent(capRem -> {
			JSONObject batteryO = new JSONObject();
			batteryO.put("value", capRem);
			udfs.put("trackerBatteryLevel", batteryO);
		});

		status.map(Status::getVoltage).ifPresent(voltage -> {
			JSONObject volatgeO = new JSONObject();
			volatgeO.put("value", voltage);
			udfs.put("trackerBatteryVoltage", volatgeO);
		});

		status.map(Status::getCycle).ifPresent(cycleCount -> {
			JSONObject cycleO = new JSONObject();
			cycleO.put("value", cycleCount);
			udfs.put("trackerCycleCount", cycleO);
		});

		status.map(Status::getCapacityFull).ifPresent(fullChargeCap -> {
			JSONObject capFullO = new JSONObject();
			capFullO.put("value", fullChargeCap);
			udfs.put("trackerFullChargeCapacity", capFullO);
		});

		status.map(Status::getTemperatureC).ifPresent(tempC -> {
			JSONObject tempCO = new JSONObject();
			tempCO.put("value", tempC);
			udfs.put("trackerTemperatureCelcius", tempCO);
		});

	}

	public static void setSensorsUDF(Optional<Sensors> sensors, JSONObject udfs) {
		sensors.map(Sensors::getGpsJamming).ifPresent(gpsJammingState -> {
			JSONObject gpsJamO = new JSONObject();
			gpsJamO.put("value", gpsJammingState);
			udfs.put("trackerGpsJammingState", gpsJamO);
		});

		sensors.map(Sensors::getHumidity).ifPresent(humidity -> {
			JSONObject humidityO = new JSONObject();
			humidityO.put("value", humidity);
			udfs.put("trackerHumidity", humidityO);
		});

		sensors.map(Sensors::getIrLight).ifPresent(irLight -> {
			JSONObject irLightO = new JSONObject();
			irLightO.put("value", irLight);
			udfs.put("trackerIrLight", irLightO);
		});

		sensors.map(Sensors::getLight).ifPresent(lightDelta -> {
			JSONObject lightDeltaO = new JSONObject();
			lightDeltaO.put("value", lightDelta);
			udfs.put("trackerLightSensorValue", lightDeltaO);
		});

		sensors.map(Sensors::getOrientation).ifPresent(orientation -> {
			JSONObject orientationO = new JSONObject();
			orientationO.put("value", orientation);
			udfs.put("trackerOrientation", orientationO);
		});

		sensors.map(Sensors::getPressure).ifPresent(pressure -> {
			JSONObject pressureO = new JSONObject();
			pressureO.put("value", pressure);
			udfs.put("trackerPressure", pressureO);
		});
	}
}
