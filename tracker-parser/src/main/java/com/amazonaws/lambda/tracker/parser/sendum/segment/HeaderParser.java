package com.amazonaws.lambda.tracker.parser.sendum.segment;

import java.util.Stack;

import com.amazonaws.lambda.tracker.model.segment.Header;
import com.amazonaws.lambda.tracker.parser.exception.TrackerParsingException;

public class HeaderParser extends SegmentParser<Header> {
	public HeaderParser(Stack<String> stack) {
		super(stack);
	}

	@Override
	public Header parse() throws TrackerParsingException {
		return new Header(
				stack.pop(),
				stack.pop(),
				stack.pop(),
				stack.pop(),
				stack.pop(),
				stack.pop());
	}
}
