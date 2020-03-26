package com.amazonaws.lambda.tracker.integration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.amazonaws.lambda.tracker.integration.payload.builder.PayloadBuilder;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.util.Base64;

public class TrackerIntegrationLambdaFunctionHandler implements RequestStreamHandler {
	JSONParser parser = new JSONParser();

	@Override
	public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
		LambdaLogger logger = context.getLogger();
		logger.log("Executing Java Lambda handler " + input);
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		JSONArray arr;
		try {
			String vizixHost = System.getenv("VIZIX_HOST");
			if (vizixHost == null)
				throw new IllegalArgumentException("VIZIX_HOST needs to be set as a Lambda environment variable");

			String apiKeyEncrpted = System.getenv("API_KEY");
			if (apiKeyEncrpted == null)
				throw new IllegalArgumentException("API_KEY needs to be set as a Lambda environment variable");
			String apiKey = decryptKey(apiKeyEncrpted);

			arr = ((JSONArray) parser.parse(reader));
			logger.log("TrackerData array received:  " + arr);
			routeToVizix(arr, logger, vizixHost, apiKey);
		} catch (IOException e) {
			logger.log("Exception caught: " + e.getMessage());
			e.printStackTrace();
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
	}

	private Integer routeToVizix(JSONArray messages, LambdaLogger logger, String vizixHost, String apiKey)
			throws IOException {
		JSONArray payloadArray = new JSONArray();

		messages.stream().map(PayloadBuilder.transformToVizixData).forEach(json -> {
			logger.log("Payload JSON Created: " + json);
			payloadArray.add(json);
		});
		URL url;
		HttpURLConnection conn = null;
		String endpoingtURL = "https://" + vizixHost
				+ "/riot-core-services/api/things/?verboseResult=true&useDefaultValue=true&upsert=false";
		url = new URL(endpoingtURL);
		allowMethods("PATCH");
		conn = (HttpURLConnection) url.openConnection();
		return sendPatchRequest(conn, apiKey, payloadArray, logger);
	}

	private static Integer sendPatchRequest(HttpURLConnection conn, String api_key, JSONArray array,
			LambdaLogger logger) throws IOException {
		conn.setRequestMethod("PATCH");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("api_key", api_key);
		conn.setDoOutput(true);
		logger.log("conn constructed " + conn.toString());
		logger.log("conn method " + conn.getRequestMethod());
		logger.log("Payload constructed " + array.toString());
		OutputStream os = conn.getOutputStream();
		os.write(array.toString().getBytes("UTF-8"));
		logger.log("Response from Vizix " + conn.getResponseCode());
		return conn.getResponseCode();
	}

	private static void allowMethods(String... methods) {
		try {
			Field methodsField = HttpURLConnection.class.getDeclaredField("methods");
			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(methodsField, methodsField.getModifiers() & ~Modifier.FINAL);
			methodsField.setAccessible(true);
			String[] oldMethods = (String[]) methodsField.get(null);
			Set<String> methodsSet = new LinkedHashSet<>(Arrays.asList(oldMethods));
			methodsSet.addAll(Arrays.asList(methods));
			String[] newMethods = methodsSet.toArray(new String[0]);
			methodsField.set(null, newMethods);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}

	private static String decryptKey(String apiKeyEncrpted) {
		byte[] encryptedKey = Base64.decode(System.getenv("API_KEY"));
		AWSKMS client = AWSKMSClientBuilder.defaultClient();
		DecryptRequest request = new DecryptRequest().withCiphertextBlob(ByteBuffer.wrap(encryptedKey));
		ByteBuffer plainTextKey = client.decrypt(request).getPlaintext();
		return new String(plainTextKey.array(), Charset.forName("UTF-8"));
	}
}
