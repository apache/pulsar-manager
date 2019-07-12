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
package com.manager.pulsar.mapper;

import com.github.pagehelper.Page;
import com.manager.pulsar.entity.NamespacesEntity;
import com.manager.pulsar.entity.TenantsEntity;
import org.apache.ibatis.annotations.*;

public interface NamespacesMapper {

    @Insert("INSERT INTO namespaces(namespaceId,tenant,namespace,authPolicies,backlogQuota,replicationClusters," +
            "numBundles,boundaries,topicDispatchRate,subscriptionDispatchRate,replicatorDispatchRate," +
            "clusterSubscribeRate,bookkeeperEnsemble,bookkeeperWriteQuorum,bookkeeperAckQuorum," +
            "managedLedgerMaxMarkDeleteRate,deduplicationEnabled,latencyStatsSampleRate,messageTtlInSeconds," +
            "retentionTimeInMinutes,retentionSizeInMB,deleted,antiAffinityGroup,encryptionRequired," +
            "subscriptionAuthMode,maxProducersPerTopic,maxConsumersPerTopic,maxConsumersPerSubscription," +
            "compactionThreshold,offloadThreshold,offloadDeletionLagMs,schemaValidationEnforced," +
            "schemaAutoApdateCompatibilityStrategy) VALUES(#{namespaceId},#{tenant},#{namespace},#{authPolicies}," +
            "#{backlogQuota},#{replicationClusters},#{numBundles}," +
            "#{boundaries},#{topicDispatchRate},#{subscriptionDispatchRate},#{replicatorDispatchRate}," +
            "#{clusterSubscribeRate},#{bookkeeperEnsemble},#{bookkeeperWriteQuorum},#{bookkeeperAckQuorum}," +
            "#{managedLedgerMaxMarkDeleteRate},#{deduplicationEnabled},#{latencyStatsSampleRate}," +
            "#{messageTtlInSeconds},#{retentionTimeInMinutes},#{retentionSizeInMB},#{deleted}," +
            "#{antiAffinityGroup},#{encryptionRequired},#{subscriptionAuthMode},#{maxProducersPerTopic}," +
            "#{maxConsumersPerTopic},#{maxConsumersPerSubscription},#{compactionThreshold},#{offloadThreshold}," +
            "#{offloadDeletionLagMs},#{schemaValidationEnforced},#{schemaAutoApdateCompatibilityStrategy})")
    void insert(NamespacesEntity namespacesEntity);

    @Select("SELECT namespaceId,tenant,namespace,authPolicies,backlogQuota,replicationClusters,numBundles,boundaries," +
            "topicDispatchRate,subscriptionDispatchRate,replicatorDispatchRate,clusterSubscribeRate," +
            "bookkeeperEnsemble,bookkeeperWriteQuorum,bookkeeperAckQuorum,managedLedgerMaxMarkDeleteRate," +
            "deduplicationEnabled,latencyStatsSampleRate,messageTtlInSeconds,retentionTimeInMinutes," +
            "retentionSizeInMB,deleted,antiAffinityGroup,encryptionRequired,subscriptionAuthMode," +
            "maxProducersPerTopic,maxConsumersPerTopic,maxConsumersPerSubscription,compactionThreshold," +
            "offloadThreshold,offloadDeletionLagMs,schemaValidationEnforced," +
            "schemaAutoApdateCompatibilityStrategy FROM namespaces WHERE namespaceId = #{namespaceId}")
    NamespacesEntity findById(long namespaceId);

    @Select("SELECT namespaceId,tenant,namespace,authPolicies,backlogQuota,replicationClusters,numBundles,boundaries," +
            "topicDispatchRate,subscriptionDispatchRate,replicatorDispatchRate,clusterSubscribeRate," +
            "bookkeeperEnsemble,bookkeeperWriteQuorum,bookkeeperAckQuorum,managedLedgerMaxMarkDeleteRate," +
            "deduplicationEnabled,latencyStatsSampleRate,messageTtlInSeconds,retentionTimeInMinutes," +
            "retentionSizeInMB,deleted,antiAffinityGroup,encryptionRequired,subscriptionAuthMode," +
            "maxProducersPerTopic,maxConsumersPerTopic,maxConsumersPerSubscription,compactionThreshold," +
            "offloadThreshold,offloadDeletionLagMs,schemaValidationEnforced," +
            "schemaAutoApdateCompatibilityStrategy FROM namespaces WHERE tenant=#{tenant} and namespace=#{namespace}")
    NamespacesEntity findByTenantNamespace(String tenant, String namespace);

    @Select("SELECT namespaceId,tenant,namespace,authPolicies,backlogQuota,replicationClusters,numBundles,boundaries," +
            "topicDispatchRate,subscriptionDispatchRate,replicatorDispatchRate,clusterSubscribeRate," +
            "bookkeeperEnsemble,bookkeeperWriteQuorum,bookkeeperAckQuorum,managedLedgerMaxMarkDeleteRate," +
            "deduplicationEnabled,latencyStatsSampleRate,messageTtlInSeconds,retentionTimeInMinutes," +
            "retentionSizeInMB,deleted,antiAffinityGroup,encryptionRequired,subscriptionAuthMode," +
            "maxProducersPerTopic,maxConsumersPerTopic,maxConsumersPerSubscription,compactionThreshold," +
            "offloadThreshold,offloadDeletionLagMs,schemaValidationEnforced," +
            "schemaAutoApdateCompatibilityStrategy FROM namespaces " +
            "WHERE tenant=#{tenantOrNamespace} or namespace=#{tenantOrNamespace}")
    Page<NamespacesEntity> findByTenantOrNamespace(String tenantOrNamespace);

    @Select("SELECT namespaceId,tenant,namespace,authPolicies,backlogQuota,replicationClusters,numBundles,boundaries," +
            "topicDispatchRate,subscriptionDispatchRate,replicatorDispatchRate,clusterSubscribeRate," +
            "bookkeeperEnsemble,bookkeeperWriteQuorum,bookkeeperAckQuorum,managedLedgerMaxMarkDeleteRate," +
            "deduplicationEnabled,latencyStatsSampleRate,messageTtlInSeconds,retentionTimeInMinutes," +
            "retentionSizeInMB,deleted,antiAffinityGroup,encryptionRequired,subscriptionAuthMode," +
            "maxProducersPerTopic,maxConsumersPerTopic,maxConsumersPerSubscription,compactionThreshold," +
            "offloadThreshold,offloadDeletionLagMs,schemaValidationEnforced," +
            "schemaAutoApdateCompatibilityStrategy FROM namespaces WHERE namespace=#{namespace}")
    Page<NamespacesEntity> findByNamespace(String namespace);


    @Select("SELECT namespaceId,tenant,namespace,authPolicies,backlogQuota,replicationClusters,numBundles,boundaries," +
            "topicDispatchRate,subscriptionDispatchRate,replicatorDispatchRate,clusterSubscribeRate," +
            "bookkeeperEnsemble,bookkeeperWriteQuorum,bookkeeperAckQuorum,managedLedgerMaxMarkDeleteRate," +
            "deduplicationEnabled,latencyStatsSampleRate,messageTtlInSeconds,retentionTimeInMinutes," +
            "retentionSizeInMB,deleted,antiAffinityGroup,encryptionRequired,subscriptionAuthMode," +
            "maxProducersPerTopic,maxConsumersPerTopic,maxConsumersPerSubscription,compactionThreshold," +
            "offloadThreshold,offloadDeletionLagMs,schemaValidationEnforced," +
            "schemaAutoApdateCompatibilityStrategy FROM namespaces")
    Page<NamespacesEntity> getNamespacesList();

    @Delete("DELETE FROM namespaces WHERE namespaceId = #{namespaceId}")
    void deleteByNamespaceId(long namespaceId);

    @Update("UPDATE namespaces set authPolicies=#{authPolicies},backlogQuota=#{backlogQuota}," +
            "replicationClusters=#{replicationClusters}," +
            "numBundles=#{numBundles},boundaries=#{boundaries},topicDispatchRate=#{topicDispatchRate}," +
            "subscriptionDispatchRate=#{subscriptionDispatchRate},replicatorDispatchRate=#{replicatorDispatchRate}," +
            "clusterSubscribeRate=#{clusterSubscribeRate},bookkeeperEnsemble=#{bookkeeperEnsemble}," +
            "bookkeeperWriteQuorum=#{bookkeeperWriteQuorum},bookkeeperAckQuorum=#{bookkeeperAckQuorum}," +
            "managedLedgerMaxMarkDeleteRate=#{managedLedgerMaxMarkDeleteRate}," +
            "deduplicationEnabled=#{deduplicationEnabled},latencyStatsSampleRate=#{latencyStatsSampleRate}," +
            "messageTtlInSeconds=#{messageTtlInSeconds},retentionTimeInMinutes=#{retentionTimeInMinutes}," +
            "retentionSizeInMB=#{retentionSizeInMB},deleted=#{deleted},antiAffinityGroup=#{antiAffinityGroup}," +
            "encryptionRequired=#{encryptionRequired},subscriptionAuthMode=#{subscriptionAuthMode}," +
            "maxProducersPerTopic=#{maxProducersPerTopic},maxConsumersPerTopic=#{maxConsumersPerTopic}," +
            "maxConsumersPerSubscription=#{maxConsumersPerSubscription},compactionThreshold=#{compactionThreshold}," +
            "offloadThreshold=#{offloadThreshold},offloadDeletionLagMs=#{offloadDeletionLagMs}," +
            "schemaValidationEnforced=#{schemaValidationEnforced}," +
            "schemaAutoApdateCompatibilityStrategy=#{schemaAutoApdateCompatibilityStrategy} " +
            "where tenant = #{tenant} and namespace = #{namespace})")
    void updateByTenantNamespace(NamespacesEntity namespacesEntity);

    @Delete("DELETE FROM namespaces WHERE tenant = #{tenant} and namespace = #{namespace}")
    void deleteByTenantNamespace(@Param("tenant") String tenant, @Param("namespace") String namespace);
}
