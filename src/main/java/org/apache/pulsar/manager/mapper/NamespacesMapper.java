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
import org.apache.ibatis.annotations.Options;
import org.apache.pulsar.manager.entity.NamespaceEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface NamespacesMapper {

    @Insert("INSERT INTO namespaces(tenant, namespace) VALUES(#{tenant},#{namespace})")
    @Options(useGeneratedKeys=true, keyProperty="namespaceId", keyColumn="namespace_id")
    long insert(NamespaceEntity namespacesEntity);

    @Select("SELECT tenant,namespace, namespace_id as namespaceId  " +
            "FROM namespaces WHERE tenant=#{tenant} and namespace=#{namespace}")
    NamespaceEntity findByTenantNamespace(@Param("tenant") String tenant, @Param("namespace") String namespace);

    @Select("SELECT tenant,namespace,namespace_id as namespaceId FROM namespaces " +
            "WHERE tenant=#{tenantOrNamespace} or namespace=#{tenantOrNamespace}")
    Page<NamespaceEntity> findByTenantOrNamespace(String tenantOrNamespace);

    @Select("SELECT tenant,namespace,namespace_id as namespaceId " +
            "FROM namespaces WHERE namespace=#{namespace}")
    Page<NamespaceEntity> findByNamespace(String namespace);

    @Select("SELECT tenant,namespace,namespace_id as namespaceId " +
            "FROM namespaces WHERE namespace_id=#{namespaceId}")
    NamespaceEntity findByNamespaceId(long namespaceId);

    @Select({"<script>",
            "SELECT tenant, namespace, namespace_id as namespaceId FROM namespaces ",
            "WHERE namespace_id IN <foreach collection='namespaceIdList' item='namespaceId' open='(' separator=',' close=')'> #{namespaceId} </foreach>" +
                    "</script>"})
    Page<NamespaceEntity> findByMultiId(@Param("namespaceIdList") List<Long> namespaceIdList);

    @Select({"<script>",
            "SELECT tenant, namespace, namespace_id as namespaceId FROM namespaces",
            "WHERE namespace_id IN <foreach collection='namespaceIdList' item='namespaceId' open='(' separator=',' close=')'> #{namespaceId} </foreach>" +
                    "</script>"})
    List<NamespaceEntity> findAllByMultiId(@Param("namespaceIdList") List<Long> namespaceIdList);

    @Select("SELECT tenant,namespace,namespace_id as namespaceId " +
            "FROM namespaces WHERE tenant=#{tenant}")
    Page<NamespaceEntity> findByTenant(String tenant);

    @Select("SELECT tenant,namespace,namespace_id as namespaceId " +
            "FROM namespaces WHERE tenant=#{tenant}")
    Page<NamespaceEntity> findAllByTenant(String tenant);

    @Select({"<script>",
            "SELECT tenant, namespace, namespace_id as namespaceId FROM namespaces",
            "WHERE tenant IN <foreach collection='tenantList' item='tenant' open='(' separator=',' close=')'> #{tenant} </foreach>" +
                    "</script>"})
    List<NamespaceEntity> findAllByMultiTenant(@Param("tenantList") List<String> tenantList);

    @Select("SELECT tenant,namespace,namespace_id as namespaceId FROM namespaces")
    Page<NamespaceEntity> getNamespacesList();

    @Delete("DELETE FROM namespaces WHERE tenant = #{tenant} and namespace = #{namespace}")
    void deleteByTenantNamespace(@Param("tenant") String tenant, @Param("namespace") String namespace);
}
