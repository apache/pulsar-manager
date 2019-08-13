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
package io.streamnative.pulsar.manager.mapper;

import com.github.pagehelper.Page;
import io.streamnative.pulsar.manager.entity.NamespaceEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
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
    void insert(NamespaceEntity namespacesEntity);

    @Select("SELECT namespaceId,tenant,namespace,authPolicies,backlogQuota,replicationClusters,numBundles,boundaries," +
            "topicDispatchRate,subscriptionDispatchRate,replicatorDispatchRate,clusterSubscribeRate," +
            "bookkeeperEnsemble,bookkeeperWriteQuorum,bookkeeperAckQuorum,managedLedgerMaxMarkDeleteRate," +
            "deduplicationEnabled,latencyStatsSampleRate,messageTtlInSeconds,retentionTimeInMinutes," +
            "retentionSizeInMB,deleted,antiAffinityGroup,encryptionRequired,subscriptionAuthMode," +
            "maxProducersPerTopic,maxConsumersPerTopic,maxConsumersPerSubscription,compactionThreshold," +
            "offloadThreshold,offloadDeletionLagMs,schemaValidationEnforced," +
            "schemaAutoApdateCompatibilityStrategy FROM namespaces WHERE namespaceId = #{namespaceId}")
    NamespaceEntity findById(long namespaceId);

    @Select("SELECT namespaceId,tenant,namespace,authPolicies,backlogQuota,replicationClusters,numBundles,boundaries," +
            "topicDispatchRate,subscriptionDispatchRate,replicatorDispatchRate,clusterSubscribeRate," +
            "bookkeeperEnsemble,bookkeeperWriteQuorum,bookkeeperAckQuorum,managedLedgerMaxMarkDeleteRate," +
            "deduplicationEnabled,latencyStatsSampleRate,messageTtlInSeconds,retentionTimeInMinutes," +
            "retentionSizeInMB,deleted,antiAffinityGroup,encryptionRequired,subscriptionAuthMode," +
            "maxProducersPerTopic,maxConsumersPerTopic,maxConsumersPerSubscription,compactionThreshold," +
            "offloadThreshold,offloadDeletionLagMs,schemaValidationEnforced," +
            "schemaAutoApdateCompatibilityStrategy FROM namespaces WHERE tenant=#{tenant} and namespace=#{namespace}")
    NamespaceEntity findByTenantNamespace(String tenant, String namespace);

    @Select("SELECT namespaceId,tenant,namespace,authPolicies,backlogQuota,replicationClusters,numBundles,boundaries," +
            "topicDispatchRate,subscriptionDispatchRate,replicatorDispatchRate,clusterSubscribeRate," +
            "bookkeeperEnsemble,bookkeeperWriteQuorum,bookkeeperAckQuorum,managedLedgerMaxMarkDeleteRate," +
            "deduplicationEnabled,latencyStatsSampleRate,messageTtlInSeconds,retentionTimeInMinutes," +
            "retentionSizeInMB,deleted,antiAffinityGroup,encryptionRequired,subscriptionAuthMode," +
            "maxProducersPerTopic,maxConsumersPerTopic,maxConsumersPerSubscription,compactionThreshold," +
            "offloadThreshold,offloadDeletionLagMs,schemaValidationEnforced," +
            "schemaAutoApdateCompatibilityStrategy FROM namespaces " +
            "WHERE tenant=#{tenantOrNamespace} or namespace=#{tenantOrNamespace}")
    Page<NamespaceEntity> findByTenantOrNamespace(String tenantOrNamespace);

    @Select("SELECT namespaceId,tenant,namespace,authPolicies,backlogQuota,replicationClusters,numBundles,boundaries," +
            "topicDispatchRate,subscriptionDispatchRate,replicatorDispatchRate,clusterSubscribeRate," +
            "bookkeeperEnsemble,bookkeeperWriteQuorum,bookkeeperAckQuorum,managedLedgerMaxMarkDeleteRate," +
            "deduplicationEnabled,latencyStatsSampleRate,messageTtlInSeconds,retentionTimeInMinutes," +
            "retentionSizeInMB,deleted,antiAffinityGroup,encryptionRequired,subscriptionAuthMode," +
            "maxProducersPerTopic,maxConsumersPerTopic,maxConsumersPerSubscription,compactionThreshold," +
            "offloadThreshold,offloadDeletionLagMs,schemaValidationEnforced," +
            "schemaAutoApdateCompatibilityStrategy FROM namespaces WHERE namespace=#{namespace}")
    Page<NamespaceEntity> findByNamespace(String namespace);

    @Select("SELECT namespaceId,tenant,namespace,authPolicies,backlogQuota,replicationClusters,numBundles,boundaries," +
            "topicDispatchRate,subscriptionDispatchRate,replicatorDispatchRate,clusterSubscribeRate," +
            "bookkeeperEnsemble,bookkeeperWriteQuorum,bookkeeperAckQuorum,managedLedgerMaxMarkDeleteRate," +
            "deduplicationEnabled,latencyStatsSampleRate,messageTtlInSeconds,retentionTimeInMinutes," +
            "retentionSizeInMB,deleted,antiAffinityGroup,encryptionRequired,subscriptionAuthMode," +
            "maxProducersPerTopic,maxConsumersPerTopic,maxConsumersPerSubscription,compactionThreshold," +
            "offloadThreshold,offloadDeletionLagMs,schemaValidationEnforced," +
            "schemaAutoApdateCompatibilityStrategy FROM namespaces WHERE tenant=#{tenant}")
    Page<NamespaceEntity> findByTenant(String tenant);


    @Select("SELECT namespaceId,tenant,namespace,authPolicies,backlogQuota,replicationClusters,numBundles,boundaries," +
            "topicDispatchRate,subscriptionDispatchRate,replicatorDispatchRate,clusterSubscribeRate," +
            "bookkeeperEnsemble,bookkeeperWriteQuorum,bookkeeperAckQuorum,managedLedgerMaxMarkDeleteRate," +
            "deduplicationEnabled,latencyStatsSampleRate,messageTtlInSeconds,retentionTimeInMinutes," +
            "retentionSizeInMB,deleted,antiAffinityGroup,encryptionRequired,subscriptionAuthMode," +
            "maxProducersPerTopic,maxConsumersPerTopic,maxConsumersPerSubscription,compactionThreshold," +
            "offloadThreshold,offloadDeletionLagMs,schemaValidationEnforced," +
            "schemaAutoApdateCompatibilityStrategy FROM namespaces")
    Page<NamespaceEntity> getNamespacesList();

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
    void updateByTenantNamespace(NamespaceEntity namespacesEntity);

    @Delete("DELETE FROM namespaces WHERE tenant = #{tenant} and namespace = #{namespace}")
    void deleteByTenantNamespace(@Param("tenant") String tenant, @Param("namespace") String namespace);
}
