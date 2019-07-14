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
package com.manager.pulsar.entity;

import org.springframework.stereotype.Repository;
import com.github.pagehelper.Page;

import java.util.Optional;

@Repository
public interface NamespacesRepository {

    void save(NamespacesEntity namespacesEntity);

    Optional<NamespacesEntity> findById(long namespaceId);

    Optional<NamespacesEntity> findByTenantNamespace(String tenant, String namespace);

    Page<NamespacesEntity> findByTenantOrNamespace(Integer pageNum, Integer pageSize, String tenantOrNamespace);

    Page<NamespacesEntity> findByNamespace(Integer pageNum, Integer pageSize, String namespace);

    Page<NamespacesEntity> getNamespacesList(Integer pageNum, Integer pageSize);

    void remove(String tenant, String namespace);

    void update(NamespacesEntity namespacesEntity);
}
