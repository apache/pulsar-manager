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
package io.streamnative.pulsar.manager.service.impl;

import com.github.pagehelper.Page;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.streamnative.pulsar.manager.entity.TopicStatsEntity;
import io.streamnative.pulsar.manager.entity.TopicsStatsRepository;
import io.streamnative.pulsar.manager.service.NamespacesService;
import io.streamnative.pulsar.manager.service.TopicsService;
import io.streamnative.pulsar.manager.utils.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class NamespacesServiceImpl implements NamespacesService {

    @Value("${backend.directRequestBroker}")
    private boolean directRequestBroker;

    @Autowired
    private TopicsStatsRepository topicsStatsRepository;

    @Autowired
    private TopicsService topicsService;

    public Map<String, Object> getNamespaceList(Integer pageNum, Integer pageSize, String tenant, String requestHost) {
        Map<String, Object> namespacesMap = Maps.newHashMap();
        List<Map<String, Object>> namespacesArray = new ArrayList<>();
        if (directRequestBroker) {
            Gson gson = new Gson();
            Map<String, String> header = Maps.newHashMap();
            header.put("Content-Type", "application/json");
            String result = HttpUtil.doGet(requestHost + "/admin/v2/namespaces/" + tenant, header);
            if (result != null) {
                List<String> namespacesList = gson.fromJson(result, new TypeToken<List<String>>(){}.getType());
                for (String tenantNamespace : namespacesList) {
                    String namespace = tenantNamespace.split("/")[1];
                    Map<String, Object> topicsEntity = Maps.newHashMap();
                    Map<String, Object> topics = topicsService.getTopicsList(
                            0, 0, tenant, namespace, requestHost);
                    topicsEntity.put("topics", topics.get("total"));
                    topicsEntity.put("namespace", namespace);
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
                    1, 1, env, tenant, namespace, topicStats.getTimestamp());
            topicCountPage.count(true);
            Page<TopicStatsEntity> topicStatsEntities = topicsStatsRepository.findByNamespace(
                    1, (int) topicCountPage.getTotal(), env, tenant, namespace, topicStats.getTimestamp());
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