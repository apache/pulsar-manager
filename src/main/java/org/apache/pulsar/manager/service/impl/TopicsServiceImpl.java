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

import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.admin.PulsarAdminBuilder;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.client.api.AuthenticationFactory;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.impl.BatchMessageIdImpl;
import org.apache.pulsar.client.impl.MessageIdImpl;
import org.apache.pulsar.common.naming.TopicDomain;
import org.apache.pulsar.common.naming.TopicName;
import org.apache.pulsar.common.partition.PartitionedTopicMetadata;
import org.apache.pulsar.manager.controller.exception.PulsarAdminOperationException;
import org.apache.pulsar.manager.entity.TopicStatsEntity;
import org.apache.pulsar.manager.entity.TopicStatsEntity.TopicStatsSummary;
import org.apache.pulsar.manager.entity.TopicsStatsRepository;
import org.apache.pulsar.manager.service.PulsarAdminService;
import org.apache.pulsar.manager.service.TopicsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TopicsServiceImpl implements TopicsService {

    private static final Logger log = LoggerFactory.getLogger(TopicsServiceImpl.class);

    public static final String PARTITIONED_TOPIC_SUFFIX = "-partition-";

    @Value("${backend.directRequestBroker}")
    private boolean directRequestBroker;

    private final TopicsStatsRepository topicsStatsRepository;

    private final PulsarAdminService pulsarAdminService;

    @Autowired
    public TopicsServiceImpl(TopicsStatsRepository topicsStatsRepository, PulsarAdminService pulsarAdminService) {
        this.topicsStatsRepository = topicsStatsRepository;
        this.pulsarAdminService = pulsarAdminService;
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
        List<Map<String, Object>> allTopics = this.getTopicStats(env, tenant, namespace, serviceUrl);
        topicsMap.put("topics", allTopics);
        topicsMap.put("isPage", false);
        topicsMap.put("total", allTopics.size());
        topicsMap.put("pageNum", 1);
        topicsMap.put("pageSize", allTopics.size());
        return topicsMap;
    }

    private List<Map<String, Object>> getTopicStats(
            String env, String tenant, String namespace, String requestHost) {
        List<Map<String, Object>> result = new ArrayList<>();

        Map<String, List<String>> allTopics
                = this.getTopicListByPulsarAdmin(tenant, namespace, requestHost);
        Map<String, List<String>> allPartitionedTopics
                = this.getPartitionedTopicListByPulsarAdmin(tenant, namespace, requestHost);

        result.addAll(this.getTopicsStatsList(
                env,
                tenant,
                namespace,
                TopicDomain.persistent.toString(),
                this.convertTopicList(
                        allTopics.get(TopicDomain.persistent.toString()),
                        allPartitionedTopics.get(TopicDomain.persistent.toString()),
                        TopicDomain.persistent.toString(),
                        requestHost
                )
        ));
        result.addAll(this.getTopicsStatsList(
                env,
                tenant,
                namespace,
                TopicDomain.non_persistent.toString(),
                this.convertTopicList(
                        allTopics.get(TopicDomain.non_persistent.toString()),
                        allPartitionedTopics.get(TopicDomain.non_persistent.toString()),
                        TopicDomain.non_persistent.toString(),
                        requestHost
                )
        ));

        return result;
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
        List<Map<String, String>> allTopics = this.getTopicsList(tenant, namespace, requestHost);
        topicsMap.put("topics", allTopics);
        topicsMap.put("isPage", false);
        topicsMap.put("total", allTopics.size());
        topicsMap.put("pageNum", 1);
        topicsMap.put("pageSize", allTopics.size());
        return topicsMap;
    }

    @Override
    public List<Map<String, Object>> peekMessages(String persistent,
                                                  String tenant,
                                                  String namespace,
                                                  String topic,
                                                  String subName,
                                                  Integer messagePosition,
                                                  String requestHost) {
        List<Message<byte[]>> messages;
        String topicFullPath = persistent + "://" + tenant + "/" + namespace + "/" + topic;
        try {
            messages = pulsarAdminService.topics(requestHost).peekMessages(topicFullPath, subName, messagePosition);
        } catch(PulsarAdminException e) {
            PulsarAdminOperationException pulsarAdminOperationException
                    = new PulsarAdminOperationException("Failed to peek messages.");
            log.error(pulsarAdminOperationException.getMessage(), e);
            throw pulsarAdminOperationException;
        }
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (Message<byte[]> msg: messages) {
            Map<String, Object> message = Maps.newHashMap();
            if (msg.getMessageId() instanceof BatchMessageIdImpl) {
                BatchMessageIdImpl msgId = (BatchMessageIdImpl) msg.getMessageId();
                message.put("ledgerId", msgId.getLedgerId());
                message.put("entryId", msgId.getEntryId());
                message.put("batchIndex", msgId.getBatchIndex());
                message.put("batch", true);
            } else {
                MessageIdImpl msgId = (MessageIdImpl) msg.getMessageId();
                message.put("ledgerId", msgId.getLedgerId());
                message.put("entryId", msgId.getEntryId());
                message.put("batch", false);
            }
            if (msg.getProperties().size() > 0) {
                msg.getProperties().forEach((k, v) -> message.put(k, v));
            }
            message.put("data", msg.getData());
            mapList.add(message);
        }
        return mapList;
    }

    private List<Map<String, String>> getTopicsList(
            String tenant, String namespace, String requestHost) {
        List<Map<String, String>> result = new ArrayList<>();

        Map<String, List<String>> allTopics
                = this.getTopicListByPulsarAdmin(tenant, namespace, requestHost);
        Map<String, List<String>> allPartitionedTopics
                = this.getPartitionedTopicListByPulsarAdmin(tenant, namespace, requestHost);

        result.addAll(this.convertTopicList(
                allTopics.get(TopicDomain.persistent.toString()),
                allPartitionedTopics.get(TopicDomain.persistent.toString()),
                TopicDomain.persistent.toString(),
                requestHost
        ));
        result.addAll(this.convertTopicList(
                allTopics.get(TopicDomain.non_persistent.toString()),
                allPartitionedTopics.get(TopicDomain.non_persistent.toString()),
                TopicDomain.non_persistent.toString(),
                requestHost
        ));

        return result;
    }

    private List<Map<String, String>> convertTopicList(
            List<String> topics, List<String> partitionedTopics, String persistent, String requestHost) {
        List<Map<String, String>> topicsArray = new ArrayList<>();

        Map<String, List<String>> partitionedMap = Maps.newHashMap();
        for (String p : partitionedTopics) {
            if (p.startsWith(persistent)) {
                partitionedMap.put(this.getTopicName(p), new ArrayList<>());
            }
        }

        for (String topic: topics) {
            if (topic.startsWith(persistent)) {
                String topicName = this.getTopicName(topic);
                Map<String, String> topicEntity = Maps.newHashMap();
                if (isPartitionedTopic(partitionedTopics, topic)) {
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

        for (String s : partitionedTopics) {
            String topicName = this.getTopicName(s);
            Map<String, String> topicEntity = Maps.newHashMap();
            List<String> partitionedTopicList = partitionedMap.get(s);
            if (partitionedTopicList != null && partitionedTopicList.size() > 0) {
                topicEntity.put("topic", topicName);
                topicEntity.put("partitions", String.valueOf(partitionedTopicList.size()));
                topicEntity.put("persistent", persistent);
            } else {
                topicEntity.put("topic", topicName);
                PartitionedTopicMetadata partitionedTopicMetadata
                        = this.getPartitionedTopicMetadataByPulsarAdmin(s, requestHost);
                topicEntity.put("partitions", String.valueOf(partitionedTopicMetadata.partitions));
                topicEntity.put("persistent", persistent);
            }
            topicsArray.add(topicEntity);
        }
        return topicsArray;
    }

    private String getTopicName(String topic) {
        String tntPath = topic.split("://")[1];
        String topicName = tntPath.split("/")[2];
        return topicName;
    }

    private Map<String, List<String>> getTopicListByPulsarAdmin(
            String tenant, String namespace, String requestHost) {
        try {
            return parseTopics(
                    pulsarAdminService.topics(requestHost).
                            getList(tenant + "/" + namespace)
            );
        } catch (PulsarAdminException e) {
            PulsarAdminOperationException pulsarAdminOperationException
                    = new PulsarAdminOperationException("Failed to get topic list.");
            log.error(pulsarAdminOperationException.getMessage(), e);
            throw pulsarAdminOperationException;
        }
    }

    private Map<String, List<String>> getPartitionedTopicListByPulsarAdmin(
            String tenant, String namespace, String requestHost) {
        try {
            return parseTopics(
                    pulsarAdminService.topics(requestHost).
                            getPartitionedTopicList(tenant + "/" + namespace)
            );
        } catch (PulsarAdminException e) {
            PulsarAdminOperationException pulsarAdminOperationException
                    = new PulsarAdminOperationException("Failed to get partitioned topic list.");
            log.error(pulsarAdminOperationException.getMessage(), e);
            throw pulsarAdminOperationException;
        }
    }

    private PartitionedTopicMetadata getPartitionedTopicMetadataByPulsarAdmin(
            String topic, String requestHost) {
        try {
            return pulsarAdminService.topics(requestHost).
                    getPartitionedTopicMetadata(topic);
        } catch (PulsarAdminException e) {
            PulsarAdminOperationException pulsarAdminOperationException
                    = new PulsarAdminOperationException("Failed to get partitioned topic metadata.");
            log.error(pulsarAdminOperationException.getMessage(), e);
            throw pulsarAdminOperationException;
        }
    }

    private Map<String, List<String>> parseTopics(List<String> topics) {
        Map<String, List<String>> result = new HashMap<>();
        List<String> persistentTopics = new ArrayList<>();
        List<String> nonPersistentTopics = new ArrayList<>();

        for (String topic : topics) {
            TopicName topicName  = TopicName.get(topic);
            if (TopicDomain.persistent.equals(topicName.getDomain())) {
                persistentTopics.add(topic);
            } else {
                nonPersistentTopics.add(topic);
            }
        }

        result.put(TopicDomain.persistent.toString(), persistentTopics);
        result.put(TopicDomain.non_persistent.toString(), nonPersistentTopics);
        return result;
    }
}
