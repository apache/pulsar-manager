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
import io.streamnative.pulsar.manager.entity.ConsumerStatsEntity;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ConsumerStatsMapper {

    @Insert("INSERT INTO consumersStats(consumer,topicStatsId,replicationStatsId,subscriptionStatsId,address," +
            "availablePermits,connectedSince,msgRateOut,msgThroughputOut,msgRateRedeliver," +
            "clientVersion,timestamp,metadata) " +
            "VALUES(#{consumer},#{topicStatsId},#{replicationStatsId},#{subscriptionStatsId},#{address}," +
            "#{availablePermits},#{connectedSince},#{msgRateOut},#{msgThroughputOut},#{msgRateRedeliver}," +
            "#{clientVersion},#{timestamp},#{metadata})")
    @Options(useGeneratedKeys=true, keyProperty="consumerStatsId", keyColumn="consumerStatsId")
    void save(ConsumerStatsEntity consumerStatsEntity);

    @Select("SELECT consumerStatsId,consumer,topicStatsId,replicationStatsId,subscriptionStatsId,address," +
            "availablePermits,connectedSince,msgRateOut,msgThroughputOut,msgRateRedeliver," +
            "clientVersion,timestamp,metadata FROM consumersStats " +
            "where topicStatsId=#{topicStatsId} and timestamp=#{timestamp}")
    Page<ConsumerStatsEntity> findByTopicStatsId(@Param("topicStatsId") long topicStatsId,
                                                 @Param("timestamp") long timestamp);

    @Select("SELECT consumerStatsId,consumer,topicStatsId,replicationStatsId,subscriptionStatsId,address," +
            "availablePermits,connectedSince,msgRateOut,msgThroughputOut,msgRateRedeliver," +
            "clientVersion,timestamp,metadata FROM consumersStats " +
            "where subscriptionStatsId=#{subscriptionStatsId} and timestamp=#{timestamp}")
    Page<ConsumerStatsEntity> findBySubscriptionStatsId(@Param("subscriptionStatsId") long subscriptionStatsId,
                                                        @Param("timestamp") long timestamp);

    @Select("SELECT consumerStatsId,consumer,topicStatsId,replicationStatsId,subscriptionStatsId,address," +
            "availablePermits,connectedSince,msgRateOut,msgThroughputOut,msgRateRedeliver," +
            "clientVersion,timestamp,metadata FROM consumersStats " +
            "where replicationStatsId=#{replicationStatsId} and timestamp=#{timestamp}")
    Page<ConsumerStatsEntity> findByReplicationStatsId(@Param("replicationStatsId") long replicationStatsId,
                                                       @Param("timestamp") long timestamp);

    @Delete("DELETE FROM consumersStats WHERE #{nowTime} - #{timeInterval} >= timestamp")
    void delete(@Param("nowTime") long nowTime, @Param("timeInterval") long timeInterval);
}
