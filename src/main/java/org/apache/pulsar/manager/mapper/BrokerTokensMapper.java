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
package org.apache.pulsar.manager.mapper;

import com.github.pagehelper.Page;
import org.apache.pulsar.manager.entity.BrokerTokenEntity;
import org.apache.ibatis.annotations.*;

/**
 * Broker Tokens Mapper
 */
@Mapper
public interface BrokerTokensMapper {

    @Insert("INSERT INTO tokens(role, description, token) VALUES(#{role},#{description},#{token})")
    @Options(useGeneratedKeys=true, keyProperty="tokenId", keyColumn="token_id")
    long save(BrokerTokenEntity brokerTokenEntity);

    @Update("UPDATE tokens set token=#{token}, description=#{description} where role=#{role}")
    void update(BrokerTokenEntity brokerTokenEntity);

    @Select("select token_id as tokenId, role, description, token FROM tokens where role=#{role}")
    BrokerTokenEntity findTokenByRole(String role);

    @Select("SELECT token_id as tokenId, role, description FROM tokens")
    Page<BrokerTokenEntity> findBrokerTokensList();

    @Delete("DELETE FROM tokens WHERE role=#{role}")
    void delete(String role);
}
