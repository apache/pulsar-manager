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
import org.apache.pulsar.manager.entity.SubscriptionStatsEntity;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SubscriptionsStatsMapper {

    @Insert("INSERT INTO subscriptions_stats(topic_stats_id,subscription,msg_backlog,msg_rate_expired," +
            "msg_rate_out,msg_throughput_out,msg_rate_redeliver,number_of_entries_since_first_not_acked_message," +
            "total_non_contiguous_deleted_messages_range,subscription_type,time_stamp) " +
            "VALUES(#{topicStatsId},#{subscription},#{msgBacklog},#{msgRateExpired},#{msgRateOut}," +
            "#{msgThroughputOut},#{msgRateRedeliver},#{numberOfEntriesSinceFirstNotAckedMessage}," +
            "#{totalNonContiguousDeletedMessagesRange},#{subscriptionType}," +
            "#{timestamp})")
    @Options(useGeneratedKeys=true, keyProperty="subscriptionStatsId", keyColumn="subscription_stats_id")
    void save(SubscriptionStatsEntity subscriptionStatsEntity);

    @Select("SELECT subscription_stats_id as subscriptionStatsId,topic_stats_id as topicStatsId," +
            "subscription as subscription,msg_backlog as msgBacklog,msg_rate_expired as msgRateExpired," +
            "msg_rate_out as msgRateOut,msg_throughput_out as msgThroughputOut,msg_rate_redeliver as msgRateRedeliver," +
            "number_of_entries_since_first_not_acked_message as numberOfEntriesSinceFirstNotAckedMessage," +
            "total_non_contiguous_deleted_messages_range as totalNonContiguousDeletedMessagesRange," +
            "subscription_type as subscriptionType,time_stamp  FROM subscriptions_stats " +
            "where topic_stats_id=#{topicStatsId} and time_stamp=#{timestamp}")
    Page<SubscriptionStatsEntity> findByTopicStatsId(@Param("topicStatsId") long topicStatsId,
                                                     @Param("timestamp") long timestamp);

    @Delete("DELETE FROM subscriptions_stats WHERE time_stamp < #{refTime}")
    void delete(@Param("refTime") long refTime);
}
