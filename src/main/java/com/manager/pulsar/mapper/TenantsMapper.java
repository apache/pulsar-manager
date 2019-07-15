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
import com.manager.pulsar.entity.TenantEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface TenantsMapper {

  @Insert("INSERT INTO tenants(tenant,tenantId,adminRoles,allowedClusters) " +
          "VALUES(#{tenant},#{tenantId},#{adminRoles},#{allowedClusters})")
  void insert(TenantEntity tenantsEntity);

  @Select("SELECT tenant,adminRoles,allowedClusters FROM tenants WHERE tenantId = #{tenantId}")
  TenantEntity findById(long tenantId);

  @Select("SELECT tenantId,tenant,adminRoles,allowedClusters FROM tenants WHERE tenant = #{tenant}")
  TenantEntity findByName(String tenant);

  @Select("SELECT tenantId,tenant,adminRoles,allowedClusters FROM tenants")
  Page<TenantEntity> getTenantsList();

  @Delete("DELETE FROM tenants WHERE tenantId = #{tenantId}")
  void delete(Long tenantId);

  @Delete("DELETE FROM tenants WHERE tenant = #{tenant}")
  void deleteByTenant(String tenant);

  @Update("UPDATE tenants set adminRoles = #{adminRoles}, allowedClusters = #{allowedClusters} where tenant = #{tenant}")
  void updateByTenant(String tenant);
}
