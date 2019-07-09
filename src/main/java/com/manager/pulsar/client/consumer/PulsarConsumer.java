/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.manager.pulsar.client.consumer;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.manager.pulsar.client.Client;
import com.manager.pulsar.client.config.ConsumerConfigurationData;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.ConsumerBuilder;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.common.schema.SchemaType;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Pulsar Consumer initialization.
 */
public class PulsarConsumer implements AutoCloseable {

    private final Client client;

    private final ConsumerConfigurationData consumerConfigurationData;

    private Schema schema;

    private Consumer consumer;

    PulsarConsumer(Client client, ConsumerConfigurationData consumerConfigurationData) {
        this.consumerConfigurationData = consumerConfigurationData;
        this.client = client;
    }

    public Consumer getConsumer() throws PulsarClientException {
        if (consumer != null) {
            return consumer;
        }
        PulsarClient pulsarClient = client.getPulsarClient();
        if (schema == null) {
            schema = initSchema(consumerConfigurationData.getSchemaType(), consumerConfigurationData.getSchema());
        }
        ConsumerBuilder consumerBuilder = pulsarClient.newConsumer(schema);
        initConsumerConfig(consumerBuilder);
        consumer = consumerBuilder.subscribe();
        return consumer;
    }

    public Schema getSchema() {
        return schema;
    }

    public void setSchema(Schema schema) {
        // Allow user use custom schema;
        this.schema = schema;
    }

    private Schema initSchema(SchemaType schemaType, Class schema) {
        switch (schemaType) {
            case INT8:
                return Schema.INT8;
            case INT16:
                return Schema.INT16;
            case INT32:
                return Schema.INT32;
            case INT64:
                return Schema.INT64;
            case STRING:
                return Schema.STRING;
            case FLOAT:
                return Schema.FLOAT;
            case DOUBLE:
                return Schema.DOUBLE;
            case BOOLEAN:
                return Schema.BOOL;
            case BYTES:
                return Schema.BYTES;
            case DATE:
                return Schema.DATE;
            case TIME:
                return Schema.TIME;
            case TIMESTAMP:
                return Schema.TIMESTAMP;
            case KEY_VALUE:
                return Schema.KV_BYTES();
            case JSON:
                return Schema.JSON(schema);
            case AVRO:
                return Schema.AVRO(schema);
            default:
                return Schema.BYTES;
        }
    }

    private void initConsumerConfig(ConsumerBuilder consumerBuilder) {
        Preconditions.checkArgument(consumerConfigurationData.getSubscriptionName() != null
                && consumerConfigurationData.getSubscriptionName().length() > 0);
        consumerBuilder.subscriptionName(consumerConfigurationData.getSubscriptionName());
        Preconditions.checkArgument(consumerConfigurationData.getSubscriptionType() != null,
                "The subscription type should be set correctly."
                        + "Exclusive, Failover Shared and Key_Shared are currently supported.");
        consumerBuilder.subscriptionType(consumerConfigurationData.getSubscriptionType());
        if (consumerConfigurationData.getTopics() != null) {
            List<String> topics = Arrays.asList(consumerConfigurationData.getTopics());
            topics.forEach((topic) -> {
                Preconditions.checkArgument(topic.length() > 0 ,
                        "Length of topic should be greater than 0");
            });
            consumerBuilder.topics(topics);
        }
        if (consumerConfigurationData.getTopicsPattern() != null
                && consumerConfigurationData.getTopicsPattern().length() > 0) {
            consumerBuilder.topicsPattern(consumerConfigurationData.getTopicsPattern());
        }
        if (consumerConfigurationData.getAckTimeout() != null) {
            Preconditions.checkArgument(consumerConfigurationData.getAckTimeout() >= 0,
                    "Parameter ackTimeout cannot be less than 0");
            consumerBuilder.ackTimeout(consumerConfigurationData.getAckTimeout(), TimeUnit.MILLISECONDS);
        }
        if (consumerConfigurationData.getReceiverQueueSize() != null) {
            Preconditions.checkArgument(consumerConfigurationData.getReceiverQueueSize() > 0,
                    "Parameter receiverQueueSize should be greater than 0");
            consumerBuilder.receiverQueueSize(consumerConfigurationData.getReceiverQueueSize());
        }

        if (consumerConfigurationData.getAcknowledgmentGroupTime() != null) {
            Preconditions.checkArgument(consumerConfigurationData.getAcknowledgmentGroupTime() >= 0,
                    "Parameter acknowledgmentGroupTime cannot be less than 0");
            consumerBuilder.acknowledgmentGroupTime(
                    consumerConfigurationData.getAcknowledgmentGroupTime(), TimeUnit.MILLISECONDS);
        }
        if (consumerConfigurationData.getConsumerName() != null
                && consumerConfigurationData.getConsumerName().length() > 0) {
            consumerBuilder.consumerName(consumerConfigurationData.getConsumerName());
        }
        if (consumerConfigurationData.getNegativeAckRedeliveryDelay() != null) {
            Preconditions.checkArgument(consumerConfigurationData.getNegativeAckRedeliveryDelay() >= 0,
                    "Parameter negativeAckRedeliveryDelay cannot be less than 0");
            consumerBuilder.negativeAckRedeliveryDelay(
                    consumerConfigurationData.getNegativeAckRedeliveryDelay(), TimeUnit.MILLISECONDS);
        }
        if (consumerConfigurationData.getPriorityLevel() != null) {
            Preconditions.checkArgument(consumerConfigurationData.getPriorityLevel() >= 0,
                    "Parameter priorityLevel cannot be less than 0");
            consumerBuilder.priorityLevel(consumerConfigurationData.getPriorityLevel());
        }
        if (consumerConfigurationData.getRegexSubscriptionMode() != null) {
            consumerBuilder.subscriptionTopicsMode(consumerConfigurationData.getRegexSubscriptionMode());
        }
    }

    @Override
    public void close() throws PulsarClientException {
        if (consumer != null) {
            consumer.close();
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("subscriptionName", consumerConfigurationData.getSubscriptionName())
                .add("subscriptionType", consumerConfigurationData.getSubscriptionType())
                .add("topics", consumerConfigurationData.getTopics())
                .add("topicsPattern", consumerConfigurationData.getTopicsPattern())
                .add("ackTimeout", consumerConfigurationData.getAckTimeout())
                .add("receiverQueueSize", consumerConfigurationData.getReceiverQueueSize())
                .add("acknowledgmentGroupTime", consumerConfigurationData.getAcknowledgmentGroupTime())
                .add("consumerName", consumerConfigurationData.getConsumerName())
                .add("priorityLevel", consumerConfigurationData.getPriorityLevel())
                .add("negativeAckRedeliveryDelay", consumerConfigurationData.getNegativeAckRedeliveryDelay())
                .add("regexSubscriptionMode", consumerConfigurationData.getRegexSubscriptionMode())
                .toString();
    }

}
