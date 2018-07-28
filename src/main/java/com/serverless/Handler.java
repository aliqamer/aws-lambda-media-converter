package com.serverless;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.elastictranscoder.AmazonElasticTranscoder;
import com.amazonaws.services.elastictranscoder.AmazonElasticTranscoderClientBuilder;
import com.amazonaws.services.elastictranscoder.model.CreateJobOutput;
import com.amazonaws.services.elastictranscoder.model.CreateJobRequest;
import com.amazonaws.services.elastictranscoder.model.JobInput;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Handler implements RequestHandler<S3Event, ApiGatewayResponse> {

	private static final Logger LOG = Logger.getLogger(Handler.class);

	private static final AmazonElasticTranscoder elasticTranscoder;
	private static final String ELASTIC_TRANSCODER_PIPELINE_ID = "ELASTIC_TRANSCODER_PIPELINE_ID";

	private static final String TRANSCODE_GENERIC_1080p_PRESET_ID = "1351620000001-000001";
	private static final String TRANSCODE_GENERIC_720p_PRESET_ID = "1351620000001-000010";
	private static final String TRANSCODE_WEB_720p_PRESET_ID = "1351620000001-100070";

	private static final String OUTPUT_KEY_PREFIX = "OUTPUT_KEY_PREFIX";
	private static final String OUTPUT_VIDEO_FORMAT = "OUTPUT_VIDEO_FORMAT";

	static {
		//elasticTranscoder = new AmazonElasticTranscoderClient(AwsAuth.getProfileCredentials("aws-access"));
		//elasticTranscoder = new AmazonElasticTranscoderClient(AwsAuth.getProfileCredentials());
		elasticTranscoder = AmazonElasticTranscoderClientBuilder.standard().withRegion(Regions.AP_SOUTH_1).build();
//		Region southEast1 = Region.getRegion(Regions.AP_SOUTHEAST_1);
//		elasticTranscoder.setRegion(southEast1);
	}

	@Override
	public ApiGatewayResponse handleRequest(S3Event input, Context context) {
		LOG.info("received: " + input);

		context.getLogger().log("Input: " + input);
		context.getLogger().log("PipelineId: " + System.getenv(ELASTIC_TRANSCODER_PIPELINE_ID));

		// Get the S3 input object key for Transcoder Job
		String inputKey = input.getRecords().get(0).getS3().getObject().getKey();
		inputKey = inputKey.replaceAll(" ", "+");
		context.getLogger().log("Input Key: " + inputKey);

		// Create the Transcoder Job input
		JobInput transcoderJobInput = new JobInput().withKey(inputKey);

		// Create the output key for Transcoder Job
		String outputKey = inputKey.split("\\.")[0];

		// Loggers
		context.getLogger().log("OutputKey: " + outputKey);

		// Configure the Transcoder Job output
		CreateJobOutput web720p = new CreateJobOutput().withKey(outputKey + "-web-720p" + "." + System.getenv(OUTPUT_VIDEO_FORMAT))
				.withPresetId(TRANSCODE_WEB_720p_PRESET_ID);
		List<CreateJobOutput> transcoderJobOutputs = Arrays.asList(web720p);

		// Create the Transcoder Job request
		CreateJobRequest createJobRequest = new CreateJobRequest()
				.withPipelineId(System.getenv(ELASTIC_TRANSCODER_PIPELINE_ID)).withInput(transcoderJobInput)
	//				.withOutputKeyPrefix(OUTPUT_KEY_PREFIX).withOutputs(transcoderJobOutputs);
				.withOutputs(transcoderJobOutputs);

		// Create the Transcoder Job
		elasticTranscoder.createJob(createJobRequest);

		Response responseBody = new Response("Go Serverless v1.x! Your function executed successfully!", null);
		return ApiGatewayResponse.builder()
				.setStatusCode(200)
				.setObjectBody(responseBody)
				.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & serverless"))
				.build();
	}
}
