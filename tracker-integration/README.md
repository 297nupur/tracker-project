# tracker-integration
AWS Lambda project implemented in java.
Environment variables: VIZIX_HOST, API_KEY (encrypted with KMS key)
This is second step of step function, called only when data validation is successful.
Request will contain JOSN array of TrackerData objects.
From request array, it will create array of payloads and PATCH it to Vizix which will update current 
parameters of tracker in database.


# Test your Function
Open up TrackerIntegrationLambdaFunctionHandler.java where handleRequest method is the entry point for the
lambda function.
Open up TrackerIntegrationLambdaFunctionHandlerTest.java and then run it locally as a normal JUnit test
The unit test provides a sample JSON input file. You can modify the JSON file, or create new ones based on it.


# Upload your Function
Under Project or Package Explorer View, right-click on your project and select Amazon Web Services Upload Function to AWS Lambda.
Then follow the steps to create a new Lambda function or upload your code to an existing function.


# Invoke your Function
Now we are ready to run the function in the cloud. Right-click on your project again and select Amazon Web Services Run on AWS Lambda
In the input dialog, enter the JSON input for your function, or select one of the JSON files in your project
Click Invoke and check the output of your function in the IDE Console View


# Function logs
CloudWatch -> Logs -> /aws/lambda/tracker-integration

