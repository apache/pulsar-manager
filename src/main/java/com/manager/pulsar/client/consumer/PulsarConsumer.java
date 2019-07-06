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
import com.manager.pulsar.client.config.PulsarConsumerConfig;
import org.apache.pulsar.client.api.*;
import org.apache.pulsar.common.schema.SchemaType;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Pulsar Consumer initialization.
 */
public class PulsarConsumer implements AutoCloseable {

    private final Client client;

    private final PulsarConsumerConfig pulsarConsumerConfig;

    private Schema schema;

    private Consumer consumer;

    PulsarConsumer(Client client, PulsarConsumerConfig pulsarConsumerConfig) {
        this.pulsarConsumerConfig = pulsarConsumerConfig;
        this.client = client;
    }

    public Consumer getConsumer() throws PulsarClientException{
        if (consumer != null) {
            return consumer;
        }
        PulsarClient pulsarClient = client.getPulsarClient();
        if (schema == null) {
            schema = initSchema(pulsarConsumerConfig.getSchemaType(), pulsarConsumerConfig.getSchema());
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
            case BYTES:
                return Schema.BYTES;
            case INT8:
                return Schema.INT8;
            case STRING:
                return Schema.STRING;
            case JSON:
                return Schema.JSON(schema);
            case AVRO:
                return Schema.AVRO(schema);
            default:
                return Schema.BYTES;
        }
    }

    private void initConsumerConfig(ConsumerBuilder consumerBuilder) {
        Preconditions.checkArgument(pulsarConsumerConfig.getSubscriptionName() != null
                && pulsarConsumerConfig.getSubscriptionName().length() > 0);
        consumerBuilder.subscriptionName(pulsarConsumerConfig.getSubscriptionName());
        consumerBuilder.subscriptionType(pulsarConsumerConfig.getSubscriptionType());
        if (pulsarConsumerConfig.getTopic().length() > 0) {
            consumerBuilder.topic(pulsarConsumerConfig.getTopic());
        } else if (pulsarConsumerConfig.getTopics().length > 0) {
            consumerBuilder.topics(Arrays.asList(pulsarConsumerConfig.getTopics()));
        }
        if (pulsarConsumerConfig.getTopicsPattern().length() > 0) {
            consumerBuilder.topicsPattern(pulsarConsumerConfig.getTopicsPattern());
        }
        if (pulsarConsumerConfig.getAckTimeout() > 0) {
            consumerBuilder.ackTimeout(pulsarConsumerConfig.getAckTimeout(), TimeUnit.MILLISECONDS);
        }
        if (pulsarConsumerConfig.getReceiverQueueSize() > 0) {
            consumerBuilder.receiverQueueSize(pulsarConsumerConfig.getReceiverQueueSize());
        }
        if (pulsarConsumerConfig.getAcknowledgmentGroupTime() > 0) {
            consumerBuilder.acknowledgmentGroupTime(pulsarConsumerConfig.getAcknowledgmentGroupTime(), TimeUnit.MILLISECONDS);
        }
        if (pulsarConsumerConfig.getConsumerName().length() > 0) {
            consumerBuilder.consumerName(pulsarConsumerConfig.getConsumerName());
        }
        if (pulsarConsumerConfig.getNegativeAckRedeliveryDelay() > 0) {
            consumerBuilder.negativeAckRedeliveryDelay(pulsarConsumerConfig.getNegativeAckRedeliveryDelay(), TimeUnit.MILLISECONDS);
        }
        if (pulsarConsumerConfig.getPriorityLevel() >= 0) {
            consumerBuilder.priorityLevel(pulsarConsumerConfig.getPriorityLevel());
        }
        consumerBuilder.subscriptionTopicsMode(pulsarConsumerConfig.getRegexSubscriptionMode());
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
                .add("subscriptionName", pulsarConsumerConfig.getSubscriptionName())
                .add("subscriptionType", pulsarConsumerConfig.getSubscriptionType())
                .add("topic", pulsarConsumerConfig.getTopic())
                .add("topics", pulsarConsumerConfig.getTopics())
                .add("topicsPattern", pulsarConsumerConfig.getTopicsPattern())
                .add("ackTimeout", pulsarConsumerConfig.getAckTimeout())
                .add("receiverQueueSize", pulsarConsumerConfig.getReceiverQueueSize())
                .add("acknowledgmentGroupTime", pulsarConsumerConfig.getAcknowledgmentGroupTime())
                .add("consumerName", pulsarConsumerConfig.getConsumerName())
                .add("negativeAckRedeliveryDelay", pulsarConsumerConfig.getNegativeAckRedeliveryDelay())
                .add("regexSubscriptionMode", pulsarConsumerConfig.getRegexSubscriptionMode())
                .toString();
    }

}
