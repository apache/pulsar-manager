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
package org.apache.pulsar.manager.mapper;

import com.github.pagehelper.Page;
import org.apache.pulsar.manager.entity.TopicStatsEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TopicsStatsMapper {

    @Insert("INSERT INTO topics_stats(environment, cluster,broker,tenant,namespace,bundle,persistent,topic," +
            "producer_count,subscription_count,msg_rate_in,msg_throughput_in,msg_rate_out,msg_throughput_out," +
            "average_msg_size,storage_size,time_stamp) " +
            "VALUES(#{environment},#{cluster},#{broker},#{tenant},#{namespace},#{bundle},#{persistent},#{topic}," +
            "#{producerCount},#{subscriptionCount},#{msgRateIn},#{msgThroughputIn},#{msgRateOut},#{msgThroughputOut}," +
            "#{averageMsgSize},#{storageSize},#{timestamp})")
    @Options(useGeneratedKeys=true, keyProperty="topicStatsId", keyColumn="topic_stats_id")
    void insert(TopicStatsEntity topicStatsEntity);

    @Select("SELECT topic_stats_id as topicStatsId,environment as environment,cluster as cluster,broker as broker," +
            "tenant as tenant,namespace as namespace,bundle as bundle,persistent as persistent," +
            "topic as topic,producer_count as producerCount,subscription_count as subscriptionCount," +
            "msg_rate_in as msgRateIn,msg_throughput_in as msgThroughputIn,msg_rate_out as msgRateOut," +
            "msg_throughput_out as msgThroughputOut,average_msg_size as averageMsgSize,storage_size as storageSize," +
            "time_stamp  FROM topics_stats " +
            "ORDER BY time_stamp DESC limit 1 ")
    TopicStatsEntity findMaxTime();

    @Select("SELECT topic_stats_id as topicStatsId,environment as environment,cluster as cluster,broker as broker," +
            "tenant as tenant,namespace as namespace,bundle as bundle,persistent as persistent," +
            "topic as topic,producer_count as producerCount,subscription_count as subscriptionCount," +
            "msg_rate_in as msgRateIn,msg_throughput_in as msgThroughputIn,msg_rate_out as msgRateOut," +
            "msg_throughput_out as msgThroughputOut,average_msg_size as averageMsgSize,storage_size as storageSize," +
            "time_stamp  FROM topics_stats " +
            "WHERE environment=#{environment} and cluster=#{cluster} and broker=#{broker} and time_stamp=#{timestamp}")
    Page<TopicStatsEntity> findByClusterBroker(
            @Param("environment") String environment,
            @Param("cluster") String cluster,
            @Param("broker") String broker,
            @Param("timestamp") long timestamp);

    @Select("SELECT topic_stats_id as topicStatsId,environment as environment,cluster as cluster,broker as broker," +
            "tenant as tenant,namespace as namespace,bundle as bundle,persistent as persistent," +
            "topic as topic,producer_count as producerCount,subscription_count as subscriptionCount," +
            "msg_rate_in as msgRateIn,msg_throughput_in as msgThroughputIn,msg_rate_out as msgRateOut," +
            "msg_throughput_out as msgThroughputOut,average_msg_size as averageMsgSize,storage_size as storageSize," +
            "time_stamp  FROM topics_stats " +
            "WHERE environment=#{environment} and tenant=#{tenant} and namespace=#{namespace} " +
            "and time_stamp=#{timestamp}")
    Page<TopicStatsEntity> findByNamespace(
            @Param("environment") String environment,
            @Param("tenant") String tenant,
            @Param("namespace") String namespace,
            @Param("timestamp") long timestamp);

    @Select({"<script>",
            "SELECT environment, cluster, tenant, namespace, persistent, topic,"
                + "sum(producer_count) as producerCount,"
                + "sum(subscription_count) as subscriptionCount,"
                + "sum(msg_rate_in) as msgRateIn,"
                + "sum(msg_throughput_in) as msgThroughputIn,"
                + "sum(msg_rate_out) as msgRateOut,"
                + "sum(msg_throughput_out) as msgThroughputOut,"
                + "avg(average_msg_size) as averageMsgSize,"
                + "sum(storage_size) as storageSize, time_stamp  FROM topics_stats",
            "WHERE environment=#{environment} and tenant=#{tenant} and namespace=#{namespace} and time_stamp=#{timestamp} and " +
                    "topic IN <foreach collection='topicList' item='topic' open='(' separator=',' close=')'> #{topic} </foreach>" +
            "GROUP BY environment, cluster, tenant, namespace, persistent, topic, time_stamp" +
            "</script>"})
    Page<TopicStatsEntity> findByMultiTopic(
            @Param("environment") String environment,
            @Param("tenant") String tenant,
            @Param("namespace") String namespace,
            @Param("persistent") String persistent,
            @Param("topicList") List<String> topicList,
            @Param("timestamp") long timestamp);

    @Select({"<script>",
            "SELECT environment, tenant,"
                    + "sum(producer_count) as producerCount,"
                    + "sum(subscription_count) as subscriptionCount,"
                    + "sum(msg_rate_in) as msgRateIn,"
                    + "sum(msg_throughput_in) as msgThroughputIn,"
                    + "sum(msg_rate_out) as msgRateOut,"
                    + "sum(msg_throughput_out) as msgThroughputOut,"
                    + "avg(average_msg_size) as averageMsgSize,"
                    + "sum(storage_size) as storageSize, time_stamp  FROM topics_stats",
            "WHERE environment=#{environment} and time_stamp=#{timestamp} and " +
                    "tenant IN <foreach collection='tenantList' item='tenant' open='(' separator=',' close=')'> #{tenant} </foreach>" +
                    "GROUP BY environment, tenant, time_stamp" +
                    "</script>"})
    Page<TopicStatsEntity> findByMultiTenant(
            @Param("environment") String environment,
            @Param("tenantList") List<String> tenantList,
            @Param("timestamp") long timestamp);

    @Select({"<script>",
            "SELECT environment, tenant, namespace,"
                    + "sum(producer_count) as producerCount,"
                    + "sum(subscription_count) as subscriptionCount,"
                    + "sum(msg_rate_in) as msgRateIn,"
                    + "sum(msg_throughput_in) as msgThroughputIn,"
                    + "sum(msg_rate_out) as msgRateOut,"
                    + "sum(msg_throughput_out) as msgThroughputOut,"
                    + "avg(average_msg_size) as averageMsgSize,"
                    + "sum(storage_size) as storageSize, time_stamp  FROM topics_stats",
            "WHERE environment=#{environment} and tenant=#{tenant} and time_stamp=#{timestamp} and " +
                    "namespace IN <foreach collection='namespaceList' item='namespace' open='(' separator=',' close=')'> #{namespace} </foreach>" +
                    "GROUP BY environment, tenant, namespace, time_stamp" +
                    "</script>"})
    Page<TopicStatsEntity> findByMultiNamespace(
            @Param("environment") String environment,
            @Param("tenant") String tenant,
            @Param("namespaceList") List<String> namespaceList,
            @Param("timestamp") long timestamp);

    @Select({"<script>",
            "SELECT topic_stats_id as topicStatsId,environment as environment,cluster as cluster,broker as broker," +
                    "tenant as tenant,namespace as namespace,bundle as bundle,persistent as persistent," +
                    "topic as topic,producer_count as producerCount,subscription_count as subscriptionCount," +
                    "msg_rate_in as msgRateIn,msg_throughput_in as msgThroughputIn,msg_rate_out as msgRateOut," +
                    "msg_throughput_out as msgThroughputOut,average_msg_size as averageMsgSize,storage_size as storageSize," +
                    "time_stamp  FROM topics_stats " +
            "WHERE time_stamp=#{timestamp} and " +
                    "environment IN <foreach collection='environmentList' item='environment' open='(' separator=',' close=')'> #{environment} </foreach>" +
                    "</script>"})
    List<TopicStatsEntity> findByMultiEnvironment(
            @Param("environmentList") List<String> environmentList,
            @Param("timestamp") long timestamp);

    @Delete("DELETE FROM topics_stats WHERE time_stamp < #{refTime}")
    void delete(@Param("refTime") long refTime);
}
