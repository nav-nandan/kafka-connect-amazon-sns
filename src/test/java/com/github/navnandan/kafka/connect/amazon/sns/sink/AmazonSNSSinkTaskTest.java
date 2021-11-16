package com.github.navnandan.kafka.connect.amazon.sns.sink;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.sink.SinkConnector;
import org.apache.kafka.connect.sink.SinkRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Author: naveen nandan
 */
public class AmazonSNSSinkTaskTest {
	
	private static final String awsSnsRegion = "ap-southeast-1";
	private static final String awsSnsTopicName = "test";
	private static final String kafkaTopic = "test";
	
	private Map<String, String> sinkProperties;
	
	private AmazonSNSSinkTask task;
	
	@BeforeEach
    public void setup() {
        sinkProperties = new HashMap<>();
        sinkProperties.put(SinkConnector.TOPICS_CONFIG, kafkaTopic);
        sinkProperties.put(AmazonSNSSinkConnectorConfig.CONFIG_NAME_AWS_SNS_REGION, awsSnsRegion);
        sinkProperties.put(AmazonSNSSinkConnectorConfig.CONFIG_NAME_AWS_SNS_TOPIC_NAME, awsSnsTopicName);
    }
	
	@Test
    public void testStart() {
        task = new AmazonSNSSinkTask();
        task.start(sinkProperties);
        
        HashMap<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();

        task.put(Arrays.asList(
                new SinkRecord(kafkaTopic, 0, null, null, Schema.STRING_SCHEMA, "abc", 1)
        ));
        offsets.put(new TopicPartition(kafkaTopic, 0), new OffsetAndMetadata(1L));
        task.flush(offsets);

        task.put(Arrays.asList(
                new SinkRecord(kafkaTopic, 0, null, null, Schema.STRING_SCHEMA, "def", 2),
                new SinkRecord(kafkaTopic, 0, null, null, Schema.STRING_SCHEMA, "ghi", 1)
        ));
        offsets.put(new TopicPartition(kafkaTopic, 0), new OffsetAndMetadata(2L));
        offsets.put(new TopicPartition(kafkaTopic, 0), new OffsetAndMetadata(1L));
        task.flush(offsets);
    }
}
