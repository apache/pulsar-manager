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
import org.apache.ibatis.annotations.Param;
import org.apache.pulsar.manager.entity.ConsumerStatsEntity;
import org.apache.pulsar.manager.entity.ConsumersStatsRepository;
import org.apache.pulsar.manager.mapper.ConsumerStatsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ConsumersStatsRepositoryImpl implements ConsumersStatsRepository {

    private final ConsumerStatsMapper consumerStatsMapper;

    @Autowired
    public ConsumersStatsRepositoryImpl(ConsumerStatsMapper consumerStatsMapper) {
        this.consumerStatsMapper = consumerStatsMapper;
    }


    public long save(ConsumerStatsEntity consumerStatsEntity) {
        consumerStatsMapper.save(consumerStatsEntity);
        return consumerStatsEntity.getConsumerStatsId();
    }

    public Page<ConsumerStatsEntity> findByTopicStatsId(Integer pageNum, Integer pageSize,
                                                 long topicStatsId, long timestamp) {
        PageHelper.startPage(pageNum, pageSize);
        return consumerStatsMapper.findByTopicStatsId(topicStatsId, timestamp);
    }

    public Page<ConsumerStatsEntity> findBySubscriptionStatsId(Integer pageNum, Integer pageSize,
                                                               long subscriptionStatsId, long timestamp) {
        PageHelper.startPage(pageNum, pageSize);
        return consumerStatsMapper.findBySubscriptionStatsId(subscriptionStatsId, timestamp);
    }

    public Page<ConsumerStatsEntity> findByReplicationStatsId(Integer pageNum, Integer pageSize,
                                                              long replicationStatsId, long timestamp) {
        PageHelper.startPage(pageNum, pageSize);
        return consumerStatsMapper.findByReplicationStatsId(replicationStatsId, timestamp);
    }

    public List<ConsumerStatsEntity> findByMultiTopicStatsId(List<Long> topicStatsIdList, long timestamp) {
        return consumerStatsMapper.findByMultiTopicStatsId(topicStatsIdList, timestamp);
    }

    public void remove(long timestamp, long timeInterval) {
        consumerStatsMapper.delete(timestamp - timeInterval);
    }

}
