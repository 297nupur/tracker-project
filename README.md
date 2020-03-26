# iot-trackers :  Sendum, Sensatag etc.
This project is all about updating the location of trackers of different varieties used by different companies at a single place in a generic way.
I chose this code to be shared because I think I used most of my IT learnings in this project recently and I initiated this idea of making something generic for
handling all the tracker types for different clients using AWS and its utilities. I finished this end to end in a short span of time being involved in discussions, 
design, development, deployment and received good applause with people in addition to solving a big challenge. And last but not the least had a good learning and I enjoyed doing it.


### Purpose of the project
Before explaining this product, want to give a brief about my previous company for whom I built this product.
My previous company works in supply chain management and provides different products and solutions to different companies for better automating supply chain thing.
A huge problem was there as different type/brands of location trackers are available in the market with different API's and data model to track location of goods at any time.
Inspite of doing the same thing which is track the location of goods with temperature conditions etc every time new chunk of code for managing tracker.
So I built different a design involving microservices talking with each other and hence tracking the location of different goods at one place as a single data model in the end.



### How it works

Three lambda functions come into picture:

1. tracker-parser : Trackers-parser will receive the request (mapping template) from API Gateway, it will parse it to generic TrackerData model and send it to SQS queue 
2. tracker-data-validation : checks if data exists for this trackerId already. If not, throw exception so that step function will fall to CatchAllFallback state and stop. 
							 This lambda function will send a GET request to Vizix.
3. tracker-integration : This lambda function will receive request only when tracker-data-validation (step 10) will validate and forward (when success). 
						 From request array, it will create array of payloads and PATCH it to Vizix.

All trackers have their API's built to integrate with different systems and different aunthentication mechanisms.
At HTTP end point defined will receive push data from tracker, when authenticated tracker-parser will parse the inputs and set it in our defined generic data model.
After parsing the data, now everything is done asynchronously. To maintain order of data which is important here I am using SQS queue which is being read by Cloud watch event
every N minutes. When there is data, step function is trigerred which is combination of tracker-data-validation and tracker-integration. Step function will update the
location of tracker if tracker is known by our system.


