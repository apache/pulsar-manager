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
package io.streamnative.pulsar.manager.client.annotation;

import org.apache.pulsar.client.api.RegexSubscriptionMode;
import org.apache.pulsar.client.api.SubscriptionType;
import org.apache.pulsar.common.schema.SchemaType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation is used to initialize the PulsarConsumer's related configuration.
 * <pre>{@code
 * @PulsarListener(topic = "test567", subscriptionName = "test567",
 *      schema = PulsarTopicEvent.class, schemaType = SchemaType.AVRO)
 * public void receive(Message message) {
 *      log.info("Received messages: {}", message.toString());
 *  }
 * }</pre>
 */
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface PulsarListener {

    /**
     * Unique id of the container thread.
     */
    String id() default "";

    /**
     * Specify multi topic that this consumer will subscribe on.
     */
    String[] topics();

    /**
     * Specify a pattern for topics that this consumer will subscribe on.
     */
    String topicsPattern() default "";

    /**
     * Set the timeout for unacked messages, truncated to the nearest millisecond. The timeout needs to be greater than
     *  10 seconds.
     */
    long ackTimeout() default 10L;

    /**
     * Set the delay to wait before re-delivering messages that have failed to be process. The default is 1 min.
     */
    long negativeAckRedeliveryDelay() default 60000L;

    /**
     * Sets the size of the consumer receive queue.
     * Default value is {@code 1000} messages and should be good for most use cases.
     */
    int receiverQueueSize() default 1000;

    /**
     * Group the consumer acknowledgments for the specified time. Default is 100ms
     */
    long acknowledgmentGroupTime() default 0;

    /**
     * Sets priority level for the shared subscription consumers to which broker gives more priority while dispatching
     * messages.
     */
    int priorityLevel() default 0;

    /**
     * Specify the subscription name for this consumer.
     */
    String subscriptionName();

    /**
     * Schema to use when receiving messages, it can be a custom pojo class, default is Byte.class.
     */
    Class schema() default Byte.class;

    /**
     * Types of supported schema for Pulsar messages.
     */
    SchemaType schemaType() default SchemaType.BYTES;

    /**
     * Select the subscription type to be used when subscribing to the topic.
     */
    SubscriptionType subscriptionType() default SubscriptionType.Exclusive;

    /**
     * If enabled, the consumer will auto subscribe for partitions increasement. This is only for partitioned consumer.
     */
    boolean autoUpdatePartitions() default false;

    /**
     * Set the consumer name.
     */
    String consumerName() default "";

    /**
     * Determines to which topics this consumer should be subscribed to - Persistent, Non-Persistent, or both. Only used
     * with pattern subscriptions.
     */
    RegexSubscriptionMode regexSubscriptionMode() default RegexSubscriptionMode.PersistentOnly;
}