package com.manager.pulsar.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Bundle entity.
 */
@Getter
@Setter
@NoArgsConstructor
public class BundlesEntity {

    private int bundleId;

    private String broker;

    private String tenant;

    private String namespace;

    private String bundle;

    private double msgRateIn;

    private double msgThroughputIn;

    private double msgRateOut;

    private double msgThroughputOut;

    private int consumerCount;

    private int producerCount;

    private long topics;

    private long cacheSize;

    private double throughputDifferenceThreshold;

    private double msgRateDifferenceThreshold;

    private double topicConnectionDifferenceThreshold;

    private double cacheSizeDifferenceThreshold;
}
