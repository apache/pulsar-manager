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

import org.apache.pulsar.client.admin.Clusters;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.common.policies.data.ClusterData;
import org.apache.pulsar.manager.PulsarManagerApplication;
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
import java.util.HashMap;
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
public class ClustersServiceImplTest {

    @Autowired
    private ClustersService clustersService;

    @MockBean
    private BrokersService brokersService;

    @MockBean
    private PulsarAdminService pulsarAdminService;

    @Mock
    private Clusters clusters;

    @Test
    public void clusterServiceImplTest() throws PulsarAdminException {
        Mockito.when(pulsarAdminService.clusters("http://localhost:8080")).thenReturn(clusters);
        Mockito.when(pulsarAdminService.clusters("http://localhost:8080").getClusters()).thenReturn(Arrays.asList("standalone"));
        ClusterData standaloneClusterData = new ClusterData("http://broker-1:8080", null, "pulsar://broker-1:6650", null);
        Mockito.when(pulsarAdminService.clusters("http://localhost:8080").getCluster("standalone")).thenReturn(standaloneClusterData);

        Map<String, Object> brokerEntity = Maps.newHashMap();
        brokerEntity.put("broker", "broker-1:8080");
        brokerEntity.put("failureDomain", null);
        List<Map<String, Object>> brokersArray = new ArrayList<>();
        brokersArray.add(brokerEntity);
        Map<String, Object> brokersMap = new HashMap<>();
        brokersMap.put("isPage", false);
        brokersMap.put("total", brokersArray.size());
        brokersMap.put("data", brokersArray);
        brokersMap.put("pageNum", 1);
        brokersMap.put("pageSize", brokersArray.size());
        Mockito.when(brokersService.getBrokersList(1, 1, "standalone", "http://localhost:8080")).thenReturn(brokersMap);

        Map<String, Object> result = clustersService.getClustersList(1, 1, "http://localhost:8080", null);
        Assert.assertEquals("[{cluster=standalone, serviceUrlTls=null, brokers=1, serviceUrl=http://broker-1:8080, " +
                        "brokerServiceUrlTls=null, brokerServiceUrl=pulsar://broker-1:6650}]", result.get("data").toString());
        Assert.assertEquals(1, result.get("total"));
        Assert.assertEquals(1, result.get("pageSize"));
    }

    @Test
    public void getClusterByAnyBroker() throws PulsarAdminException  {
        Mockito.when(pulsarAdminService.clusters("http://localhost:8080")).thenReturn(clusters);
        Mockito.when(pulsarAdminService.clusters("http://localhost:8080").getClusters()).thenReturn(Arrays.asList("standalone"));

        List<String> clusterList = clustersService.getClusterByAnyBroker("http://localhost:8080");
        Assert.assertEquals("standalone", clusterList.get(0));
    }
}
