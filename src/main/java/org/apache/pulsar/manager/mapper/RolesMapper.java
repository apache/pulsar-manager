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
import org.apache.pulsar.manager.entity.RoleInfoEntity;

import java.util.List;

@Mapper
public interface RolesMapper {

    @Insert("INSERT INTO roles (role_name, description, resource_type, resource_name, resource_verbs," +
            "resource_id, role_source, flag) " +
            "VALUES (#{roleName}, #{description}, #{resourceType}, #{resourceName}, #{resourceVerbs}," +
            "#{resourceId}, #{roleSource}, #{flag})")
    @Options(useGeneratedKeys=true, keyProperty="roleId", keyColumn="role_id")
    long save(RoleInfoEntity roleInfoEntity);

    @Select("SELECT role_id AS roleId, role_name AS roleName, description, resource_type AS resourceType," +
            "resource_name AS resourceName, resource_verbs AS resourceVerbs, resource_id as resourceId," +
            "role_source AS roleSource, flag " +
            "FROM roles " +
            "WHERE role_name = #{roleName} and role_source = #{roleSource}")
    RoleInfoEntity findByRoleName(@Param("roleName") String roleName, @Param("roleSource") String roleSource);

    @Select("SELECT role_id AS roleId, role_name AS roleName, description, resource_type AS resourceType," +
            "resource_name AS resourceName, resource_verbs AS resourceVerbs, resource_id as resourceId," +
            "role_source AS roleSource, flag " +
            "FROM roles " +
            "WHERE flag=#{flag} limit 1")
    RoleInfoEntity findByRoleFlag(@Param("flag") int flag);

    @Select("SELECT role_name AS roleName, role_id AS roleId, description, resource_type AS resourceType," +
            "resource_name AS resourceName, resource_verbs AS resourceVerbs, resource_id as resourceId," +
            "role_source AS roleSource, flag FROM roles")
    Page<RoleInfoEntity> findRoleList();

    @Select("SELECT role_name AS roleName, role_id AS roleId, description, resource_type AS resourceType," +
            "resource_name AS resourceName, resource_verbs AS resourceVerbs, resource_id as resourceId," +
            "role_source AS roleSource, flag FROM roles")
    List<RoleInfoEntity> findAllRoleList();

    @Select("SELECT role_name AS roleName, role_id AS roleId, description, resource_type AS resourceType," +
            "resource_name AS resourceName, resource_verbs AS resourceVerbs, resource_id as resourceId," +
            "role_source AS roleSource, flag FROM roles WHERE role_source=#{roleSource}")
    List<RoleInfoEntity> findRoleListByRoleSource(String roleSource);

    @Select({"<script>",
            "SELECT role_name AS roleName, role_id AS roleId, description, resource_type AS resourceType," +
                    "resource_name AS resourceName, resource_verbs AS resourceVerbs, resource_id as resourceId," +
                    "role_source AS roleSource, flag FROM roles",
            "WHERE role_id IN <foreach collection='roleIdList' item='roleId' open='(' separator=',' close=')'> #{roleId} </foreach>" +
                    "</script>"})
    Page<RoleInfoEntity> findByMultiId(@Param("roleIdList") List<Long> roleIdList);

    @Select({"<script>",
            "SELECT role_name AS roleName, role_id AS roleId, description, resource_type AS resourceType," +
                    "resource_name AS resourceName, resource_verbs AS resourceVerbs, resource_id as resourceId," +
                    "role_source AS roleSource, flag FROM roles",
            "WHERE role_id IN <foreach collection='roleIdList' item='roleId' open='(' separator=',' close=')'> #{roleId} </foreach>" +
                    "</script>"})
    List<RoleInfoEntity> findAllByMultiId(@Param("roleIdList") List<Long> roleIdList);

    @Update("UPDATE roles " +
            "SET description = #{description}, resource_type = #{resourceType}," +
            "resource_name = #{resourceName}, resource_verbs = #{resourceVerbs}," +
            "resource_id = #{resourceId}, flag=#{flag} " +
            "WHERE role_name = #{roleName} and role_source = #{roleSource}")
    void update(RoleInfoEntity roleInfoEntity);

    @Delete("DELETE FROM roles WHERE role_name=#{roleName} and role_source = #{roleSource} ")
    void delete(@Param("roleName") String roleName, @Param("roleSource") String roleSource);
}
