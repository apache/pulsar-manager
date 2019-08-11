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
package com.manager.pulsar.dao;

import com.github.pagehelper.Page;
import com.manager.pulsar.PulsarManagerApplication;
import com.manager.pulsar.entity.NamespaceEntity;
import com.manager.pulsar.entity.NamespacesRepository;
import com.manager.pulsar.entity.TenantEntity;
import com.manager.pulsar.entity.TenantsRepository;
import com.manager.pulsar.profiles.SqliteDBTestProfile;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(
    classes = {
        PulsarManagerApplication.class,
        SqliteDBTestProfile.class
    }
)
@ActiveProfiles("test")
public class NamespacesRepositoryImplTest {

    @Autowired
    private TenantsRepository tenantsRepository;

    @Autowired
    private NamespacesRepository namespacesRepository;

    public void initNamespaceEntity(NamespaceEntity namespacesEntity) {
        namespacesEntity.setNamespaceId(1);
        namespacesEntity.setTenant("test-namespace-public");
        namespacesEntity.setNamespace("test-namespace-default");
        namespacesEntity.setAuthPolicies("{\"namespace_auth\":{},\"destination_auth\":{},\"subscription_auth_roles\":{}}");
        namespacesEntity.setReplicationClusters("[\"pulsar-cluster-1\"]");
        namespacesEntity.setBoundaries("[\"0x00000000\",\"0x40000000\",\"0x80000000\",\"0xc0000000\",\"0xffffffff\"]");
        namespacesEntity.setNumBundles(4);
        namespacesEntity.setBacklogQuota("{}");
        namespacesEntity.setTopicDispatchRate("{}");
        namespacesEntity.setSubscriptionDispatchRate("{}");
        namespacesEntity.setReplicatorDispatchRate("{}");
        namespacesEntity.setClusterSubscribeRate("{}");
        namespacesEntity.setLatencyStatsSampleRate("{}");
        namespacesEntity.setMessageTtlInSeconds(0);
        namespacesEntity.setDeleted(true);
        namespacesEntity.setEncryptionRequired(false);
        namespacesEntity.setSubscriptionAuthMode("None");
        namespacesEntity.setMaxProducersPerTopic(0);
        namespacesEntity.setMaxConsumersPerTopic(0);
        namespacesEntity.setMaxConsumersPerSubscription(0);
        namespacesEntity.setCompactionThreshold(0);
        namespacesEntity.setOffloadThreshold(-1);
        namespacesEntity.setSchemaAutoApdateCompatibilityStrategy("FULL");
        namespacesEntity.setBookkeeperAckQuorum(0);
        namespacesEntity.setSchemaValidationEnforced(false);
        namespacesEntity.setManagedLedgerMaxMarkDeleteRate(0);
        namespacesEntity.setBookkeeperEnsemble(0);
        namespacesEntity.setBookkeeperWriteQuorum(0);
    }

    public void checkResult(Page<NamespaceEntity> namespacesEntityPage) {
        long total = namespacesEntityPage.getTotal();
        Assert.assertEquals(total, 1);
        namespacesEntityPage.getResult().forEach((result) -> {
            Assert.assertEquals(result.getNamespaceId(), 1);
            Assert.assertEquals(result.getTenant(), "test-namespace-public");
            Assert.assertEquals(result.getNamespace(), "test-namespace-default");
            Assert.assertEquals(result.getNumBundles(), 4);
            Assert.assertEquals(result.getBoundaries(),
                    "[\"0x00000000\",\"0x40000000\",\"0x80000000\",\"0xc0000000\",\"0xffffffff\"]");
            Assert.assertEquals(result.getAuthPolicies(),
                    "{\"namespace_auth\":{},\"destination_auth\":{},\"subscription_auth_roles\":{}}");
            Assert.assertEquals(result.getTopicDispatchRate(), "{}");
            Assert.assertEquals(result.getBacklogQuota(), "{}");
            Assert.assertEquals(result.getSubscriptionAuthMode(), "None");
            Assert.assertEquals(result.getReplicatorDispatchRate(), "{}");
            Assert.assertEquals(result.getClusterSubscribeRate(), "{}");
            Assert.assertEquals(result.getLatencyStatsSampleRate(), "{}");
            Assert.assertEquals(result.getMessageTtlInSeconds(), 0);
            Assert.assertEquals(result.isDeleted(), true);
            Assert.assertEquals(result.isEncryptionRequired(), false);
            Assert.assertEquals(result.getSubscriptionAuthMode(), "None");
            Assert.assertEquals(result.getMaxProducersPerTopic(), 0);
            Assert.assertEquals(result.getMaxConsumersPerTopic(), 0);
            Assert.assertEquals(result.getMaxConsumersPerSubscription(), 0);
            Assert.assertEquals(result.getCompactionThreshold(), 0);
            Assert.assertEquals(result.getOffloadThreshold(), -1);
            Assert.assertEquals(result.getSchemaAutoApdateCompatibilityStrategy(), "FULL");
            Assert.assertEquals(result.getBookkeeperAckQuorum(), 0);
            Assert.assertEquals(result.isSchemaValidationEnforced(), false);
            Assert.assertEquals(result.getBookkeeperEnsemble(), 0);
            Assert.assertEquals(result.getManagedLedgerMaxMarkDeleteRate(), 0, 0);
            Assert.assertEquals(result.getBookkeeperEnsemble(), 0);
        });
    }

    public void checkDeleteResult(Page<NamespaceEntity> namespacesEntityPage) {
        long total = namespacesEntityPage.getTotal();
        Assert.assertEquals(total, 0);
    }

    @Before
    public void setup() {
        prepareTenant();
    }

    @After
    public void clear() {
        clearTenant();
    }

    public void prepareTenant() {
        TenantEntity tenantsEntity = new TenantEntity(
                1, "test-namespace-public",  "testrole", "testCluster");
        tenantsRepository.save(tenantsEntity);
    }

    public void clearTenant() {
        tenantsRepository.removeByTenant("test-namespace-public");
    }

    @Test
    public void getNamespacesList() {
        NamespaceEntity namespacesEntity = new NamespaceEntity();
        initNamespaceEntity(namespacesEntity);
        namespacesRepository.save(namespacesEntity);
        Page<NamespaceEntity> namespacesEntityPage = namespacesRepository.getNamespacesList(1, 2);
        namespacesEntityPage.count(true);
        checkResult(namespacesEntityPage);
        namespacesEntityPage.getResult().forEach((result) -> {
            namespacesRepository.remove(result.getTenant(), result.getNamespace());
        });
        Page<NamespaceEntity> deleteNamespace = namespacesRepository.getNamespacesList(1, 2);
        deleteNamespace.count(true);
        checkDeleteResult(deleteNamespace);
    }

    @Test
    public void getNamespaceByTenantOrNamespace() {
        NamespaceEntity namespacesEntity = new NamespaceEntity();
        initNamespaceEntity(namespacesEntity);
        namespacesRepository.save(namespacesEntity);
        String tenant = "test-namespace-public";
        Page<NamespaceEntity> namespacesEntityPageByTenant = namespacesRepository.
                findByTenantOrNamespace(1, 2, tenant);
        namespacesEntityPageByTenant.count(true);
        checkResult(namespacesEntityPageByTenant);
        String namespace = "test-namespace-default";
        Page<NamespaceEntity> namespacesEntityPageByNamespace = namespacesRepository.
                findByTenantOrNamespace(1, 2, namespace);
        namespacesEntityPageByNamespace.count(true);
        checkResult(namespacesEntityPageByNamespace);
        namespacesEntityPageByNamespace.getResult().forEach((result) -> {
            namespacesRepository.remove(result.getTenant(), result.getNamespace());
        });
        Page<NamespaceEntity> deleteNamespace = namespacesRepository.getNamespacesList(1, 2);
        deleteNamespace.count(true);
        checkDeleteResult(deleteNamespace);
    }
}
