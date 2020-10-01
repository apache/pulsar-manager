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

import org.springframework.stereotype.Repository;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Optional;

@Repository
public interface NamespacesRepository {

    long save(NamespaceEntity namespacesEntity);

    Optional<NamespaceEntity> findByTenantNamespace(String tenant, String namespace);

    Optional<NamespaceEntity> findByNamespaceId(long namespaceId);

    Page<NamespaceEntity> findByTenantOrNamespace(Integer pageNum, Integer pageSize, String tenantOrNamespace);

    Page<NamespaceEntity> findByNamespace(Integer pageNum, Integer pageSize, String namespace);

    Page<NamespaceEntity> findByMultiId(Integer pageNum, Integer pageSize, List<Long> tenantIdList);

    List<NamespaceEntity> findByMultiId(List<Long> tenantIdList);

    Page<NamespaceEntity> getNamespacesList(Integer pageNum, Integer pageSize);

    Page<NamespaceEntity> findByTenant(Integer pageNum, Integer pageSize, String tenant);

    List<NamespaceEntity> findByTenant(String tenant);

    List<NamespaceEntity> findByMultiTenant(List<String> tenantList);

    void remove(String tenant, String namespace);

}

