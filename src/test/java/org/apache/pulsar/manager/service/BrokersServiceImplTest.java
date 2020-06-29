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

import org.apache.pulsar.client.admin.Brokers;
import org.apache.pulsar.client.admin.Clusters;
import org.apache.pulsar.common.policies.data.FailureDomain;
import org.apache.pulsar.manager.PulsarManagerApplication;
import org.apache.pulsar.manager.profiles.HerdDBTestProfile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

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

@RunWith(SpringRunner.class)
@SpringBootTest(
    classes = {
        PulsarManagerApplication.class,
        HerdDBTestProfile.class
    }
)
@ActiveProfiles("test")
public class BrokersServiceImplTest {

    @MockBean
    private PulsarAdminService pulsarAdminService;

    @Autowired
    private BrokersService brokersService;

    @Mock
    private Clusters clusters;

    @Mock
    private Brokers brokers;

    @Test
    public void brokersServiceTest() throws Exception{
        FailureDomain fdomain = new FailureDomain();
        fdomain.setBrokers(new HashSet<String>(Arrays.asList("broker-1:8080")));
        Map<String, FailureDomain> fMap = new HashMap<>();
        fMap.put("fdomain-1",fdomain);
        Mockito.when(pulsarAdminService.clusters("http://localhost:8080")).thenReturn(clusters);
        Mockito.when(clusters.getFailureDomains("standalone"))
                .thenReturn(fMap);

        Mockito.when(pulsarAdminService.brokers("http://localhost:8080")).thenReturn(brokers);
        Mockito.when(brokers.getActiveBrokers("standalone"))
                .thenReturn(Arrays.asList("broker-1:8080"));

        Map<String, Object> result = brokersService.getBrokersList(
                1, 1, "standalone", "http://localhost:8080");
        Assert.assertEquals(1, result.get("total"));
        Assert.assertEquals("[{failureDomain=[fdomain-1], broker=broker-1:8080}]", result.get("data").toString());
        Assert.assertEquals(1, result.get("pageSize"));
    }
}
