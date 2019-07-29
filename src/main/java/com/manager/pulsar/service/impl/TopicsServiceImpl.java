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
import com.manager.pulsar.service.TopicsService;
import com.manager.pulsar.utils.HttpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class TopicsServiceImpl implements TopicsService {

    @Value("${backend.directRequestBroker}")
    private boolean directRequestBroker;

    @Value("${backend.directRequestHost}")
    private String directRequestHost;

    public static final String PARTITIONED_TOPIC_SUFFIX = "-partition-";

    private boolean isPartitonedTopic(List<String> topics, String topic) {
        if (topic.contains(PARTITIONED_TOPIC_SUFFIX)) {
            String[] t = topic.split(PARTITIONED_TOPIC_SUFFIX);
            if (topics != null && topics.contains(t[0])) {
                return true;
            }
        }
        return false;
    }

    public Map<String, Object> getTopicsList(Integer pageNum, Integer pageSize, String tenant, String namespace) {
        Map<String, Object> topicsMap = Maps.newHashMap();
        List<Map<String, String>> persistentTopic = this.getTopicListByHttp(tenant, namespace, "persistent");
        List<Map<String, String>> nonPersistentTopic = this.getTopicListByHttp(tenant, namespace, "non-persistent");
        persistentTopic.addAll(nonPersistentTopic);
        topicsMap.put("topics", persistentTopic);
        topicsMap.put("isPage", false);
        topicsMap.put("total", persistentTopic.size());
        topicsMap.put("pageNum", 1);
        topicsMap.put("pageSize", persistentTopic.size());
        return topicsMap;
    }

    private List<Map<String, String>> getTopicListByHttp(String tenant, String namespace, String persistent) {
        List<Map<String, String>> topicsArray = new ArrayList<>();
        Map<String, String> header = Maps.newHashMap();
        header.put("Content-Type", "application/json");
        String prefix = "/admin/v2/" + persistent + "/" + tenant + "/" + namespace;
        Gson gson = new Gson();
        String partitonedUrl = directRequestHost + prefix + "/partitioned";
        String partitonedTopic = HttpUtil.doGet(partitonedUrl, header);
        List<String> partitionedTopicsList = Arrays.asList();
        Map<String, List<String>> partitionedMap = Maps.newHashMap();
        if (partitonedTopic != null) {
            partitionedTopicsList = gson.fromJson(
                    partitonedTopic, new TypeToken<List<String>>(){}.getType());
            for (String p : partitionedTopicsList) {
                if (p.startsWith(persistent)) {
                    partitionedMap.put(this.getTopicName(p), new ArrayList<>());
                }
            }
        }

        String topicUrl = directRequestHost + prefix;
        String topics = HttpUtil.doGet(topicUrl, header);
        if (topics != null) {
            List<String> topicsList = gson.fromJson(
                    topics, new TypeToken<List<String>>(){}.getType());
            for (String topic: topicsList) {
                if (topic.startsWith(persistent)) {
                    String topicName = this.getTopicName(topic);
                    Map<String, String> topicEntity = Maps.newHashMap();
                    if (isPartitonedTopic(partitionedTopicsList, topic)) {
                        String[] name = topicName.split(PARTITIONED_TOPIC_SUFFIX);
                        partitionedMap.get(name[0]).add(topicName);
                    } else {
                        topicEntity.put("topic", topicName);
                        topicEntity.put("partitions", "0");
                        topicEntity.put("persistent", persistent);
                        topicsArray.add(topicEntity);
                    }
                }
            }
        }
        if (partitionedTopicsList != null) {
            for (String s : partitionedTopicsList) {
                String topicName = this.getTopicName(s);
                Map<String, String> topicEntity = Maps.newHashMap();
                List<String> partitionedTopicList = partitionedMap.get(s);
                if (partitionedTopicList != null && partitionedTopicList.size() > 0) {
                    topicEntity.put("topic", topicName);
                    topicEntity.put("partitions", String.valueOf(partitionedTopicList.size()));
                    topicEntity.put("persistent", persistent);
                } else {
                    topicEntity.put("topic", topicName);
                    String metadataTopicUrl = directRequestHost + prefix + "/" + topicName + "/partitions";
                    String metadataTopic = HttpUtil.doGet(metadataTopicUrl, header);
                    Map<String, Integer> metadata = gson.fromJson(
                            metadataTopic, new TypeToken<Map<String, Integer>>(){}.getType());
                    topicEntity.put("partitions", String.valueOf(metadata.get("partitions")));
                    topicEntity.put("persistent", persistent);
                }
                topicsArray.add(topicEntity);
            }
        }
        return topicsArray;
    }

    private String getTopicName(String topic) {
        String tntPath = topic.split("://")[1];
        String topicName = tntPath.split("/")[2];
        return topicName;
    }
}