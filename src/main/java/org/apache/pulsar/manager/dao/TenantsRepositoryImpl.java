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
import org.apache.pulsar.manager.entity.TenantsRepository;
import org.apache.pulsar.manager.entity.TenantEntity;
import org.apache.pulsar.manager.mapper.TenantsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TenantsRepositoryImpl implements TenantsRepository {
    private final TenantsMapper tenantsMapper;

    @Autowired
    public TenantsRepositoryImpl(TenantsMapper tenantsMapper) { this.tenantsMapper = tenantsMapper; }

    @Override
    public Optional<TenantEntity> findByName(String tenant) {
        return Optional.ofNullable(tenantsMapper.findByName(tenant));
    }

    @Override
    public Optional<TenantEntity> findByTenantId(long tenantId) {
        return Optional.ofNullable(tenantsMapper.findByTenantId(tenantId));
    }

    @Override
    public Page<TenantEntity> getTenantsList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TenantEntity> tenantsEntities = tenantsMapper.getTenantsList();
        return tenantsEntities;
    }

    @Override
    public Page<TenantEntity> findByMultiId(Integer pageNum, Integer pageSize, List<Long> tenantIdList) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TenantEntity> tenantsEntities = tenantsMapper.findByMultiId(tenantIdList);
        return tenantsEntities;
    }

    @Override
    public List<TenantEntity> findByMultiId(List<Long> tenantIdList) {
        List<TenantEntity> tenantsEntities = tenantsMapper.findAllByMultiId(tenantIdList);
        return tenantsEntities;
    }

    @Override
    public List<TenantEntity> findByMultiEnvironmentName(List<String> environmentNameList) {
        List<TenantEntity> tenantEntities = tenantsMapper.findAllByMultiEnvironmentName(environmentNameList);
        return tenantEntities;
    }

    @Override
    public long save(TenantEntity tenantsEntity) {
        tenantsMapper.insert(tenantsEntity);
        return tenantsEntity.getTenantId();
    }


    @Override
    public void remove(String tenant) {
        tenantsMapper.delete(tenant);
    }

}
