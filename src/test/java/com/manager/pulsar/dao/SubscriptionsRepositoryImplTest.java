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
import com.manager.pulsar.entity.SubscriptionEntity;
import com.manager.pulsar.entity.SubscriptionsRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Subscriptions test class
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SubscriptionsRepositoryImplTest {

    @Autowired
    private SubscriptionsRepository subscriptionsRepository;

    private void initSubscriptionsEntity(SubscriptionEntity subscriptionsEntity) {
        subscriptionsEntity.setSubscriptionId(1);
        subscriptionsEntity.setTopicId(1);
        subscriptionsEntity.setTenant("public");
        subscriptionsEntity.setNamespace("default");
        subscriptionsEntity.setPersistent(true);
        subscriptionsEntity.setTopic("test-topic");
        subscriptionsEntity.setSubscription("test-subscription");
    }

    private void checkResut(Page<SubscriptionEntity> subscriptionsEntityPage) {
        long total = subscriptionsEntityPage.getTotal();
        Assert.assertEquals(total, 1);
        subscriptionsEntityPage.getResult().forEach((result) -> {
            Assert.assertEquals(result.getSubscriptionId(), 1);
            Assert.assertEquals(result.getTopicId(), 1);
            Assert.assertEquals(result.getTenant(), "public");
            Assert.assertEquals(result.getNamespace(), "default");
            Assert.assertEquals(result.getTopic(), "test-topic");
            Assert.assertEquals(result.isPersistent(), true);
            Assert.assertEquals(result.getSubscription(), "test-subscription");
        });
    }

    private void checkDeleteResult(Page<SubscriptionEntity> subscriptionsEntityPage) {
        long total = subscriptionsEntityPage.getTotal();
        Assert.assertEquals(total, 0);
    }

    @Test
    public void getSubscriptionsList() {
        SubscriptionEntity subscriptionsEntity = new SubscriptionEntity();
        initSubscriptionsEntity(subscriptionsEntity);
        subscriptionsRepository.save(subscriptionsEntity);
        Page<SubscriptionEntity> subscriptionsEntityPage = subscriptionsRepository
                .getSubscriptionsList(1, 2);
        subscriptionsEntityPage.count(true);
        checkResut(subscriptionsEntityPage);
        subscriptionsEntityPage.getResult().forEach((result) -> {
            subscriptionsRepository.remove(
                    result.getTenant(), result.getNamespace(),
                    result.getTopic(), result.getSubscription(), result.isPersistent());
        });
        Page<SubscriptionEntity> deleteSubscription = subscriptionsRepository.getSubscriptionsList(1, 2);
        deleteSubscription.count(true);
        checkDeleteResult(deleteSubscription);
    }

    @Test
    public void getSubscriptionByTenantOrNamespaceOrTopicOrSubscription() {
        SubscriptionEntity subscriptionsEntity = new SubscriptionEntity();
        initSubscriptionsEntity(subscriptionsEntity);
        subscriptionsRepository.save(subscriptionsEntity);
        String tenant = "public";
        Page<SubscriptionEntity> subscriptionsEntityPageByTenant = subscriptionsRepository
                .findByTenantOrNamespaceOrTopicOrSubscription(1, 2, tenant);
        subscriptionsEntityPageByTenant.count(true);
        checkResut(subscriptionsEntityPageByTenant);
        String namespace = "default";
        Page<SubscriptionEntity> subscriptionsEntityPageByNamespace = subscriptionsRepository
                .findByTenantOrNamespaceOrTopicOrSubscription(1, 2, namespace);
        subscriptionsEntityPageByNamespace.count(true);
        checkResut(subscriptionsEntityPageByNamespace);
        String topic = "test-topic";
        Page<SubscriptionEntity> subscriptionsEntityPageByTopic = subscriptionsRepository
                .findByTenantOrNamespaceOrTopicOrSubscription(1, 2, topic);
        subscriptionsEntityPageByTopic.count(true);
        checkResut(subscriptionsEntityPageByTopic);
        subscriptionsEntityPageByTopic.getResult().forEach((result) -> {
            subscriptionsRepository.remove(result.getTenant(), result.getNamespace(),
                    result.getTopic(), result.getSubscription(), result.isPersistent());
        });

    }
}