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
import com.manager.pulsar.entity.BundleStatsEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface BundleStatsMapper {
    @Insert("INSERT INTO bundleStats(broker,tenant,namespace,bundle,msgRateIn,msgThroughputIn,msgRateOut,msgThroughputOut," +
            "consumerCount,producerCount,topics,cacheSize) " +
            "VALUES(#{broker},#{tenant},#{namespace},#{bundle},#{msgRateIn},#{msgThroughputIn},#{msgRateOut}," +
            "#{msgThroughputOut},#{consumerCount},#{producerCount},#{topics},#{cacheSize})")
    void insert(BundleStatsEntity bundleStatsEntity);

    @Select("SELECT broker,tenant,namespace,bundle,msgRateIn,msgThroughputIn,msgRateOut,msgThroughputOut," +
            "consumerCount,producerCount,topics,cacheSize,throughputDifferenceThreshold,msgRateDifferenceThreshold," +
            "topicConnectionDifferenceThreshold,cacheSizeDifferenceThreshold FROM bundleStats WHERE broker=#{broker} and " +
            "tenant=#{tenant} and namespace=#{namespace} and bundle=#{bundle}")
    BundleStatsEntity findByBrokerTenantNamespaceBundle(String broker, String tenant, String namespace, String bundle);

    @Select("SELECT broker,tenant,namespace,bundle,msgRateIn,msgThroughputIn,msgRateOut,msgThroughputOut," +
            "consumerCount,producerCount,topics,cacheSize FROM bundleStats WHERE broker=#{btnb} or " +
            "tenant=#{btnb} or namespace=#{btnb} or bundle=#{btnb}")
    Page<BundleStatsEntity> findByBrokerOrTenantOrNamespaceOrBundle(String btnb);

    @Select("SELECT broker,tenant,namespace,bundle,msgRateIn,msgThroughputIn,msgRateOut,msgThroughputOut," +
            "consumerCount,producerCount,topics,cacheSize FROM bundleStats WHERE bundle=#{bundle}")
    Page<BundleStatsEntity> findByBundle(String bundle);

    @Select("SELECT broker,tenant,namespace,bundle,msgRateIn,msgThroughputIn,msgRateOut,msgThroughputOut," +
            "consumerCount,producerCount,topics,cacheSize FROM bundleStats")
    Page<BundleStatsEntity> getBundlesList();

    @Update("UPDATE bundles set msgRateIn=#{msgRateIn},msgThroughputIn=#{msgThroughputIn},msgRateOut=#{msgRateOut}," +
            "msgThroughputOut=#{msgThroughputOut},consumerCount=#{consumerCount},producerCount=#{producerCount}," +
            "topics=#{topics},cacheSize=#{cacheSize} WHERE broker=#{broker} and tenant=#{tenant}" +
            " and namespace=#{namespace} and bundle={bundle}")
    void update(BundleStatsEntity bundleStatsEntity);

    @Delete("DELETE FROM bundleStats WHERE broker=#{broker} and tenant=#{tenant} " +
            "and namespace=#{namespace} and bundle=#{bundle}")
    void delete(@Param("broker") String broker, @Param("tenant") String tenant,
                @Param("namespace") String namespace, @Param("bundle") String bundle);
}
