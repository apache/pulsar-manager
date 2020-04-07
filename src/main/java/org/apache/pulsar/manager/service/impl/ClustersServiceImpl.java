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

import com.google.common.collect.Maps;

import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.manager.controller.exception.PulsarAdminOperationException;
import org.apache.pulsar.manager.service.BrokersService;
import org.apache.pulsar.manager.service.ClustersService;
import org.apache.pulsar.manager.service.PulsarAdminService;
import java.util.function.Function;

import org.apache.pulsar.common.policies.data.ClusterData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ClustersServiceImpl implements ClustersService {

    private static final Logger log = LoggerFactory.getLogger(ClustersServiceImpl.class);

    @Value("${backend.directRequestBroker}")
    private boolean directRequestBroker;

    private final BrokersService brokersService;

    private final PulsarAdminService pulsarAdminService;

    @Autowired
    public ClustersServiceImpl(BrokersService brokersService, PulsarAdminService pulsarAdminService) {
        this.brokersService = brokersService;
        this.pulsarAdminService = pulsarAdminService;
    }

    public Map<String, Object> getClustersList(Integer pageNum,
                                               Integer pageSize,
                                               String envServiceUrl,
                                               Function<String, String> serviceUrlProvider) {
        Map<String, Object> clustersMap = Maps.newHashMap();
        List<Map<String, Object>> clustersArray = new ArrayList<>();
        if (directRequestBroker) {
            List<String> clustersList;
            try {
                clustersList = pulsarAdminService.clusters(envServiceUrl).getClusters();
            } catch (PulsarAdminException e) {
                PulsarAdminOperationException pulsarAdminOperationException
                        = new PulsarAdminOperationException("Failed to get clusters list.");
                log.error(pulsarAdminOperationException.getMessage(), e);
                throw pulsarAdminOperationException;
            }
            for (String cluster: clustersList) {
                String clusterServiceUrl =
                    serviceUrlProvider == null ? envServiceUrl : serviceUrlProvider.apply(cluster);
                Map<String, Object> clusterEntity = Maps.newHashMap();
                Map<String, Object> brokers =
                    brokersService.getBrokersList(1, 1, cluster, clusterServiceUrl);
                clusterEntity.put("brokers", brokers.get("total"));
                clusterEntity.put("cluster", cluster);
                ClusterData clusterData;
                try {
                    clusterData = pulsarAdminService.clusters(clusterServiceUrl).getCluster(cluster);
                } catch (PulsarAdminException e) {
                    PulsarAdminOperationException pulsarAdminOperationException
                            = new PulsarAdminOperationException("Failed to get cluster data.");
                    log.error(pulsarAdminOperationException.getMessage(), e);
                    throw pulsarAdminOperationException;
                }
                clusterEntity.put("serviceUrl", clusterData.getServiceUrl());
                clusterEntity.put("serviceUrlTls", clusterData.getServiceUrlTls());
                clusterEntity.put("brokerServiceUrl", clusterData.getBrokerServiceUrl());
                clusterEntity.put("brokerServiceUrlTls", clusterData.getBrokerServiceUrlTls());
                clustersArray.add(clusterEntity);
            }
            clustersMap.put("isPage", false);
            clustersMap.put("total", clustersArray.size());
            clustersMap.put("data", clustersArray);
            clustersMap.put("pageNum", 1);
            clustersMap.put("pageSize", clustersArray.size());
        }
        return clustersMap;
    }

    public List<String> getClusterByAnyBroker(String requestHost) {
        List<String> clustersList;
        try {
            clustersList = pulsarAdminService.clusters(requestHost).getClusters();
        } catch (PulsarAdminException e) {
            PulsarAdminOperationException pulsarAdminOperationException
                    = new PulsarAdminOperationException("Failed to get clusters list.");
            log.error(pulsarAdminOperationException.getMessage(), e);
            throw pulsarAdminOperationException;
        }

        return clustersList;
    }
}
