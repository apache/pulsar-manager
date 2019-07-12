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
import com.manager.pulsar.entity.BrokersEntity;
import org.apache.ibatis.annotations.*;

/**
 * Insert, delete, udpate and query for Broker.
 */
@Mapper
public interface BrokersMapper {

    @Insert("INSERT INTO brokers(brokerId,broker,webServiceUrl,webServiceUrlTls,pulsarServiceUrl,pulsarServiceUrlTls," +
            "persistentTopicsEnabled,nonPersistentTopicsEnabled,cpuUsage,cpuLimit,memoryUsage," +
            "memoryLimit,directMemoryUsage,directMemoryLimit,bandwidthInUsage,bandwidthInLimit," +
            "bandwidthOutUsage,bandwidthOutLimit,msgThroughputIn,msgThroughputOut,msgRateIn,msgRateOut," +
            "lastUpdate,lastStats,bundleStats,numTopics,numBundles,numConsumers,numProducers,bundles," +
            "lastBundleGains,lastBundleLosses,brokerVersionString,loadReportType,maxResourceUsage) " +
            "VALUES(#{brokerId}, #{broker},#{webServiceUrl},#{webServiceUrlTls},#{pulsarServiceUrl},#{pulsarServiceUrlTls}," +
            "#{persistentTopicsEnabled},#{nonPersistentTopicsEnabled},#{cpuUsage},#{cpuLimit}," +
            "#{memoryUsage},#{memoryLimit},#{directMemoryUsage},#{directMemoryLimit},#{bandwidthInUsage}," +
            "#{bandwidthInLimit},#{bandwidthOutUsage},#{bandwidthOutLimit},#{msgThroughputIn},#{msgThroughputOut}," +
            "#{msgRateIn},#{msgRateOut},#{lastUpdate},#{lastStats},#{bundleStats},#{numTopics},#{numBundles}," +
            "#{numConsumers},#{numProducers},#{bundles},#{lastBundleGains},#{lastBundleLosses}," +
            "#{brokerVersionString},#{loadReportType},#{maxResourceUsage})")
    void insert(BrokersEntity brokersEntity);

    @Select("SELECT broker,webServiceUrl,webServiceUrlTls,pulsarServiceUrl,pulsarServiceUrlTls," +
            "persistentTopicsEnabled,nonPersistentTopicsEnabled,cpuUsage,cpuLimit,memoryUsage," +
            "memoryLimit,directMemoryUsage,directMemoryLimit,bandwidthInUsage,bandwidthInLimit," +
            "bandwidthOutUsage,bandwidthOutLimit,msgThroughputIn,msgThroughputOut,msgRateIn,msgRateOut," +
            "lastUpdate,lastStats,bundleStats,numTopics,numBundles,numConsumers,numProducers,bundles," +
            "lastBundleGains,lastBundleLosses,brokerVersionString,loadReportType,maxResourceUsage FROM brokers " +
            "WHERE brokerId=#{brokerId}")
    BrokersEntity findById(long brokerId);

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
