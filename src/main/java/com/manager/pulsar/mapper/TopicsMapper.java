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
import com.manager.pulsar.entity.TopicEntity;
import org.apache.ibatis.annotations.*;

/**
 * Topic insert, delete, update, search
 */
@Mapper
public interface TopicsMapper {

    @Insert("INSERT INTO topics(topicId,tenant,namespaceId,namespace,topic,persistent,partitions) " +
            "VALUES(#{topicId},#{tenant},#{namespaceId},#{namespace},#{topic},#{persistent},#{partitions})")
    void insert(TopicEntity topicsEntity);

    @Select("SELECT topicId,tenant,namespaceId,namespace,topic,persistent,partitions FROM topics WHERE topicId=#{topicId}")
    TopicEntity findById(long topicId);

    @Select("SELECT topicId,tenant,namespaceId,namespace,topic,persistent,partitions FROM topics " +
            "WHERE tenant=#{tenant} and namespace=#{namespace} and topic=#{topic}")
    TopicEntity findByTenantNamespaceTopic(
            @Param("tenant") String tenant, @Param("namespace") String namespace, @Param("topic") String topic);

    @Select("SELECT topicId,tenant,namespaceId,namespace,topic,persistent,partitions FROM topics " +
            "WHERE tenant=#{tnt} or namespace=#{tnt} or topic=#{tnt}")
    Page<TopicEntity> findByTenantOrNamespaceOrTopic(String tnt);

    @Select("SELECT topicId,tenant,namespaceId,namespace,topic,persistent,partitions FROM topics WHERE topic=#{topic}")
    Page<TopicEntity> findByTopic(String topic);

    @Select("SELECT topicId,tenant,namespaceId,namespace,topic,persistent,partitions FROM topics")
    Page<TopicEntity> findTopicsList();

    @Update("UPDATE topics set partitions=#{partitions}" +
            "WHERE tenant=#{tenant} and namespace=#{namespace} and topic=#{topic} and persistent=#{persistent}")
    void update(TopicEntity topicsEntity);

    @Delete("DELETE FROM topics WHERE tenant=#{tenant} and namespace=#{namespace} and topic=#{topic} and persistent=#{persistent}")
    void deleteByTenantNamespaceTopic(@Param("tenant") String tenant,
                                      @Param("namespace") String namespace,
                                      @Param("topic") String topic,
                                      @Param("persistent") boolean persistent);

    @Delete("DELETE FROM topics WHERE topicId=#{topicId}")
    void deleteByTopicId(Integer topicId);
}