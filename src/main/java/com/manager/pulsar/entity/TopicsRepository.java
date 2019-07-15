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
package com.manager.pulsar.entity;

import com.github.pagehelper.Page;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 *
 */
@Repository
public interface TopicsRepository {
    void save(TopicEntity topicsEntity);

    Optional<TopicEntity> findById(Long topicId);

    Optional<TopicEntity> findByFullyQualifiedTopicName(String tenant, String namespace, String topic);

    Page<TopicEntity> findByTenantOrNamespaceOrTopic(Integer pageNum, Integer pageSize, String tns);

    Page<TopicEntity> findByShortTopicName(String topic);

    Page<TopicEntity> getTopicsList(Integer pageNum, Integer pageSize);

    void remove(String tenant, String namespace, String topic, boolean persistent);

    void update(TopicEntity topicsEntity);
}