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
import com.manager.pulsar.entity.NamespacesEntity;
import com.manager.pulsar.entity.NamespacesRepository;
import com.manager.pulsar.mapper.NamespacesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MyBatisNamespacesRepository implements NamespacesRepository {

    private final NamespacesMapper namespacesMapper;

    @Autowired
    public MyBatisNamespacesRepository(NamespacesMapper namespacesMapper) {
        this.namespacesMapper = namespacesMapper;
    }

    @Override
    public Optional<NamespacesEntity> findById(int namespaceId) {
        return Optional.ofNullable(namespacesMapper.findById(namespaceId));
    }

    @Override
    public Optional<NamespacesEntity> findByTenantNamespace(String tenant, String namespace) {
        return Optional.ofNullable(namespacesMapper.findByTenantNamespace(tenant, namespace));
    }

    @Override
    public Page<NamespacesEntity> findByTenantOrNamespace(Integer pageNum, Integer pageSize, String tenantOrNamespace) {
        PageHelper.startPage(pageNum, pageSize);
        Page<NamespacesEntity> namespacesEntities = namespacesMapper.findByTenantOrNamespace(tenantOrNamespace);
        return namespacesEntities;
    }

    @Override
    public Page<NamespacesEntity> findByNamespace(Integer pageNum, Integer pageSize, String namespace) {
        PageHelper.startPage(pageNum, pageSize);
        Page<NamespacesEntity> namespacesEntities = namespacesMapper.findByNamespace(namespace);
        return namespacesEntities;
    }

    @Override
    public Page<NamespacesEntity> getNamespacesList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<NamespacesEntity> namespacesEntities = namespacesMapper.getNamespacesList();
        return namespacesEntities;
    }

    @Override
    public void remove(String tenant, String namespace) {
        namespacesMapper.deleteByTenantNamespace(tenant, namespace);
    }

    @Override
    public void update(NamespacesEntity namespacesEntity) {
        namespacesMapper.updateByTenantNamespace(namespacesEntity);
    }

    @Override
    public int save(NamespacesEntity namespacesEntity) {
        namespacesMapper.insert(namespacesEntity);
        return namespacesEntity.getNamespaceId();
    }
}
