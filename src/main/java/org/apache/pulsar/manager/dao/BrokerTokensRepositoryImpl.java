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
import org.apache.pulsar.manager.entity.BrokerTokenEntity;
import org.apache.pulsar.manager.entity.BrokerTokensRepository;
import org.apache.pulsar.manager.mapper.BrokerTokensMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class BrokerTokensRepositoryImpl implements BrokerTokensRepository {

    private final BrokerTokensMapper brokerTokensMapper;

    @Autowired
    public BrokerTokensRepositoryImpl(BrokerTokensMapper brokerTokensMapper) {
        this.brokerTokensMapper = brokerTokensMapper;
    }

    public long save(BrokerTokenEntity brokerTokenEntity) {
        brokerTokensMapper.save(brokerTokenEntity);
        return brokerTokenEntity.getTokenId();
    }

    public void update(BrokerTokenEntity brokerTokenEntity) {
        brokerTokensMapper.update(brokerTokenEntity);
    }

    public void remove(String role) {
        brokerTokensMapper.delete(role);
    }

    public Optional<BrokerTokenEntity> findTokenByRole(String role) {
        return Optional.ofNullable(brokerTokensMapper.findTokenByRole(role));
    }

    public Page<BrokerTokenEntity> getBrokerTokensList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return brokerTokensMapper.findBrokerTokensList();
    }
}
