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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.eq;

import com.google.gson.Gson;
import org.apache.pulsar.manager.PulsarManagerApplication;
import org.apache.pulsar.manager.entity.EnvironmentEntity;
import org.apache.pulsar.manager.entity.EnvironmentsRepository;
import org.apache.pulsar.manager.profiles.HerdDBTestProfile;
import org.apache.pulsar.manager.utils.HttpUtil;
import java.util.NoSuchElementException;
import org.apache.pulsar.common.policies.data.ClusterData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Unit test {@link EnvironmentCacheService}.
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PowerMockIgnore( {"javax.*", "sun.*", "com.sun.*", "org.xml.*", "org.w3c.*"})
@PrepareForTest(HttpUtil.class)
@SpringBootTest(
    classes = {
        PulsarManagerApplication.class,
        HerdDBTestProfile.class
    }
)
@ActiveProfiles("test")
public class EnvironmentCacheServiceImplTest {

    @Autowired
    private EnvironmentsRepository environmentsRepository;

    @Autowired
    private EnvironmentCacheService environmentCache;

    private EnvironmentEntity environment1;
    private EnvironmentEntity environment2;
    private EnvironmentEntity emptyEnvironment;
    private final String cluster1_0_name = "cluster1_0";
    private ClusterData cluster1_0;
    private final String cluster2_0_name = "cluster2_0";
    private ClusterData cluster2_0;
    private final String cluster2_1_name = "cluster2_1";
    private ClusterData cluster2_1;

    @Before
    public void setup() {
        // setup 3 environments
        environment1 = new EnvironmentEntity();
        environment1.setBroker("http://cluster1_0:8080");
        environment1.setName("environment1");
        environment2 = new EnvironmentEntity();
        environment2.setBroker("http://cluster2_0:8080");
        environment2.setName("environment2");
        emptyEnvironment = new EnvironmentEntity();
        emptyEnvironment.setName("emptyEnvironment");
        emptyEnvironment.setBroker("http://empty_env:8080");

        // setup 3 clusters
        cluster1_0 = new ClusterData();
        cluster1_0.setServiceUrl("http://cluster1_0:8080");

        cluster2_0 = new ClusterData();
        cluster2_0.setServiceUrl("http://cluster2_0:8080");

        cluster2_1 = new ClusterData();
        cluster2_1.setServiceUrl("http://cluster2_1:8080");

        PowerMockito.mockStatic(HttpUtil.class);
        // empty environment
        PowerMockito.when(HttpUtil.doGet(
            eq(emptyEnvironment.getBroker() + "/admin/v2/clusters"),
            anyMap()
        )).thenReturn("[]");

        // environment 1
        PowerMockito.when(HttpUtil.doGet(
            eq(cluster1_0.getServiceUrl() + "/admin/v2/clusters"),
            anyMap()
        )).thenReturn("[\""+ cluster1_0_name + "\"]");
        PowerMockito.when(HttpUtil.doGet(
            eq(cluster1_0.getServiceUrl() + "/admin/v2/clusters/" + cluster1_0_name),
            anyMap()
        )).thenReturn(new Gson().toJson(cluster1_0));

        // environment 2
        PowerMockito.when(HttpUtil.doGet(
            eq(cluster2_0.getServiceUrl() + "/admin/v2/clusters"),
            anyMap()
        )).thenReturn("[\""+ cluster2_0_name + "\", \"" + cluster2_1_name + "\"]");
        PowerMockito.when(HttpUtil.doGet(
            eq(cluster2_0.getServiceUrl() + "/admin/v2/clusters/" + cluster2_0_name),
            anyMap()
        )).thenReturn(new Gson().toJson(cluster2_0));
        PowerMockito.when(HttpUtil.doGet(
            eq(cluster2_0.getServiceUrl() + "/admin/v2/clusters/" + cluster2_1_name),
            anyMap()
        )).thenReturn(new Gson().toJson(cluster2_1));
    }

    @After
    public void teardown() {
        environmentsRepository.remove(environment1.getName());
        environmentsRepository.remove(environment2.getName());
        environmentsRepository.remove(emptyEnvironment.getName());
    }

    @Test
    public void testEmptyEnvironments() {
        environmentCache.reloadEnvironments();

        try {
            environmentCache.getServiceUrl(environment1.getName(), null);
            fail("Should fail to get service url if environments is empty");
        } catch (NoSuchElementException e) {
            // expected
        }
    }

    @Test
    public void testEmptyEnvironment() {
        environmentsRepository.save(emptyEnvironment);

        try {
            environmentCache.getServiceUrl(emptyEnvironment.getName(), cluster1_0_name);
            fail("Should fail to get service url if environments is empty");
        } catch (RuntimeException e) {
            // expected
            assertEquals(
                "No cluster '" + cluster1_0_name + "' found in environment '"
                    + emptyEnvironment.getName() + "'",
                e.getMessage());
        }
    }

    @Test
    public void testReloadEnvironments() {
        environmentsRepository.save(emptyEnvironment);
        environmentsRepository.save(environment1);
        environmentsRepository.save(environment2);

        // without cluster

        assertEquals(cluster1_0.getServiceUrl(),
            environmentCache.getServiceUrl(environment1.getName(), null));
        assertEquals(cluster2_0.getServiceUrl(),
            environmentCache.getServiceUrl(environment2.getName(), null));

        // with cluster

        assertEquals(cluster1_0.getServiceUrl(),
            environmentCache.getServiceUrl(environment1.getName(), cluster1_0_name));
        assertEquals(cluster2_0.getServiceUrl(),
            environmentCache.getServiceUrl(environment2.getName(), cluster2_0_name));
        assertEquals(cluster2_1.getServiceUrl(),
            environmentCache.getServiceUrl(environment2.getName(), cluster2_1_name));
    }

    @Test
    public void testReloadEnvironmentsAddNewEnvironmentsAndRemoveOldEnvironments() {
        environmentsRepository.save(environment1);

        environmentCache.reloadEnvironments();
        assertEquals(cluster1_0.getServiceUrl(),
            environmentCache.getServiceUrl(environment1.getName(), null));
        try {
            environmentCache.getServiceUrl(environment2.getName(), null);
            fail("Should fail to get service url if environments is empty");
        } catch (NoSuchElementException e) {
            // expected
        }

        environmentsRepository.save(environment2);
        environmentsRepository.remove(environment1.getName());
        environmentCache.reloadEnvironments();

        assertEquals(cluster2_0.getServiceUrl(),
            environmentCache.getServiceUrl(environment2.getName(), null));
        try {
            environmentCache.getServiceUrl(environment1.getName(), null);
            fail("Should fail to get service url if environments is empty");
        } catch (NoSuchElementException e) {
            // expected
        }
    }

    @Test
    public void testReloadEnvironmentsAddNewClusterAndRemoveOldCluster() {
        environmentsRepository.save(environment2);
        environmentCache.reloadEnvironments();
        assertEquals(cluster2_0.getServiceUrl(),
            environmentCache.getServiceUrl(environment2.getName(), cluster2_0_name));
        assertEquals(cluster2_1.getServiceUrl(),
            environmentCache.getServiceUrl(environment2.getName(), cluster2_1_name));

        PowerMockito.when(HttpUtil.doGet(
            eq(cluster2_0.getServiceUrl() + "/admin/v2/clusters"),
            anyMap()
        )).thenReturn("[\""+ cluster2_0_name + "\"]");
        PowerMockito.when(HttpUtil.doGet(
            eq(cluster2_0.getServiceUrl() + "/admin/v2/clusters/" + cluster2_1_name),
            anyMap()
        )).thenReturn(null);

        environmentCache.reloadEnvironments();
        assertEquals(cluster2_0.getServiceUrl(),
            environmentCache.getServiceUrl(environment2.getName(), cluster2_0_name));
        try {
            assertEquals(cluster2_1.getServiceUrl(),
                environmentCache.getServiceUrl(environment2.getName(), cluster2_1_name));
            fail("Should fail to get service url if cluster is not found");
        } catch (RuntimeException e) {
            // expected
            assertEquals(
                "No cluster '" + cluster2_1_name + "' found in environment '"
                    + environment2.getName() + "'",
                e.getMessage());
        }
    }

}
