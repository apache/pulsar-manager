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

import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.manager.controller.exception.PulsarAdminOperationException;
import org.apache.pulsar.manager.entity.TopicStatsEntity;
import org.apache.pulsar.manager.entity.TopicsStatsRepository;
import org.apache.pulsar.manager.service.BrokerStatsService;
import org.apache.pulsar.manager.service.PulsarAdminService;
import org.apache.pulsar.manager.service.TenantsService;
import org.apache.pulsar.common.policies.data.TenantInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TenantsServiceImpl implements TenantsService {

    private static final Logger log = LoggerFactory.getLogger(TenantsServiceImpl.class);

    @Value("${backend.directRequestBroker}")
    private boolean directRequestBroker;

    private final BrokerStatsService brokerStatsService;

    private final TopicsStatsRepository topicsStatsRepository;

    private final PulsarAdminService pulsarAdminService;

    private final HttpServletRequest request;

    @Autowired
    public TenantsServiceImpl(BrokerStatsService brokerStatsService, TopicsStatsRepository topicsStatsRepository, PulsarAdminService pulsarAdminService, HttpServletRequest request) {
        this.brokerStatsService = brokerStatsService;
        this.topicsStatsRepository = topicsStatsRepository;
        this.pulsarAdminService = pulsarAdminService;
        this.request = request;
    }

    public Map<String, Object> getTenantsList(Integer pageNum, Integer pageSize, String requestHost) {
        Map<String, Object> tenantsMap = Maps.newHashMap();
        List<Map<String, Object>> tenantsArray = new ArrayList<>();
        if (directRequestBroker) {
            List<String> tenantsList;
            try {
                tenantsList = pulsarAdminService.tenants(requestHost).getTenants();
            } catch (PulsarAdminException e) {
                PulsarAdminOperationException pulsarAdminOperationException
                        = new PulsarAdminOperationException("Failed to get tenants list.");
                log.error(pulsarAdminOperationException.getMessage(), e);
                throw pulsarAdminOperationException;
            }

            if (!tenantsList.isEmpty()) {
                Optional<TopicStatsEntity> topicStatsEntityOptional = topicsStatsRepository.findMaxTime();
                Map<String, TopicStatsEntity> topicStatsEntityMap = Maps.newHashMap();
                if (topicStatsEntityOptional.isPresent()) {
                    TopicStatsEntity topicStatsEntity = topicStatsEntityOptional.get();
                    String environment = request.getHeader("environment");
                    Page<TopicStatsEntity> tenantCountPage = brokerStatsService.findByMultiTenant(
                            1, 1, environment, tenantsList, topicStatsEntity.getTimestamp());
                    tenantCountPage.count(true);
                    Page<TopicStatsEntity> tenantAllCountPage = brokerStatsService.findByMultiTenant(1,
                            (int)tenantCountPage.getTotal(), environment, tenantsList, topicStatsEntity.getTimestamp());
                    for (TopicStatsEntity statsEntity : tenantAllCountPage) {
                        topicStatsEntityMap.put(statsEntity.getTenant(), statsEntity);
                    }
                }
                for (String tenant : tenantsList) {
                    TenantInfo tenantInfo;
                    try {
                        tenantInfo = pulsarAdminService.tenants(requestHost).getTenantInfo(tenant);
                    } catch (PulsarAdminException e) {
                        PulsarAdminOperationException pulsarAdminOperationException
                                = new PulsarAdminOperationException("Failed to get tenant info.");
                        log.error(pulsarAdminOperationException.getMessage(), e);
                        throw pulsarAdminOperationException;
                    }
                    Map<String, Object> tenantEntity = Maps.newHashMap();
                    tenantEntity.put("tenant", tenant);
                    tenantEntity.put("adminRoles", String.join(",", tenantInfo.getAdminRoles()));
                    tenantEntity.put("allowedClusters", String.join(",",  tenantInfo.getAllowedClusters()));
                    List<String> namespacesList;
                    try {
                        namespacesList = pulsarAdminService.namespaces(requestHost).getNamespaces(tenant);
                    } catch (PulsarAdminException e) {
                        PulsarAdminOperationException pulsarAdminOperationException
                                = new PulsarAdminOperationException("Failed to get namespaces list.");
                        log.error(pulsarAdminOperationException.getMessage(), e);
                        throw pulsarAdminOperationException;
                    }
                    tenantEntity.put("namespaces", namespacesList.size());
                    if (topicStatsEntityMap.get(tenant) != null ) {
                        TopicStatsEntity topicStatsEntity = topicStatsEntityMap.get(tenant);
                        tenantEntity.put("inMsg", topicStatsEntity.getMsgRateIn());
                        tenantEntity.put("outMsg", topicStatsEntity.getMsgRateOut());
                        tenantEntity.put("inBytes", topicStatsEntity.getMsgThroughputIn());
                        tenantEntity.put("outBytes", topicStatsEntity.getMsgThroughputOut());
                        tenantEntity.put("storageSize", topicStatsEntity.getStorageSize());
                    }
                    tenantsArray.add(tenantEntity);
                }
                tenantsMap.put("isPage", false);
                tenantsMap.put("total", tenantsList.size());
                tenantsMap.put("data", tenantsArray);
                tenantsMap.put("pageNum", 1);
                tenantsMap.put("pageSize", tenantsList.size());
            }
        } else {
           // to do query from local database
        }
        return tenantsMap;
    }

    public Map<String, String> createTenant(String tenant, String role, String cluster, String requestHost) {
        TenantInfo tenantInfo = new TenantInfo(Sets.newHashSet(role), Sets.newHashSet(cluster));
        Map<String, String> result = Maps.newHashMap();
        try {
            pulsarAdminService.tenants(requestHost).createTenant(tenant, tenantInfo);
            result.put("message", "Create tenant success");
        } catch (PulsarAdminException e) {
            PulsarAdminOperationException pulsarAdminOperationException
                    = new PulsarAdminOperationException("Failed to create tenant.");
            log.error(pulsarAdminOperationException.getMessage(), e);
            result.put("error", "Create tenant failed");
        }
        return result;
    }

}
