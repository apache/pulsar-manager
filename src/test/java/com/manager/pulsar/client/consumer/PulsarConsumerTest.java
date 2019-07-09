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
    public void testPulsarConsumer() throws PulsarClientException {
        PulsarClient pulsarClient = mock(PulsarClient.class);
        Client client = mock(Client.class);
        ConsumerBuilder consumerBuilder = mock(ConsumerBuilder.class);

        ConsumerConfigurationData consumerConfigurationData = new ConsumerConfigurationData();
        consumerConfigurationData.setSchemaType(SchemaType.BYTES);
        consumerConfigurationData.setSchema(null);
        PulsarConsumer pulsarConsumer = new PulsarConsumer(client, consumerConfigurationData);

        when(client.getPulsarClient()).thenReturn(pulsarClient);
        when(pulsarClient.newConsumer(Schema.BYTES)).thenReturn(consumerBuilder);
        when(consumerBuilder.subscriptionName(
                consumerConfigurationData.getSubscriptionName())).thenReturn(consumerBuilder);

        // test subscription name is null
        consumerConfigurationData.setSubscriptionName(null);
        try {
            pulsarConsumer.getConsumer();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("The subscription name is incorrect", e.getMessage());
        }
        // test subscription name is ""
        consumerConfigurationData.setSubscriptionName("");
        try {
            pulsarConsumer.getConsumer();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("The subscription name is incorrect", e.getMessage());
        }

        consumerConfigurationData.setSubscriptionName("test-consumer");

        // test subscriptiontype is null
        consumerConfigurationData.setSubscriptionType(null);
        try {
            pulsarConsumer.getConsumer();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("The subscription type should be set correctly." +
                    "Exclusive, Failover, Shared and Key_Shared are currently supported.", e.getMessage());
        }

        consumerConfigurationData.setSubscriptionType(SubscriptionType.Exclusive);
        when(consumerBuilder.subscriptionType(SubscriptionType.Exclusive)).thenReturn(consumerBuilder);

        String[] testNullTopics = {null};
        consumerConfigurationData.setTopics(testNullTopics);
        try {
            pulsarConsumer.getConsumer();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Length of topic should be greater than 0", e.getMessage());
        }

        String[] testEmptyTopics = {""};
        consumerConfigurationData.setTopics(testEmptyTopics);
        try {
            pulsarConsumer.getConsumer();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Length of topic should be greater than 0", e.getMessage());
        }

        String[] topics = {"test-topics"};
        consumerConfigurationData.setTopics(topics);
        when(consumerBuilder.topics(Arrays.asList(consumerConfigurationData.getTopics()))).thenReturn(consumerBuilder);

        consumerConfigurationData.setAckTimeout(-1L);
        try {
            pulsarConsumer.getConsumer();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Parameter ackTimeout cannot be less than 10s", e.getMessage());
        }

        consumerConfigurationData.setAckTimeout(0L);
        try {
            pulsarConsumer.getConsumer();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Parameter ackTimeout cannot be less than 10s", e.getMessage());
        }

        consumerConfigurationData.setAckTimeout(10L);
        when(consumerBuilder.ackTimeout(10L, TimeUnit.SECONDS)).thenReturn(consumerBuilder);

        consumerConfigurationData.setReceiverQueueSize(-1);
        try {
            pulsarConsumer.getConsumer();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Parameter receiverQueueSize should be greater than 0", e.getMessage());
        }

        consumerConfigurationData.setReceiverQueueSize(1000);
        when(consumerBuilder.receiverQueueSize(1000)).thenReturn(consumerBuilder);

        consumerConfigurationData.setAcknowledgmentGroupTime(-1L);

        try {
            pulsarConsumer.getConsumer();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Parameter acknowledgmentGroupTime cannot be less than 0", e.getMessage());
        }

        consumerConfigurationData.setAcknowledgmentGroupTime(1000L);
        when(consumerBuilder.acknowledgmentGroupTime(1000L, TimeUnit.MILLISECONDS)).thenReturn(consumerBuilder);

        consumerConfigurationData.setConsumerName("test-consumer");
        when(consumerBuilder.consumerName("test-consumer")).thenReturn(consumerBuilder);

        consumerConfigurationData.setNegativeAckRedeliveryDelay(-1L);

        try {
            pulsarConsumer.getConsumer();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Parameter negativeAckRedeliveryDelay cannot be less than 0", e.getMessage());
        }

        consumerConfigurationData.setNegativeAckRedeliveryDelay(1000L);
        when(consumerBuilder.negativeAckRedeliveryDelay(1000L, TimeUnit.MILLISECONDS)).thenReturn(consumerBuilder);

        consumerConfigurationData.setPriorityLevel(-1);

        try {
            pulsarConsumer.getConsumer();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Parameter priorityLevel cannot be less than 0", e.getMessage());
        }
        consumerConfigurationData.setPriorityLevel(0);
        when(consumerBuilder.priorityLevel(0)).thenReturn(consumerBuilder);
        consumerConfigurationData.setRegexSubscriptionMode(RegexSubscriptionMode.PersistentOnly);
        when(consumerBuilder.subscriptionTopicsMode(
                consumerConfigurationData.getRegexSubscriptionMode())).thenReturn(consumerBuilder);

        pulsarConsumer.getConsumer();
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
