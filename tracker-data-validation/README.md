# tracker-data-validation
AWS Lambda project implemented in java.
Configure environment variables: API_KEY (encrypted using KMS key), QUEUE_URL, VIZIX_HOST and IS_SINGLE_MAPPING_CHECK_REQUIRED (true/false).
This is first step of step function, it will send a GET request to Vizix And checks if data exists for this trackerId already. If not, throw exception so that step function will fall to CatchAllFallback state and stop.


# Test your Function
Open up TrackerDataLambdaFunctionHandler.java where handleRequest method is the entry point for the
lambda function.
Open up TrackerDataLambdaFunctionHandlerTest.java and then run it locally as a normal JUnit test
The unit test provides a sample JSON input file. You can modify the JSON file, or create new ones based on it.


# Upload your Function
Under Project or Package Explorer View, right-click on your project and select Amazon Web Services Upload Function to AWS Lambda.
Then follow the steps to create a new Lambda function or upload your code to an existing function.


# Invoke your Function
Now we are ready to run the function in the cloud. Right-click on your project again and select Amazon Web Services Run on AWS Lambda
In the input dialog, enter the JSON input for your function, or select one of the JSON files in your project
Click Invoke and check the output of your function in the IDE Console View


# Function logs
CloudWatch -> Logs -> /aws/lambda/tracker-data-validation

