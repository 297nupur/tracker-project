package com.amazonaws.lambda.tracker.data.validation;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class TrackerDataLambdaFunctionHandlerTest {
   
        
   @Test
    public void testVizixGetService() throws IOException, ParseException {
    	TrackerDataLambdaFunctionHandler handler = new TrackerDataLambdaFunctionHandler();
    	LambdaLogger logger = null;
    	JSONObject trackerDataVizix = handler.getTrackerDataFromVizix("vizix.eps.scan-track.eu", "73RHC1RGD7", logger, "SENSATAG", "E849A6");
    	assertEquals(true, handler.isSingleMappingExist4Tracker((JSONArray)trackerDataVizix.get("results")));			
    }
  
}
