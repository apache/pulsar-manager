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
import org.apache.commons.lang3.StringUtils;
import org.apache.pulsar.manager.PulsarManagerApplication;
import org.apache.pulsar.manager.entity.*;
import org.apache.pulsar.manager.profiles.HerdDBTestProfile;
import org.apache.pulsar.manager.utils.HttpUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PowerMockIgnore( {"javax.*", "sun.*", "com.sun.*", "org.xml.*", "org.w3c.*"})
@PrepareForTest(HttpUtil.class)
@TestPropertySource(locations= "classpath:test-bookie.properties")
@SpringBootTest(
    classes = {
        PulsarManagerApplication.class,
        HerdDBTestProfile.class
    }
)
@ActiveProfiles("test")
public class DashboardServiceImplTest {

    @Autowired
    DashboardService dashboardService;

    @Autowired
    TopicsStatsRepository topicsStatsRepository;

    @Autowired
    ConsumersStatsRepository consumersStatsRepository;

    @Autowired
    TenantsRepository tenantsRepository;

    @Autowired
    NamespacesRepository namespacesRepository;

    @Value("${backend.jwt.token}")
    private static String pulsarJwtToken;

    @Test
    public void getDashboardStatsTest() {
        List<String> environmentList = Arrays.asList("environment0", "environment1");
        List<String> clusterList = Arrays.asList("cluster0");
        List<String> brokerList = Arrays.asList("broker1", "broker2");
        List<String> tenantList = Arrays.asList("tenant0", "tenant1", "tenant2");
        List<String> namespaceList = Arrays.asList("namespace0", "namespace1", "namespace2", "namespace3");
        List<Long> timestampList = Arrays.asList(System.currentTimeMillis() / 1000L, System.currentTimeMillis() / 1000L - 10);
        int producerPerTopic = 1;
        int consumerPerTopic = 1;

        PowerMockito.mockStatic(HttpUtil.class);
        Map<String, String> header = Maps.newHashMap();
        header.put("Content-Type", "application/json");
        if (StringUtils.isNotBlank(pulsarJwtToken)) {
            header.put("Authorization", String.format("Bearer %s", pulsarJwtToken));
        }
        PowerMockito.when(HttpUtil.doGet("http://localhost:8050/api/v1/bookie/list_bookies?type=rw&print_hostnames=true", header))
                .thenReturn("{\"192.168.2.116:3181\" : \"192.168.2.116\"}");
        PowerMockito.when(HttpUtil.doGet("http://localhost:8080/admin/v2/brokers/standalone", header))
                .thenReturn("{ }");
        PowerMockito.when(HttpUtil.doGet("http://localhost:8050/api/v1/bookie/list_bookie_info", header))
                .thenReturn("{\"192.168.2.116:3181\" : \": {Free: 48920571904(48.92GB), Total: 250790436864(250.79GB)}," +
                        "\",\"ClusterInfo: \" : \"{Free: 48920571904(48.92GB), Total: 250790436864(250.79GB)}\" }");

        long topicStatsId = 0L;
        for (String tenant: tenantList) {
            TenantEntity tenantEntity = new TenantEntity();
            tenantEntity.setEnvironmentName(environmentList.get(0));
            tenantEntity.setTenant(tenant);
            tenantsRepository.save(tenantEntity);
            for (String namespace: namespaceList) {
                NamespaceEntity namespaceEntity = new NamespaceEntity();
                namespaceEntity.setTenant(tenant);
                namespaceEntity.setNamespace(namespace);
                namespacesRepository.save(namespaceEntity);
            }
        }
        for (String environment: environmentList) {
            for (Long timestamp : timestampList) {
                for (String cluster : clusterList) {
                    for (String broker : brokerList) {
                        TopicStatsEntity topicStatsEntity = new TopicStatsEntity();
                        topicStatsEntity.setEnvironment(environment);
                        topicStatsEntity.setPersistent("persistent");
                        topicStatsEntity.setBundle("neutral");
                        topicStatsEntity.setCluster(cluster);
                        topicStatsEntity.setBroker(broker);
                        topicStatsEntity.setTenant(tenantList.get(0));
                        topicStatsEntity.setNamespace(namespaceList.get(0));
                        topicStatsEntity.setTopic("neutral");
                        topicStatsEntity.setProducerCount(producerPerTopic);
                        topicStatsEntity.setTime_stamp(timestamp);
                        topicsStatsRepository.save(topicStatsEntity);
                        topicStatsId++;
                        for (int i = 0; i < consumerPerTopic; i++) {
                            ConsumerStatsEntity consumerStatsEntity = new ConsumerStatsEntity();
                            consumerStatsEntity.setConsumer("neutral");
                            consumerStatsEntity.setTopicStatsId(topicStatsId);
                            consumerStatsEntity.setTime_stamp(timestamp);
                            consumersStatsRepository.save(consumerStatsEntity);
                        }
                    }
                }
            }
        }

        long topicCount = clusterList.size() * brokerList.size();
        Map<String, Object> dashboardStats = dashboardService.getDashboardStats(Arrays.asList(environmentList.get(0)));
        Assert.assertEquals(clusterList.size(), dashboardStats.get("totalClusterCount"));
        Assert.assertEquals(brokerList.size(), dashboardStats.get("totalBrokerCount"));
        Assert.assertEquals(tenantList.size(), dashboardStats.get("totalTenantCount"));
        Assert.assertEquals(tenantList.size() * namespaceList.size(), dashboardStats.get("totalNamespaceCount"));
        Assert.assertEquals(topicCount, dashboardStats.get("totalTopicCount"));
        Assert.assertEquals(topicCount * producerPerTopic, dashboardStats.get("totalProducerCount"));
        Assert.assertEquals(topicCount * consumerPerTopic, dashboardStats.get("totalConsumerCount"));
        Assert.assertEquals(1, dashboardStats.get("totalBookieCount"));
    }
}
