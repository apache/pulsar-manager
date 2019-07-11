package com.manager.pulsar.mapper;

import com.github.pagehelper.Page;
import com.manager.pulsar.entity.BrokersEntity;
import org.apache.ibatis.annotations.*;

/**
 * Insert, delete, udpate and query for Broker.
 */
@Mapper
public interface BrokersMapper {

    @Insert("INSERT INTO brokers(broker,webServiceUrl,webServiceUrlTls,pulsarServiceUrl,pulsarServiceUrlTls," +
            "persistentTopicsEnabled,nonPersistentTopicsEnabled,cpuUsage,cpuLimit,memoryUsage," +
            "memoryLimit,directMemoryUsage,directMemoryLimit,bandwidthInUsage,bandwidthInLimit," +
            "bandwidthOutUsage,bandwidthOutLimit,msgThroughputIn,msgThroughputOut,msgRateIn,msgRateOut," +
            "lastUpdate,lastStats,bundleStats,numTopics,numBundles,numConsumers,numProducers,bundles," +
            "lastBundleGains,lastBundleLosses,brokerVersionString,loadReportType,maxResourceUsage) " +
            "VALUES(#{broker},#{webServiceUrl},#{webServiceUrlTls},#{pulsarServiceUrl},#{pulsarServiceUrlTls}," +
            "#{persistentTopicsEnabled},#{nonPersistentTopicsEnabled},#{cpuUsage},#{cpuLimit}," +
            "#{memoryUsage},#{memoryLimit},#{directMemoryUsage},#{directMemoryLimit},#{bandwidthInUsage}," +
            "#{bandwidthInLimit},#{bandwidthOutUsage},#{bandwidthOutLimit},#{msgThroughputIn},#{msgThroughputOut}," +
            "#{msgRateIn},#{msgRateOut},#{lastUpdate},#{lastStats},#{bundleStats},#{numTopics},#{numBundles}," +
            "#{numConsumers},#{numProducers},#{bundles},#{lastBundleGains},#{lastBundleLosses}," +
            "#{brokerVersionString},#{loadReportType},#{maxResourceUsage})")
    @Options(useGeneratedKeys = true, keyProperty = "brokerId", keyColumn = "brokerId")
    void insert(BrokersEntity brokersEntity);

    @Select("SELECT broker,webServiceUrl,webServiceUrlTls,pulsarServiceUrl,pulsarServiceUrlTls," +
            "persistentTopicsEnabled,nonPersistentTopicsEnabled,cpuUsage,cpuLimit,memoryUsage," +
            "memoryLimit,directMemoryUsage,directMemoryLimit,bandwidthInUsage,bandwidthInLimit," +
            "bandwidthOutUsage,bandwidthOutLimit,msgThroughputIn,msgThroughputOut,msgRateIn,msgRateOut," +
            "lastUpdate,lastStats,bundleStats,numTopics,numBundles,numConsumers,numProducers,bundles," +
            "lastBundleGains,lastBundleLosses,brokerVersionString,loadReportType,maxResourceUsage FROM brokers " +
            "WHERE brokerId=#{brokerId}")
    BrokersEntity findById(int brokerId);

    @Select("SELECT brokerId,broker,webServiceUrl,webServiceUrlTls,pulsarServiceUrl,pulsarServiceUrlTls," +
            "persistentTopicsEnabled,nonPersistentTopicsEnabled,cpuUsage,cpuLimit,memoryUsage," +
            "memoryLimit,directMemoryUsage,directMemoryLimit,bandwidthInUsage,bandwidthInLimit," +
            "bandwidthOutUsage,bandwidthOutLimit,msgThroughputIn,msgThroughputOut,msgRateIn,msgRateOut," +
            "lastUpdate,lastStats,bundleStats,numTopics,numBundles,numConsumers,numProducers,bundles," +
            "lastBundleGains,lastBundleLosses,brokerVersionString,loadReportType,maxResourceUsage FROM brokers " +
            "WHERE broker=#{broker}")
    BrokersEntity findByBroker(String broker);

    @Select("SELECT brokerId,broker,webServiceUrl,webServiceUrlTls,pulsarServiceUrl,pulsarServiceUrlTls," +
            "persistentTopicsEnabled,nonPersistentTopicsEnabled,cpuUsage,cpuLimit,memoryUsage," +
            "memoryLimit,directMemoryUsage,directMemoryLimit,bandwidthInUsage,bandwidthInLimit," +
            "bandwidthOutUsage,bandwidthOutLimit,msgThroughputIn,msgThroughputOut,msgRateIn,msgRateOut," +
            "lastUpdate,lastStats,bundleStats,numTopics,numBundles,numConsumers,numProducers,bundles," +
            "lastBundleGains,lastBundleLosses,brokerVersionString,loadReportType,maxResourceUsage FROM brokers")
    Page<BrokersEntity> getBrokersList();

    @Update("UPDATE brokers set webServiceUrl=#{webServiceUrl},webServiceUrlTls=#{webServiceUrlTls}," +
            "pulsarServiceUrl=#{pulsarServiceUrl},pulsarServiceUrlTls=#{pulsarServiceUrlTls}," +
            "persistentTopicsEnabled=#{persistentTopicsEnabled}," +
            "nonPersistentTopicsEnabled=#{nonPersistentTopicsEnabled},cpuUsage=#{cpuUsage}," +
            "cpuLimit=#{cpuLimit},memoryUsage=#{memoryUsage},memoryLimit=#{memoryLimit}," +
            "directMemoryUsage=#{directMemoryUsage},directMemoryLimit=#{directMemoryLimit}," +
            "bandwidthInUsage=#{bandwidthInUsage},bandwidthInLimit=#{bandwidthInLimit}," +
            "bandwidthOutUsage=#{bandwidthOutUsage},bandwidthOutLimit=#{bandwidthOutLimit}," +
            "msgThroughputIn=#{msgThroughputIn},msgThroughputOut=#{msgThroughputOut}," +
            "msgRateIn=#{msgRateIn},msgRateOut=#{msgRateOut},lastUpdate=#{lastUpdate}," +
            "lastStats=#{lastStats},bundleStats=#{bundleStats},numTopics=#{numTopics}," +
            "numBundles=#{numBundles},numConsumers=#{numConsumers},numProducers=#{numProducers}," +
            "bundles=#{bundles},lastBundleGains=#{lastBundleGains},lastBundleLosses=#{lastBundleLosses}," +
            "brokerVersionString=#{brokerVersionString},loadReportType=#{loadReportType}," +
            "maxResourceUsage=#{maxResourceUsage} WHERE broker=#{broker}")
    void update(BrokersEntity brokersEntity);

    @Delete("DELETE FROM brokers WHERE brokerId=#{brokerId}")
    void deleteByBrokerId(Integer brokerId);

    @Delete("DELETE FROM brokers WHERE broker=#{broker}")
    void deleteByBroker(String broker);

}
