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
import org.apache.pulsar.manager.entity.TopicStatsEntity.TopicStatsSummary;
import org.apache.pulsar.manager.entity.TopicsStatsRepository;
import org.apache.pulsar.manager.service.TopicsService;
import org.apache.pulsar.manager.utils.HttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TopicsServiceImpl implements TopicsService {

    public static final String PARTITIONED_TOPIC_SUFFIX = "-partition-";

    @Value("${backend.directRequestBroker}")
    private boolean directRequestBroker;

    @Value("${backend.jwt.token}")
    private String pulsarJwtToken;

    private final TopicsStatsRepository topicsStatsRepository;

    @Autowired
    public TopicsServiceImpl(TopicsStatsRepository topicsStatsRepository) {
        this.topicsStatsRepository = topicsStatsRepository;
    }

    private boolean isPartitionedTopic(List<String> topics, String topic) {
        if (topic.contains(PARTITIONED_TOPIC_SUFFIX)) {
            String[] t = topic.split(PARTITIONED_TOPIC_SUFFIX);
            if (topics != null && topics.contains(t[0])) {
                return true;
            }
        }
        return false;
    }

    public Map<String, Object> getTopicStats(
            Integer pageNum,
            Integer pageSize,
            String tenant,
            String namespace,
            String env,
            String serviceUrl) {
        Map<String, Object> topicsMap = Maps.newHashMap();
        List<Map<String, String>> persistentTopics = this.getTopicListByHttp(
                tenant, namespace, "persistent", serviceUrl);
        List<Map<String, Object>> persistentTopicsArray = this.getTopicsStatsList(
                env, tenant, namespace, "persistent", persistentTopics);
        List<Map<String, String>> nonPersistentTopics = this.getTopicListByHttp(
                tenant, namespace, "non-persistent", serviceUrl);
        List<Map<String, Object>> nonPersistentTopicsArray = this.getTopicsStatsList(
                env, tenant, namespace, "non-persistent", nonPersistentTopics);
        persistentTopicsArray.addAll(nonPersistentTopicsArray);
        topicsMap.put("topics", persistentTopicsArray);
        topicsMap.put("isPage", false);
        topicsMap.put("total", persistentTopicsArray.size());
        topicsMap.put("pageNum", 1);
        topicsMap.put("pageSize", persistentTopicsArray.size());
        return topicsMap;
    }

    public List<Map<String, Object>> getTopicsStatsList(String env,
                                                        String tenant,
                                                        String namespace,
                                                        String persistent,
                                                        List<Map<String, String>> topics) {
        List<Map<String, Object>> topicsArray = new ArrayList<>();
        if (topics.size() <= 0) {
            return topicsArray;
        }
        List<String> topicList = new ArrayList<>();
        for (Map<String, String> topic: topics) {
            String topicName = topic.get("topic");
            int partitions = Integer.parseInt(topic.get("partitions"));
            if (partitions > 0) {
                for (int i = 0; i < partitions; i++) {
                    topicList.add(topicName + PARTITIONED_TOPIC_SUFFIX + i);
                }
            } else {
                topicList.add(topicName);
            }
        }
        Optional<TopicStatsEntity> topicStatsEntity = topicsStatsRepository.findMaxTime();
        Map<String, Map<String, TopicStatsEntity>> tempTopicsMap = new HashMap<>();
        if (topicStatsEntity.isPresent()) {
            TopicStatsEntity topicStats = topicStatsEntity.get();
            Page<TopicStatsEntity> topicCountPage = topicsStatsRepository.findByMultiTopic(
                    1, 1, env, tenant, namespace, persistent, topicList, topicStats.getTime_stamp());
            topicCountPage.count(true);
            Page<TopicStatsEntity> topicStatsEntities = topicsStatsRepository.findByMultiTopic(
                    0, (int) topicCountPage.getTotal(), env, tenant, namespace, persistent, topicList, topicStats.getTime_stamp());
            topicStatsEntities.getResult().forEach((t) -> {
                tempTopicsMap.computeIfAbsent(t.getTopic(), (ignored) -> new HashMap<>())
                    .put(t.getCluster(), t);
            });
        }
        for (Map<String, String> topic: topics) {
            String topicName = topic.get("topic");
            Map<String, Object> topicEntity = Maps.newHashMap();
            int partitions = Integer.parseInt(topic.get("partitions"));
            TopicStatsSummary totalStats = new TopicStatsSummary();
            Map<String, TopicStatsSummary> clusterSummaries = new HashMap<>();
            if (partitions > 0) {
                for (int i = 0; i < partitions; i++) {
                    Map<String, TopicStatsEntity> partitionTopicStatsEntity = tempTopicsMap.get(
                            topicName + PARTITIONED_TOPIC_SUFFIX + i);
                    if (partitionTopicStatsEntity != null) {
                        for (Map.Entry<String, TopicStatsEntity> cluster : partitionTopicStatsEntity.entrySet()) {
                            TopicStatsSummary clusterSummary = cluster.getValue().getSummary();
                            totalStats = totalStats.add(clusterSummary);

                            TopicStatsSummary clusterTotalStats =
                                clusterSummaries.computeIfAbsent(cluster.getKey(), (ignored) -> new TopicStatsSummary());
                            clusterTotalStats = clusterTotalStats.add(clusterSummary);
                            clusterSummaries.put(cluster.getKey(), clusterTotalStats);
                        }
                    }
                }
            } else {
                if (tempTopicsMap.containsKey(topicName)) {
                    Map<String, TopicStatsEntity> clusters = tempTopicsMap.get(topicName);
                    for (Map.Entry<String, TopicStatsEntity> cluster : clusters.entrySet()) {
                        TopicStatsEntity clusterEntity = cluster.getValue();
                        TopicStatsSummary clusterSummary = clusterEntity.getSummary();

                        totalStats = totalStats.add(clusterSummary);
                        clusterSummaries.put(cluster.getKey(), clusterSummary);
                    }
                }
            }

            for (Map.Entry<String, TopicStatsSummary> summary : clusterSummaries.entrySet()) {
                summary.getValue().setTopic(summary.getKey());
                summary.getValue().setPartitions(partitions);
                summary.getValue().setPersistent(persistent);
            }

            topicEntity.put("topic", topicName);
            topicEntity.put("partitions", partitions);
            topicEntity.put("persistent", persistent);
            topicEntity.put("producers", totalStats.getProducerCount());
            topicEntity.put("subscriptions", totalStats.getSubscriptionCount());
            topicEntity.put("inMsg", totalStats.getMsgRateIn());
            topicEntity.put("outMsg", totalStats.getMsgRateOut());
            topicEntity.put("inBytes", totalStats.getMsgThroughputIn());
            topicEntity.put("outBytes", totalStats.getMsgThroughputOut());
            topicEntity.put("storageSize", totalStats.getStorageSize());
            topicEntity.put("clusters", clusterSummaries.values());
            topicsArray.add(topicEntity);
        }
        return topicsArray;
    }

    public Map<String, Object> getTopicsList(
            Integer pageNum, Integer pageSize, String tenant, String namespace, String requestHost) {
        Map<String, Object> topicsMap = Maps.newHashMap();
        List<Map<String, String>> persistentTopic = this.getTopicListByHttp(
                tenant, namespace, "persistent", requestHost);
        List<Map<String, String>> nonPersistentTopic = this.getTopicListByHttp(
                tenant, namespace, "non-persistent", requestHost);
        persistentTopic.addAll(nonPersistentTopic);
        topicsMap.put("topics", persistentTopic);
        topicsMap.put("isPage", false);
        topicsMap.put("total", persistentTopic.size());
        topicsMap.put("pageNum", 1);
        topicsMap.put("pageSize", persistentTopic.size());
        return topicsMap;
    }

    private List<Map<String, String>> getTopicListByHttp(
            String tenant, String namespace, String persistent, String requestHost) {
        List<Map<String, String>> topicsArray = new ArrayList<>();
        Map<String, String> header = Maps.newHashMap();
        if (StringUtils.isNotBlank(pulsarJwtToken)) {
            header.put("Authorization", String.format("Bearer %s", pulsarJwtToken));
        }
        String prefix = "/admin/v2/" + persistent + "/" + tenant + "/" + namespace;
        Gson gson = new Gson();
        String partitionedUrl = requestHost + prefix + "/partitioned";
        String partitionedTopic = HttpUtil.doGet(partitionedUrl, header);
        List<String> partitionedTopicsList = Arrays.asList();
        Map<String, List<String>> partitionedMap = Maps.newHashMap();
        if (partitionedTopic != null) {
            partitionedTopicsList = gson.fromJson(
                    partitionedTopic, new TypeToken<List<String>>(){}.getType());
            for (String p : partitionedTopicsList) {
                if (p.startsWith(persistent)) {
                    partitionedMap.put(this.getTopicName(p), new ArrayList<>());
                }
            }
        }

        String topicUrl = requestHost + prefix;
        String topics = HttpUtil.doGet(topicUrl, header);
        if (topics != null) {
            List<String> topicsList = gson.fromJson(
                    topics, new TypeToken<List<String>>(){}.getType());
            for (String topic: topicsList) {
                if (topic.startsWith(persistent)) {
                    String topicName = this.getTopicName(topic);
                    Map<String, String> topicEntity = Maps.newHashMap();
                    if (isPartitionedTopic(partitionedTopicsList, topic)) {
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
                    String metadataTopicUrl = requestHost + prefix + "/" + topicName + "/partitions";
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
