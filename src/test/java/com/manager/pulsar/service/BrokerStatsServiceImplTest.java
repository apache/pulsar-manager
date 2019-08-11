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
package com.manager.pulsar.service;

import com.github.pagehelper.Page;
import com.google.common.collect.Maps;
import com.manager.pulsar.entity.*;
import com.manager.pulsar.utils.HttpUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.Optional;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PowerMockIgnore( {"javax.management.*", "javax.net.ssl.*"})
@PrepareForTest(HttpUtil.class)
@SpringBootTest
public class BrokerStatsServiceImplTest {

    @Autowired
    private BrokerStatsService brokerStatsService;

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

    public static String testData = "{\n" +
            "\t\"public/functions\": {\n" +
            "\t\t\"0x40000000_0x80000000\": {\n" +
            "\t\t\t\"persistent\": {\n" +
            "\t\t\t\t\"persistent://public/functions/metadata\": {\n" +
            "\t\t\t\t\t\"publishers\": [{\n" +
            "\t\t\t\t\t\t\"msgRateIn\": 0.0,\n" +
            "\t\t\t\t\t\t\"msgThroughputIn\": 0.0,\n" +
            "\t\t\t\t\t\t\"averageMsgSize\": 0.0,\n" +
            "\t\t\t\t\t\t\"address\": \"/127.0.0.1:59668\",\n" +
            "\t\t\t\t\t\t\"producerId\": 1,\n" +
            "\t\t\t\t\t\t\"producerName\": \"standalone-1-1\",\n" +
            "\t\t\t\t\t\t\"connectedSince\": \"2019-08-10T11:37:22.405+08:00\",\n" +
            "\t\t\t\t\t\t\"clientVersion\": \"2.5.0-SNAPSHOT\",\n" +
            "\t\t\t\t\t\t\"metadata\": {}\n" +
            "\t\t\t\t\t}],\n" +
            "\t\t\t\t\t\"replication\": {\n" +
            "\t\t\t\t\t\t\"test-replications\": {\n" +
            "\t\t\t\t\t\t\t\"msgRateIn\": 123,\n" +
            "\t\t\t\t\t\t\t\"msgThroughputIn\": 456,\n" +
            "\t\t\t\t\t\t\t\"msgRateOut\": 456,\n" +
            "\t\t\t\t\t\t\t\"msgThroughputOut\": 789,\n" +
            "\t\t\t\t\t\t\t\"msgRateExpired\": 990,\n" +
            "\t\t\t\t\t\t\t\"replicationBacklog\": 100,\n" +
            "\t\t\t\t\t\t\t\"connected\": false,\n" +
            "\t\t\t\t\t\t\t\"replicationDelayInSeconds\": 890,\n" +
            "\t\t\t\t\t\t\t\"inboundConnection\": \"test\",\n" +
            "\t\t\t\t\t\t\t\"inboundConnectedSince\": \"test2\",\n" +
            "\t\t\t\t\t\t\t\"outboundConnection\": \"test3\",\n" +
            "\t\t\t\t\t\t\t\"outboundConnectedSince\": \"test4\"\n" +
            "\t\t\t\t\t\t}\n" +
            "\t\t\t\t\t},\n" +
            "\t\t\t\t\t\"subscriptions\": {\n" +
            "\t\t\t\t\t\t\"reader-1ddabdb183\": {\n" +
            "\t\t\t\t\t\t\t\"consumers\": [{\n" +
            "\t\t\t\t\t\t\t\t\"address\": \"/127.0.0.1:59668\",\n" +
            "\t\t\t\t\t\t\t\t\"consumerName\": \"543bd\",\n" +
            "\t\t\t\t\t\t\t\t\"availablePermits\": 1000,\n" +
            "\t\t\t\t\t\t\t\t\"connectedSince\": \"2019-08-10T11:37:24.306+08:00\",\n" +
            "\t\t\t\t\t\t\t\t\"msgRateOut\": 0.0,\n" +
            "\t\t\t\t\t\t\t\t\"msgThroughputOut\": 0.0,\n" +
            "\t\t\t\t\t\t\t\t\"msgRateRedeliver\": 0.0,\n" +
            "\t\t\t\t\t\t\t\t\"clientVersion\": \"2.5.0-SNAPSHOT\",\n" +
            "\t\t\t\t\t\t\t\t\"metadata\": {}\n" +
            "\t\t\t\t\t\t\t}],\n" +
            "\t\t\t\t\t\t\t\"msgBacklog\": 0,\n" +
            "\t\t\t\t\t\t\t\"msgRateExpired\": 0.0,\n" +
            "\t\t\t\t\t\t\t\"msgRateOut\": 0.0,\n" +
            "\t\t\t\t\t\t\t\"msgThroughputOut\": 0.0,\n" +
            "\t\t\t\t\t\t\t\"msgRateRedeliver\": 0.0,\n" +
            "\t\t\t\t\t\t\t\"numberOfEntriesSinceFirstNotAckedMessage\": 1,\n" +
            "\t\t\t\t\t\t\t\"totalNonContiguousDeletedMessagesRange\": 0,\n" +
            "\t\t\t\t\t\t\t\"type\": \"Exclusive\"\n" +
            "\t\t\t\t\t\t}\n" +
            "\t\t\t\t\t},\n" +
            "\t\t\t\t\t\"producerCount\": 1,\n" +
            "\t\t\t\t\t\"averageMsgSize\": 0.0,\n" +
            "\t\t\t\t\t\"msgRateIn\": 0.0,\n" +
            "\t\t\t\t\t\"msgRateOut\": 0.0,\n" +
            "\t\t\t\t\t\"msgThroughputIn\": 0.0,\n" +
            "\t\t\t\t\t\"msgThroughputOut\": 0.0,\n" +
            "\t\t\t\t\t\"storageSize\": 0,\n" +
            "\t\t\t\t\t\"pendingAddEntriesCount\": 0\n" +
            "\t\t\t\t}\n" +
            "\t\t\t}\n" +
            "\t\t}\n" +
            "\t}\n" +
            "}";

    private void checkTopicStatsResult(TopicStatsEntity topicStatsEntity) {
        Assert.assertEquals(topicStatsEntity.getAverageMsgSize(), 0.0, 1);
        Assert.assertEquals(topicStatsEntity.getMsgRateIn(), 0.0, 1);
        Assert.assertEquals(topicStatsEntity.getMsgRateOut(), 0.0, 1);
        Assert.assertEquals(topicStatsEntity.getMsgThroughputIn(), 0.0, 1);
        Assert.assertEquals(topicStatsEntity.getMsgThroughputOut(), 0.0, 1);
        Assert.assertEquals(topicStatsEntity.getStorageSize(), 0, 0);
        Assert.assertEquals(topicStatsEntity.getCluster(), "standalone");
        Assert.assertEquals(topicStatsEntity.getBroker(), "localhost:8080");
        Assert.assertEquals(topicStatsEntity.getTenant(), "public");
        Assert.assertEquals(topicStatsEntity.getNamespace(), "functions");
        Assert.assertEquals(topicStatsEntity.getBundle(), "0x40000000_0x80000000");
        Assert.assertEquals(topicStatsEntity.getPersistent(), "persistent");
        Assert.assertEquals(topicStatsEntity.getTopic(), "metadata");
    }

    private void checkPublisherStatsResult(PublisherStatsEntity publisherStatsEntity) {
        Assert.assertEquals(publisherStatsEntity.getMsgRateIn(), 0.0, 1);
        Assert.assertEquals(publisherStatsEntity.getMsgThroughputIn(), 0.0, 1);
        Assert.assertEquals(publisherStatsEntity.getAverageMsgSize(), 0.0, 1);
        Assert.assertEquals(publisherStatsEntity.getAddress(), "/127.0.0.1:59668");
        Assert.assertEquals(publisherStatsEntity.getProducerId(), 1);
        Assert.assertEquals(publisherStatsEntity.getProducerName(), "standalone-1-1");
        Assert.assertEquals(publisherStatsEntity.getConnectedSince(), "2019-08-10T11:37:22.405+08:00");
        Assert.assertEquals(publisherStatsEntity.getClientVersion(), "2.5.0-SNAPSHOT");
        Assert.assertEquals(publisherStatsEntity.getMetadata(), "{}");
    }

    private void checkReplicationStatsResult(ReplicationStatsEntity replicationStatsEntity) {
        Assert.assertEquals(replicationStatsEntity.getCluster(), "test-replications");
        Assert.assertEquals(replicationStatsEntity.getMsgRateIn(), 123, 0);
        Assert.assertEquals(replicationStatsEntity.getMsgThroughputIn(), 456, 0);
        Assert.assertEquals(replicationStatsEntity.getMsgRateOut(), 456, 0);
        Assert.assertEquals(replicationStatsEntity.getMsgThroughputOut(), 789, 0);
        Assert.assertEquals(replicationStatsEntity.getMsgRateExpired(), 990, 0);
        Assert.assertEquals(replicationStatsEntity.getReplicationBacklog(), 100, 0);
        Assert.assertFalse(replicationStatsEntity.isConnected());
        Assert.assertEquals(replicationStatsEntity.getReplicationDelayInSeconds(), 890, 0);
        Assert.assertEquals(replicationStatsEntity.getInboundConnection(), "test");
        Assert.assertEquals(replicationStatsEntity.getInboundConnectedSince(), "test2");
        Assert.assertEquals(replicationStatsEntity.getOutboundConnection(), "test3");
        Assert.assertEquals(replicationStatsEntity.getOutboundConnectedSince(), "test4");
    }

    private void checkSubscriptionStatsResult(SubscriptionStatsEntity subscriptionStatsEntity) {
        Assert.assertEquals(subscriptionStatsEntity.getSubscription(), "reader-1ddabdb183");
        Assert.assertEquals(subscriptionStatsEntity.getMsgBacklog(), 0.0, 0);
        Assert.assertEquals(subscriptionStatsEntity.getMsgRateOut(), 0.0, 0);
        Assert.assertEquals(subscriptionStatsEntity.getMsgThroughputOut(), 0.0, 0);
        Assert.assertEquals(subscriptionStatsEntity.getMsgRateExpired(), 0.0, 0);
        Assert.assertEquals(subscriptionStatsEntity.getMsgRateRedeliver(), 0.0, 0);
        Assert.assertEquals(subscriptionStatsEntity.getNumberOfEntriesSinceFirstNotAckedMessage(), 1);
        Assert.assertEquals(subscriptionStatsEntity.getTotalNonContiguousDeletedMessagesRange(), 0);
        Assert.assertEquals(subscriptionStatsEntity.getSubscriptionType(), "Exclusive");
    }

    private void checkConsumerStatsResult(ConsumerStatsEntity consumerStatsEntity) {
        Assert.assertEquals(consumerStatsEntity.getConsumer(), "543bd");
        Assert.assertEquals(consumerStatsEntity.getConsumerName(), "543bd");
        Assert.assertEquals(consumerStatsEntity.getAddress(), "/127.0.0.1:59668");
        Assert.assertEquals(consumerStatsEntity.getConnectedSince(), "2019-08-10T11:37:24.306+08:00");
        Assert.assertEquals(consumerStatsEntity.getAvailablePermits(), 1000, 0);
        Assert.assertEquals(consumerStatsEntity.getMsgRateOut(), 0.0, 0);
        Assert.assertEquals(consumerStatsEntity.getMsgThroughputOut(), 0.0, 0);
        Assert.assertEquals(consumerStatsEntity.getMsgRateRedeliver(), 0.0, 0);
        Assert.assertEquals(consumerStatsEntity.getClientVersion(), "2.5.0-SNAPSHOT");
        Assert.assertEquals(consumerStatsEntity.getMetadata(), "{}");
    }

    @Test
    public void convertStatsToDbTest() {
        PowerMockito.mockStatic(HttpUtil.class);
        Map<String, String> header = Maps.newHashMap();
        header.put("Content-Type", "application/json");
        PowerMockito.when(HttpUtil.doGet("http://localhost:8080/admin/v2/clusters", header))
                .thenReturn("[\"standalone\"]");
        PowerMockito.when(HttpUtil.doGet("http://localhost:8080/admin/v2/clusters/standalone", header))
                .thenReturn("{\n" +
                        "\"serviceUrl\" : \"http://tengdeMBP:8080\",\n" +
                        "\"brokerServiceUrl\" : \"pulsar://tengdeMBP:6650\"\n" +
                        "}");
        PowerMockito.when(HttpUtil.doGet("http://localhost:8080/admin/v2/brokers/standalone", header))
                .thenReturn("[\"localhost:8080\"]");
        PowerMockito.when(HttpUtil.doGet("http://localhost:8080/admin/v2/broker-stats/topics", header))
                .thenReturn(testData);
        PowerMockito.when(HttpUtil.doGet("http://localhost:8080/admin/v2/clusters/standalone/failureDomains", header))
                .thenReturn("{}");

        String requestHost = "http://localhost:8080";
        brokerStatsService.convertStatsToDb(1, 1, requestHost);
        Optional<TopicStatsEntity> topicStatsEntity = topicsStatsRepository.findMaxTime();
        TopicStatsEntity topicStatsEntity1 = topicStatsEntity.get();
        Page<TopicStatsEntity> topicStatsEntityPage = topicsStatsRepository.findByClusterBroker(
                1, 1, "standalone", "localhost:8080", topicStatsEntity1.getTimestamp());
        topicStatsEntityPage.getResult().forEach((t) -> {
            checkTopicStatsResult(t);
        });
        Page<SubscriptionStatsEntity> subscriptionStatsEntities = subscriptionsStatsRepository.findByTopicStatsId(
                1, 1, topicStatsEntity1.getTopicStatsId(), topicStatsEntity1.getTimestamp());
        subscriptionStatsEntities.getResult().forEach((subscription) -> {
            checkSubscriptionStatsResult(subscription);
            Page<ConsumerStatsEntity> consumerStatsEntities = consumersStatsRepository.findBySubscriptionStatsId(
                    1, 1, subscription.getSubscriptionStatsId(), subscription.getTimestamp());
            consumerStatsEntities.getResult().forEach((consumer) -> {
                checkConsumerStatsResult(consumer);
            });
        });
        Page<PublisherStatsEntity> publisherStatsEntities = publishersStatsRepository.findByTopicStatsId(
                1, 1, topicStatsEntity1.getTopicStatsId(), topicStatsEntity1.getTimestamp());
        publisherStatsEntities.getResult().forEach((publisher) -> {
            checkPublisherStatsResult(publisher);
        });
        Page<ReplicationStatsEntity> replicationStatsEntities = replicationsStatsRepository.findByTopicStatsId(
                1, 1, topicStatsEntity1.getTopicStatsId(), topicStatsEntity1.getTimestamp());
        replicationStatsEntities.getResult().forEach((replication) -> {
            checkReplicationStatsResult(replication);
        });
        long unixTime = System.currentTimeMillis() / 1000L;
        brokerStatsService.clearStats(unixTime, 0);

        try {
            Thread.sleep(2000);
        } catch (Exception e) {

        }

        Optional<TopicStatsEntity> deleteTopicStatsEntity = topicsStatsRepository.findMaxTime();
        Assert.assertFalse(deleteTopicStatsEntity.isPresent());
        Page<SubscriptionStatsEntity> deleteSubscriptionStatsEntities = subscriptionsStatsRepository.findByTopicStatsId(
                1, 1, topicStatsEntity1.getTopicStatsId(), topicStatsEntity1.getTimestamp());
        Assert.assertEquals(deleteSubscriptionStatsEntities.getTotal(), 0);
        Page<PublisherStatsEntity> deletePublisherStatsEntities = publishersStatsRepository.findByTopicStatsId(
                1, 1, topicStatsEntity1.getTopicStatsId(), topicStatsEntity1.getTimestamp());
        Assert.assertEquals(deletePublisherStatsEntities.getTotal(), 0);
        Page<ReplicationStatsEntity> deleteReplicationStatsEntities = replicationsStatsRepository.findByTopicStatsId(
                1, 1, topicStatsEntity1.getTopicStatsId(), topicStatsEntity1.getTimestamp());
        Assert.assertEquals(deleteReplicationStatsEntities.getTotal(), 0);
    }
}
