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
package com.manager.pulsar.mapper;

import com.github.pagehelper.Page;
import com.manager.pulsar.entity.TenantsEntity;
import org.apache.ibatis.annotations.*;


@Mapper
public interface TenantsMapper {

  @Insert("INSERT INTO tenants(tenantId,tenant,adminRoles,allowedClusters) VALUES(#{tenantId},#{tenant},#{adminRoles},#{allowedClusters})")
  @Options(useGeneratedKeys=true, keyProperty="tenantId", keyColumn="tenantId")
  void insert(TenantsEntity tenant);

  @Select("SELECT tenant,adminRoles,allowedClusters FROM tenants WHERE tenantId = #{tenantId}")
  TenantsEntity findById(long tenantId);

  @Select("SELECT tenantId,adminRoles,allowedClusters FROM tenants WHERE tenant_name = #{tenant}")
  TenantsEntity findByName(String tenant);

  @Select("SELECT tenantId,tenant,adminRoles,allowedClusters FROM tenants")
  Page<TenantsEntity> getTenantsList();

  @Delete("DELETE FROM tenants WHERE tenantId = #{tenantId}")
  void delete(TenantsEntity tenant);
}
