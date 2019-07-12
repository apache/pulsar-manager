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
import com.manager.pulsar.entity.BundlesEntity;
import org.apache.ibatis.annotations.*;

/**
 * Insert, delete, udpate and query for Bundle.
 */
@Mapper
public interface BundlesMapper {

    @Insert("INSERT INTO bundles(broker,tenant,namespace,bundle,msgRateIn,msgThroughputIn,msgRateOut,msgThroughputOut," +
            "consumerCount,producerCount,topics,cacheSize,throughputDifferenceThreshold,msgRateDifferenceThreshold," +
            "topicConnectionDifferenceThreshold,cacheSizeDifferenceThreshold) " +
            "VALUES(#{broker},#{tenant},#{namespace},#{bundle},#{msgRateIn},#{msgThroughputIn},#{msgRateOut}," +
            "#{msgThroughputOut},#{consumerCount},#{producerCount},#{topics},#{cacheSize}," +
            "#{throughputDifferenceThreshold},#{msgRateDifferenceThreshold},#{topicConnectionDifferenceThreshold}," +
            "#{cacheSizeDifferenceThreshold})")
    void insert(BundlesEntity bundlesEntity);

    @Select("SELECT broker,tenant,namespace,bundle,msgRateIn,msgThroughputIn,msgRateOut,msgThroughputOut," +
            "consumerCount,producerCount,topics,cacheSize,throughputDifferenceThreshold,msgRateDifferenceThreshold," +
            "topicConnectionDifferenceThreshold,cacheSizeDifferenceThreshold FROM bundles WHERE broker=#{broker} and " +
            "tenant=#{tenant} and namespace=#{namespace} and bundle=#{bundle}")
    BundlesEntity findByBrokerTenantNamespaceBundle(String broker, String tenant, String namespace, String bundle);

    @Select("SELECT broker,tenant,namespace,bundle,msgRateIn,msgThroughputIn,msgRateOut,msgThroughputOut," +
            "consumerCount,producerCount,topics,cacheSize,throughputDifferenceThreshold,msgRateDifferenceThreshold," +
            "topicConnectionDifferenceThreshold,cacheSizeDifferenceThreshold FROM bundles WHERE broker=#{btnb} or " +
            "tenant=#{btnb} or namespace=#{btnb} or bundle=#{btnb}")
    Page<BundlesEntity> findByBrokerOrTenantOrNamespaceOrBundle(String btnb);

    @Select("SELECT broker,tenant,namespace,bundle,msgRateIn,msgThroughputIn,msgRateOut,msgThroughputOut," +
            "consumerCount,producerCount,topics,cacheSize,throughputDifferenceThreshold,msgRateDifferenceThreshold," +
            "topicConnectionDifferenceThreshold,cacheSizeDifferenceThreshold FROM bundles WHERE bundle=#{bundle}")
    Page<BundlesEntity> findByBundle(String bundle);

    @Select("SELECT broker,tenant,namespace,bundle,msgRateIn,msgThroughputIn,msgRateOut,msgThroughputOut," +
            "consumerCount,producerCount,topics,cacheSize,throughputDifferenceThreshold,msgRateDifferenceThreshold," +
            "topicConnectionDifferenceThreshold,cacheSizeDifferenceThreshold FROM bundles")
    Page<BundlesEntity> getBundlesList();

    @Update("UPDATE bundles set msgRateIn=#{msgRateIn},msgThroughputIn=#{msgThroughputIn},msgRateOut=#{msgRateOut}," +
            "msgThroughputOut=#{msgThroughputOut},consumerCount=#{consumerCount},producerCount=#{producerCount}," +
            "topics=#{topics},cacheSize=#{cacheSize},throughputDifferenceThreshold=#{throughputDifferenceThreshold}," +
            "msgRateDifferenceThreshold=#{msgRateDifferenceThreshold}," +
            "topicConnectionDifferenceThreshold=#{topicConnectionDifferenceThreshold}," +
            "cacheSizeDifferenceThreshold=#{cacheSizeDifferenceThreshold} WHERE broker=#{broker} and tenant=#{tenant}" +
            " and namespace=#{namespace} and bundle={bundle}")
    void update(BundlesEntity bundlesEntity);

    @Delete("DELETE FROM bundles WHERE broker=#{broker} and tenant=#{tenant} " +
            "and namespace=#{namespace} and bundle=#{bundle}")
    void delete(@Param("broker") String broker, @Param("tenant") String tenant,
                @Param("namespace") String namespace, @Param("bundle") String bundle);

}
