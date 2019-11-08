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
import org.apache.pulsar.manager.entity.PublisherStatsEntity;
import org.apache.ibatis.annotations.*;

@Mapper
public interface PublishersStatsMapper {

    @Insert("INSERT INTO publishers_stats(producer_id,topic_stats_id,producer_name,msg_rate_in," +
            "msg_throughput_in,average_msg_size,address,connected_since,client_version,metadata,time_stamp) " +
            "VALUES(#{producerId},#{topicStatsId},#{producerName},#{msgRateIn},#{msgThroughputIn}," +
            "#{averageMsgSize},#{address},#{connectedSince},#{clientVersion},#{metadata},#{timestamp})")
    @Options(useGeneratedKeys=true, keyProperty="publisherStatsId", keyColumn="publisher_stats_id")
    void save(PublisherStatsEntity publisherStatsEntity);

    @Select("SELECT publisher_stats_id as publisherStatsId,producer_id as producerId,topic_stats_id as topicStatsId," +
            "producer_name as producerName,msg_rate_in as msgRateIn,msg_throughput_in as msgThroughputIn," +
            "average_msg_size as averageMsgSize,address as address,connected_since as connectedSince," +
            "client_version as clientVersion,metadata as metadata,time_stamp  From publishers_stats " +
            "WHERE topic_stats_id=#{topicStatsId} and time_stamp=#{timestamp}")
    Page<PublisherStatsEntity> findByTopicStatsId(@Param("topicStatsId") long topicStatsId,
                                                  @Param("timestamp") long timestamp);

    @Delete("DELETE FROM publishers_stats WHERE time_stamp < #{refTime}")
    void delete(@Param("refTime") long refTime);
}
