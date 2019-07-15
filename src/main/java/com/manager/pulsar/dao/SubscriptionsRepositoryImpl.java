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
import com.github.pagehelper.PageHelper;
import com.manager.pulsar.entity.SubscriptionEntity;
import com.manager.pulsar.entity.SubscriptionsRepository;
import com.manager.pulsar.mapper.SubscriptionsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Subscriptions implements SubscriptionsRepository for support crud.
 */
@Repository
public class SubscriptionsRepositoryImpl implements SubscriptionsRepository {

    private SubscriptionsMapper subscriptionsMapper;

    @Autowired
    public SubscriptionsRepositoryImpl(SubscriptionsMapper subscriptionsMapper) {
        this.subscriptionsMapper = subscriptionsMapper;
    }

    @Override
    public Optional<SubscriptionEntity> findById(Long subscriptionId) {
        return Optional.ofNullable(subscriptionsMapper.findById(subscriptionId));
    }

    @Override
    public Optional<SubscriptionEntity> findByTenantNamespaceTopicSubscription(
            String tenant, String namespace, String topic, String subscription) {
        return Optional.ofNullable(subscriptionsMapper.findByTenantNamespaceTopicSubscription(
                tenant, namespace, topic, subscription));
    }

    @Override
    public Page<SubscriptionEntity> findByTenantOrNamespaceOrTopicOrSubscription(Integer pageNum, Integer pageSize, String tnts) {
        PageHelper.startPage(pageNum, pageSize);
        Page<SubscriptionEntity> subscriptionsEntities = subscriptionsMapper.findByTenantOrNamespaceOrTopicOrSubscription(tnts);
        return subscriptionsEntities;
    }

    @Override
    public Page<SubscriptionEntity> findBySubscription(Integer pageNum, Integer pageSize, String subscription) {
        PageHelper.startPage(pageNum, pageSize);
        Page<SubscriptionEntity> subscriptionsEntities = subscriptionsMapper.findBySubscription(subscription);
        return subscriptionsEntities;
    }

    @Override
    public Page<SubscriptionEntity> getSubscriptionsList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<SubscriptionEntity> subscriptionsEntities = subscriptionsMapper.findSubscriptionsList();
        return subscriptionsEntities;
    }

    @Override
    public void remove(String tenant, String namespace, String topic, String subscription) {
        subscriptionsMapper.deleteByTenantNamespaceTopic(tenant, namespace, topic, subscription);
    }

    @Override
    public void update(SubscriptionEntity subscriptionsEntity) {
        subscriptionsMapper.update(subscriptionsEntity);
    }

    @Override
    public void save(SubscriptionEntity subscriptionsEntity) {
        subscriptionsMapper.insert(subscriptionsEntity);
    }
}