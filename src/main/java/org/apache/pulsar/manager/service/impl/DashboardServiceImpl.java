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
package org.apache.pulsar.manager.service.impl;

import com.github.pagehelper.Page;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.pulsar.manager.entity.*;
import org.apache.pulsar.manager.service.BookiesService;
import org.apache.pulsar.manager.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final TopicsStatsRepository topicsStatsRepository;
    private final ConsumersStatsRepository consumersStatsRepository;
    private final TenantsRepository tenantsRepository;
    private final NamespacesRepository namespacesRepository;
    private final BookiesService bookiesService;

    @Autowired
    public DashboardServiceImpl(
            TopicsStatsRepository topicsStatsRepository,
            ConsumersStatsRepository consumersStatsRepository,
            TenantsRepository tenantsRepository,
            NamespacesRepository namespacesRepository,
            BookiesService bookiesService) {
        this.topicsStatsRepository = topicsStatsRepository;
        this.consumersStatsRepository = consumersStatsRepository;
        this.tenantsRepository = tenantsRepository;
        this.namespacesRepository = namespacesRepository;
        this.bookiesService = bookiesService;
    }

    public Map<String, Object> getDashboardStats(List<String> environmentList) {
        int totalClusterCount = 0;
        int totalBrokerCount = 0;
        int totalTenantCount;
        int totalNamespaceCount;
        long totalTopicCount = 0;
        long totalProducerCount = 0L;
        long totalConsumerCount = 0L;
        int totalBookieCount = 0;
        Map<String, Object> dashboardStatsMap = Maps.newHashMap();

        List<TenantEntity> tenantEntities= tenantsRepository.findByMultiEnvironmentName(environmentList);
        totalTenantCount = tenantEntities.size();
        List<String> tenantList = new LinkedList<>();
        for (TenantEntity tenantEntity: tenantEntities) {
            tenantList.add(tenantEntity.getTenant());
        }
        List<NamespaceEntity> namespaceEntities = namespacesRepository.findByMultiTenant(tenantList);
        totalNamespaceCount = namespaceEntities.size();

        Optional<TopicStatsEntity> topicStatsEntity = topicsStatsRepository.findMaxTime();
        if (topicStatsEntity.isPresent()) {
            Set<String> clusterSet = Sets.newHashSet();
            Set<String> brokerSet = Sets.newHashSet();
            TopicStatsEntity topicStats = topicStatsEntity.get();
            long timestamp = topicStats.getTime_stamp();
            List<TopicStatsEntity> topicStatsEntities = topicsStatsRepository.findByMultiEnvironment(
                    environmentList, timestamp);
            totalTopicCount = topicStatsEntities.size();
            List<Long> topicStatsIdList = new LinkedList<>();
            for (TopicStatsEntity statsEntity : topicStatsEntities) {
                clusterSet.add(statsEntity.getCluster());
                brokerSet.add(statsEntity.getBroker());
                topicStatsIdList.add(statsEntity.getTopicStatsId());
                totalProducerCount += statsEntity.getProducerCount();
            }
            totalClusterCount = clusterSet.size();
            totalBrokerCount = brokerSet.size();
            List<ConsumerStatsEntity> consumerStatsEntities = consumersStatsRepository.findByMultiTopicStatsId(
                    topicStatsIdList, timestamp);
            totalConsumerCount = consumerStatsEntities.size();
            totalBookieCount = (int) bookiesService.getBookiesList(
                    1, 10, "").getOrDefault(
                            "total", 0);
        }
        dashboardStatsMap.put("totalClusterCount", totalClusterCount);
        dashboardStatsMap.put("totalBrokerCount", totalBrokerCount);
        dashboardStatsMap.put("totalTenantCount", totalTenantCount);
        dashboardStatsMap.put("totalNamespaceCount", totalNamespaceCount);
        dashboardStatsMap.put("totalTopicCount", totalTopicCount);
        dashboardStatsMap.put("totalProducerCount", totalProducerCount);
        dashboardStatsMap.put("totalConsumerCount", totalConsumerCount);
        dashboardStatsMap.put("totalBookieCount", totalBookieCount);
        return dashboardStatsMap;
    }
}
