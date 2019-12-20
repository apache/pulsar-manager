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
package org.apache.pulsar.manager.service;

import com.github.pagehelper.Page;
import org.apache.pulsar.manager.entity.TopicStatsEntity;

import java.util.List;

public interface BrokerStatsService {

    String forwardBrokerStatsMetrics(String broker, String requestHost);

    String forwardBrokerStatsTopics(String broker, String requestHost);

    void collectStatsToDB(long unixTime, String environment, String cluster, String serviceUrl);

    void clearStats(long nowTime, long timeInterval);

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

}
