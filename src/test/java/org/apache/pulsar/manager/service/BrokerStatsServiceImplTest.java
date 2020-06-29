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
package org.apache.pulsar.manager.service;

import com.github.pagehelper.Page;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.pulsar.client.admin.BrokerStats;
import org.apache.pulsar.manager.PulsarManagerApplication;
import org.apache.pulsar.manager.entity.ConsumerStatsEntity;
import org.apache.pulsar.manager.entity.ConsumersStatsRepository;
import org.apache.pulsar.manager.entity.PublisherStatsEntity;
import org.apache.pulsar.manager.entity.PublishersStatsRepository;
import org.apache.pulsar.manager.entity.ReplicationsStatsRepository;
import org.apache.pulsar.manager.entity.SubscriptionStatsEntity;
import org.apache.pulsar.manager.entity.SubscriptionsStatsRepository;
import org.apache.pulsar.manager.entity.ReplicationStatsEntity;
import org.apache.pulsar.manager.entity.TopicStatsEntity;
import org.apache.pulsar.manager.entity.TopicsStatsRepository;
import org.apache.pulsar.manager.profiles.HerdDBTestProfile;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = {
                PulsarManagerApplication.class,
                HerdDBTestProfile.class
        }
)
@ActiveProfiles("test")
public class BrokerStatsServiceImplTest {
    @MockBean
    private PulsarAdminService pulsarAdminService;

    @Autowired
    private BrokerStatsService brokerStatsService;

    @MockBean
    private BrokersService brokersService;

    @Mock
    private BrokerStats stats;

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
        Assert.assertEquals(0.0, topicStatsEntity.getAverageMsgSize(), 1);
        Assert.assertEquals(0.0, topicStatsEntity.getMsgRateIn(), 1);
        Assert.assertEquals(0.0, topicStatsEntity.getMsgRateOut(), 1);
        Assert.assertEquals(0.0, topicStatsEntity.getMsgThroughputIn(), 1);
        Assert.assertEquals(0.0, topicStatsEntity.getMsgThroughputOut(), 1);
        Assert.assertEquals(0, topicStatsEntity.getStorageSize(), 0);
        Assert.assertEquals("standalone", topicStatsEntity.getCluster());
        Assert.assertEquals("localhost:8080", topicStatsEntity.getBroker());
        Assert.assertEquals("public", topicStatsEntity.getTenant());
        Assert.assertEquals("functions", topicStatsEntity.getNamespace());
        Assert.assertEquals("0x40000000_0x80000000", topicStatsEntity.getBundle());
        Assert.assertEquals("persistent", topicStatsEntity.getPersistent());
        Assert.assertEquals("metadata", topicStatsEntity.getTopic());
    }

    private void checkPublisherStatsResult(PublisherStatsEntity publisherStatsEntity) {
        Assert.assertEquals(0.0, publisherStatsEntity.getMsgRateIn(), 1);
        Assert.assertEquals(0.0, publisherStatsEntity.getMsgThroughputIn(), 1);
        Assert.assertEquals(0.0, publisherStatsEntity.getAverageMsgSize(), 1);
        Assert.assertEquals("/127.0.0.1:59668", publisherStatsEntity.getAddress());
        Assert.assertEquals(1, publisherStatsEntity.getProducerId());
        Assert.assertEquals("standalone-1-1", publisherStatsEntity.getProducerName());
        Assert.assertEquals("2019-08-10T11:37:22.405+08:00", publisherStatsEntity.getConnectedSince());
        Assert.assertEquals("2.5.0-SNAPSHOT", publisherStatsEntity.getClientVersion());
        Assert.assertEquals("{}", publisherStatsEntity.getMetadata());
    }

    private void checkReplicationStatsResult(ReplicationStatsEntity replicationStatsEntity) {
        Assert.assertEquals("test-replications", replicationStatsEntity.getCluster());
        Assert.assertEquals(123, replicationStatsEntity.getMsgRateIn(), 0);
        Assert.assertEquals(456, replicationStatsEntity.getMsgThroughputIn(), 0);
        Assert.assertEquals(456, replicationStatsEntity.getMsgRateOut(), 0);
        Assert.assertEquals(789, replicationStatsEntity.getMsgThroughputOut(), 0);
        Assert.assertEquals(990, replicationStatsEntity.getMsgRateExpired(), 0);
        Assert.assertEquals(100, replicationStatsEntity.getReplicationBacklog(), 0);
        Assert.assertFalse(replicationStatsEntity.isConnected());
        Assert.assertEquals(890, replicationStatsEntity.getReplicationDelayInSeconds(), 0);
        Assert.assertEquals("test", replicationStatsEntity.getInboundConnection());
        Assert.assertEquals("test2", replicationStatsEntity.getInboundConnectedSince());
        Assert.assertEquals("test3", replicationStatsEntity.getOutboundConnection());
        Assert.assertEquals("test4", replicationStatsEntity.getOutboundConnectedSince());
    }

    private void checkSubscriptionStatsResult(SubscriptionStatsEntity subscriptionStatsEntity) {
        Assert.assertEquals("reader-1ddabdb183", subscriptionStatsEntity.getSubscription());
        Assert.assertEquals(0.0, subscriptionStatsEntity.getMsgBacklog(), 0);
        Assert.assertEquals(0.0, subscriptionStatsEntity.getMsgRateOut(), 0);
        Assert.assertEquals(0.0, subscriptionStatsEntity.getMsgThroughputOut(), 0);
        Assert.assertEquals(0.0, subscriptionStatsEntity.getMsgRateExpired(), 0);
        Assert.assertEquals( 0.0, subscriptionStatsEntity.getMsgRateRedeliver(), 0);
        Assert.assertEquals(1, subscriptionStatsEntity.getNumberOfEntriesSinceFirstNotAckedMessage());
        Assert.assertEquals(0, subscriptionStatsEntity.getTotalNonContiguousDeletedMessagesRange());
        Assert.assertEquals("Exclusive", subscriptionStatsEntity.getSubscriptionType());
    }

    private void checkConsumerStatsResult(ConsumerStatsEntity consumerStatsEntity) {
        Assert.assertEquals("543bd", consumerStatsEntity.getConsumer());
        Assert.assertEquals("/127.0.0.1:59668", consumerStatsEntity.getAddress());
        Assert.assertEquals("2019-08-10T11:37:24.306+08:00", consumerStatsEntity.getConnectedSince());
        Assert.assertEquals(1000, consumerStatsEntity.getAvailablePermits(), 0);
        Assert.assertEquals(0.0, consumerStatsEntity.getMsgRateOut(), 0);
        Assert.assertEquals(0.0, consumerStatsEntity.getMsgThroughputOut(), 0);
        Assert.assertEquals(0.0, consumerStatsEntity.getMsgRateRedeliver(), 0);
        Assert.assertEquals("2.5.0-SNAPSHOT", consumerStatsEntity.getClientVersion());
        Assert.assertEquals("{}", consumerStatsEntity.getMetadata());
    }

    @Test
    public void convertStatsToDbTest() throws Exception {
        String environment = "staging";
        String cluster = "standalone";
        String serviceUrl = "http://localhost:8080";

        Map<String, Object> brokersMap = new HashMap<>();
        List<Map<String, Object>> brokersArray = new ArrayList<>();
        Map<String, Object> brokerEntity = Maps.newHashMap();
        brokerEntity.put("broker", "localhost:8080");
        brokersArray.add(brokerEntity);
        brokersMap.put("data", brokersArray);
        Mockito.when(brokersService.getBrokersList(0,0, cluster, serviceUrl))
                .thenReturn(brokersMap);
        Mockito.when(pulsarAdminService.brokerStats(serviceUrl)).thenReturn(stats);
        JsonObject data = new Gson().fromJson(testData, JsonObject.class);
        Mockito.when(stats.getTopics())
                .thenReturn(data);

        brokerStatsService.collectStatsToDB(
            System.currentTimeMillis() / 1000,
            environment,
            cluster,
            serviceUrl
        );
        Optional<TopicStatsEntity> topicStatsEntity = topicsStatsRepository.findMaxTime();
        TopicStatsEntity topicStatsEntity1 = topicStatsEntity.get();
        Page<TopicStatsEntity> topicStatsEntityPage = topicsStatsRepository.findByClusterBroker(
                1, 1, environment, cluster, "localhost:8080", topicStatsEntity1.getTime_stamp());
        topicStatsEntityPage.getResult().forEach((t) -> {
            checkTopicStatsResult(t);
        });
        Page<SubscriptionStatsEntity> subscriptionStatsEntities = subscriptionsStatsRepository.findByTopicStatsId(
                1, 1, topicStatsEntity1.getTopicStatsId(), topicStatsEntity1.getTime_stamp());
        subscriptionStatsEntities.getResult().forEach((subscription) -> {
            checkSubscriptionStatsResult(subscription);
            Page<ConsumerStatsEntity> consumerStatsEntities = consumersStatsRepository.findBySubscriptionStatsId(
                    1, 1, subscription.getSubscriptionStatsId(), subscription.getTime_stamp());
            consumerStatsEntities.getResult().forEach((consumer) -> {
                checkConsumerStatsResult(consumer);
            });
        });
        Page<PublisherStatsEntity> publisherStatsEntities = publishersStatsRepository.findByTopicStatsId(
                1, 1, topicStatsEntity1.getTopicStatsId(), topicStatsEntity1.getTime_stamp());
        publisherStatsEntities.getResult().forEach((publisher) -> {
            checkPublisherStatsResult(publisher);
        });
        Page<ReplicationStatsEntity> replicationStatsEntities = replicationsStatsRepository.findByTopicStatsId(
                1, 1, topicStatsEntity1.getTopicStatsId(), topicStatsEntity1.getTime_stamp());
        replicationStatsEntities.getResult().forEach((replication) -> {
            checkReplicationStatsResult(replication);
        });

        try {
            Thread.sleep(1000);
        } catch (Exception e) {

        }

        long unixTime = System.currentTimeMillis() / 1000L;
        brokerStatsService.clearStats(unixTime, 0);

        Optional<TopicStatsEntity> deleteTopicStatsEntity = topicsStatsRepository.findMaxTime();
        Assert.assertFalse(deleteTopicStatsEntity.isPresent());

        Page<SubscriptionStatsEntity> deleteSubscriptionStatsEntities = subscriptionsStatsRepository.findByTopicStatsId(
                1, 1, topicStatsEntity1.getTopicStatsId(), topicStatsEntity1.getTime_stamp());
        Assert.assertEquals(0, deleteSubscriptionStatsEntities.getTotal());
        Page<PublisherStatsEntity> deletePublisherStatsEntities = publishersStatsRepository.findByTopicStatsId(
                1, 1, topicStatsEntity1.getTopicStatsId(), topicStatsEntity1.getTime_stamp());
        Assert.assertEquals(0, deletePublisherStatsEntities.getTotal());
        Page<ReplicationStatsEntity> deleteReplicationStatsEntities = replicationsStatsRepository.findByTopicStatsId(
                1, 1, topicStatsEntity1.getTopicStatsId(), topicStatsEntity1.getTime_stamp());
        Assert.assertEquals(0, deleteReplicationStatsEntities.getTotal());
    }

    @Test
    public void findByMultiTenantOrMultiNamespace() throws Exception {
        String environment = "staging";
        String cluster = "standalone";
        String serviceUrl = "http://localhost:8080";

        Map<String, Object> brokersMap = new HashMap<>();
        List<Map<String, Object>> brokersArray = new ArrayList<>();
        Map<String, Object> brokerEntity = Maps.newHashMap();
        brokerEntity.put("broker", "localhost:8080");
        brokersArray.add(brokerEntity);
        brokersMap.put("data", brokersArray);
        Mockito.when(brokersService.getBrokersList(0,0, cluster, serviceUrl))
                .thenReturn(brokersMap);
        Mockito.when(pulsarAdminService.brokerStats(serviceUrl)).thenReturn(stats);
        JsonObject data = new Gson().fromJson(testData, JsonObject.class);
        Mockito.when(stats.getTopics())
                .thenReturn(data);

        brokerStatsService.collectStatsToDB(
                System.currentTimeMillis() / 1000,
                environment,
                cluster,
                serviceUrl
        );
        Optional<TopicStatsEntity> topicStatsEntity = topicsStatsRepository.findMaxTime();
        TopicStatsEntity topicStatsEntity1 = topicStatsEntity.get();
        ArrayList<String> tenantList = new ArrayList<>();
        tenantList.add(topicStatsEntity1.getTenant());
        Page<TopicStatsEntity> tenantCountPage = brokerStatsService.findByMultiTenant(
                1, 1, environment, tenantList, topicStatsEntity1.getTimestamp());
        tenantCountPage.count(true);
        Page<TopicStatsEntity> tenantAllCountPage = brokerStatsService.findByMultiTenant(
                1, (int)tenantCountPage.getTotal(), environment, tenantList, topicStatsEntity1.getTimestamp());


        tenantAllCountPage.getResult().forEach((result) -> {
            Assert.assertEquals(0.0, result.getAverageMsgSize(), 1);
            Assert.assertEquals(0.0, result.getMsgRateIn(), 1);
            Assert.assertEquals(0.0, result.getMsgRateOut(), 1);
            Assert.assertEquals(0.0, result.getMsgThroughputIn(), 1);
            Assert.assertEquals(0.0, result.getMsgThroughputOut(), 1);
            Assert.assertEquals(0, result.getStorageSize(), 0);
        });

        ArrayList<String> namespaceList = new ArrayList<>();
        namespaceList.add(topicStatsEntity1.getNamespace());
        Page<TopicStatsEntity> namespaceCountPage = brokerStatsService.findByMultiNamespace(
                1, 1, environment, topicStatsEntity1.getTenant(),
                namespaceList, topicStatsEntity1.getTimestamp());
        namespaceCountPage.count(true);
        Page<TopicStatsEntity> namespaceAllCountPage = brokerStatsService.findByMultiNamespace(
                1, (int)namespaceCountPage.getTotal(), environment, topicStatsEntity1.getTenant(),
                namespaceList, topicStatsEntity1.getTimestamp());


        namespaceAllCountPage.getResult().forEach((result) -> {
            Assert.assertEquals(0.0, result.getAverageMsgSize(), 1);
            Assert.assertEquals(0.0, result.getMsgRateIn(), 1);
            Assert.assertEquals(0.0, result.getMsgRateOut(), 1);
            Assert.assertEquals(0.0, result.getMsgThroughputIn(), 1);
            Assert.assertEquals(0.0, result.getMsgThroughputOut(), 1);
            Assert.assertEquals(0, result.getStorageSize(), 0);
        });

        long unixTime = System.currentTimeMillis() / 1000L;
        brokerStatsService.clearStats(unixTime, 0);
    }
}
