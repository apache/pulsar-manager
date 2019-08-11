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
import com.manager.pulsar.entity.SubscriptionStatsEntity;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SubscriptionsStatsMapper {

    @Insert("INSERT INTO subscriptionsStats(topicStatsId,subscription,msgBacklog,msgRateExpired," +
            "msgRateOut,msgThroughputOut,msgRateRedeliver,numberOfEntriesSinceFirstNotAckedMessage," +
            "totalNonContiguousDeletedMessagesRange,subscriptionType,timestamp) " +
            "VALUES(#{topicStatsId},#{subscription},#{msgBacklog},#{msgRateExpired},#{msgRateOut}," +
            "#{msgThroughputOut},#{msgRateRedeliver},#{numberOfEntriesSinceFirstNotAckedMessage}," +
            "#{totalNonContiguousDeletedMessagesRange},#{subscriptionType}," +
            "#{timestamp})")
    @Options(useGeneratedKeys=true, keyProperty="subscriptionStatsId", keyColumn="subscriptionStatsId")
    void save(SubscriptionStatsEntity subscriptionStatsEntity);

    @Select("SELECT subscriptionStatsId,topicStatsId,subscription,msgBacklog,msgRateExpired,msgRateOut," +
            "msgThroughputOut,msgRateRedeliver,numberOfEntriesSinceFirstNotAckedMessage," +
            "totalNonContiguousDeletedMessagesRange,subscriptionType,timestamp FROM subscriptionsStats " +
            "where topicStatsId=#{topicStatsId} and timestamp=#{timestamp}")
    Page<SubscriptionStatsEntity> findByTopicStatsId(@Param("topicStatsId") long topicStatsId,
                                                     @Param("timestamp") long timestamp);

    @Delete("DELETE FROM subscriptionsStats WHERE #{nowTime} - #{timeInterval} >= timestamp")
    void delete(@Param("nowTime") long nowTime, @Param("timeInterval") long timeInterval);
}
