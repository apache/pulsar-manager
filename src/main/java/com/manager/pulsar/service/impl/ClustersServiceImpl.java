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
package com.manager.pulsar.service.impl;

import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.manager.pulsar.entity.EnvironmentsRepository;
import com.manager.pulsar.service.BrokersService;
import com.manager.pulsar.service.ClustersService;
import com.manager.pulsar.utils.HttpUtil;
import org.apache.pulsar.common.policies.data.ClusterData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ClustersServiceImpl implements ClustersService {

    @Value("${backend.directRequestBroker}")
    private boolean directRequestBroker;

    @Autowired
    private BrokersService brokersService;


    public Map<String, Object> getClustersList(Integer pageNum, Integer pageSize, String requestHost) {
        Map<String, Object> clustersMap = Maps.newHashMap();
        List<Map<String, Object>> clustersArray = new ArrayList<>();
        if (directRequestBroker) {
            Gson gson = new Gson();
            Map<String, String> header = Maps.newHashMap();
            header.put("Content-Type", "application/json");
            String result = HttpUtil.doGet(requestHost + "/admin/v2/clusters", header);
            List<String> clustersList = gson.fromJson(result, new TypeToken<List<String>>(){}.getType());
            for (String cluster: clustersList) {
                Map<String, Object> clusterEntity = Maps.newHashMap();
                Map<String, Object> brokers = brokersService.getBrokersList(1, 1, cluster, requestHost);
                clusterEntity.put("brokers", brokers.get("total"));
                clusterEntity.put("cluster", cluster);
                String clusterInfo = HttpUtil.doGet(requestHost + "/admin/v2/clusters/" + cluster, header);
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
}
