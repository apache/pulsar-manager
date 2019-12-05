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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.DecimalFormat;
import org.apache.pulsar.manager.service.BrokerStatsService;
import org.apache.pulsar.manager.service.BrokersService;
import org.apache.pulsar.manager.service.ClustersService;
import org.apache.pulsar.manager.service.EnvironmentCacheService;
import org.apache.pulsar.manager.utils.HttpUtil;
import org.apache.pulsar.manager.entity.ConsumerStatsEntity;
import org.apache.pulsar.manager.entity.ConsumersStatsRepository;
import org.apache.pulsar.manager.entity.EnvironmentEntity;
import org.apache.pulsar.manager.entity.EnvironmentsRepository;
import org.apache.pulsar.manager.entity.PublisherStatsEntity;
import org.apache.pulsar.manager.entity.PublishersStatsRepository;
import org.apache.pulsar.manager.entity.PulsarManagerTopicStats;
import org.apache.pulsar.manager.entity.ReplicationStatsEntity;
import org.apache.pulsar.manager.entity.ReplicationsStatsRepository;
import org.apache.pulsar.manager.entity.SubscriptionStatsEntity;
import org.apache.pulsar.manager.entity.SubscriptionsStatsRepository;
import org.apache.pulsar.manager.entity.TopicStatsEntity;
import org.apache.pulsar.manager.entity.TopicsStatsRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Configuration
@Component
@EnableScheduling
public class BrokerStatsServiceImpl implements BrokerStatsService {

    private static final Logger log = LoggerFactory.getLogger(BrokerStatsServiceImpl.class);


    @Value("${backend.directRequestHost}")
    private String directRequestHost;

    @Value("${backend.jwt.token}")
    private String pulsarJwtToken;

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

    @Autowired
    private EnvironmentCacheService environmentCache;

    private static final Map<String, String> header = new HashMap<String, String>(){{
        put("Content-Type","application/json");
    }};

    public String forwardBrokerStatsMetrics(String broker, String requestHost) {
        if (StringUtils.isNotBlank(pulsarJwtToken)) {
            header.put("Authorization", String.format("Bearer %s", pulsarJwtToken));
        }

        broker = checkServiceUrl(broker, requestHost);
        return HttpUtil.doGet(broker + "/admin/v2/broker-stats/metrics", header);
    }

    public String forwardBrokerStatsTopics(String broker, String requestHost) {
        if (StringUtils.isNotBlank(pulsarJwtToken)) {
            header.put("Authorization", String.format("Bearer %s", pulsarJwtToken));
        }
        broker = checkServiceUrl(broker, requestHost);
        return HttpUtil.doGet(broker + "/admin/v2/broker-stats/topics", header);
    }

    @Scheduled(initialDelayString = "${init.delay.interval}", fixedDelayString = "${insert.stats.interval}")
    private void scheduleCollectStats() {
        long unixTime = System.currentTimeMillis() / 1000L;
        List<EnvironmentEntity> environmentEntities = environmentsRepository.getAllEnvironments();
        Map<Pair<String, String>, String> collectStatsServiceUrls = new HashMap<>();
        for (EnvironmentEntity env : environmentEntities) {
            String serviceUrl = checkServiceUrl(null, env.getBroker());
            Map<String, Object> clusterObject =
                clustersService.getClustersList(0, 0, serviceUrl, (c) -> serviceUrl);
            List<HashMap<String, Object>> clusterLists = (List<HashMap<String, Object>>) clusterObject.get("data");
            clusterLists.forEach((clusterMap) -> {
                String cluster = (String) clusterMap.get("cluster");
                Pair<String, String> envCluster = Pair.of(env.getName(), cluster);
                collectStatsServiceUrls.put(envCluster, (String) clusterMap.get("serviceUrl"));
            });
        }
        collectStatsServiceUrls.forEach((envCluster, serviceUrl) -> {
            log.info("Start collecting stats from env {} / cluster {} @ {}",
                envCluster.getLeft(), envCluster.getRight(), serviceUrl);
            collectStatsToDB(unixTime, envCluster.getLeft(), envCluster.getRight(), serviceUrl);
        });

        log.info("Start clearing stats from broker");
        clearStats(unixTime, clearStatsInterval / 1000);
    }

    public void collectStatsToDB(long unixTime, String env, String cluster, String serviceUrl) {
        if (StringUtils.isNotBlank(pulsarJwtToken)) {
            header.put("Authorization", String.format("Bearer %s", pulsarJwtToken));
        }
        Map<String, Object> brokerObject = brokersService.getBrokersList(0, 0, cluster, serviceUrl);
        List<HashMap<String, Object>> brokerLists = (List<HashMap<String, Object>>) brokerObject.get("data");
        brokerLists.forEach((brokerMap) -> {
            String tempBroker = (String) brokerMap.get("broker");
            // TODO: handle other protocols
            String broker = "http://" + tempBroker;
            String result = HttpUtil.doGet(broker + "/admin/v2/broker-stats/topics", header);
            Gson gson = new Gson();
            HashMap<String, HashMap<String, HashMap<String, HashMap<String, PulsarManagerTopicStats>>>> brokerStatsTopicEntity = gson.fromJson(result,
                new TypeToken<HashMap<String, HashMap<String, HashMap<String, HashMap<String, PulsarManagerTopicStats>>>>>() {
                }.getType());
            brokerStatsTopicEntity.forEach((namespace, namespaceStats) -> {
                namespaceStats.forEach((bundle, bundleStats) -> {
                    bundleStats.forEach((persistent, persistentStats) -> {
                        persistentStats.forEach((topic, topicStats) -> {
                            DecimalFormat df = new DecimalFormat("#.##");
                            TopicStatsEntity topicStatsEntity = new TopicStatsEntity();
                            String[] topicPath = this.parseTopic(topic);
                            topicStatsEntity.setEnvironment(env);
                            topicStatsEntity.setCluster(cluster);
                            topicStatsEntity.setBroker(tempBroker);
                            topicStatsEntity.setTenant(topicPath[0]);
                            topicStatsEntity.setNamespace(topicPath[1]);
                            topicStatsEntity.setBundle(bundle);
                            topicStatsEntity.setPersistent(persistent);
                            topicStatsEntity.setTopic(topicPath[2]);
                            topicStatsEntity.setMsgRateIn(Double.parseDouble(df.format(topicStats.getMsgRateIn())));
                            topicStatsEntity.setMsgRateOut(Double.parseDouble(df.format(topicStats.getMsgRateOut())));
                            topicStatsEntity.setMsgThroughputIn(Double.parseDouble(df.format(topicStats.getMsgThroughputIn())));
                            topicStatsEntity.setMsgThroughputOut(Double.parseDouble(df.format(topicStats.getMsgThroughputOut())));
                            topicStatsEntity.setAverageMsgSize(Double.parseDouble(df.format(topicStats.getAverageMsgSize())));
                            topicStatsEntity.setStorageSize(Double.parseDouble(df.format(topicStats.getStorageSize())));
                            topicStatsEntity.setSubscriptionCount(topicStats.getSubscriptions().size());
                            topicStatsEntity.setProducerCount(topicStats.getPublishers().size());
                            topicStatsEntity.setTime_stamp(unixTime);
                            long topicStatsId = topicsStatsRepository.save(topicStatsEntity);
                            if (topicStats.getSubscriptions() != null) {
                                topicStats.getSubscriptions().forEach((subscription, subscriptionStats) -> {
                                    SubscriptionStatsEntity subscriptionStatsEntity = new SubscriptionStatsEntity();
                                    subscriptionStatsEntity.setTopicStatsId(topicStatsId);
                                    subscriptionStatsEntity.setSubscription(subscription);
                                    subscriptionStatsEntity.setMsgRateOut(Double.parseDouble(df.format(subscriptionStats.getMsgRateOut())));
                                    subscriptionStatsEntity.setMsgThroughputOut(Double.parseDouble(df.format(subscriptionStats.getMsgThroughputOut())));
                                    subscriptionStatsEntity.setMsgRateRedeliver(Double.parseDouble(df.format(subscriptionStats.getMsgRateRedeliver())));
                                    subscriptionStatsEntity.setNumberOfEntriesSinceFirstNotAckedMessage(
                                        subscriptionStats.getNumberOfEntriesSinceFirstNotAckedMessage());
                                    subscriptionStatsEntity.setTotalNonContiguousDeletedMessagesRange(
                                        subscriptionStats.getTotalNonContiguousDeletedMessagesRange());
                                    subscriptionStatsEntity.setMsgBacklog(subscriptionStats.getMsgBacklog());
                                    subscriptionStatsEntity.setSubscriptionType(String.valueOf(subscriptionStats.getType()));
                                    subscriptionStatsEntity.setMsgRateExpired(Double.parseDouble(df.format(subscriptionStats.getMsgRateExpired())));
                                    subscriptionStatsEntity.setReplicated(subscriptionStats.isReplicated());
                                    subscriptionStatsEntity.setTime_stamp(unixTime);
                                    long subscriptionStatsId = subscriptionsStatsRepository.save(subscriptionStatsEntity);
                                    if (subscriptionStats.getConsumers() != null) {
                                        subscriptionStats.getConsumers().forEach((consumerStats) -> {
                                            ConsumerStatsEntity consumerStatsEntity = new ConsumerStatsEntity();
                                            consumerStatsEntity.setSubscriptionStatsId(subscriptionStatsId);
                                            consumerStatsEntity.setTopicStatsId(topicStatsId);
                                            consumerStatsEntity.setReplicationStatsId(-1);
                                            consumerStatsEntity.setConsumer(consumerStats.getConsumerName());
                                            consumerStatsEntity.setMsgRateOut(Double.parseDouble(df.format(consumerStats.getMsgRateOut())));
                                            consumerStatsEntity.setMsgThroughputOut(Double.parseDouble(df.format(consumerStats.getMsgThroughputOut())));
                                            consumerStatsEntity.setMsgRateRedeliver(Double.parseDouble(df.format(consumerStats.getMsgRateRedeliver())));
                                            consumerStatsEntity.setAvailablePermits(consumerStats.getAvailablePermits());
                                            consumerStatsEntity.setAddress(consumerStats.getAddress());
                                            consumerStatsEntity.setConnectedSince(consumerStats.getConnectedSince());
                                            consumerStatsEntity.setClientVersion(consumerStats.getClientVersion());
                                            consumerStatsEntity.setMetadata(gson.toJson(consumerStats.getMetadata()));
                                            consumerStatsEntity.setTime_stamp(unixTime);
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
                                    publisherStatsEntity.setMsgRateIn(Double.parseDouble(df.format(producer.getMsgRateIn())));
                                    publisherStatsEntity.setMsgThroughputIn(Double.parseDouble(df.format(producer.getMsgThroughputIn())));
                                    publisherStatsEntity.setAverageMsgSize(Double.parseDouble(df.format(producer.getAverageMsgSize())));
                                    publisherStatsEntity.setAddress(producer.getAddress());
                                    publisherStatsEntity.setConnectedSince(producer.getConnectedSince());
                                    publisherStatsEntity.setClientVersion(producer.getClientVersion());
                                    publisherStatsEntity.setMetadata(gson.toJson(producer.getMetadata()));
                                    publisherStatsEntity.setTime_stamp(unixTime);
                                    publishersStatsRepository.save(publisherStatsEntity);
                                });
                            }
                            if (topicStats.getReplication() != null) {
                                topicStats.getReplication().forEach((replication, replicatorStats) -> {
                                    ReplicationStatsEntity replicationStatsEntity = new ReplicationStatsEntity();
                                    replicationStatsEntity.setCluster(replication);
                                    replicationStatsEntity.setTopicStatsId(topicStatsId);
                                    replicationStatsEntity.setMsgRateIn(Double.parseDouble(df.format(replicatorStats.getMsgRateIn())));
                                    replicationStatsEntity.setMsgThroughputIn(Double.parseDouble(df.format(replicatorStats.getMsgThroughputIn())));
                                    replicationStatsEntity.setMsgRateOut(Double.parseDouble(df.format(replicatorStats.getMsgRateOut())));
                                    replicationStatsEntity.setMsgThroughputOut(Double.parseDouble(df.format(replicatorStats.getMsgThroughputOut())));
                                    replicationStatsEntity.setMsgRateExpired(Double.parseDouble(df.format(replicatorStats.getMsgRateExpired())));
                                    replicationStatsEntity.setReplicationBacklog(replicatorStats.getReplicationBacklog());
                                    replicationStatsEntity.setConnected(replicatorStats.isConnected());
                                    replicationStatsEntity.setReplicationDelayInSeconds(replicatorStats.getReplicationDelayInSeconds());
                                    replicationStatsEntity.setInboundConnection(replicatorStats.getInboundConnection());
                                    replicationStatsEntity.setInboundConnectedSince(replicatorStats.getInboundConnectedSince());
                                    replicationStatsEntity.setOutboundConnection(replicatorStats.getOutboundConnection());
                                    replicationStatsEntity.setOutboundConnectedSince(replicatorStats.getOutboundConnectedSince());
                                    replicationStatsEntity.setTime_stamp(unixTime);
                                    replicationsStatsRepository.save(replicationStatsEntity);
                                });
                            }
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

    public static String checkServiceUrl(String serviceUrl, String requestHost) {
        if (serviceUrl == null || serviceUrl.length() <= 0) {
            serviceUrl = requestHost;
        }

        if (!serviceUrl.startsWith("http")) {
            serviceUrl = "http://" + serviceUrl;
        }
        return serviceUrl;
    }

    private String[] parseTopic(String topic) {
        String tntPath = topic.split("://")[1];
        return tntPath.split("/");
    }
}
