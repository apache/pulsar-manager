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

import java.util.List;
import java.util.Optional;

import com.github.pagehelper.Page;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantsRepository {

    long save(TenantEntity tenantsEntity);

    Optional<TenantEntity> findByName(String tenant);

    Optional<TenantEntity> findByTenantId(long tenantId);

    Page<TenantEntity> getTenantsList(Integer pageNum, Integer pageSize);

    Page<TenantEntity> findByMultiId(Integer pageNum, Integer pageSize, List<Long> tenantIdList);

    List<TenantEntity> findByMultiId(List<Long> tenantIdList);

    List<TenantEntity> findByMultiEnvironmentName(List<String> environmentNameList);

    void remove(String tenant);

}

