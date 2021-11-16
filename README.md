Welcome to your new Kafka Connect connector!

## Generated using Maven Archetype (example below)

```
mvn archetype:generate -B \
-DarchetypeGroupId=io.confluent.maven \
-DarchetypeArtifactId=kafka-connect-quickstart \
-DarchetypeVersion=0.10.0.0 \
-Dpackage=com.github.navnandan.kafka.connect.amazon.sns \
-DgroupId=com.github.navnandan.kafka.connect.amazon.sns \
-DartifactId=kafka-connect-amazon-sns \
-Dversion=1.0.0
```

# Running in development

```
mvn clean package
export CLASSPATH="$(find target/ -type f -name '*.jar'| grep '\-package' | tr '\n' ':')"
$CONFLUENT_HOME/bin/connect-standalone $CONFLUENT_HOME/etc/kafka/connect-standalone.properties config/AmazonSNSSinkConnector.properties
```

# Running on a Kafka Connect Distributed Cluster

```
curl -X PUT \
    -H "Content-Type: application/json" \
    --data '{
            "name": "amazon-sns-sink-connector",
            "connector.class": "com.github.navnandan.kafka.connect.amazon.sns.sink.AmazonSNSSinkConnector",
            "tasks.max": "1",
            "topics": "test",
            "aws.sns.region": "ap-southeast-1",
            "aws.sns.topic.name": "test",

            "value.converter": "org.apache.kafka.connect.storage.StringConverter",
            "value.converter.schemas.enable": "false",
            "key.converter": "org.apache.kafka.connect.storage.StringConverter",
            "key.converter.schemas.enable": "false"
    }' \
    http://localhost:8083/connectors/amazon-sns-sink-connector/config | jq .
```
