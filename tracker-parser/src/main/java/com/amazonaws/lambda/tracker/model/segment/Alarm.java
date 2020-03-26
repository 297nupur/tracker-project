package com.amazonaws.lambda.tracker.model.segment;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Alarm implements Serializable {
	private static final long serialVersionUID = -5372618509967437618L;
	private Map<AlarmType, Long> alarmMap;

	public Alarm() {}

	public Alarm setAlarm(AlarmType alarm, Long time) {
		if (alarmMap == null) {
			alarmMap = new HashMap<>();
		}
		alarmMap.put(alarm, time);
		return this;
	}

	public void removeAlarm(AlarmType alarm) {
		if (alarmMap != null) {
			alarmMap.remove(alarm);
		}
	}

	public boolean hasAlarm(AlarmType alarmType) {
		return alarmMap != null && alarmMap.keySet().contains(alarmType);
	}

	public boolean hasAny() {
		return alarmMap != null && !alarmMap.keySet().isEmpty();
	}

	public Set<AlarmType> getAlarms() {
		if (alarmMap == null) {
			return null;
		}
		return alarmMap.keySet();
	}

	public Long getAlarmTriggeredAt(AlarmType alarmType) {
		if (!alarmMap.containsKey(alarmType)) {
			return null;
		}
		return alarmMap.get(alarmType);
	}
}
