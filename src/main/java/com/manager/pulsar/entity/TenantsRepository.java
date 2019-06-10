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

import java.util.Optional;

import com.github.pagehelper.Page;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantsRepository {

  int save(TenantsEntity tenantsEntity);

  Optional<TenantsEntity> findById(int tenantId);

  Optional<TenantsEntity> findByName(String tenant);

  Page<TenantsEntity> getTenantsList(Integer pageNum, Integer pageSize);

  void remove(TenantsEntity tenantsEntity);

  void removeByTenant(TenantsEntity tenantsEntity);

  void updateByTenant(TenantsEntity tenantsEntity);
}


