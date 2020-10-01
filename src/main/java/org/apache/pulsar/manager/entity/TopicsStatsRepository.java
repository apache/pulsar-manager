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
package org.apache.pulsar.manager.entity;

import com.github.pagehelper.Page;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TopicsStatsRepository {

    long save(TopicStatsEntity topicStatsEntity);

    Optional<TopicStatsEntity> findMaxTime();

    Page<TopicStatsEntity> findByClusterBroker(Integer pageNum,
                                               Integer pageSize,
                                               String environment,
                                               String cluster,
                                               String broker, long timestamp);

    Page<TopicStatsEntity> findByNamespace(Integer pageNum,
                                           Integer pageSize,
                                           String environment,
                                           String tenant,
                                           String namespace,
                                           long timestamp);

    Page<TopicStatsEntity> findByMultiTopic(Integer pageNum,
                                            Integer pageSize,
                                            String environment,
                                            String tenant,
                                            String namespace,
                                            String persistent,
                                            List<String> topicList,
                                            long timestamp);

    Page<TopicStatsEntity> findByMultiTenant(Integer pageNum,
                                            Integer pageSize,
                                            String environment,
                                            List<String> tenantList,
                                            long timestamp);

    Page<TopicStatsEntity> findByMultiNamespace(Integer pageNum,
                                             Integer pageSize,
                                             String environment,
                                             String tenant,
                                             List<String> namespaceList,
                                             long timestamp);

    List<TopicStatsEntity> findByMultiEnvironment(List<String> environmentList,
                                                     long timestamp);

    void remove(long timestamp, long timeInterval);
}
