package com.amazonaws.lambda.tracker.data.validation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.amazonaws.lambda.tracker.data.validation.exception.TrackerDataValidationException;
import com.amazonaws.lambda.tracker.model.TrackerData;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.util.Base64;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TrackerDataLambdaFunctionHandler {
	JSONParser parser = new JSONParser();
	final AmazonSQS sqs = AmazonSQSClientBuilder.standard().withRegion("eu-west-1").build();

	public OutputStream handleRequest(InputStream input, OutputStream outputStream, Context context)
			throws TrackerDataValidationException, IOException {
		LambdaLogger logger = context.getLogger();
		logger.log("Calling Java Lambda handler of TrackerDataLambdaFunctionHandler" + input);

		String queueUrl = System.getenv("QUEUE_URL");
		if (queueUrl == null)
			throw new IllegalArgumentException("QUEUE_URL needs to be set as a Lambda environment variable");

		String vizixHost = System.getenv("VIZIX_HOST");
		if (vizixHost == null)
			throw new IllegalArgumentException("VIZIX_HOST needs to be set as a Lambda environment variable");

		String isSingleMappingCheckRequired = System.getenv("IS_SINGLE_MAPPING_CHECK_REQUIRED");
		if (isSingleMappingCheckRequired == null)
			throw new IllegalArgumentException(
					"IS_SINGLE_MAPPING_CHECK_REQUIRED needs to be set as a Lambda environment variable");

		String apiKeyEncrpted = System.getenv("API_KEY");
		if (apiKeyEncrpted == null)
			throw new IllegalArgumentException("API_KEY needs to be set as a Lambda environment variable");
		String apiKey = decryptKey(apiKeyEncrpted);

		final ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl)
				.withMaxNumberOfMessages(10);
		final List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
		logger.log(messages.size() + " messages read from queue..");
		if (messages.isEmpty())
			throw new TrackerDataValidationException("No Tracker data received from Queue.");

		JSONArray output = new JSONArray();
		ObjectMapper mapper = new ObjectMapper();

		messages.forEach(msg -> {
			try {
				TrackerData trackerData = mapper.readValue(msg.getBody(), TrackerData.class);
				JSONObject trackerDataVizix = getTrackerDataFromVizix(vizixHost, apiKey, logger,
						trackerData.getHeader().getTrackerType(), trackerData.getHeader().getDeviceId());
				if (!isDataExists4Tracker((JSONArray) trackerDataVizix.get("results"))) {
					logger.log("Data validation fails.. No data exists for this tracker  " + msg);
					deleteMessage(queueUrl, msg, logger);
					throw new TrackerDataValidationException(
							"Data validation fails.. No data exists for this tracker  " + msg);
				} else if (parseBoolean(isSingleMappingCheckRequired)
						&& !isSingleMappingExist4Tracker((JSONArray) trackerDataVizix.get("results"))) {
					logger.log("Data validation fails.. Multiple mapping fround for tracker  " + msg);
					deleteMessage(queueUrl, msg, logger);
					throw new TrackerDataValidationException(
							"Data validation fails.. Multiple mapping fround for tracker  " + msg);
				}
				trackerData = setSerialNumber(trackerData, trackerDataVizix);
				output.add(mapper.writeValueAsString(trackerData));
				logger.log("Need to forward this message to Vizix  " + msg);
				deleteMessage(queueUrl, msg, logger);
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (TrackerDataValidationException e) {
				e.printStackTrace();
			}
		});
		if (output.isEmpty())
			throw new TrackerDataValidationException("No Valid Tracker data available for routing to Vizix");
		OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
		writer.write(output.toJSONString());
		writer.close();
		return outputStream;
	}

	protected JSONObject getTrackerDataFromVizix(String vizixHost, String api_key, LambdaLogger logger,
			String trackerType, String trackerId) throws IOException, ParseException {

		HttpURLConnection conn = null;
		StringBuilder stringBuilder = new StringBuilder("https://" + vizixHost + "/riot-core-services/api/things");
		stringBuilder.append("?where=trackerType.value%3D%27");
		stringBuilder.append(trackerType);
		stringBuilder.append("%27%20%26%20trackerId.value%3D%27");
		stringBuilder.append(trackerId);
		stringBuilder.append(
				"%27&only=serialNumber%2Clocation&treeView=false&returnFavorite=false&reportApi=false&includeResults=true&includeTotal=true");

		URL url = new URL(stringBuilder.toString());
		conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("api_key", api_key);
		conn.setDoOutput(true);

		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line;
		StringBuffer response = new StringBuffer();

		while ((line = in.readLine()) != null) {
			response.append(line);
		}
		in.close();
		//logger.log("Response from wizix : " + conn.getResponseCode() + " with response body " + response);
		return (JSONObject) parser.parse(response.toString());
	}

	public boolean isSingleMappingExist4Tracker(JSONArray results) {
		return results.size() == 1 ? true : false;
	}

	public boolean isDataExists4Tracker(JSONArray results) {
		return results.size() == 0 ? false : true;
	}

	private void deleteMessage(String myQueueUrl, Message msg, LambdaLogger logger) {
		sqs.deleteMessage(new DeleteMessageRequest(myQueueUrl, msg.getReceiptHandle()));
		logger.log("Message deleted from queue" + msg);
	}

	private void deleteMessages(String myQueueUrl, List<Message> messages, LambdaLogger logger) {
		messages.forEach(msg -> {
			sqs.deleteMessage(new DeleteMessageRequest(myQueueUrl, msg.getReceiptHandle()));
			logger.log("Message deleted from queue" + msg);
		});
	}

	private TrackerData setSerialNumber(TrackerData trackerData, JSONObject trackerDataVizix) {
		String serialNumberFromVizix = (String) ((JSONObject) ((JSONArray) trackerDataVizix.get("results")).get(0))
				.get("serialNumber");
		trackerData.getHeader().setSerialNumber(serialNumberFromVizix);
		return trackerData;
	}

	private static String decryptKey(String apiKeyEncrpted) {
		byte[] encryptedKey = Base64.decode(System.getenv("API_KEY"));

		AWSKMS client = AWSKMSClientBuilder.defaultClient();

		DecryptRequest request = new DecryptRequest().withCiphertextBlob(ByteBuffer.wrap(encryptedKey));

		ByteBuffer plainTextKey = client.decrypt(request).getPlaintext();
		return new String(plainTextKey.array(), Charset.forName("UTF-8"));
	}

	protected boolean parseBoolean(String value) {
		return Boolean.valueOf(value);
	}
}
