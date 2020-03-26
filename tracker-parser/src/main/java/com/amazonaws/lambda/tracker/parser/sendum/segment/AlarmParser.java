package com.amazonaws.lambda.tracker.parser.sendum.segment;

import java.util.Stack;

import com.amazonaws.lambda.tracker.model.segment.Alarm;
import com.amazonaws.lambda.tracker.model.segment.AlarmType;
import com.amazonaws.lambda.tracker.parser.exception.TrackerParsingException;

public class AlarmParser extends SegmentParser<Alarm> {
	public AlarmParser(Stack<String> stack) {
		super(stack);
	}

	@Override
	public Alarm parse() throws TrackerParsingException {
		Alarm alarm = new Alarm();
		Pair pair = fetchPair(stack);
		AlarmType alarmType = getAlarmType(pair);
		while (alarmType != null) {
			if (alarmType != AlarmType.NONE) {
				alarm.setAlarm(alarmType, parseTimestamp(pair.getValue()));
			}
			stack.remove(pair.getOrigin());
			pair = fetchPair(stack);
			alarmType = getAlarmType(pair);
		}
		return alarm;
	}

	private AlarmType getAlarmType(Pair pair) throws TrackerParsingException {
		if (pair == null) {
			return null;
		}
		for (AlarmType alarmType : AlarmType.values()) {
			if (alarmType.name().equals(pair.getKey())) {
				return alarmType;
			}
		}
		return null;
	}
}
