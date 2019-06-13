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
import com.manager.pulsar.entity.NamespacesEntity;
import com.manager.pulsar.entity.NamespacesRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MyBatisNamespacesRepositoryTest {

    @Autowired
    private NamespacesRepository namespacesRepository;

    public void initNamespaceEntity(NamespacesEntity namespacesEntity) {
        namespacesEntity.setTenant("public");
        namespacesEntity.setNamespace("default");
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
    }

    @Test
    public void getNamespacesList() {
        NamespacesEntity namespacesEntity = new NamespacesEntity();
        initNamespaceEntity(namespacesEntity);
        namespacesRepository.save(namespacesEntity);
        Page<NamespacesEntity> namespacesEntityPage = namespacesRepository.getNamespacesList(1, 2);
        namespacesEntityPage.count(true);
        long total = namespacesEntityPage.getTotal();
        Assert.assertEquals(total, 1);
        namespacesEntityPage.getResult().forEach((result) -> {
            Assert.assertEquals(result.getTenant(), "public");
            Assert.assertEquals(result.getNamespace(), "default");
            Assert.assertEquals(result.getNumBundles(), 4);
        });
        namespacesEntityPage.getResult().forEach((result) -> {
            namespacesRepository.remove(result);
        });
    }
}
