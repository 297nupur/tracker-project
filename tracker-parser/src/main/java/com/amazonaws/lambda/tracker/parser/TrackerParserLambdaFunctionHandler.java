package com.amazonaws.lambda.tracker.parser;

import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.amazonaws.lambda.tracker.model.TrackerData;
import com.amazonaws.lambda.tracker.parser.exception.TrackerParsingException;
import com.amazonaws.lambda.tracker.parser.sendum.SendumParser;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.amazonaws.lambda.tracker.parser.sensatag.SenatagParser;

public class TrackerParserLambdaFunctionHandler implements RequestStreamHandler {
	JSONParser parser = new JSONParser();

	@Override
	public void handleRequest(InputStream input, OutputStream output, Context context) {
		LambdaLogger logger = context.getLogger();
		logger.log("Calling Java Lambda handler of TrackerParserLambdaFunctionHandler");
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));

		String queueUrl = System.getenv("QUEUE_URL");
		if (queueUrl == null)
			throw new IllegalArgumentException("QUEUE_URL needs to be set as a Lambda environment variable");
		String messageGroupId = System.getenv("MESSAGE_GROUP_ID");
		if (messageGroupId == null)
			throw new IllegalArgumentException("MESSAGE_GROUP_ID needs to be set as a Lambda environment variable");
		try {
			TrackerData parsedTrackerData = parseByTrackerType((JSONObject) parser.parse(reader), logger);
			logger.log("Parsed Object" + parsedTrackerData);
			ObjectMapper mapperObj = new ObjectMapper();
			String parsedJsonStr = mapperObj.writeValueAsString(parsedTrackerData);
			logger.log("Parsed as String" + parsedJsonStr);
			sendToSQS(messageGroupId, queueUrl, parsedJsonStr, logger);
		} catch (ParseException e) {
			logger.log("Exception caught: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.log("Exception caught: " + e.getMessage());
			e.printStackTrace();
		} catch (TrackerParsingException e) {
			logger.log("Exception caught: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private TrackerData parseByTrackerType(JSONObject trackerData, LambdaLogger logger)
			throws IOException, TrackerParsingException {
		TrackerData parsedData = null;
		String trackerType = (String) trackerData.get("trackerType");
		logger.log("Parsing input: " + trackerData + " from " + trackerType);
		switch (trackerType) {
		case Constants.SENSATAG_TRACKER: {
			parsedData = new SenatagParser().parse(trackerData);
			break;
		}
		case Constants.SENDUM_TRACKER: {
			parsedData = new SendumParser().parse(trackerData);
			break;
		}
		}
		return parsedData;
	}

	private static void sendToSQS(String messageGroupId, String queueUrl, String trackerData, LambdaLogger logger) {
		logger.log("Sending a message: " + trackerData + " to queue " + queueUrl);
		final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
		final SendMessageRequest sendMessageRequest = new SendMessageRequest(queueUrl, trackerData);
		sendMessageRequest.setMessageGroupId(messageGroupId);
		final SendMessageResult sendMessageResult = sqs.sendMessage(sendMessageRequest);
		logger.log("Message sent " + sendMessageResult);
	}
}