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

import com.google.common.collect.Maps;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.client.admin.Topics;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.impl.MessageImpl;
import org.apache.pulsar.common.partition.PartitionedTopicMetadata;
import org.apache.pulsar.manager.PulsarManagerApplication;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(
    classes = {
        PulsarManagerApplication.class,
        HerdDBTestProfile.class
    }
)
@ActiveProfiles("test")
public class TopicsServiceImplTest {

    @MockBean
    private PulsarAdminService pulsarAdminService;

    @Mock
    private Topics topics;

    @Autowired
    private TopicsService topicsService;

    @Autowired
    private TopicsStatsRepository topicsStatsRepository;

    @Test
    public void topicsServiceImplTest() throws PulsarAdminException {
        Mockito.when(pulsarAdminService.topics("http://localhost:8080")).thenReturn(topics);
        Mockito.when(topics.getList("public/default")).thenReturn(
                Arrays.asList(
                        "persistent://public/default/test789",
                        "persistent://public/default/test900-partition-0",
                        "persistent://public/default/test900-partition-1",
                        "persistent://public/default/test900-partition-2"
                )
        );
        Mockito.when(topics.getPartitionedTopicList("public/default")).thenReturn(
                Arrays.asList(
                        "persistent://public/default/test900"
                )
        );
        Mockito.when(topics.getPartitionedTopicMetadata("persistent://public/default/test900")).thenReturn(
                new PartitionedTopicMetadata(3)
        );
        Map<String, Object> topicsMap = topicsService.getTopicsList(
                1, 1, "public", "default", "http://localhost:8080");
        Assert.assertEquals(2, topicsMap.get("total"));
        Assert.assertFalse((Boolean) topicsMap.get("isPage"));
        Assert.assertEquals("[{partitions=0, topic=test789, persistent=persistent}, {partitions=3, topic=test900, persistent=persistent}]", topicsMap.get("topics").toString());
    }

    @Test
    public void getTopicsStatsImplTest() throws Exception {
        String environment = "staging";
        String tenant = "public";
        String namespace = "functions";
        String topic = "metadata";
        String policy = "persistent";

        TopicStatsEntity topicStatsEntity = new TopicStatsEntity();
        topicStatsEntity.setEnvironment(environment);
        topicStatsEntity.setCluster("standalone");
        topicStatsEntity.setBroker("localhost:8080");
        topicStatsEntity.setPersistent(policy);
        topicStatsEntity.setTenant(tenant);
        topicStatsEntity.setNamespace(namespace);
        topicStatsEntity.setTopic(topic);
        topicStatsEntity.setBundle("0x40000000_0x80000000");
        topicStatsEntity.setMsgRateIn(0.0);
        topicStatsEntity.setSubscriptionCount(1);
        topicStatsEntity.setProducerCount(1);
        topicStatsEntity.setTime_stamp((System.currentTimeMillis() / 1000L));
        topicsStatsRepository.save(topicStatsEntity);

        List<Map<String, String>> topics = new ArrayList<>();
        Map<String, String> topicMap = Maps.newHashMap();
        topicMap.put("topic", topic);
        topicMap.put("partitions", "0");
        topics.add(topicMap);

        List<Map<String, Object>> topicsList =  topicsService.getTopicsStatsList(
                environment, tenant, namespace, policy, topics);
        topicsList.forEach((t) -> {
            Assert.assertEquals(0, t.get("partitions"));
            Assert.assertEquals(1, t.get("subscriptions"));
            Assert.assertEquals(0.0, t.get("inMsg"));
            Assert.assertEquals(1, t.get("producers"));
            Assert.assertEquals(policy, t.get("persistent"));
            Assert.assertEquals(topic, t.get("topic"));
        });
    }

    @Test
    public void peekMessagesTest() throws PulsarAdminException {
        Mockito.when(pulsarAdminService.topics("http://localhost:8080")).thenReturn(topics);
        List<Message<byte[]>> messages = new ArrayList<>();
        messages.add(new MessageImpl<byte[]>("persistent://public/default/test", "1:1", Maps.newTreeMap(), "test".getBytes(), Schema.BYTES));
        Mockito.when(topics.peekMessages("persistent://public/default/test", "sub-1", 1)).thenReturn(messages);

        List<Map<String, Object>> result = topicsService.peekMessages(
                "persistent", "public",
                "default", "test",
                "sub-1", 1,
                "http://localhost:8080");
        Assert.assertEquals(1, result.size());
        result.forEach((message) -> {
            Assert.assertEquals(1L, message.get("ledgerId"));
            Assert.assertEquals(1L, message.get("entryId"));
            Assert.assertEquals(false, message.get("batch"));
            Assert.assertEquals(new String("test".getBytes()), new String((byte[]) message.get("data")));
        });
    }
}
