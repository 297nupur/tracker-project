package com.amazonaws.lambda.tracker.sendum.parser;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.amazonaws.lambda.tracker.model.TrackerData;
import com.amazonaws.lambda.tracker.parser.exception.TrackerParsingException;
import com.amazonaws.lambda.tracker.parser.sendum.SendumParser;
import com.amazonaws.lambda.tracker.parser.sensatag.SenatagParser;
import com.amazonaws.services.lambda.runtime.Context;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
@RunWith(MockitoJUnitRunner.class)
public class TrackersParserLambdaFunctionHandlerTest {
	JSONParser parser = new JSONParser();
	private InputStream inputSenatagStream;
	private InputStream inputSendumStream;

	@Before
	public void setUp() throws IOException {
		inputSenatagStream = TestUtils.parse("/api-gateway-senatag-stream.json", InputStream.class);
		inputSendumStream = TestUtils.parse("/api-gateway-sendum-stream.json", InputStream.class);
	}

	private Context createContext() {
		TestContext ctx = new TestContext();
		ctx.setFunctionName("Your Function Name");
		return ctx;
	}

	@Test
	public void testSendumParserLambdaFunctionHandler() throws IOException, ParseException, TrackerParsingException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputSendumStream));
		JSONObject input = (JSONObject) parser.parse(reader);
		SendumParser parser = new SendumParser();
		TrackerData trackerData = parser.parse(input);
		assertEquals(Double.valueOf("217.70404"), trackerData.getLocation().getLatitude());
	}

	@Test
	public void testParsingSenatag() throws IOException, ParseException, TrackerParsingException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputSenatagStream));
		JSONObject input = (JSONObject) parser.parse(reader);
		SenatagParser parser = new SenatagParser();
		TrackerData trackerData = parser.parse(input);
		assertEquals("E84B35", trackerData.getHeader().getDeviceId());
		assertEquals(Double.valueOf("40.44985"), trackerData.getLocation().getLatitude());
	}
}
