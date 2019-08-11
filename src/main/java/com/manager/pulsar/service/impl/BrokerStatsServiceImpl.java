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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.manager.pulsar.entity.*;
import com.manager.pulsar.service.BrokerStatsService;
import com.manager.pulsar.service.BrokersService;
import com.manager.pulsar.service.ClustersService;
import com.manager.pulsar.utils.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Configuration
@Component
@EnableScheduling
public class BrokerStatsServiceImpl implements BrokerStatsService {

    private static final Logger log = LoggerFactory.getLogger(BrokerStatsServiceImpl.class);


    @Value("${backend.directRequestHost}")
    private String directRequestHost;

    @Value("${clear.stats.interval}")
    private Long clearStatsInterval;

    @Autowired
    private EnvironmentsRepository environmentsRepository;

    @Autowired
    private ClustersService clustersService;

    @Autowired
    private BrokersService brokersService;

    @Autowired
    private TopicsStatsRepository topicsStatsRepository;

    @Autowired
    private SubscriptionsStatsRepository subscriptionsStatsRepository;

    @Autowired
    private PublishersStatsRepository publishersStatsRepository;

    @Autowired
    private ReplicationsStatsRepository replicationsStatsRepository;

    @Autowired
    private ConsumersStatsRepository consumersStatsRepository;

    private static final Map<String, String> header = new HashMap<String, String>(){{
        put("Content-Type","application/json");
    }};

    public String forwarBrokerStatsMetrics(String broker, String requestHost) {

        broker = checkBroker(broker, requestHost);
        return HttpUtil.doGet(broker + "/admin/v2/broker-stats/metrics", header);
    }

    public String forwardBrokerStatsTopics(String broker, String requestHost) {

        broker = checkBroker(broker, requestHost);
        return HttpUtil.doGet(broker + "/admin/v2/broker-stats/topics", header);
    }

    @Scheduled(initialDelayString = "${init.delay.interval}", fixedDelayString = "${insert.stats.interval}")
    private void scheduleCollectStats() {
        List<EnvironmentEntity> environmentEntities = environmentsRepository.getAllEnvironments();
        List<String> brokerList = new ArrayList<>();
        List<String> clusterList = new ArrayList<>();
        environmentEntities.forEach((environmentEntity -> {
            brokerList.add(environmentEntity.getBroker());
        }));
        Set<String> collectStatsBroker = new HashSet<>();
        for (String b : brokerList) {
            String broker = checkBroker(null, b);
            Map<String, Object> clusterObject = clustersService.getClustersList(0, 0, broker);
            List<HashMap<String, Object>> clusterLists = (List<HashMap<String, Object>>) clusterObject.get("data");
            clusterLists.forEach((clusterMap) -> {
                String cluster = (String) clusterMap.get("cluster");
                if (!clusterList.contains(cluster)) {
                    collectStatsBroker.add(b);
                }
            });
        }
        collectStatsBroker.forEach((broker) -> {
            log.info("Start collecting stats from broker: {}", broker);
            convertStatsToDb(0, 0, checkBroker(null, broker));
        });

        log.info("Start clearing stats from broker");
        long unixTime = System.currentTimeMillis() / 1000L;
        clearStats(unixTime, clearStatsInterval);
    }

    public void convertStatsToDb(Integer pageNum, Integer pageSize, String requestHost) {
        Map<String, Object> clusterObject = clustersService.getClustersList(pageNum, pageSize, requestHost);
        List<HashMap<String, Object>> clusterLists = (List<HashMap<String, Object>>) clusterObject.get("data");
        clusterLists.forEach((clusterMap) -> {
            String cluster = (String) clusterMap.get("cluster");
            Map<String, Object> brokerObject = brokersService.getBrokersList(pageNum, pageSize, cluster, requestHost);
            List<HashMap<String, Object>> brokerLists = (List<HashMap<String, Object>>) brokerObject.get("data");
            brokerLists.forEach((brokerMap) -> {
                String tempBroker = (String) brokerMap.get("broker");
                String broker = checkBroker(tempBroker, requestHost);
                String result = HttpUtil.doGet(broker + "/admin/v2/broker-stats/topics", header);
                Gson gson = new Gson();
                HashMap<String, HashMap<String, HashMap<String, HashMap<String, PulsarManagerTopicStats>>>> brokerStatsTopicEntity = gson.fromJson(result,
                        new TypeToken<HashMap<String, HashMap<String, HashMap<String, HashMap<String, PulsarManagerTopicStats>>>>>() {}.getType());
                brokerStatsTopicEntity.forEach((namespace, namespaceStats) -> {
                    namespaceStats.forEach((bundle, bundleStats) -> {
                        bundleStats.forEach((persistent, persistentStats) -> {
                            persistentStats.forEach((topic, topicStats) -> {
                                long unixTime = System.currentTimeMillis() / 1000L;
                                TopicStatsEntity topicStatsEntity = new TopicStatsEntity();
                                String[] topicPath = this.parseTopic(topic);
                                topicStatsEntity.setCluster(cluster);
                                topicStatsEntity.setBroker(tempBroker);
                                topicStatsEntity.setTenant(topicPath[0]);
                                topicStatsEntity.setNamespace(topicPath[1]);
                                topicStatsEntity.setBundle(bundle);
                                topicStatsEntity.setPersistent(persistent);
                                topicStatsEntity.setTopic(topicPath[2]);
                                topicStatsEntity.setMsgRateIn(topicStats.getMsgRateIn());
                                topicStatsEntity.setMsgRateOut(topicStats.getMsgRateOut());
                                topicStatsEntity.setMsgThroughputIn(topicStats.getMsgThroughputIn());
                                topicStatsEntity.setMsgThroughputOut(topicStats.getMsgThroughputOut());
                                topicStatsEntity.setAverageMsgSize(topicStats.getAverageMsgSize());
                                topicStatsEntity.setStorageSize(topicStats.getStorageSize());
                                topicStatsEntity.setSubscriptionCount(topicStats.getSubscriptions().size());
                                topicStatsEntity.setProducerCount(topicStats.getPublishers().size());
                                topicStatsEntity.setTimestamp(unixTime);
                                long topicStatsId = topicsStatsRepository.save(topicStatsEntity);
                                if (topicStats.getSubscriptions() != null) {
                                    topicStats.getSubscriptions().forEach((subscription, subscriptionStats) -> {
                                        SubscriptionStatsEntity subscriptionStatsEntity = new SubscriptionStatsEntity();
                                        subscriptionStatsEntity.setTopicStatsId(topicStatsId);
                                        subscriptionStatsEntity.setSubscription(subscription);
                                        subscriptionStatsEntity.setMsgRateOut(subscriptionStats.getMsgRateOut());
                                        subscriptionStatsEntity.setMsgThroughputOut(subscriptionStats.getMsgThroughputOut());
                                        subscriptionStatsEntity.setMsgRateRedeliver(subscriptionStats.getMsgRateRedeliver());
                                        subscriptionStatsEntity.setNumberOfEntriesSinceFirstNotAckedMessage(
                                                subscriptionStats.getNumberOfEntriesSinceFirstNotAckedMessage());
                                        subscriptionStatsEntity.setTotalNonContiguousDeletedMessagesRange(
                                                subscriptionStats.getTotalNonContiguousDeletedMessagesRange());
                                        subscriptionStatsEntity.setMsgBacklog(subscriptionStats.getMsgBacklog());
                                        subscriptionStatsEntity.setSubscriptionType(String.valueOf(subscriptionStats.getType()));
                                        subscriptionStatsEntity.setMsgRateExpired(subscriptionStats.getMsgRateExpired());
                                        subscriptionStatsEntity.setReplicated(subscriptionStats.isReplicated());
                                        subscriptionStatsEntity.setTimestamp(unixTime);
                                        long subscriptionStatsId = subscriptionsStatsRepository.save(subscriptionStatsEntity);
                                        if (subscriptionStats.getConsumers() != null) {
                                            subscriptionStats.getConsumers().forEach((consumerStats) -> {
                                                ConsumerStatsEntity consumerStatsEntity = new ConsumerStatsEntity();
                                                consumerStatsEntity.setSubscriptionStatsId(subscriptionStatsId);
                                                consumerStatsEntity.setTopicStatsId(topicStatsId);
                                                consumerStatsEntity.setReplicationStatsId(-1);
                                                consumerStatsEntity.setConsumer(consumerStats.getConsumerName());
                                                consumerStatsEntity.setMsgRateOut(consumerStats.getMsgRateOut());
                                                consumerStatsEntity.setMsgThroughputOut(consumerStats.getMsgThroughputOut());
                                                consumerStatsEntity.setMsgRateRedeliver(consumerStats.getMsgRateRedeliver());
                                                consumerStatsEntity.setAvailablePermits(consumerStats.getAvailablePermits());
                                                consumerStatsEntity.setAddress(consumerStats.getAddress());
                                                consumerStatsEntity.setConnectedSince(consumerStats.getConnectedSince());
                                                consumerStatsEntity.setClientVersion(consumerStats.getClientVersion());
                                                consumerStatsEntity.setMetadata(gson.toJson(consumerStats.getMetadata()));
                                                consumerStatsEntity.setTimestamp(unixTime);
                                                consumersStatsRepository.save(consumerStatsEntity);
                                            });
                                        }
                                    });
                                }
                                if (topicStats.getPublishers() != null) {
                                    topicStats.getPublishers().forEach((producer) -> {
                                        PublisherStatsEntity publisherStatsEntity = new PublisherStatsEntity();
                                        publisherStatsEntity.setTopicStatsId(topicStatsId);
                                        publisherStatsEntity.setProducerId(producer.getProducerId());
                                        publisherStatsEntity.setProducerName(producer.getProducerName());
                                        publisherStatsEntity.setMsgRateIn(producer.getMsgRateIn());
                                        publisherStatsEntity.setMsgThroughputIn(producer.getMsgThroughputIn());
                                        publisherStatsEntity.setAverageMsgSize(producer.getAverageMsgSize());
                                        publisherStatsEntity.setAddress(producer.getAddress());
                                        publisherStatsEntity.setConnectedSince(producer.getConnectedSince());
                                        publisherStatsEntity.setClientVersion(producer.getClientVersion());
                                        publisherStatsEntity.setMetadata(gson.toJson(producer.getMetadata()));
                                        publisherStatsEntity.setTimestamp(unixTime);
                                        publishersStatsRepository.save(publisherStatsEntity);
                                    });
                                }
                                if (topicStats.getReplication() != null) {
                                    topicStats.getReplication().forEach((replication, replicatorStats) -> {
                                        ReplicationStatsEntity replicationStatsEntity = new ReplicationStatsEntity();
                                        replicationStatsEntity.setCluster(replication);
                                        replicationStatsEntity.setTopicStatsId(topicStatsId);
                                        replicationStatsEntity.setMsgRateIn(replicatorStats.getMsgRateIn());
                                        replicationStatsEntity.setMsgThroughputIn(replicatorStats.getMsgThroughputIn());
                                        replicationStatsEntity.setMsgRateOut(replicatorStats.getMsgRateOut());
                                        replicationStatsEntity.setMsgThroughputOut(replicatorStats.getMsgThroughputOut());
                                        replicationStatsEntity.setMsgRateExpired(replicatorStats.getMsgRateExpired());
                                        replicationStatsEntity.setReplicationBacklog(replicatorStats.getReplicationBacklog());
                                        replicationStatsEntity.setConnected(replicatorStats.isConnected());
                                        replicationStatsEntity.setReplicationDelayInSeconds(replicatorStats.getReplicationDelayInSeconds());
                                        replicationStatsEntity.setInboundConnection(replicatorStats.getInboundConnection());
                                        replicationStatsEntity.setInboundConnectedSince(replicatorStats.getInboundConnectedSince());
                                        replicationStatsEntity.setOutboundConnection(replicatorStats.getOutboundConnection());
                                        replicationStatsEntity.setOutboundConnectedSince(replicatorStats.getOutboundConnectedSince());
                                        replicationStatsEntity.setTimestamp(unixTime);
                                        replicationsStatsRepository.save(replicationStatsEntity);
                                    });
                                }
                            });
                        });
                    });
                });
            });
        });
    }

    public void clearStats(long nowTime, long timeInterval) {
        consumersStatsRepository.remove(nowTime, timeInterval);
        subscriptionsStatsRepository.remove(nowTime, timeInterval);
        publishersStatsRepository.remove(nowTime, timeInterval);
        replicationsStatsRepository.remove(nowTime, timeInterval);
        topicsStatsRepository.remove(nowTime, timeInterval);
    }

    public static String checkBroker(String broker, String requestHost) {
        if (broker == null || broker.length() <= 0) {
            broker = requestHost;
        }

        if (!broker.startsWith("http")) {
            broker = "http://" + broker;
        }
        return broker;
    }

    private String[] parseTopic(String topic) {
        String tntPath = topic.split("://")[1];
        String[] topicPath = tntPath.split("/");
        return topicPath;
    }
}
