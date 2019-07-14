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
package com.manager.pulsar.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NamespacesEntity {
    private long namespaceId;
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
