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
package com.manager.pulsar.dao;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.manager.pulsar.entity.TopicEntity;
import com.manager.pulsar.entity.TopicsRepository;
import com.manager.pulsar.mapper.TopicsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Topics implements TopicsRepository for support crud.
 */
@Repository
public class TopicsRepositoryImpl implements TopicsRepository {

    private final TopicsMapper topicsMapper;

    @Autowired
    public TopicsRepositoryImpl(TopicsMapper topicsMapper) { this.topicsMapper = topicsMapper; }

    @Override
    public Optional<TopicEntity> findById(Long topicId) {
        return Optional.ofNullable(topicsMapper.findById(topicId));
    }

    @Override
    public Optional<TopicEntity> findByFullyQualifiedTopicName(String tenant, String namespace, String topic) {
        return Optional.ofNullable(topicsMapper.findByTenantNamespaceTopic(tenant, namespace, topic));
    }

    @Override
    public Page<TopicEntity> findByShortTopicName(String topic) {
        return topicsMapper.findByTopic(topic);
    }

    @Override
    public Page<TopicEntity> findByTenantOrNamespaceOrTopic(Integer pageNum, Integer pageSize, String tns) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TopicEntity> topicsEntities = topicsMapper.findByTenantOrNamespaceOrTopic(tns);
        return topicsEntities;
    }

    @Override
    public Page<TopicEntity> getTopicsList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TopicEntity> topicsEntities = topicsMapper.findTopicsList();
        return topicsEntities;
    }

    @Override
    public void save(TopicEntity topicsEntity) {
        topicsMapper.insert(topicsEntity);
    }

    @Override
    public void remove(String tenant, String namespace, String topic, boolean persistent) {
        topicsMapper.deleteByTenantNamespaceTopic(tenant, namespace, topic, persistent);
    }

    @Override
    public void update(TopicEntity topicsEntity) {
        topicsMapper.update(topicsEntity);
    }
}