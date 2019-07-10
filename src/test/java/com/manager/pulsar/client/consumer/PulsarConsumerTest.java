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

import com.manager.pulsar.client.Client;
import com.manager.pulsar.client.config.ConsumerConfigurationData;
import org.apache.pulsar.client.api.*;
import org.apache.pulsar.common.schema.SchemaType;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Pulsar Consumer Test Class.
 */
public class PulsarConsumerTest {

    @Test
    public void testPulsarConsumerInitConfig() {
        Client client = mock(Client.class);
        ConsumerBuilder consumerBuilder = mock(ConsumerBuilder.class);

        ConsumerConfigurationData consumerConfigurationData = new ConsumerConfigurationData();
        PulsarConsumer pulsarConsumer = new PulsarConsumer(client, consumerConfigurationData);

        when(consumerBuilder.subscriptionName(
                consumerConfigurationData.getSubscriptionName())).thenReturn(consumerBuilder);

        // test subscription name is null
        consumerConfigurationData.setSubscriptionName(null);
        try {
            pulsarConsumer.initConsumerConfig(consumerBuilder);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("The subscription name is incorrect", e.getMessage());
        }
        // test subscription name is ""
        consumerConfigurationData.setSubscriptionName("");
        try {
            pulsarConsumer.initConsumerConfig(consumerBuilder);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("The subscription name is incorrect", e.getMessage());
        }

        consumerConfigurationData.setSubscriptionName("test-consumer");

        // test subscriptiontype is null
        consumerConfigurationData.setSubscriptionType(null);
        try {
            pulsarConsumer.initConsumerConfig(consumerBuilder);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("The subscription type should be set correctly." +
                    "Exclusive, Failover, Shared and Key_Shared are currently supported.", e.getMessage());
        }

        consumerConfigurationData.setSubscriptionType(SubscriptionType.Exclusive);
        when(consumerBuilder.subscriptionType(SubscriptionType.Exclusive)).thenReturn(consumerBuilder);

        String[] testNullTopics = {null};
        consumerConfigurationData.setTopics(testNullTopics);
        try {
            pulsarConsumer.initConsumerConfig(consumerBuilder);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Length of topic should be greater than 0", e.getMessage());
        }

        String[] testEmptyTopics = {""};
        consumerConfigurationData.setTopics(testEmptyTopics);
        try {
            pulsarConsumer.initConsumerConfig(consumerBuilder);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Length of topic should be greater than 0", e.getMessage());
        }

        String[] topics = {"test-topics"};
        consumerConfigurationData.setTopics(topics);
        when(consumerBuilder.topics(Arrays.asList(consumerConfigurationData.getTopics()))).thenReturn(consumerBuilder);

        consumerConfigurationData.setAckTimeout(-1L);
        try {
            pulsarConsumer.initConsumerConfig(consumerBuilder);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Parameter ackTimeout cannot be less than 10s", e.getMessage());
        }

        consumerConfigurationData.setAckTimeout(0L);
        try {
            pulsarConsumer.initConsumerConfig(consumerBuilder);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Parameter ackTimeout cannot be less than 10s", e.getMessage());
        }

        consumerConfigurationData.setAckTimeout(10L);
        when(consumerBuilder.ackTimeout(10L, TimeUnit.SECONDS)).thenReturn(consumerBuilder);

        consumerConfigurationData.setReceiverQueueSize(-1);
        try {
            pulsarConsumer.initConsumerConfig(consumerBuilder);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Parameter receiverQueueSize should be greater than 0", e.getMessage());
        }

        consumerConfigurationData.setReceiverQueueSize(1000);
        when(consumerBuilder.receiverQueueSize(1000)).thenReturn(consumerBuilder);

        consumerConfigurationData.setAcknowledgmentGroupTime(-1L);

        try {
            pulsarConsumer.initConsumerConfig(consumerBuilder);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Parameter acknowledgmentGroupTime cannot be less than 0", e.getMessage());
        }

        consumerConfigurationData.setAcknowledgmentGroupTime(1000L);
        when(consumerBuilder.acknowledgmentGroupTime(1000L, TimeUnit.MILLISECONDS)).thenReturn(consumerBuilder);

        consumerConfigurationData.setConsumerName("test-consumer");
        when(consumerBuilder.consumerName("test-consumer")).thenReturn(consumerBuilder);

        consumerConfigurationData.setNegativeAckRedeliveryDelay(-1L);

        try {
            pulsarConsumer.initConsumerConfig(consumerBuilder);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Parameter negativeAckRedeliveryDelay cannot be less than 0", e.getMessage());
        }

        consumerConfigurationData.setNegativeAckRedeliveryDelay(1000L);
        when(consumerBuilder.negativeAckRedeliveryDelay(1000L, TimeUnit.MILLISECONDS)).thenReturn(consumerBuilder);

        consumerConfigurationData.setPriorityLevel(-1);

        try {
            pulsarConsumer.initConsumerConfig(consumerBuilder);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Parameter priorityLevel cannot be less than 0", e.getMessage());
        }
        consumerConfigurationData.setPriorityLevel(0);
        when(consumerBuilder.priorityLevel(0)).thenReturn(consumerBuilder);
        consumerConfigurationData.setRegexSubscriptionMode(RegexSubscriptionMode.PersistentOnly);
        when(consumerBuilder.subscriptionTopicsMode(
                consumerConfigurationData.getRegexSubscriptionMode())).thenReturn(consumerBuilder);

        pulsarConsumer.initConsumerConfig(consumerBuilder);
        verify(consumerBuilder, atLeast(1))
                .subscriptionName(consumerConfigurationData.getSubscriptionName());
        verify(consumerBuilder, atLeast(1))
                .subscriptionType(consumerConfigurationData.getSubscriptionType());
        verify(consumerBuilder, atLeast(1)).ackTimeout(10L, TimeUnit.SECONDS);
        verify(consumerBuilder, atLeast(1))
                .topics(Arrays.asList(consumerConfigurationData.getTopics()));
        verify(consumerBuilder, atLeast(1)).receiverQueueSize(1000);
        verify(consumerBuilder, atLeast(1))
                .acknowledgmentGroupTime(1000L, TimeUnit.MILLISECONDS);
        verify(consumerBuilder, atLeast(1)).consumerName("test-consumer");
        verify(consumerBuilder, atLeast(1))
                .negativeAckRedeliveryDelay(1000L, TimeUnit.MILLISECONDS);
        verify(consumerBuilder, atLeast(1)).priorityLevel(0);
        verify(consumerBuilder, atLeast(1))
                .subscriptionTopicsMode(consumerConfigurationData.getRegexSubscriptionMode());
    }
}
