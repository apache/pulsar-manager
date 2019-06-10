package com.manager.pulsar.service;

import org.apache.pulsar.ecosystem.io.function.PulsarEvent;

public interface PulsarEventService {

    void start();

    void stop();

    void convertPulsarEvent(PulsarEvent pulsarEvent);
}
