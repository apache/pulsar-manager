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

import com.manager.pulsar.entity.TenantsEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

public interface TenantsMapper {

  @Insert("INSERT INTO tenants(tenant_name) VALUES(#{tenantName})")
  @Options(useGeneratedKeys=true, keyProperty="tenantId", keyColumn="id")
  void insert(TenantsEntity tenant);

  @Select("SELECT id tenantId,tenant_name tenantName FROM tenants WHERE id = #{id}")
  TenantsEntity findById(Integer id);

  @Select("SELECT id,tenant_name FROM tenants WHERE tenant_name = #{tenantName}")
  TenantsEntity findByName(String tenantName);

  @Delete("DELETE FROM tenants WHERE id = #{id}")
  void delete(TenantsEntity tenant);
}
