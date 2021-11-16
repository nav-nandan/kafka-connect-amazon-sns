package com.github.navnandan.kafka.connect.amazon.sns.sink;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.kafka.common.config.Config;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;

/**
 * Author: naveen nandan
 */
public class AmazonSNSSinkConnector extends SinkConnector {

	public static String VERSION = "1.0.0";

	private Map<String, String> props;

	@Override public String version() {
		return VERSION;
	}

	@Override public void start(Map<String, String> props) {
		this.props = props;
	}

	@Override public Class<? extends Task> taskClass() {
		return AmazonSNSSinkTask.class;
	}

	@Override public List<Map<String, String>> taskConfigs(int maxTasks) {
		return Collections.nCopies(maxTasks, props);
	}

	@Override public void stop() {

	}

	@Override public ConfigDef config() {
		return AmazonSNSSinkConnectorConfig.config();
	}

	@Override public Config validate(Map<String, String> connectorConfigs) {
		return super.validate(connectorConfigs);
	}
}
