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
package io.streamnative.pulsar.manager.dao;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.streamnative.pulsar.manager.entity.TenantsRepository;
import io.streamnative.pulsar.manager.entity.TenantEntity;
import io.streamnative.pulsar.manager.mapper.TenantsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class TenantsRepositoryImpl implements TenantsRepository {
    private final TenantsMapper tenantsMapper;

    @Autowired
    public TenantsRepositoryImpl(TenantsMapper tenantsMapper) { this.tenantsMapper = tenantsMapper; }

    @Override
    public Optional<TenantEntity> findById(long tenantId) {
        return Optional.ofNullable(tenantsMapper.findById(tenantId));
    }

    @Override
    public Optional<TenantEntity> findByName(String tenant) {
        return Optional.ofNullable(tenantsMapper.findByName(tenant));
    }

    @Override
    public Page<TenantEntity> getTenantsList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TenantEntity> tenantsEntities = tenantsMapper.getTenantsList();
        return tenantsEntities;
    }

    @Override
    public void save(TenantEntity tenantsEntity) {
        tenantsMapper.insert(tenantsEntity);
    }


    @Override
    public void remove(Long tenantId) {
        tenantsMapper.delete(tenantId);
    }

    @Override
    public void removeByTenant(String tenant) {
        tenantsMapper.deleteByTenant(tenant);
    }

    @Override
    public void updateByTenant(String tenant) {
        tenantsMapper.updateByTenant(tenant);
    }
}
