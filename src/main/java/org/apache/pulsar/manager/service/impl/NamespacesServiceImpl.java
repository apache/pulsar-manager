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
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.apache.pulsar.manager.entity.TopicStatsEntity;
import org.apache.pulsar.manager.entity.TopicsStatsRepository;
import org.apache.pulsar.manager.service.BrokerStatsService;
import org.apache.pulsar.manager.service.NamespacesService;
import org.apache.pulsar.manager.service.TopicsService;
import org.apache.pulsar.manager.utils.HttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
public class NamespacesServiceImpl implements NamespacesService {

    @Value("${backend.directRequestBroker}")
    private boolean directRequestBroker;

    @Value("${backend.jwt.token}")
    private String pulsarJwtToken;

    private final TopicsStatsRepository topicsStatsRepository;
    private final TopicsService topicsService;
    private final HttpServletRequest request;
    private final BrokerStatsService brokerStatsService;

    @Autowired
    public NamespacesServiceImpl(
            TopicsStatsRepository topicsStatsRepository,
            TopicsService topicsService,
            HttpServletRequest request,
            BrokerStatsService brokerStatsService) {
        this.topicsStatsRepository = topicsStatsRepository;
        this.topicsService = topicsService;
        this.request = request;
        this.brokerStatsService = brokerStatsService;
    }

    public Map<String, Object> getNamespaceList(Integer pageNum, Integer pageSize, String tenant, String requestHost) {
        Map<String, Object> namespacesMap = Maps.newHashMap();
        List<Map<String, Object>> namespacesArray = new ArrayList<>();
        if (directRequestBroker) {
            Gson gson = new Gson();
            Map<String, String> header = Maps.newHashMap();
            if (StringUtils.isNotBlank(pulsarJwtToken)) {
                header.put("Authorization", String.format("Bearer %s", pulsarJwtToken));
            }
            String result = HttpUtil.doGet(requestHost + "/admin/v2/namespaces/" + tenant, header);
            if (result != null) {
                List<String> namespacesList = gson.fromJson(result, new TypeToken<List<String>>(){}.getType());
                Optional<TopicStatsEntity> topicStatsEntityOptional = topicsStatsRepository.findMaxTime();
                Map<String, TopicStatsEntity> topicStatsEntityMap = Maps.newHashMap();
                if (topicStatsEntityOptional.isPresent()) {
                    TopicStatsEntity topicStatsEntity = topicStatsEntityOptional.get();
                    String environment = request.getHeader("environment");
                    ArrayList<String> namespaceList = new ArrayList<>();
                    for (String namespace : namespacesList) {
                        String[] path = namespace.split("/");
                        if (path.length > 1) {
                            namespaceList.add(path[1]);
                        }
                    }
                    Page<TopicStatsEntity> namespaceCountPage = brokerStatsService.findByMultiNamespace(
                            1, 1, environment, tenant,
                            namespaceList, topicStatsEntity.getTimestamp());
                    namespaceCountPage.count(true);
                    Page<TopicStatsEntity> namespaceAllCountPage = brokerStatsService.findByMultiNamespace(
                            1, (int)namespaceCountPage.getTotal(), environment, tenant,
                            namespaceList, topicStatsEntity.getTimestamp());
                    for (TopicStatsEntity statsEntity : namespaceAllCountPage) {
                        topicStatsEntityMap.put(statsEntity.getNamespace(), statsEntity);
                    }

                }
                for (String tenantNamespace : namespacesList) {
                    String namespace = tenantNamespace.split("/")[1];
                    Map<String, Object> topicsEntity = Maps.newHashMap();
                    Map<String, Object> topics = topicsService.getTopicsList(
                            0, 0, tenant, namespace, requestHost);
                    topicsEntity.put("topics", topics.get("total"));
                    topicsEntity.put("namespace", namespace);
                    if (topicStatsEntityMap.get(namespace) != null) {
                        TopicStatsEntity topicStatsEntity = topicStatsEntityMap.get(namespace);
                        topicsEntity.put("inMsg", topicStatsEntity.getMsgRateIn());
                        topicsEntity.put("outMsg", topicStatsEntity.getMsgRateOut());
                        topicsEntity.put("inBytes", topicStatsEntity.getMsgThroughputIn());
                        topicsEntity.put("outBytes", topicStatsEntity.getMsgThroughputOut());
                        topicsEntity.put("storageSize", topicStatsEntity.getStorageSize());
                    }
                    namespacesArray.add(topicsEntity);
                }
                namespacesMap.put("isPage", false);
                namespacesMap.put("total", namespacesList.size());
                namespacesMap.put("data", namespacesArray);
                namespacesMap.put("pageNum", 1);
                namespacesMap.put("pageSize", namespacesArray.size());
            }
        }
        return namespacesMap;
    }

    public Map<String, Object> getNamespaceStats(
            String tenant,
            String namespace,
            String env) {
        Map<String, Object> namespaceStatsMap = Maps.newHashMap();
        Optional<TopicStatsEntity> topicStatsEntity = topicsStatsRepository.findMaxTime();
        if (topicStatsEntity.isPresent()) {
            double msgRateIn = 0;
            double msgThroughputIn = 0;
            double msgRateOut = 0;
            double msgThroughputOut = 0;
            TopicStatsEntity topicStats = topicStatsEntity.get();
            Page<TopicStatsEntity> topicCountPage = topicsStatsRepository.findByNamespace(
                    1, 1, env, tenant, namespace, topicStats.getTime_stamp());
            topicCountPage.count(true);
            Page<TopicStatsEntity> topicStatsEntities = topicsStatsRepository.findByNamespace(
                    1, (int) topicCountPage.getTotal(), env, tenant, namespace, topicStats.getTime_stamp());
            for (TopicStatsEntity statsEntity : topicStatsEntities.getResult()) {
                msgRateIn += statsEntity.getMsgRateIn();
                msgRateOut += statsEntity.getMsgRateOut();
                msgThroughputIn += statsEntity.getMsgThroughputIn();
                msgThroughputOut += statsEntity.getMsgThroughputOut();
            }
            namespaceStatsMap.put("inMsg", msgRateIn);
            namespaceStatsMap.put("outMsg", msgRateOut);
            namespaceStatsMap.put("msgThroughputIn", msgThroughputIn);
            namespaceStatsMap.put("msgThroughputOut", msgThroughputOut);
        }
        return namespaceStatsMap;
    }

}
