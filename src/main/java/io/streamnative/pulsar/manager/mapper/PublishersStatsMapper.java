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
import io.streamnative.pulsar.manager.entity.PublisherStatsEntity;
import org.apache.ibatis.annotations.*;

@Mapper
public interface PublishersStatsMapper {

    @Insert("INSERT INTO publishersStats(producerId,topicStatsId,producerName,msgRateIn," +
            "msgThroughputIn,averageMsgSize,address,connectedSince,clientVersion,metadata,`timestamp`) " +
            "VALUES(#{producerId},#{topicStatsId},#{producerName},#{msgRateIn},#{msgThroughputIn}," +
            "#{averageMsgSize},#{address},#{connectedSince},#{clientVersion},#{metadata},#{timestamp})")
    @Options(useGeneratedKeys=true, keyProperty="publisherStatsId", keyColumn="publisherStatsId")
    void save(PublisherStatsEntity publisherStatsEntity);

    @Select("SELECT publisherStatsId,producerId,topicStatsId,producerName,msgRateIn,msgThroughputIn,averageMsgSize," +
            "address,connectedSince,clientVersion,metadata,`timestamp` From publishersStats " +
            "WHERE topicStatsId=#{topicStatsId} and timestamp=#{timestamp}")
    Page<PublisherStatsEntity> findByTopicStatsId(@Param("topicStatsId") long topicStatsId,
                                                  @Param("timestamp") long timestamp);

    @Delete("DELETE FROM publishersStats WHERE `timestamp` <= #{refTime}")
    void delete(@Param("refTime") long refTime);
}
