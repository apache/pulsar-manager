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
import org.apache.pulsar.manager.entity.ReplicationStatsEntity;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ReplicationsStatsMapper {

    @Insert("INSERT INTO replications_stats(topic_stats_id,cluster,connected,msg_rate_in,msg_rate_out,msg_throughput_in," +
            "msg_throughput_out,replication_backlog,replication_delay_in_seconds,inbound_connection," +
            "inbound_connected_since,outbound_connection,outbound_connected_since,time_stamp,msg_rate_expired) " +
            "VALUES(#{topicStatsId},#{cluster},#{connected},#{msgRateIn},#{msgRateOut},#{msgThroughputIn}," +
            "#{msgThroughputOut},#{replicationBacklog},#{replicationDelayInSeconds}," +
            "#{inboundConnection},#{inboundConnectedSince},#{outboundConnection},#{outboundConnectedSince}," +
            "#{timestamp},#{msgRateExpired})")
    @Options(useGeneratedKeys=true, keyProperty="replicationStatsId", keyColumn="replication_stats_id")
    void save(ReplicationStatsEntity replicationStatsEntity);

    @Select("SELECT replication_stats_id as replicationStatsId,topic_stats_id as topicStatsId,cluster as cluster," +
            "connected as connected,msg_rate_in as msgRateIn,msg_rate_out as msgRateOut," +
            "msg_throughput_in as msgThroughputIn,msg_throughput_out as msgThroughputOut," +
            "replication_backlog as replicationBacklog,replication_delay_in_seconds as replicationDelayInSeconds," +
            "inbound_connection as inboundConnection,inbound_connected_since as inboundConnectedSince," +
            "outbound_connection as outboundConnection,outbound_connected_since as outboundConnectedSince," +
            "time_stamp ,msg_rate_expired as msgRateExpired FROM replications_stats " +
            "where topic_stats_id=#{topicStatsId} and time_stamp=#{timestamp}")
    Page<ReplicationStatsEntity> findByTopicStatsId(@Param("topicStatsId") long topicStatsId,
                                                    @Param("timestamp") long timestamp);

    @Delete("DELETE FROM replications_stats WHERE time_stamp < #{refTime}")
    void delete(@Param("refTime") long refTime);
}
