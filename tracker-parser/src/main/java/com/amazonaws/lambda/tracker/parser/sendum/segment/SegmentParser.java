package com.amazonaws.lambda.tracker.parser.sendum.segment;

import java.math.BigInteger;
import java.util.Date;
import java.util.Stack;

import com.amazonaws.lambda.tracker.parser.exception.TrackerParsingException;

public abstract class SegmentParser<T> {
	/** Begin of the epoch: January 6 1980 00:00 */
	private static final long BEGIN_OF_EPOCH = 315964800000L;
	protected Stack<String> stack;

	public SegmentParser(Stack<String> stack) {
		this.stack = stack;
	}

	public abstract T parse() throws TrackerParsingException;

	protected Pair fetchPair(Stack<String> stack) {
		if (stack.isEmpty()) {
			return null;
		}
		return new Pair(stack.peek());
	}

	protected int parseInt(String value) throws TrackerParsingException {
		try {
			return Integer.valueOf(value);
		} catch (NumberFormatException ex) {
			throw new TrackerParsingException("Could not parse integer value '" + value + "'", ex);
		}
	}

	protected Double parseDouble(String value) throws TrackerParsingException {
		try {
			return Double.valueOf(value);
		} catch (NumberFormatException ex) {
			throw new TrackerParsingException("Could not parse double value '" + value + "'", ex);
		}
	}

	protected boolean parseBoolean(String value) throws TrackerParsingException {
		try {
			return Integer.valueOf(value).equals(1);
		} catch (NumberFormatException ex) {
			throw new TrackerParsingException("Could not parse boolean value '" + value + "'", ex);
		}
	}

	protected long parseTimestamp(String value) throws TrackerParsingException {
		try {
			Date date = new Date(BEGIN_OF_EPOCH + hexToDec(value).longValue() * 1000);
			return date.toInstant().toEpochMilli();
		} catch (NumberFormatException ex) {
			throw new TrackerParsingException("Could not parse timestamp value '" + value + "'", ex);
		}
	}

	protected double parseCoordinate(String value) {
		double fullValue = hexToDec(value).doubleValue() * 180 / 33554432; // 2^25
		return (double) Math.round(fullValue * 100000) / 100000;
	}

	public Number hexToDec(String hex) {
		if (hex == null) {
			throw new NullPointerException("hexToDec: hex String is null.");
		}
		if (hex.equals("")) {
			return Byte.valueOf("0");
		}
		hex = hex.toUpperCase();
		// Check if high bit is set.
		boolean isNegative = hex.startsWith("8") || hex.startsWith("9") || hex.startsWith("A") || hex.startsWith("B")
				|| hex.startsWith("C") || hex.startsWith("D") || hex.startsWith("E") || hex.startsWith("F");
		BigInteger temp;
		if (isNegative) {
			// Negative number
			temp = new BigInteger(hex, 16);
			BigInteger subtrahend = BigInteger.ONE.shiftLeft(hex.length() * 4);
			temp = temp.subtract(subtrahend);
		} else {
			// Positive number
			temp = new BigInteger(hex, 16);
		}
		// Cut BigInteger down to size.
		if (hex.length() <= 2) {
			return temp.byteValue();
		}
		if (hex.length() <= 4) {
			return temp.shortValue();
		}
		if (hex.length() <= 8) {
			return temp.intValue();
		}
		if (hex.length() <= 16) {
			return temp.longValue();
		}
		return temp;
	}
}
