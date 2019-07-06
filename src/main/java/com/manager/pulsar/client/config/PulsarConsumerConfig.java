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
package com.manager.pulsar.client.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.pulsar.client.api.RegexSubscriptionMode;
import org.apache.pulsar.client.api.SubscriptionType;
import org.apache.pulsar.common.schema.SchemaType;

import java.lang.reflect.Method;

/**
 * Pulsar PulsarConsumer Configuration{@link PulsarListener} class.
 */
@Getter
@Setter
@ToString
public class PulsarConsumerConfig {

    private String id;

    private String topic;

    private String[] topics;

    private String topicsPattern;

    private long ackTimeout;

    private long negativeAckRedeliveryDelay;

    private int receiverQueueSize;

    private long acknowledgmentGroupTime;

    private int priorityLevel;

    private Method method;

    private Class schema;

    private SchemaType schemaType;

    private Object bean;

    private String subscriptionName;

    private SubscriptionType subscriptionType;

    private boolean autoUpdatePartitions;

    private String consumerName;

    private RegexSubscriptionMode regexSubscriptionMode;
}
