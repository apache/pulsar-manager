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
import com.google.common.collect.Sets;

import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.manager.controller.exception.PulsarAdminOperationException;
import org.apache.pulsar.manager.entity.EnvironmentEntity;
import org.apache.pulsar.manager.entity.EnvironmentsRepository;
import org.apache.pulsar.manager.service.EnvironmentCacheService;
import org.apache.pulsar.manager.service.PulsarAdminService;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.pulsar.common.policies.data.ClusterData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * A cache that caches environments.
 */
@Slf4j
@Service
public class EnvironmentCacheServiceImpl implements EnvironmentCacheService {

    private final EnvironmentsRepository environmentsRepository;

    private final Map<String, Map<String, ClusterData>> environments;

    private final PulsarAdminService pulsarAdminService;

    @Autowired
    public EnvironmentCacheServiceImpl(EnvironmentsRepository environmentsRepository, PulsarAdminService pulsarAdminService) {
        this.environmentsRepository = environmentsRepository;
        this.environments = new ConcurrentHashMap<>();
        this.pulsarAdminService = pulsarAdminService;
    }

    @Override
    public String getServiceUrl(HttpServletRequest request) {
        String cluster = request.getHeader("x-pulsar-cluster");
        return getServiceUrl(request, cluster);
    }

    @Override
    public String getServiceUrl(HttpServletRequest request, String cluster) {
        String environment = request.getHeader("environment");
        return getServiceUrl(environment, cluster);
    }

    public String getServiceUrl(String environment, String cluster) {
        if (StringUtils.isBlank(cluster)) {
            // if there is no cluster is specified, forward the request to environment service url
            Optional<EnvironmentEntity> environmentEntityOptional = environmentsRepository.findByName(environment);
            EnvironmentEntity environmentEntity = environmentEntityOptional.get();
            return environmentEntity.getBroker();
        } else {
            return getServiceUrl(environment, cluster, 0);
        }
    }

    private String getServiceUrl(String environment, String cluster, int numReloads) {
        // if there is a cluster specified, lookup the cluster.
        Map<String, ClusterData> clusters = environments.get(environment);
        ClusterData clusterData;
        if (null == clusters) {
            clusterData = reloadCluster(environment, cluster);
        } else {
            clusterData = clusters.get(cluster);
            if (clusterData == null) {
                clusterData = reloadCluster(environment, cluster);
            }
        }

        if (null == clusterData) {
            // no environment and no cluster
            throw new RuntimeException(
                "No cluster '" + cluster + "' found in environment '" + environment + "'");
        }
        return clusterData.getServiceUrl();
    }

    @Scheduled(
        initialDelay = 0L,
        fixedDelayString = "${cluster.cache.reload.interval.ms}")
    @Override
    public void reloadEnvironments() {
        int pageNum = 0;
        final int pageSize = 100;
        Set<String> newEnvironments = new HashSet<>();
        Page<EnvironmentEntity> environmentPage = environmentsRepository.getEnvironmentsList(pageNum, pageSize);
        List<EnvironmentEntity> environmentList = environmentPage.getResult();
        while (!environmentList.isEmpty()) {
            environmentList.forEach(env -> {
                try {
                    reloadEnvironment(env);
                } catch (PulsarAdminOperationException e) {
                    log.error(e.getMessage(), e);
                }
                newEnvironments.add(env.getName());
            });
            ++pageNum;
            environmentPage = environmentsRepository.getEnvironmentsList(pageNum, pageSize);
            environmentList = environmentPage.getResult();
        }
        log.info("Successfully reloaded environments : {}", newEnvironments);
        Set<String> oldEnvironments = environments.keySet();
        Set<String> goneEnvironments = Sets.difference(oldEnvironments, newEnvironments);
        for (String env : goneEnvironments) {
            environments.remove(env);
            log.info("Removed cached environment {} since it is already deleted.", env);
        }
    }

    private void reloadEnvironment(String environment) {
        // if there is no clusters, lookup the clusters
        EnvironmentEntity entity = environmentsRepository.findByName(environment).get();
        reloadEnvironment(entity);
    }

    public void reloadEnvironment(EnvironmentEntity environment) {
        List<String> clustersList;
        try {
            clustersList = pulsarAdminService.clusters(environment.getBroker()).getClusters();
        } catch(PulsarAdminException e) {
            PulsarAdminOperationException pulsarAdminOperationException
                    = new PulsarAdminOperationException("Failed to get clusters list.");
            log.error(pulsarAdminOperationException.getMessage(), e);
            throw pulsarAdminOperationException;
        }
        log.info("Reload cluster list for environment {} : {}", environment.getName(), clustersList);
        Set<String> newClusters = Sets.newHashSet(clustersList);
        Map<String, ClusterData> clusterDataMap = environments.computeIfAbsent(
            environment.getName(),
            (e) -> new ConcurrentHashMap<>());
        Set<String> oldClusters = clusterDataMap.keySet();
        Set<String> goneClusters = Sets.difference(oldClusters, newClusters);
        for (String cluster : goneClusters) {
            log.info("Remove cluster {} from environment {}.", cluster, environment.getName());
            clusterDataMap.remove(cluster);
        }
        for (String cluster : clustersList) {
            reloadCluster(environment, cluster);
        }
    }

    private ClusterData reloadCluster(String environment, String cluster) {
        // if there is no clusters, lookup the clusters
        return environmentsRepository.findByName(environment).map(env ->
            reloadCluster(env, cluster)
        ).orElse(null);
    }

    private ClusterData reloadCluster(EnvironmentEntity environment, String cluster) {
        log.info("Reloading cluster data for cluster {} @ environment {} ...",
            cluster, environment.getName());
        ClusterData clusterData;
        try {
            clusterData = pulsarAdminService.clusters(environment.getBroker()).getCluster(cluster);
        } catch(PulsarAdminException e) {
            log.error("Failed to get cluster data.", e);
            return null;
        }
        log.info("Loaded cluster data for cluster {} @ environment {} : {}",
            cluster, environment.getName(), clusterData.toString());
        Map<String, ClusterData> clusters = environments.computeIfAbsent(
            environment.getName(),
            (e) -> new ConcurrentHashMap<>());
        clusters.put(cluster, clusterData);
        log.info("Successfully loaded cluster data for cluster {} @ environment {} : {}",
            cluster, environment.getName(), clusterData);
        return clusterData;
    }

}
