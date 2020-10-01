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
import org.apache.pulsar.manager.entity.ConsumerStatsEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ConsumerStatsMapper {

    @Insert("INSERT INTO consumers_stats(consumer,topic_stats_id,replication_stats_id,subscription_stats_id,address," +
            "available_permits,connected_since,msg_rate_out,msg_throughput_out,msg_rate_redeliver," +
            "client_version,time_stamp,metadata) " +
            "VALUES(#{consumer},#{topicStatsId},#{replicationStatsId},#{subscriptionStatsId},#{address}," +
            "#{availablePermits},#{connectedSince},#{msgRateOut},#{msgThroughputOut},#{msgRateRedeliver}," +
            "#{clientVersion},#{timestamp},#{metadata})")
    @Options(useGeneratedKeys=true, keyProperty="consumerStatsId", keyColumn="consumer_stats_id")
    void save(ConsumerStatsEntity consumerStatsEntity);

    @Select("SELECT consumer_stats_id as consumerStatsId,consumer as consumer,topic_stats_id as topicStatsId," +
            "replication_stats_id as replicationStatsId,subscription_stats_id as subscriptionStatsId,address as address," +
            "available_permits as availablePermits,connected_since as connectedSince,msg_rate_out as msgRateOut," +
            "msg_throughput_out as msgThroughputOut,msg_rate_redeliver as msgRateRedeliver," +
            "client_version as clientVersion,time_stamp ,metadata as metadata FROM consumers_stats " +
            "where topic_stats_id=#{topicStatsId} and time_stamp=#{timestamp}")
    Page<ConsumerStatsEntity> findByTopicStatsId(@Param("topicStatsId") long topicStatsId,
                                                 @Param("timestamp") long timestamp);

    @Select("SELECT consumer_stats_id as consumerStatsId,consumer as consumer,topic_stats_id as topicStatsId," +
            "replication_stats_id as replicationStatsId,subscription_stats_id as subscriptionStatsId,address as address," +
            "available_permits as availablePermits,connected_since as connectedSince,msg_rate_out as msgRateOut," +
            "msg_throughput_out as msgThroughputOut,msg_rate_redeliver as msgRateRedeliver," +
            "client_version as clientVersion,time_stamp ,metadata as metadata FROM consumers_stats " +
            "where subscription_stats_id=#{subscriptionStatsId} and time_stamp=#{timestamp}")
    Page<ConsumerStatsEntity> findBySubscriptionStatsId(@Param("subscriptionStatsId") long subscriptionStatsId,
                                                        @Param("timestamp") long timestamp);

    @Select("SELECT consumer_stats_id as consumerStatsId,consumer as consumer,topic_stats_id as topicStatsId," +
            "replication_stats_id as replicationStatsId,subscription_stats_id as subscriptionStatsId,address as address," +
            "available_permits as availablePermits,connected_since as connectedSince,msg_rate_out as msgRateOut," +
            "msg_throughput_out as msgThroughputOut,msg_rate_redeliver as msgRateRedeliver," +
            "client_version as clientVersion,time_stamp ,metadata as metadata FROM consumers_stats " +
            "where replication_stats_id=#{replicationStatsId} and time_stamp=#{timestamp}")
    Page<ConsumerStatsEntity> findByReplicationStatsId(@Param("replicationStatsId") long replicationStatsId,
                                                       @Param("timestamp") long timestamp);

    @Select({"<script>",
            "SELECT consumer_stats_id as consumerStatsId,consumer as consumer,topic_stats_id as topicStatsId," +
                    "replication_stats_id as replicationStatsId,subscription_stats_id as subscriptionStatsId,address as address," +
                    "available_permits as availablePermits,connected_since as connectedSince,msg_rate_out as msgRateOut," +
                    "msg_throughput_out as msgThroughputOut,msg_rate_redeliver as msgRateRedeliver," +
                    "client_version as clientVersion,time_stamp ,metadata as metadata FROM consumers_stats " +
            "where time_stamp=#{timestamp} and " +
                    "topic_stats_id IN <foreach collection='topicStatsIdList' item='topicStatsId' open='(' separator=',' close=')'> #{topicStatsId} </foreach>" +
                    "</script>"})
    List<ConsumerStatsEntity> findByMultiTopicStatsId(@Param("topicStatsIdList") List<Long> topicStatsIdList,
                                                      @Param("timestamp") long timestamp);

    @Delete("DELETE FROM consumers_stats WHERE time_stamp < #{refTime}")
    void delete(@Param("refTime") long refTime);
}
