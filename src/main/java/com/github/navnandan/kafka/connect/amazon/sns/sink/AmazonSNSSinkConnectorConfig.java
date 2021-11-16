package com.github.navnandan.kafka.connect.amazon.sns.sink;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import java.util.Map;

/**
 * Author: naveen nandan
 */
public class AmazonSNSSinkConnectorConfig extends AbstractConfig {
	
	private static final String CONFIG_CONNECTION_GROUP = "Connection";
	
	public static final String CONFIG_NAME_AWS_SNS_REGION = "aws.sns.region";
    private static final String CONFIG_DOC_AWS_SNS_REGION = "Destination AWS SNS Region.";
    private static final String CONFIG_DISPLAY_AWS_SNS_REGION = "AWS SNS Region";

    public static final String CONFIG_NAME_AWS_SNS_TOPIC_NAME = "aws.sns.topic.name";
    private static final String CONFIG_DOC_AWS_SNS_TOPIC_NAME = "Destination AWS SNS Topic Name.";
    private static final String CONFIG_DISPLAY_AWS_SNS_TOPIC_NAME = "Destination AWS SNS Topic Name.";

    public static ConfigDef config() {
        ConfigDef config = new ConfigDef();
        
        config.define(CONFIG_NAME_AWS_SNS_REGION,
        		ConfigDef.Type.STRING,
                ConfigDef.NO_DEFAULT_VALUE,
                ConfigDef.Importance.HIGH,
                CONFIG_DOC_AWS_SNS_REGION,
                CONFIG_CONNECTION_GROUP,
                1,
                ConfigDef.Width.LONG,
                CONFIG_DISPLAY_AWS_SNS_REGION);
        
        config.define(CONFIG_NAME_AWS_SNS_TOPIC_NAME,
        		ConfigDef.Type.STRING,
                ConfigDef.NO_DEFAULT_VALUE,
                ConfigDef.Importance.HIGH,
                CONFIG_DOC_AWS_SNS_TOPIC_NAME,
                CONFIG_CONNECTION_GROUP,
                1,
                ConfigDef.Width.LONG,
                CONFIG_DISPLAY_AWS_SNS_TOPIC_NAME);
        
        return config;
    }
    
    protected AmazonSNSSinkConnectorConfig(final Map<?, ?> props) {
		super(config(), props);
	}
}
