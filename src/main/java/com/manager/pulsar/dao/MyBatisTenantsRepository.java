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

import com.manager.pulsar.entity.TenantsRepository;
import com.manager.pulsar.entity.TenantsEntity;
import com.manager.pulsar.mapper.TenantsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MyBatisTenantsRepository implements TenantsRepository {
    private final TenantsMapper tenantsMapper;

    @Autowired
    public MyBatisTenantsRepository(TenantsMapper tenantsMapper) { this.tenantsMapper = tenantsMapper; }

    @Override
    public Optional<TenantsEntity> findById(Integer id) {
      return Optional.ofNullable(tenantsMapper.findById(id));
    }

    @Override
    public Optional<TenantsEntity> findByName(String tenantName) {
        return Optional.ofNullable(tenantsMapper.findByName(tenantName));
    }

    @Override
    public Integer save(TenantsEntity tenantsEntity) {
        tenantsMapper.insert(tenantsEntity);
        return tenantsEntity.getTenantId();
    }

    @Override
    public void remove(TenantsEntity tenantsEntity) {
        tenantsMapper.delete(tenantsEntity);
    }
}
