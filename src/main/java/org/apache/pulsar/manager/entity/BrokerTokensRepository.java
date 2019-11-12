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
package org.apache.pulsar.manager.entity;

import com.github.pagehelper.Page;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrokerTokensRepository {

    long save(BrokerTokenEntity brokerTokenEntity);

    void update(BrokerTokenEntity brokerTokenEntity);

    void remove(String role);

    Optional<BrokerTokenEntity> findTokenByRole(String role);

    Page<BrokerTokenEntity> getBrokerTokensList(Integer pageNum, Integer pageSize);
}
