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
import org.apache.pulsar.client.admin.BrokerStats;
import org.apache.pulsar.client.admin.Namespaces;
import org.apache.pulsar.client.admin.PulsarAdminException;
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

import java.util.Arrays;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(
    classes = {
        PulsarManagerApplication.class,
        HerdDBTestProfile.class
    }
)
@ActiveProfiles("test")
public class NamespacesServiceImplTest {

    @Autowired
    private NamespacesService namespacesService;

    @MockBean
    private PulsarAdminService pulsarAdminService;

    @MockBean
    private TopicsService topicsService;

    @Mock
    private Namespaces namespaces;

    @Autowired
    private TopicsStatsRepository topicsStatsRepository;

    @Test
    public void namespaceServiceImplTest() throws PulsarAdminException {
        Mockito.when(pulsarAdminService.namespaces("http://localhost:8080")).thenReturn(namespaces);
        Mockito.when(namespaces.getNamespaces("public")).thenReturn(Arrays.asList("public/default"));
        Map<String, Object> topics = Maps.newHashMap();
        topics.put("total", 1);
        Mockito.when(topicsService.getTopicsList(0, 0, "public", "default", "http://localhost:8080"))
                .thenReturn(topics);

        Map<String, Object> result = namespacesService.getNamespaceList(1, 1, "public", "http://localhost:8080");
        Assert.assertEquals(1, result.get("total"));
        Assert.assertFalse((Boolean) result.get("isPage"));
        Assert.assertEquals("[{topics=1, namespace=default}]", result.get("data").toString());
    }

    @Test
    public void getNamespaceStatsTest() {
        String environment = "staging";
        String tenant = "public";
        String namespace = "functions";

        TopicStatsEntity topicStatsEntity = new TopicStatsEntity();
        topicStatsEntity.setEnvironment(environment);
        topicStatsEntity.setCluster("standalone");
        topicStatsEntity.setBroker("localhost:8080");
        topicStatsEntity.setPersistent("persistent");
        topicStatsEntity.setTenant(tenant);
        topicStatsEntity.setNamespace(namespace);
        topicStatsEntity.setTopic("metadata");
        topicStatsEntity.setBundle("0x40000000_0x80000000");
        topicStatsEntity.setMsgRateIn(0.0);
        topicStatsEntity.setSubscriptionCount(1);
        topicStatsEntity.setProducerCount(1);
        topicStatsEntity.setTime_stamp((System.currentTimeMillis() / 1000L));
        topicsStatsRepository.save(topicStatsEntity);

        Map<String, Object> namespaceStats = namespacesService.getNamespaceStats(
                tenant, namespace, environment);
        Assert.assertEquals(0.0, namespaceStats.get("outMsg"));
        Assert.assertEquals(0.0, namespaceStats.get("inMsg"));
        Assert.assertEquals(0.0, namespaceStats.get("msgThroughputIn"));
        Assert.assertEquals(0.0, namespaceStats.get("msgThroughputOut"));
    }
}
