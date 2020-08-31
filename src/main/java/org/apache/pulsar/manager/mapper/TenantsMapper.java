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
package org.apache.pulsar.manager.mapper;

import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.pulsar.manager.entity.NamespaceEntity;
import org.apache.pulsar.manager.entity.TenantEntity;

import java.util.List;

@Mapper
public interface TenantsMapper {

    @Insert("INSERT INTO tenants (admin_roles, allowed_clusters, tenant, environment_name) " +
            "VALUES (#{adminRoles}, #{allowedClusters}, #{tenant}, #{environmentName})")
    @Options(useGeneratedKeys=true, keyProperty="tenantId", keyColumn="tenant_id")
    long insert(TenantEntity tenantEntity);


    @Select("SELECT tenant, tenant_id as tenantId, admin_roles as adminRoles,allowed_clusters as allowedClusters," +
            "environment_name as environmentName " +
            "FROM tenants WHERE tenant = #{tenant}")
    TenantEntity findByName(String tenant);

    @Select("SELECT tenant, tenant_id as tenantId, admin_roles as adminRoles,allowed_clusters as allowedClusters," +
            "environment_name as environmentName " +
            "FROM tenants WHERE tenant_id = #{tenantId}")
    TenantEntity findByTenantId(long tenantId);

    @Select("SELECT tenant, tenant_id as tenantId, admin_roles as adminRoles,allowed_clusters as allowedClusters," +
            "environment_name as environmentName  " +
            "FROM tenants")
    Page<TenantEntity> getTenantsList();

    @Select({"<script>",
            "SELECT tenant, tenant_id as tenantId, admin_roles as adminRoles,allowed_clusters as allowedClusters," +
                    "environment_name as environmentName " +
                    " FROM tenants ",
            "WHERE tenant_id IN <foreach collection='tenantIdList' item='tenantId' open='(' separator=',' close=')'> #{tenantId} </foreach>" +
                    "</script>"})
    Page<TenantEntity> findByMultiId(@Param("tenantIdList") List<Long> tenantIdList);

    @Select({"<script>",
            "SELECT tenant, tenant_id as tenantId, admin_roles as adminRoles,allowed_clusters as allowedClusters," +
                    "environment_name as environmentName " +
                    " FROM tenants ",
            "WHERE tenant_id IN <foreach collection='tenantIdList' item='tenantId' open='(' separator=',' close=')'> #{tenantId} </foreach>" +
                    "</script>"})
    List<TenantEntity> findAllByMultiId(@Param("tenantIdList") List<Long> tenantIdList);

    @Select({"<script>",
            "SELECT tenant, tenant_id as tenantId, admin_roles as adminRoles,allowed_clusters as allowedClusters," +
                    "environment_name as environmentName " +
                    " FROM tenants ",
            "WHERE environment_name IN <foreach collection='environmentNameList' item='environmentName' open='(' separator=',' close=')'> #{environmentName} </foreach>" +
                    "</script>"})
    List<TenantEntity> findAllByMultiEnvironmentName(@Param("environmentNameList") List<String> environmentNameList);

    @Delete("DELETE FROM tenants WHERE tenant = #{tenant}")
    void delete(String tenant);

}
