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

import com.github.pagehelper.Page;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.manager.pulsar.entity.TopicStatsEntity;
import com.manager.pulsar.entity.TopicsStatsRepository;
import com.manager.pulsar.service.BrokerStatsService;
import com.manager.pulsar.service.TopicsService;
import com.manager.pulsar.utils.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TopicsServiceImpl implements TopicsService {

    private static final Logger log = LoggerFactory.getLogger(TopicsServiceImpl.class);


    @Value("${backend.directRequestBroker}")
    private boolean directRequestBroker;

    @Autowired
    private TopicsStatsRepository topicsStatsRepository;

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

    public Map<String, Object> getTopicStats(
            Integer pageNum, Integer pageSize, String tenant, String namespace, String requestHost) {
        Map<String, Object> topicsMap = Maps.newHashMap();
        String broker = BrokerStatsServiceImpl.checkBroker(null, requestHost);
        List<Map<String, String>> persistentTopics = this.getTopicListByHttp(
                tenant, namespace, "persistent", broker);
        List<Map<String, Object>> persistentTopicsArray = this.getTopicsStatsList(
                broker, tenant, namespace, "persistent", persistentTopics);
        List<Map<String, String>> nonPersistentTopics = this.getTopicListByHttp(
                tenant, namespace, "non-persistent", broker);
        List<Map<String, Object>> nonPersistentTopicsArray = this.getTopicsStatsList(
                broker, tenant, namespace, "non-persistent", nonPersistentTopics);
        persistentTopicsArray.addAll(nonPersistentTopicsArray);
        topicsMap.put("topics", persistentTopicsArray);
        topicsMap.put("isPage", false);
        topicsMap.put("total", persistentTopicsArray.size());
        topicsMap.put("pageNum", 1);
        topicsMap.put("pageSize", persistentTopicsArray.size());
        return topicsMap;
    }

    public List<Map<String, Object>> getTopicsStatsList(String broker, String tenant, String namespace,
                                   String persistent, List<Map<String, String>> topics) {
        List<Map<String, Object>> topicsArray = new ArrayList<>();
        if (topics.size() <= 0) {
            return topicsArray;
        }
        try {
            URL url = new URL(broker);
            broker = url.getHost() + ":" + url.getPort();
        } catch (MalformedURLException e) {
            log.error("Parse Url failed: {}, message: {}", broker, e.getMessage());
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
        Map<String, TopicStatsEntity> tempTopicsMap = new HashMap<>();
        if (topicStatsEntity.isPresent()) {
            TopicStatsEntity topicStats = topicStatsEntity.get();
            Page<TopicStatsEntity> topicCountPage = topicsStatsRepository.findByMultiTopic(
                    1, 1, broker, tenant, namespace, persistent, topicList, topicStats.getTimestamp());
            topicCountPage.count(true);
            Page<TopicStatsEntity> topicStatsEntities = topicsStatsRepository.findByMultiTopic(
                    0, (int) topicCountPage.getTotal(), broker, tenant, namespace, persistent, topicList, topicStats.getTimestamp());
            topicStatsEntities.getResult().forEach((t) -> {
                tempTopicsMap.put(t.getTopic(), t);
            });
        }
        for (Map<String, String> topic: topics) {
            String topicName = topic.get("topic");
            Map<String, Object> topicEntity = Maps.newHashMap();
            int partitions = Integer.parseInt(topic.get("partitions"));
            int producerCount = 0;
            int subscriptionCount = 0;
            double msgRateIn = 0;
            double msgThroughputIn = 0;
            double msgRateOut = 0;
            double msgThroughputOut = 0;
            double storageSize = 0;
            if (partitions > 0) {
                for (int i = 0; i < partitions; i++) {
                    TopicStatsEntity partitionTopicStatsEntity = tempTopicsMap.get(
                            topicName + PARTITIONED_TOPIC_SUFFIX + i);
                    if (partitionTopicStatsEntity != null) {
                        producerCount += partitionTopicStatsEntity.getProducerCount();
                        subscriptionCount += partitionTopicStatsEntity.getSubscriptionCount();
                        msgRateIn += partitionTopicStatsEntity.getMsgRateIn();
                        msgRateOut += partitionTopicStatsEntity.getMsgRateOut();
                        msgThroughputIn += partitionTopicStatsEntity.getMsgThroughputIn();
                        msgThroughputOut += partitionTopicStatsEntity.getMsgThroughputOut();
                        storageSize += partitionTopicStatsEntity.getStorageSize();
                    }
                }
            } else {
                if (tempTopicsMap.containsKey(topicName)) {
                    TopicStatsEntity nonPartitionTopicStatsEntity = tempTopicsMap.get(topicName);
                    producerCount = nonPartitionTopicStatsEntity.getProducerCount();
                    subscriptionCount = nonPartitionTopicStatsEntity.getSubscriptionCount();
                    msgRateIn = nonPartitionTopicStatsEntity.getMsgRateIn();
                    msgRateOut = nonPartitionTopicStatsEntity.getMsgRateOut();
                    msgThroughputIn = nonPartitionTopicStatsEntity.getMsgThroughputIn();
                    msgThroughputOut = nonPartitionTopicStatsEntity.getMsgThroughputOut();
                    storageSize = nonPartitionTopicStatsEntity.getStorageSize();
                }
            }
            topicEntity.put("topic", topicName);
            topicEntity.put("partitions", partitions);
            topicEntity.put("persistent", "persistent");
            topicEntity.put("producers", producerCount);
            topicEntity.put("subscriptions", subscriptionCount);
            topicEntity.put("inMsg", msgRateIn);
            topicEntity.put("outMsg", msgRateOut);
            topicEntity.put("inBytes", msgThroughputIn);
            topicEntity.put("outBytes", msgThroughputOut);
            topicEntity.put("storageSize", storageSize);
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
        header.put("Content-Type", "application/json");
        String prefix = "/admin/v2/" + persistent + "/" + tenant + "/" + namespace;
        Gson gson = new Gson();
        String partitonedUrl = requestHost + prefix + "/partitioned";
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

        String topicUrl = requestHost + prefix;
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