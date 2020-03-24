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
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.apache.pulsar.manager.entity.TopicStatsEntity;
import org.apache.pulsar.manager.entity.TopicsStatsRepository;
import org.apache.pulsar.manager.service.BrokerStatsService;
import org.apache.pulsar.manager.service.TenantsService;
import org.apache.pulsar.manager.utils.HttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.pulsar.common.policies.data.TenantInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TenantsServiceImpl implements TenantsService {

    private static final Logger log = LoggerFactory.getLogger(TenantsServiceImpl.class);


    @Value("${backend.directRequestBroker}")
    private boolean directRequestBroker;

    @Value("${backend.jwt.token}")
    private String pulsarJwtToken;

    @Autowired
    private BrokerStatsService brokerStatsService;

    @Autowired
    private TopicsStatsRepository topicsStatsRepository;

    @Autowired
    private HttpServletRequest request;

    public Map<String, Object> getTenantsList(Integer pageNum, Integer pageSize, String requestHost) {
        Map<String, Object> tenantsMap = Maps.newHashMap();
        List<Map<String, Object>> tenantsArray = new ArrayList<>();
        if (directRequestBroker) {
            Gson gson = new Gson();
            Map<String, String> header = Maps.newHashMap();
            if (StringUtils.isNotBlank(pulsarJwtToken)) {
                header.put("Authorization", String.format("Bearer %s", pulsarJwtToken));
            }
            String result = HttpUtil.doGet( requestHost + "/admin/v2/tenants", header);
            if (result != null) {
                List<String> tenantsList = gson.fromJson(result, new TypeToken<List<String>>(){}.getType());
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
                    Map<String, Object> tenantEntity = Maps.newHashMap();
                    String info = HttpUtil.doGet( requestHost + "/admin/v2/tenants/" + tenant, header);
                    TenantInfo tenantInfo = gson.fromJson(info, TenantInfo.class);
                    tenantEntity.put("tenant", tenant);
                    tenantEntity.put("adminRoles", String.join(",", tenantInfo.getAdminRoles()));
                    tenantEntity.put("allowedClusters", String.join(",",  tenantInfo.getAllowedClusters()));
                    String namespace = HttpUtil.doGet(requestHost + "/admin/v2/namespaces/" + tenant, header);
                    if (namespace != null) {
                        List<String> namespacesList = gson.fromJson(namespace, new TypeToken<List<String>>(){}.getType());
                        tenantEntity.put("namespaces", namespacesList.size());
                    } else {
                        tenantEntity.put("namespaces", 0);
                    }
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
        Map<String, String> header = Maps.newHashMap();
        header.put("Content-Type", "application/json");
        if (StringUtils.isNotBlank(pulsarJwtToken)) {
            header.put("Authorization", String.format("Bearer %s", pulsarJwtToken));
        }
        Map<String, Object> body = Maps.newHashMap();
        body.put("adminRoles", Sets.newHashSet(role));
        // Get cluster from standalone, to do
        body.put("allowedClusters", Sets.newHashSet(cluster));
        Gson gson = new Gson();
        Map<String, String> result = Maps.newHashMap();
        try {
            HttpUtil.doPut( requestHost + "/admin/v2/tenants/" + tenant, header, gson.toJson(body));
            result.put("message", "Create tenant success");
        } catch (UnsupportedEncodingException e) {
            log.error("Init tenant failed for user: {}", tenant);
            result.put("error", "Create tenant failed");
        }
        return result;
    }

}
