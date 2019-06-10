package com.manager.pulsar.client;

import com.manager.pulsar.utils.PulsarClientTool;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.schema.SchemaDefinition;
import org.apache.pulsar.client.impl.schema.AvroSchema;
import org.apache.pulsar.ecosystem.io.function.PulsarEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Component
public class PulsarEventClient {

    private static Logger logger = LogManager.getLogger(PulsarEventClient.class);

    @Value("${pulsar.serviceUrl}")
    private String serviceUrl;

    @Value("${pulsar.topic}")
    private String topic;

    @Value("${pulsar.subscription}")
    private String subscription;

    private PulsarClient pulsarClient;

    private Consumer<PulsarEvent> pulsarEventConsumer;

    public void initPulsarClientForPuslarEvent() {
        try {
            pulsarClient = PulsarClientTool.initPulsarClient(serviceUrl);
            logger.info("Pulsar Client init success use serviceUrl: {}", serviceUrl);
        } catch (Exception e) {
            logger.error("Pulsar client init failed: {}", e.getMessage());
        }
    }

    public void initTopicsForPulsarEvent() {
        if (pulsarClient == null) {
            initPulsarClientForPuslarEvent();
        }
        try {
            SchemaDefinition<PulsarEvent> schemaDefinition = SchemaDefinition.<PulsarEvent>builder().withPojo(PulsarEvent.class).build();
            AvroSchema<PulsarEvent> schema = AvroSchema.of(schemaDefinition);
            pulsarEventConsumer = PulsarClientTool.initConsumer(pulsarClient, schema, topic, subscription);
            logger.info("Pulsar Consumer init success use topic: {}, subscritpion: {}", topic, subscription);
        } catch (Exception e) {
            logger.error("Pulsar consumer init failed: {}", e.getMessage());
        }
    }

    public Consumer<PulsarEvent> getPulsarEventConsumer() {
        if (pulsarEventConsumer == null) {
            initTopicsForPulsarEvent();
        }
        return pulsarEventConsumer;
    }

    public void close() {
        try {
            if (pulsarEventConsumer != null) {
                pulsarEventConsumer.close();
            }
            if (pulsarClient != null) {
                pulsarClient.close();
            }
            logger.info("Pulsar client and consumer close success");
        } catch (Exception e) {
            logger.error("Pulsar client close failed: {}", e.getMessage());
        }
    }
}
