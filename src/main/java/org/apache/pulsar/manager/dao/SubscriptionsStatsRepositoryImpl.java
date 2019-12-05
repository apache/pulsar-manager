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
package org.apache.pulsar.manager.dao;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.pulsar.manager.entity.SubscriptionStatsEntity;
import org.apache.pulsar.manager.entity.SubscriptionsStatsRepository;
import org.apache.pulsar.manager.mapper.SubscriptionsStatsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SubscriptionsStatsRepositoryImpl implements SubscriptionsStatsRepository {

    private final SubscriptionsStatsMapper subscriptionsStatsMapper;

    @Autowired
    public SubscriptionsStatsRepositoryImpl(SubscriptionsStatsMapper subscriptionsStatsMapper) {
        this.subscriptionsStatsMapper = subscriptionsStatsMapper;
    }

    public long save(SubscriptionStatsEntity subscriptionStatsEntity) {
        subscriptionsStatsMapper.save(subscriptionStatsEntity);
        return subscriptionStatsEntity.getSubscriptionStatsId();
    }

    public Page<SubscriptionStatsEntity> findByTopicStatsId(Integer pageNum, Integer pageSize,
                                                     long topicStatsId, long timestamp) {
        PageHelper.startPage(pageNum, pageSize);
        return subscriptionsStatsMapper.findByTopicStatsId(topicStatsId, timestamp);
    }

    public void remove(long timestamp, long timeInterval) {
        subscriptionsStatsMapper.delete(timestamp - timeInterval);
    }
}
