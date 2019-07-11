package com.manager.pulsar.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Broker entity.
 */
@Getter
@Setter
@NoArgsConstructor
public class BrokersEntity {
    private int brokerId;
    private String broker;
    // URLs to satisfy contract of ServiceLookupData (used by NamespaceService).
    private String webServiceUrl;
    private String webServiceUrlTls;
    private String pulsarServiceUrl;
    private String pulsarServiceUrlTls;
    private boolean persistentTopicsEnabled = true;
    private boolean nonPersistentTopicsEnabled = true;

    // Most recently available system resource usage.
    private double cpuUsage;
    private double cpuLimit;
    private double memoryUsage;
    private double memoryLimit;
    private double directMemoryUsage;
    private double directMemoryLimit;

    private double bandwidthInUsage;
    private double bandwidthInLimit;
    private double bandwidthOutUsage;
    private double bandwidthOutLimit;

    // Message data from the most recent namespace bundle stats.
    private double msgThroughputIn;
    private double msgThroughputOut;
    private double msgRateIn;
    private double msgRateOut;

    // Timestamp of last update.
    private long lastUpdate;

    // The stats given in the most recent invocation of update.
    private String lastStats;

    private String bundleStats;

    private int numTopics;
    private int numBundles;
    private int numConsumers;
    private int numProducers;

    // All bundles belonging to this broker.
    private String bundles;

    // The bundles gained since the last invocation of update.
    private String lastBundleGains;

    // The bundles lost since the last invocation of update.
    private String lastBundleLosses;

    private String brokerVersionString;

    private String loadReportType;

    private double maxResourceUsage;
}
