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
import com.manager.pulsar.entity.SubscriptionEntity;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SubscriptionsMapper {

    @Insert("INSERT INTO subscriptions(subscriptionId,tenant,namespace,topicId,topic,persistent,subscription) " +
            "VALUES(#{subscriptionId},#{tenant},#{namespace},#{topicId},#{topic},#{persistent},#{subscription})")
    void insert(SubscriptionEntity subscriptionsEntity);


    @Select("SELECT subscriptionId,tenant,namespace,topicId,topic,persistent,subscription FROM subscriptions" +
            "WHERE subscriptionId=#{subscriptionId}")
    SubscriptionEntity findById(long subscriptionId);

    @Select("SELECT subscriptionId,tenant,namespace,topicId,topic,persistent,subscription FROM subscriptions " +
            "WHERE tenant=#{tenant} or namespace=#{namespace} or topic=#{topic} or subscription=#{subscription}")
    SubscriptionEntity findByTenantNamespaceTopicSubscription(@Param("tenant") String tenant,
                                                              @Param("namespace") String namespace,
                                                              @Param("topic") String topic,
                                                              @Param("subscription") String subscription);

    @Select("SELECT subscriptionId,tenant,namespace,topicId,topic,persistent,subscription FROM subscriptions " +
            "WHERE tenant=#{tnts} or namespace=#{tnts} or topic=#{tnts} or subscription=#{tnts}")
    Page<SubscriptionEntity> findByTenantOrNamespaceOrTopicOrSubscription(String tnts);

    @Select("SELECT subscriptionId,tenant,namespace,topicId,topic,persistent,partition,subscription FROM subscriptions " +
            "WHERE subscription=#{subscription}")
    Page<SubscriptionEntity> findBySubscription(String subscription);

    @Select("SELECT subscriptionId,tenant,namespace,topicId,topic,persistent,subscription FROM subscriptions")
    Page<SubscriptionEntity> findSubscriptionsList();

    @Delete("DELETE FROM subscriptions " +
            "WHERE tenant=#{tenant} and namespace=#{namespace} and topic=#{topic} and subscription=#{subscription} and persistent=#{persistent}")
    void deleteByTenantNamespaceTopic(@Param("tenant") String tenant,
                                      @Param("namespace") String namespace,
                                      @Param("topic") String topic,
                                      @Param("subscription") String subscription,
                                      @Param("persistent") boolean persistent);
}