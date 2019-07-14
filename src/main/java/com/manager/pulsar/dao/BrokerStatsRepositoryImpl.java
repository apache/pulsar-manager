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
import com.manager.pulsar.entity.BrokerStatsEntity;
import com.manager.pulsar.entity.BrokerStatsRepository;
import com.manager.pulsar.mapper.BrokerStatsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * BrokerStatsRepositoryImpl implements BrokerStatsRepository for operation crud of broker.
 */
@Repository
public class BrokerStatsRepositoryImpl implements BrokerStatsRepository {

    private final BrokerStatsMapper brokerStatsMapper;

    @Autowired
    public BrokerStatsRepositoryImpl(BrokerStatsMapper brokerStatsMapper) {
        this.brokerStatsMapper = brokerStatsMapper;
    }


    @Override
    public Optional<BrokerStatsEntity> findByBroker(String broker) {
        return Optional.ofNullable(brokerStatsMapper.findByBroker(broker));
    }

    @Override
    public Page<BrokerStatsEntity> getBrokerStatsList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<BrokerStatsEntity> brokersEntities = brokerStatsMapper.getBrokerStatsList();
        return brokersEntities;
    }

    @Override
    public void save(BrokerStatsEntity brokerStatsEntity) {
        brokerStatsMapper.insert(brokerStatsEntity);
    }

    @Override
    public void remove(String broker) {
        brokerStatsMapper.delete(broker);
    }

    @Override
    public void update(BrokerStatsEntity brokerStatsEntity) {
        brokerStatsMapper.update(brokerStatsEntity);
    }
}
