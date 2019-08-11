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

import com.google.common.collect.Maps;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PowerMockIgnore( {"javax.management.*", "javax.net.ssl.*"})
@PrepareForTest(HttpUtil.class)
@SpringBootTest
public class TopicsServiceImplTest {

    @Autowired
    private TopicsService topicsService;

    @Autowired
    private BrokerStatsService brokerStatsService;

    private final String topics = "[" +
            "\"persistent://public/default/test789\"," +
            "\"persistent://public/default/test900-partition-0\"," +
            "\"persistent://public/default/test900-partition-1\"," +
            "\"persistent://public/default/test900-partition-2\"]";

    private final String partitionedTopics = "[\"persistent://public/default/test900\"]";

    @Test
    public void topicsServiceImplTest() {
        PowerMockito.mockStatic(HttpUtil.class);
        Map<String, String> header = Maps.newHashMap();
        header.put("Content-Type", "application/json");
        PowerMockito.when(HttpUtil.doGet("http://localhost:8080/admin/v2/persistent/public/default", header))
                .thenReturn(topics);
        PowerMockito.when(HttpUtil.doGet(
                "http://localhost:8080/admin/v2/persistent/public/default/partitioned", header))
                .thenReturn(partitionedTopics);
        PowerMockito.when(HttpUtil.doGet(
                "http://localhost:8080/admin/v2/persistent/public/default/test900/partitions", header))
                .thenReturn("{\"partitions\":3}");
        Map<String, Object> topicsMap = topicsService.getTopicsList(
                1, 1, "public", "default", "http://localhost:8080");
        Assert.assertEquals(topicsMap.get("total"), 2);
        Assert.assertFalse((Boolean) topicsMap.get("isPage"));
        Assert.assertEquals(topicsMap.get("topics").toString(),
                "[{partitions=0, topic=test789, persistent=persistent}, {partitions=3, topic=test900, persistent=persistent}]");
    }

    @Test
    public void getTopicsStatsImpleTest() {
        PowerMockito.mockStatic(HttpUtil.class);
        Map<String, String> header = Maps.newHashMap();
        header.put("Content-Type", "application/json");
        String requestHost = "http://localhost:8080";
        PowerMockito.when(HttpUtil.doGet("http://localhost:8080/admin/v2/clusters", header))
                .thenReturn("[\"standalone\"]");

        PowerMockito.when(HttpUtil.doGet("http://localhost:8080/admin/v2/brokers/standalone", header))
                .thenReturn("[\"localhost:8080\"]");
        PowerMockito.when(HttpUtil.doGet("http://localhost:8080/admin/v2/broker-stats/topics", header))
                .thenReturn(BrokerStatsServiceImplTest.testData);
        PowerMockito.when(HttpUtil.doGet("http://localhost:8080/admin/v2/clusters/standalone/failureDomains", header))
                .thenReturn("{}");
        PowerMockito.when(HttpUtil.doGet("http://localhost:8080/admin/v2/clusters/standalone", header))
                .thenReturn("{\n" +
                        "\"serviceUrl\" : \"http://tengdeMBP:8080\",\n" +
                        "\"brokerServiceUrl\" : \"pulsar://tengdeMBP:6650\"\n" +
                        "}");
        brokerStatsService.convertStatsToDb(1, 1, requestHost);

        List<Map<String, String>> topics = new ArrayList<>();
        Map<String, String> topic = Maps.newHashMap();
        topic.put("topic", "metadata");
        topic.put("partitions", "0");
        topics.add(topic);

        List<Map<String, Object>> topicsList =  topicsService.getTopicsStatsList(
                "http://localhost:8080", "public", "functions", "persistent", topics);
        topicsList.forEach((t) -> {
            Assert.assertEquals(t.get("partitions"), 0);
            Assert.assertEquals(t.get("subscriptions"), 1);
            Assert.assertEquals(t.get("inMsg"), 0.0);
            Assert.assertEquals(t.get("producers"), 1);
            Assert.assertEquals(t.get("persistent"), "persistent");
            Assert.assertEquals(t.get("topic"), "metadata");
        });
    }
}
