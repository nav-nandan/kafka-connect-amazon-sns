package com.github.navnandan.kafka.connect.amazon.sns.sink;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.kafka.common.config.ConfigValue;
import org.apache.kafka.connect.connector.ConnectorContext;
import org.apache.kafka.connect.sink.SinkConnector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import software.amazon.awssdk.regions.Region;

/**
 * Author: naveen nandan
 */
public class AmazonSNSSinkConnectorTest {
	
	private static final String awsSnsRegion = "ap-southeast-1";
	private static final String awsSnsTopicName = "test";
	private static final String kafkaTopic = "test";
	
	private AmazonSNSSinkConnector connector;
	private ConnectorContext ctx;
	private Map<String, String> sinkProperties;
	
	@BeforeEach
    public void setup() {
        connector = new AmazonSNSSinkConnector();
        ctx = mock(ConnectorContext.class);
        connector.initialize(ctx);

        sinkProperties = new HashMap<>();
        sinkProperties.put(SinkConnector.TOPICS_CONFIG, kafkaTopic);
        sinkProperties.put(AmazonSNSSinkConnectorConfig.CONFIG_NAME_AWS_SNS_REGION, awsSnsRegion);
        sinkProperties.put(AmazonSNSSinkConnectorConfig.CONFIG_NAME_AWS_SNS_TOPIC_NAME, awsSnsTopicName);
    }

	@Test
    public void testConnectorConfigValidation() {
        List<ConfigValue> configValues = connector.config().validate(sinkProperties);
        for (ConfigValue val : configValues) {
            assertEquals(0, val.errorMessages().size(), "Config property errors: " + val.errorMessages());
        }
    }
	
	@Test
    public void testSinkTasks() {
        connector.start(sinkProperties);
        List<Map<String, String>> taskConfigs = connector.taskConfigs(1);
        assertEquals(1, taskConfigs.size());
        assertEquals("ap-southeast-1", taskConfigs.get(0).get(AmazonSNSSinkConnectorConfig.CONFIG_NAME_AWS_SNS_REGION));
        Region region = Region.regions().stream().filter(reg -> reg.id().equals(awsSnsRegion)).collect(Collectors.toList()).get(0);
        assertEquals(Region.AP_SOUTHEAST_1, region);

        taskConfigs = connector.taskConfigs(2);
        assertEquals(2, taskConfigs.size());
        for (int i = 0; i < 2; i++) {
            assertEquals("ap-southeast-1", taskConfigs.get(0).get(AmazonSNSSinkConnectorConfig.CONFIG_NAME_AWS_SNS_REGION));
            region = Region.regions().stream().filter(reg -> reg.id().equals(awsSnsRegion)).collect(Collectors.toList()).get(0);
            assertEquals(Region.AP_SOUTHEAST_1, region);
        }
    }
	
	@Test
    public void testSinkTasksStdout() {
        sinkProperties.remove(AmazonSNSSinkConnectorConfig.CONFIG_NAME_AWS_SNS_REGION);
        sinkProperties.remove(AmazonSNSSinkConnectorConfig.CONFIG_NAME_AWS_SNS_TOPIC_NAME);
        connector.start(sinkProperties);
        List<Map<String, String>> taskConfigs = connector.taskConfigs(1);
        assertEquals(1, taskConfigs.size());
        assertNull(taskConfigs.get(0).get(AmazonSNSSinkConnectorConfig.CONFIG_NAME_AWS_SNS_REGION));
        assertNull(taskConfigs.get(0).get(AmazonSNSSinkConnectorConfig.CONFIG_NAME_AWS_SNS_TOPIC_NAME));
    }
	
	@Test
    public void testTaskClass() {
        connector.start(sinkProperties);
        assertEquals(AmazonSNSSinkTask.class, connector.taskClass());
    }
}
