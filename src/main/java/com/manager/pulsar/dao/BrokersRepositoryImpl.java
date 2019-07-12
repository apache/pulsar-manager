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
import com.manager.pulsar.entity.BrokersEntity;
import com.manager.pulsar.entity.BrokersRepository;
import com.manager.pulsar.mapper.BrokersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * BrokersRepositoryImpl implements BrokersRepository for operation crud of broker.
 */
@Repository
public class BrokersRepositoryImpl implements BrokersRepository {

    private final BrokersMapper brokersMapper;

    @Autowired
    public BrokersRepositoryImpl(BrokersMapper brokersMapper) {
        this.brokersMapper = brokersMapper;
    }

    @Override
    public Optional<BrokersEntity> findById(long brokerId) {
        return Optional.ofNullable(brokersMapper.findById(brokerId));
    }

    @Override
    public Optional<BrokersEntity> findByBroker(String broker) {
        return Optional.ofNullable(brokersMapper.findByBroker(broker));
    }

    @Override
    public Page<BrokersEntity> getBrokersList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<BrokersEntity> brokersEntities = brokersMapper.getBrokersList();
        return brokersEntities;
    }

    @Override
    public void save(BrokersEntity brokersEntity) {
        brokersMapper.insert(brokersEntity);
    }

    @Override
    public void remove(String broker) {
        brokersMapper.deleteByBroker(broker);
    }

    @Override
    public void update(BrokersEntity brokersEntity) {
        brokersMapper.update(brokersEntity);
    }

}
