package com.manager.pulsar.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NamespacesEntity {
    private int namespaceId;
    private String tenant;
    private String namespace;

    private String authPolicies;
    private String replicationClusters;

    private int numBundles;
    private String boundaries;
    private String backlogQuota;
    private String topicDispatchRate;
    private String subscriptionDispatchRate;
    private String replicatorDispatchRate;
    private String clusterSubscribeRate;


    private int bookkeeperEnsemble;
    private int bookkeeperWriteQuorum;
    private int bookkeeperAckQuorum;
    private double managedLedgerMaxMarkDeleteRate;
    // If set, it will override the broker settings for enabling deduplication
    private Boolean deduplicationEnabled;

    private String latencyStatsSampleRate;
    private int messageTtlInSeconds;

    private int retentionTimeInMinutes;
    private long retentionSizeInMB;
    private boolean deleted;
    private String antiAffinityGroup;

    private boolean encryptionRequired;
    private String subscriptionAuthMode;

    private int maxProducersPerTopic;
    private int maxConsumersPerTopic;
    private int maxConsumersPerSubscription;

    private long compactionThreshold;
    private long offloadThreshold;
    private Long offloadDeletionLagMs;

    private String schemaAutoApdateCompatibilityStrategy;

    private boolean schemaValidationEnforced;
}
