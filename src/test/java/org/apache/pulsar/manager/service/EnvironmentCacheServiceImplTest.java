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

import org.apache.pulsar.client.admin.Clusters;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.manager.PulsarManagerApplication;
import org.apache.pulsar.manager.entity.EnvironmentEntity;
import org.apache.pulsar.manager.entity.EnvironmentsRepository;
import org.apache.pulsar.manager.profiles.HerdDBTestProfile;

import java.util.Arrays;
import java.util.NoSuchElementException;

import org.apache.pulsar.common.policies.data.ClusterData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Unit test {@link EnvironmentCacheService}.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
    classes = {
        PulsarManagerApplication.class,
        HerdDBTestProfile.class
    }
)
@ActiveProfiles("test")
public class EnvironmentCacheServiceImplTest {

    @Autowired
    private EnvironmentCacheService environmentCacheService;

    @Autowired
    private EnvironmentsRepository environmentsRepository;

    @MockBean
    private PulsarAdminService pulsarAdminService;

    @Mock
    private Clusters emptyClusters;

    @Mock
    private Clusters cluster1Clusters;

    @Mock
    private Clusters cluster2Clusters;

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
    public void setup() throws PulsarAdminException {
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

        Mockito.when(pulsarAdminService.clusters(emptyEnvironment.getBroker())).thenReturn(emptyClusters);
        Mockito.when(emptyClusters.getClusters()).thenReturn(Arrays.asList());

        Mockito.when(pulsarAdminService.clusters(cluster1_0.getServiceUrl())).thenReturn(cluster1Clusters);
        Mockito.when(cluster1Clusters.getClusters()).thenReturn(Arrays.asList(cluster1_0_name));
        Mockito.when(cluster1Clusters.getCluster(cluster1_0_name)).thenReturn(cluster1_0);

        Mockito.when(pulsarAdminService.clusters(cluster2_0.getServiceUrl())).thenReturn(cluster2Clusters);
        Mockito.when(cluster2Clusters.getClusters()).thenReturn(Arrays.asList(cluster2_0_name, cluster2_1_name));
        Mockito.when(cluster2Clusters.getCluster(cluster2_0_name)).thenReturn(cluster2_0);
        Mockito.when(cluster2Clusters.getCluster(cluster2_1_name)).thenReturn(cluster2_1);
    }

    @After
    public void teardown() {
        environmentsRepository.remove(environment1.getName());
        environmentsRepository.remove(environment2.getName());
        environmentsRepository.remove(emptyEnvironment.getName());
    }

    @Test
    public void testEmptyEnvironments() {
        environmentCacheService.reloadEnvironments();

        try {
            environmentCacheService.getServiceUrl(environment1.getName(), null);
            fail("Should fail to get service url if environments is empty");
        } catch (NoSuchElementException e) {
            // expected
        }
    }

    @Test
    public void testEmptyEnvironment() throws PulsarAdminException {
        environmentsRepository.save(emptyEnvironment);
        PulsarAdminException pulsarAdminException = new PulsarAdminException("Cluster does not exist");
        Mockito.when(emptyClusters.getCluster(cluster1_0_name)).thenThrow(pulsarAdminException);
        environmentCacheService.reloadEnvironments();

        try {
            environmentCacheService.getServiceUrl(emptyEnvironment.getName(), cluster1_0_name);
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
            environmentCacheService.getServiceUrl(environment1.getName(), null));
        assertEquals(cluster2_0.getServiceUrl(),
            environmentCacheService.getServiceUrl(environment2.getName(), null));

        // with cluster

        assertEquals(cluster1_0.getServiceUrl(),
            environmentCacheService.getServiceUrl(environment1.getName(), cluster1_0_name));
        assertEquals(cluster2_0.getServiceUrl(),
            environmentCacheService.getServiceUrl(environment2.getName(), cluster2_0_name));
        assertEquals(cluster2_1.getServiceUrl(),
            environmentCacheService.getServiceUrl(environment2.getName(), cluster2_1_name));
    }

    @Test
    public void testReloadEnvironmentsAddNewEnvironmentsAndRemoveOldEnvironments() {
        environmentsRepository.save(environment1);
        environmentCacheService.reloadEnvironments();

        assertEquals(cluster1_0.getServiceUrl(),
            environmentCacheService.getServiceUrl(environment1.getName(), null));
        try {
            environmentCacheService.getServiceUrl(environment2.getName(), null);
            fail("Should fail to get service url if environments is empty");
        } catch (NoSuchElementException e) {
            // expected
        }

        environmentsRepository.save(environment2);
        environmentsRepository.remove(environment1.getName());
        environmentCacheService.reloadEnvironments();

        assertEquals(cluster2_0.getServiceUrl(),
            environmentCacheService.getServiceUrl(environment2.getName(), null));
        try {
            environmentCacheService.getServiceUrl(environment1.getName(), null);
            fail("Should fail to get service url if environments is empty");
        } catch (NoSuchElementException e) {
            // expected
        }
    }

    @Test
    public void testReloadEnvironmentsAddNewClusterAndRemoveOldCluster() throws PulsarAdminException {
        environmentsRepository.save(environment2);
        environmentCacheService.reloadEnvironments();
        assertEquals(cluster2_0.getServiceUrl(),
            environmentCacheService.getServiceUrl(environment2.getName(), cluster2_0_name));
        assertEquals(cluster2_1.getServiceUrl(),
            environmentCacheService.getServiceUrl(environment2.getName(), cluster2_1_name));

        Mockito.when(cluster2Clusters.getClusters()).thenReturn(Arrays.asList(cluster2_0_name));
        PulsarAdminException pulsarAdminException = new PulsarAdminException("Cluster does not exist");
        Mockito.when(cluster2Clusters.getCluster(cluster2_1_name)).thenThrow(pulsarAdminException);

        environmentCacheService.reloadEnvironments();
        assertEquals(cluster2_0.getServiceUrl(),
            environmentCacheService.getServiceUrl(environment2.getName(), cluster2_0_name));
        try {
            assertEquals(cluster2_1.getServiceUrl(),
                environmentCacheService.getServiceUrl(environment2.getName(), cluster2_1_name));
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
