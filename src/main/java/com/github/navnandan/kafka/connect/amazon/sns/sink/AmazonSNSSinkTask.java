package com.github.navnandan.kafka.connect.amazon.sns.sink;

import org.apache.kafka.clients.consumer.OffsetAndMetadata;

import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.CreateTopicResponse;
import software.amazon.awssdk.services.sns.model.DeleteTopicRequest;
import software.amazon.awssdk.services.sns.model.DeleteTopicResponse;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;
import software.amazon.awssdk.services.sns.model.SubscribeResponse;
import software.amazon.awssdk.services.sns.model.UnsubscribeRequest;
import software.amazon.awssdk.services.sns.model.UnsubscribeResponse;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Author: naveen nandan
 */
public class AmazonSNSSinkTask extends SinkTask {

	private AmazonSNSSinkConnectorConfig config;

	private static SnsClient snsClient;

	private static String snsTopicArn;

	@Override
	public void start(Map<String, String> props) {
		config = new AmazonSNSSinkConnectorConfig(props);

		String awsSnsRegion = config.getString(AmazonSNSSinkConnectorConfig.CONFIG_NAME_AWS_SNS_REGION);

		Region region = Region.regions().stream().filter(reg -> reg.id().equals(awsSnsRegion)).collect(Collectors.toList()).get(0);

		// TODO: sns client with aws credentials via connector properties instead of env
		snsClient = SnsClient.builder().region(region).build();

		String awsSnsTopic = config.getString(AmazonSNSSinkConnectorConfig.CONFIG_NAME_AWS_SNS_TOPIC_NAME);

		// TODO: allow specifying topic arn via connector property for existing sns topic
		snsTopicArn = createSNSTopic(snsClient, awsSnsTopic);
	}

	@Override
	public void put(Collection<SinkRecord> records) {
		if (records.isEmpty()) {
			return;
		}

		final SinkRecord first = records.iterator().next();
		final int recordsCount = records.size();

		// TODO: convert to lambda
		for (SinkRecord record : records) {
			// TODO: use schema registry for wider message format support and deserialization
			publishToSNSTopic(snsClient, record.value().toString(), snsTopicArn);
		}
	}

	// helper function to create sns topic, else fetches topic arn if exists
	public String createSNSTopic(SnsClient snsClient, String topicName) {
		try {
			CreateTopicRequest request = CreateTopicRequest.builder().name(topicName).build();

			CreateTopicResponse result = snsClient.createTopic(request);

			return result.topicArn();
		} catch (SnsException e) {
			System.err.println(e.awsErrorDetails().errorMessage());
			System.exit(1);
		}

		return "";
	}

	// helper function to publish message to a sns topic
	public void publishToSNSTopic(SnsClient snsClient, String message, String topicArn) {
		try {
			PublishRequest request = PublishRequest.builder().message(message).topicArn(topicArn).build();

			PublishResponse result = snsClient.publish(request);
			System.out.println(result.messageId() + " Message sent. Status is " + result.sdkHttpResponse().statusCode());
		} catch (SnsException e) {
			System.err.println(e.awsErrorDetails().errorMessage());
			System.exit(1);
		}
	}

	// helper function to create an endpoint subscription
	// TO DO: generalise to use protocol, endpoint from connector properties
	public String createSubsciption(SnsClient snsClient, String topicArn, String protocol, String endpoint) {
		try {
			SubscribeRequest request = SubscribeRequest.builder().protocol(protocol).endpoint(endpoint).returnSubscriptionArn(true).topicArn(topicArn).build();

			SubscribeResponse result = snsClient.subscribe(request);
			System.out.println("Subscription ARN: " + result.subscriptionArn() + "\n\n Status is " + result.sdkHttpResponse().statusCode());

			return result.subscriptionArn();
		} catch (SnsException e) {
			System.err.println(e.awsErrorDetails().errorMessage());
			System.exit(1);
		}

		return "";
	}

	@Override public void stop() {

	}

	// helper function to delete sns topic
	public void deleteSNSTopic(SnsClient snsClient, String topicArn) {
		try {
			DeleteTopicRequest request = DeleteTopicRequest.builder().topicArn(topicArn).build();

			DeleteTopicResponse result = snsClient.deleteTopic(request);
			System.out.println("\n\nStatus was " + result.sdkHttpResponse().statusCode());
		} catch (SnsException e) {
			System.err.println(e.awsErrorDetails().errorMessage());
			System.exit(1);
		}
	}

	// helper function to unsubscribe an endpoint
	public void unSubscribe(SnsClient snsClient, String subscriptionArn) {
		try {
			UnsubscribeRequest request = UnsubscribeRequest.builder().subscriptionArn(subscriptionArn).build();

			UnsubscribeResponse result = snsClient.unsubscribe(request);

			System.out.println("\n\nStatus was " + result.sdkHttpResponse().statusCode()
					+ "\n\nSubscription was removed for " + request.subscriptionArn());

		} catch (SnsException e) {
			System.err.println(e.awsErrorDetails().errorMessage());
			System.exit(1);
		}
	}

	@Override public void flush(Map<TopicPartition, OffsetAndMetadata> map) {

	}

	@Override public String version() {
		return getClass().getPackage().getImplementationVersion();
	}
}
