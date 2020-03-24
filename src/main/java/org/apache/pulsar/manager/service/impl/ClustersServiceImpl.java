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
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.apache.pulsar.manager.service.BrokersService;
import org.apache.pulsar.manager.service.ClustersService;
import org.apache.pulsar.manager.utils.HttpUtil;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.apache.pulsar.common.policies.data.ClusterData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ClustersServiceImpl implements ClustersService {

    @Value("${backend.directRequestBroker}")
    private boolean directRequestBroker;

    @Value("${backend.jwt.token}")
    private String pulsarJwtToken;

    private final BrokersService brokersService;

    @Autowired
    public ClustersServiceImpl(BrokersService brokersService) {
        this.brokersService = brokersService;
    }


    public Map<String, Object> getClustersList(Integer pageNum,
                                               Integer pageSize,
                                               String envServiceUrl,
                                               Function<String, String> serviceUrlProvider) {
        Map<String, Object> clustersMap = Maps.newHashMap();
        List<Map<String, Object>> clustersArray = new ArrayList<>();
        if (directRequestBroker) {
            Gson gson = new Gson();
            Map<String, String> header = Maps.newHashMap();
            if (StringUtils.isNotBlank(pulsarJwtToken)) {
                header.put("Authorization", String.format("Bearer %s", pulsarJwtToken));
            }
            String result = HttpUtil.doGet(envServiceUrl + "/admin/v2/clusters", header);
            List<String> clustersList = gson.fromJson(result, new TypeToken<List<String>>(){}.getType());
            for (String cluster: clustersList) {
                String clusterServiceUrl =
                    serviceUrlProvider == null ? envServiceUrl : serviceUrlProvider.apply(cluster);
                Map<String, Object> clusterEntity = Maps.newHashMap();
                Map<String, Object> brokers =
                    brokersService.getBrokersList(1, 1, cluster, clusterServiceUrl);
                clusterEntity.put("brokers", brokers.get("total"));
                clusterEntity.put("cluster", cluster);
                String clusterInfo = HttpUtil.doGet(clusterServiceUrl + "/admin/v2/clusters/" + cluster, header);
                ClusterData clusterData = gson.fromJson(clusterInfo, ClusterData.class);
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
        Gson gson = new Gson();
        Map<String, String> header = Maps.newHashMap();
        if (StringUtils.isNotBlank(pulsarJwtToken)) {
            header.put("Authorization", String.format("Bearer %s", pulsarJwtToken));
        }
        String result = HttpUtil.doGet(requestHost + "/admin/v2/clusters", header);
        List<String> clustersList = gson.fromJson(result, new TypeToken<List<String>>(){}.getType());
        return clustersList;
    }
}
