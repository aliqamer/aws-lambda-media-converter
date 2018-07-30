# aws-lambda-media-converter
serverless aws lambda function to convert videos into different format using aws elastic transcoder


This project demostrate media converter using aws elastic transcoder, aws lambda and s3 buckets in java.

Before deploying this application in aws lambda using serverless you should have

1. Elastic transcoder pipeline created in aws
2. S3 bucket created for storing converted media files
3. serverless is installed and configured on your machine

Once deployed, to test you need to add one sample video file in input s3 bucket.
As soon as file is upload in s3 bucket an event is triggered which will invoke this lambda function and it will create job for elastic transcoder and submit the job to transcoder pipeline.
Elastic transcoder will then execute job and store the convered media file to output bucket which is configured.

command to deploy application in aws lambda
> sls deploy -v

to delete stack in aws
> sls remove
