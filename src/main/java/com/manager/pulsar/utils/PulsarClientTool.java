package com.manager.pulsar.utils;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.ecosystem.io.function.PulsarEvent;

public class PulsarClientTool {

    public static PulsarClient initPulsarClient(String serviceUrl) throws Exception {
        try {
            PulsarClient client = PulsarClient.builder()
                    .serviceUrl(serviceUrl)
                    .ioThreads(5)
                    .build();
            return client;
        } catch (Exception e) {
            return null;
        }
    }

    public static Consumer<PulsarEvent> initConsumer(
            PulsarClient client, Schema<PulsarEvent> schema, String topic, String subscription) {
        try {
            Consumer<PulsarEvent> consumer = client.newConsumer(schema)
                    .topic(topic).subscriptionName(subscription)
                    .subscribe();
            return consumer;
        } catch (Exception e) {
            return null;
        }
    }
}
