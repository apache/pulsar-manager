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
import org.apache.pulsar.manager.entity.NamespaceEntity;
import org.apache.pulsar.manager.entity.NamespacesRepository;
import org.apache.pulsar.manager.mapper.NamespacesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class NamespacesRepositoryImpl implements NamespacesRepository {

    private final NamespacesMapper namespacesMapper;

    @Autowired
    public NamespacesRepositoryImpl(NamespacesMapper namespacesMapper) {
        this.namespacesMapper = namespacesMapper;
    }

    @Override
    public Optional<NamespaceEntity> findByTenantNamespace(String tenant, String namespace) {
        return Optional.ofNullable(namespacesMapper.findByTenantNamespace(tenant, namespace));
    }

    @Override
    public Optional<NamespaceEntity> findByNamespaceId(long namespaceId) {
        return Optional.ofNullable(namespacesMapper.findByNamespaceId(namespaceId));
    }

    @Override
    public Page<NamespaceEntity> findByTenantOrNamespace(Integer pageNum, Integer pageSize, String tenantOrNamespace) {
        PageHelper.startPage(pageNum, pageSize);
        Page<NamespaceEntity> namespacesEntities = namespacesMapper.findByTenantOrNamespace(tenantOrNamespace);
        return namespacesEntities;
    }

    @Override
    public Page<NamespaceEntity> findByNamespace(Integer pageNum, Integer pageSize, String namespace) {
        PageHelper.startPage(pageNum, pageSize);
        Page<NamespaceEntity> namespacesEntities = namespacesMapper.findByNamespace(namespace);
        return namespacesEntities;
    }

    @Override
    public Page<NamespaceEntity> findByMultiId(Integer pageNum, Integer pageSize, List<Long> namespaceIdList) {
        PageHelper.startPage(pageNum, pageSize);
        Page<NamespaceEntity> namespacesEntities = namespacesMapper.findByMultiId(namespaceIdList);
        return namespacesEntities;
    }

    @Override
    public List<NamespaceEntity> findByMultiId(List<Long> namespaceIdList) {
        List<NamespaceEntity> namespacesEntities = namespacesMapper.findByMultiId(namespaceIdList);
        return namespacesEntities;
    }

    @Override
    public Page<NamespaceEntity> getNamespacesList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<NamespaceEntity> namespacesEntities = namespacesMapper.getNamespacesList();
        return namespacesEntities;
    }


    @Override
    public Page<NamespaceEntity> findByTenant(Integer pageNum, Integer pageSize, String tenant) {
        PageHelper.startPage(pageNum, pageSize);
        Page<NamespaceEntity> namespacesEntities = namespacesMapper.findByTenant(tenant);
        return namespacesEntities;
    }

    @Override
    public List<NamespaceEntity> findByTenant(String tenant) {
        Page<NamespaceEntity> namespacesEntities = namespacesMapper.findAllByTenant(tenant);
        return namespacesEntities;
    }

    @Override
    public List<NamespaceEntity> findByMultiTenant(List<String> tenantList) {
        List<NamespaceEntity> namespaceEntities = namespacesMapper.findAllByMultiTenant(tenantList);
        return namespaceEntities;
    }

    @Override
    public void remove(String tenant, String namespace) {
        namespacesMapper.deleteByTenantNamespace(tenant, namespace);
    }

    @Override
    public long save(NamespaceEntity namespacesEntity) {
        namespacesMapper.insert(namespacesEntity);
        return namespacesEntity.getNamespaceId();
    }
}