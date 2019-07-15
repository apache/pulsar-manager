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
import com.manager.pulsar.entity.BrokerStatsEntity;
import org.apache.ibatis.annotations.*;

/**
 * Insert, delete, udpate and query for broker stats.
 */
@Mapper
public interface BrokerStatsMapper {

    @Insert("INSERT INTO brokerStats(broker,cpuUsage,cpuLimit,memoryUsage," +
            "memoryLimit,directMemoryUsage,directMemoryLimit,bandwidthInUsage,bandwidthInLimit," +
            "bandwidthOutUsage,bandwidthOutLimit,msgThroughputIn,msgThroughputOut,msgRateIn,msgRateOut," +
            "lastUpdate,lastStats,bundleStats,numTopics,numBundles,numConsumers,numProducers,bundles," +
            "lastBundleGains,lastBundleLosses) " +
            "VALUES(#{broker},#{cpuUsage},#{cpuLimit},#{memoryUsage},#{memoryLimit},#{directMemoryUsage}," +
            "#{directMemoryLimit},#{bandwidthInUsage},#{bandwidthInLimit},#{bandwidthOutUsage}," +
            "#{bandwidthOutLimit},#{msgThroughputIn},#{msgThroughputOut},#{msgRateIn},#{msgRateOut},#{lastUpdate}," +
            "#{lastStats},#{bundleStats},#{numTopics},#{numBundles},#{numConsumers},#{numProducers},#{bundles}," +
            "#{lastBundleGains},#{lastBundleLosses})")
    void insert(BrokerStatsEntity brokerStatsEntity);

    @Select("SELECT broker,cpuUsage,cpuLimit,memoryUsage," +
            "memoryLimit,directMemoryUsage,directMemoryLimit,bandwidthInUsage,bandwidthInLimit," +
            "bandwidthOutUsage,bandwidthOutLimit,msgThroughputIn,msgThroughputOut,msgRateIn,msgRateOut," +
            "lastUpdate,lastStats,bundleStats,numTopics,numBundles,numConsumers,numProducers,bundles," +
            "lastBundleGains,lastBundleLosses FROM brokerStats WHERE broker=#{broker}")
    BrokerStatsEntity findByBroker(String broker);

    @Select("SELECT broker,cpuUsage,cpuLimit,memoryUsage," +
            "memoryLimit,directMemoryUsage,directMemoryLimit,bandwidthInUsage,bandwidthInLimit," +
            "bandwidthOutUsage,bandwidthOutLimit,msgThroughputIn,msgThroughputOut,msgRateIn,msgRateOut," +
            "lastUpdate,lastStats,bundleStats,numTopics,numBundles,numConsumers,numProducers,bundles," +
            "lastBundleGains,lastBundleLosses FROM brokerStats")
    Page<BrokerStatsEntity> getBrokerStatsList();

    @Update("UPDATE brokerStats set cpuUsage=#{cpuUsage}," +
            "cpuLimit=#{cpuLimit},memoryUsage=#{memoryUsage},memoryLimit=#{memoryLimit}," +
            "directMemoryUsage=#{directMemoryUsage},directMemoryLimit=#{directMemoryLimit}," +
            "bandwidthInUsage=#{bandwidthInUsage},bandwidthInLimit=#{bandwidthInLimit}," +
            "bandwidthOutUsage=#{bandwidthOutUsage},bandwidthOutLimit=#{bandwidthOutLimit}," +
            "msgThroughputIn=#{msgThroughputIn},msgThroughputOut=#{msgThroughputOut}," +
            "msgRateIn=#{msgRateIn},msgRateOut=#{msgRateOut},lastUpdate=#{lastUpdate}," +
            "lastStats=#{lastStats},bundleStats=#{bundleStats},numTopics=#{numTopics}," +
            "numBundles=#{numBundles},numConsumers=#{numConsumers},numProducers=#{numProducers}," +
            "bundles=#{bundles},lastBundleGains=#{lastBundleGains},lastBundleLosses=#{lastBundleLosses}" +
            " WHERE broker=#{broker}")
    void update(BrokerStatsEntity brokerStatsEntity);

    @Delete("DELETE FROM brokerStats WHERE broker=#{broker}")
    void delete(String broker);
}
