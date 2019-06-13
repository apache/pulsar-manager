package com.manager.pulsar.mapper;

import com.github.pagehelper.Page;
import com.manager.pulsar.entity.NamespacesEntity;
import com.manager.pulsar.entity.TenantsEntity;
import org.apache.ibatis.annotations.*;

public interface NamespacesMapper {

    @Insert("INSERT INTO namespaces(tenant,namespace,authPolicies,replicationClusters," +
            "numBundles,boundaries,topicDispatchRate,subscriptionDispatchRate,replicatorDispatchRate," +
            "clusterSubscribeRate,bookkeeperEnsemble,bookkeeperWriteQuorum,bookkeeperAckQuorum," +
            "managedLedgerMaxMarkDeleteRate,deduplicationEnabled,latencyStatsSampleRate,messageTtlInSeconds," +
            "retentionTimeInMinutes,retentionSizeInMB,deleted,antiAffinityGroup,encryptionRequired," +
            "subscriptionAuthMode,maxProducersPerTopic,maxConsumersPerTopic,maxConsumersPerSubscription," +
            "compactionThreshold,offloadThreshold,offloadDeletionLagMs,schemaValidationEnforced," +
            "schemaAutoApdateCompatibilityStrategy)" +
            "VALUES(#{tenant},#{namespace},#{authPolicies},#{replicationClusters},#{numBundles}," +
            "#{boundaries},#{topicDispatchRate},#{subscriptionDispatchRate},#{replicatorDispatchRate}," +
            "#{clusterSubscribeRate},#{bookkeeperEnsemble},#{bookkeeperWriteQuorum},#{bookkeeperAckQuorum}," +
            "#{managedLedgerMaxMarkDeleteRate},#{deduplicationEnabled},#{latencyStatsSampleRate}," +
            "#{messageTtlInSeconds},#{retentionTimeInMinutes},#{retentionSizeInMB},#{deleted}," +
            "#{antiAffinityGroup},#{encryptionRequired},#{subscriptionAuthMode},#{maxProducersPerTopic}," +
            "#{maxConsumersPerTopic},#{maxConsumersPerSubscription},#{compactionThreshold},#{offloadThreshold}," +
            "#{offloadDeletionLagMs},#{schemaValidationEnforced},#{schemaAutoApdateCompatibilityStrategy})")
    @Options(useGeneratedKeys=true, keyProperty="namespaceId", keyColumn="namespaceId")
    void insert(NamespacesEntity namespacesEntity);

    @Select("SELECT namespaceId,tenant,namespace,authPolicies,replicationClusters,numBundles,boundaries," +
            "topicDispatchRate,subscriptionDispatchRate,replicatorDispatchRate,clusterSubscribeRate," +
            "bookkeeperEnsemble,bookkeeperWriteQuorum,bookkeeperAckQuorum,managedLedgerMaxMarkDeleteRate," +
            "deduplicationEnabled,latencyStatsSampleRate,messageTtlInSeconds,retentionTimeInMinutes," +
            "retentionSizeInMB,deleted,antiAffinityGroup,encryptionRequired,subscriptionAuthMode," +
            "maxProducersPerTopic,maxConsumersPerTopic,maxConsumersPerSubscription,compactionThreshold," +
            "offloadThreshold,offloadDeletionLagMs,schemaValidationEnforced," +
            "schemaAutoApdateCompatibilityStrategy FROM namespaces WHERE namespaceId = #{namespaceId}")
    NamespacesEntity findById(int namespaceId);

    @Select("SELECT namespaceId,tenant,namespace,authPolicies,replicationClusters,numBundles,boundaries," +
            "topicDispatchRate,subscriptionDispatchRate,replicatorDispatchRate,clusterSubscribeRate," +
            "bookkeeperEnsemble,bookkeeperWriteQuorum,bookkeeperAckQuorum,managedLedgerMaxMarkDeleteRate," +
            "deduplicationEnabled,latencyStatsSampleRate,messageTtlInSeconds,retentionTimeInMinutes," +
            "retentionSizeInMB,deleted,antiAffinityGroup,encryptionRequired,subscriptionAuthMode," +
            "maxProducersPerTopic,maxConsumersPerTopic,maxConsumersPerSubscription,compactionThreshold," +
            "offloadThreshold,offloadDeletionLagMs,schemaValidationEnforced," +
            "schemaAutoApdateCompatibilityStrategy FROM namespaces WHERE tenant=#{tenant} and namespace=#{namespace}")
    NamespacesEntity findByTenantNamespace(String tenant, String namespace);

    @Select("SELECT namespaceId,tenant,namespace,authPolicies,replicationClusters,numBundles,boundaries," +
            "topicDispatchRate,subscriptionDispatchRate,replicatorDispatchRate,clusterSubscribeRate," +
            "bookkeeperEnsemble,bookkeeperWriteQuorum,bookkeeperAckQuorum,managedLedgerMaxMarkDeleteRate," +
            "deduplicationEnabled,latencyStatsSampleRate,messageTtlInSeconds,retentionTimeInMinutes," +
            "retentionSizeInMB,deleted,antiAffinityGroup,encryptionRequired,subscriptionAuthMode," +
            "maxProducersPerTopic,maxConsumersPerTopic,maxConsumersPerSubscription,compactionThreshold," +
            "offloadThreshold,offloadDeletionLagMs,schemaValidationEnforced," +
            "schemaAutoApdateCompatibilityStrategy FROM namespaces " +
            "WHERE tenant=#{tenantOrNamespace} or namespace=#{tenantOrNamespace}")
    Page<NamespacesEntity> findByTenantOrNamespace(String tenantOrNamespace);

    @Select("SELECT namespaceId,tenant,namespace,authPolicies,replicationClusters,numBundles,boundaries," +
            "topicDispatchRate,subscriptionDispatchRate,replicatorDispatchRate,clusterSubscribeRate," +
            "bookkeeperEnsemble,bookkeeperWriteQuorum,bookkeeperAckQuorum,managedLedgerMaxMarkDeleteRate," +
            "deduplicationEnabled,latencyStatsSampleRate,messageTtlInSeconds,retentionTimeInMinutes," +
            "retentionSizeInMB,deleted,antiAffinityGroup,encryptionRequired,subscriptionAuthMode," +
            "maxProducersPerTopic,maxConsumersPerTopic,maxConsumersPerSubscription,compactionThreshold," +
            "offloadThreshold,offloadDeletionLagMs,schemaValidationEnforced," +
            "schemaAutoApdateCompatibilityStrategy FROM namespaces WHERE namespace=#{namespace}")
    Page<NamespacesEntity> findByNamespace(String namespace);


    @Select("SELECT namespaceId,tenant,namespace,authPolicies,replicationClusters,numBundles,boundaries," +
            "topicDispatchRate,subscriptionDispatchRate,replicatorDispatchRate,clusterSubscribeRate," +
            "bookkeeperEnsemble,bookkeeperWriteQuorum,bookkeeperAckQuorum,managedLedgerMaxMarkDeleteRate," +
            "deduplicationEnabled,latencyStatsSampleRate,messageTtlInSeconds,retentionTimeInMinutes," +
            "retentionSizeInMB,deleted,antiAffinityGroup,encryptionRequired,subscriptionAuthMode," +
            "maxProducersPerTopic,maxConsumersPerTopic,maxConsumersPerSubscription,compactionThreshold," +
            "offloadThreshold,offloadDeletionLagMs,schemaValidationEnforced," +
            "schemaAutoApdateCompatibilityStrategy FROM namespaces")
    Page<NamespacesEntity> getNamespacesList();

    @Delete("DELETE FROM namespaces WHERE namespaceId = #{namespaceId}")
    void delete(NamespacesEntity namespacesEntity);

    @Update("UPDATE namespaces set authPolicies=#{authPolicies},replicationClusters=#{replicationClusters}," +
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
    void deleteByTenantNamespace(NamespacesEntity namespacesEntity);
}
