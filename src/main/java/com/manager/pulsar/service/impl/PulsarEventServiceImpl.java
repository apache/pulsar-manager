package com.manager.pulsar.service.impl;

import com.manager.pulsar.client.PulsarEventClient;
import com.manager.pulsar.entity.TenantsEntity;
import com.manager.pulsar.entity.TenantsRepository;
import com.manager.pulsar.service.PulsarEventService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.ecosystem.io.function.ActionType;
import org.apache.pulsar.ecosystem.io.function.EventType;
import org.apache.pulsar.ecosystem.io.function.PulsarEvent;
import org.apache.pulsar.ecosystem.io.function.TenantEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class PulsarEventServiceImpl implements PulsarEventService {

    private static Logger logger = LogManager.getLogger(PulsarEventServiceImpl.class);

    protected Thread thread = null;

    protected volatile boolean running = false;

    @Autowired
    private PulsarEventClient pulsarClient;

    @Autowired
    private TenantsRepository tenantsRepository;

    public void init() {
        logger.info("Start init client and consumer");
        pulsarClient.initPulsarClientForPuslarEvent();
        pulsarClient.initTopicsForPulsarEvent();
        start();
    }

    @Override
    public void start() {
        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                processEvent();
            }
        });
        thread.setName("Pulasr Event Service Thread");
        running = true;
        thread.start();
    }

    protected void processEvent() {
        while(running) {
            Consumer<PulsarEvent> consumer = pulsarClient.getPulsarEventConsumer();
            try {
                Message<PulsarEvent> msg = consumer.receive();
                PulsarEvent pulsarEvent = msg.getValue();
                convertPulsarEvent(pulsarEvent);
                consumer.acknowledge(msg);
            } catch (Exception e) {
                logger.error("Process event error: {}!", e.getMessage());
            }
        }
    }


    @Override
    public void stop() {
        pulsarClient.close();
        running = false;
    }

    @Override
    public void convertPulsarEvent(PulsarEvent pulsarEvent) {
        EventType eventType = pulsarEvent.getEventType();
        switch(eventType) {
            case TENANT:
                convertTenantEvent(pulsarEvent);
                break;
        }
    }

    public void convertTenantEvent(PulsarEvent pulsarEvent) {
        TenantsEntity tenantsEntity = new TenantsEntity();
        TenantEvent tenantEvent = pulsarEvent.getTenantEvent();
        ActionType action = pulsarEvent.getAction();
        tenantsEntity.setTenant(tenantEvent.getTenant());
        if (action != ActionType.DELETE) {
            Set<String> adminRoles = tenantEvent.getAdminRoles();
            Set<String> allowedClusters = tenantEvent.getAllowedClusters();
            tenantsEntity.setAdminRoles(String.join(",", adminRoles));
            tenantsEntity.setAllowedClusters(String.join(",", allowedClusters));
        }
        if (action == ActionType.INSERT) {
            tenantsRepository.save(tenantsEntity);
        }
        if (action == ActionType.UPDATE) {
            tenantsRepository.updateByTenant(tenantsEntity);
        }
        if (action == ActionType.DELETE) {
            tenantsRepository.removeByTenant(tenantsEntity);
        }
    }
}
