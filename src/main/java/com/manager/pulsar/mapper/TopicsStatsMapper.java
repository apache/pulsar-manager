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
import com.manager.pulsar.entity.TopicStatsEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TopicsStatsMapper {

    @Insert("INSERT INTO topicsStats(cluster,broker,tenant,namespace,bundle,persistent,topic," +
            "producerCount,subscriptionCount,msgRateIn,msgThroughputIn,msgRateOut,msgThroughputOut," +
            "averageMsgSize,storageSize,timestamp) " +
            "VALUES(#{cluster},#{broker},#{tenant},#{namespace},#{bundle},#{persistent},#{topic}," +
            "#{producerCount},#{subscriptionCount},#{msgRateIn},#{msgThroughputIn},#{msgRateOut},#{msgThroughputOut}," +
            "#{averageMsgSize},#{storageSize},#{timestamp})")
    @Options(useGeneratedKeys=true, keyProperty="topicStatsId", keyColumn="topicStatsId")
    void insert(TopicStatsEntity topicStatsEntity);

    @Select("SELECT topicStatsId,cluster,broker,tenant,namespace,bundle,persistent,topic,producerCount,subscriptionCount," +
            "msgRateIn,msgThroughputIn,msgRateOut,msgThroughputOut,averageMsgSize,storageSize,timestamp FROM topicsStats " +
            "ORDER BY timestamp DESC limit 1 ")
    TopicStatsEntity findMaxTime();

    @Select("SELECT topicStatsId,cluster,broker,tenant,namespace,bundle,persistent,topic,producerCount,subscriptionCount," +
            "msgRateIn,msgThroughputIn,msgRateOut,msgThroughputOut,averageMsgSize,storageSize,timestamp FROM topicsStats " +
            "WHERE cluster=#{cluster} and broker=#{broker} and timestamp=#{timestamp}")
    Page<TopicStatsEntity> findByClusterBroker(
            @Param("cluster") String cluster, @Param("broker") String broker, @Param("timestamp") long timestamp);

    @Select("SELECT topicStatsId,cluster,broker,tenant,namespace,bundle,persistent,topic,producerCount,subscriptionCount," +
            "msgRateIn,msgThroughputIn,msgRateOut,msgThroughputOut,averageMsgSize,storageSize,timestamp FROM topicsStats " +
            "WHERE cluster=#{cluster} and broker=#{broker} and tenant=#{tenant} and namespace=#{namespace} " +
            "and timestamp=#{timestamp}")
    Page<TopicStatsEntity> findByNamespace(@Param("cluster") String cluster, @Param("broker") String broker,
                                           @Param("tenant") String tenant, @Param("namespace") String namespace,
                                           @Param("timestamp") long timestamp);

    @Select({"<script>",
            "SELECT topicStatsId,cluster,broker,tenant,namespace,bundle,persistent,topic,producerCount,subscriptionCount," +
            "msgRateIn,msgThroughputIn,msgRateOut,msgThroughputOut,averageMsgSize,storageSize,timestamp FROM topicsStats",
            "WHERE broker=#{broker} and tenant=#{tenant} and namespace=#{namespace} and timestamp=#{timestamp} and " +
                    "topic IN <foreach collection='topicList' item='topic' open='(' separator=',' close=')'> #{topic} </foreach>" +
            "</script>"})
    Page<TopicStatsEntity> findByMultiTopic(@Param("broker") String broker, @Param("tenant") String tenant,
                                            @Param("namespace") String namespace, @Param("persistent") String persistent,
                                            @Param("topicList") List<String> topicList, @Param("timestamp") long timestamp);

    @Delete("DELETE FROM topicsStats WHERE #{nowTime} - #{timeInterval} >= timestamp")
    void delete(@Param("nowTime") long nowTime, @Param("timeInterval") long timeInterval);
}
