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

@Mapper
public interface RolesMapper {

    @Insert("INSERT INTO roles (role_name, description, resource_type, resource_name, resource_verbs, role_source, flag) " +
            "VALUES (#{roleName}, #{description}, #{resourceType}, #{resourceName}, #{resourceVerbs}, #{roleSource}, #{flag})")
    @Options(useGeneratedKeys=true, keyProperty="roleId", keyColumn="role_id")
    long save(RoleInfoEntity roleInfoEntity);

    @Select("SELECT role_id AS roleId, role_name AS roleName, description, resource_type AS resourceType," +
            "resource_name AS resourceName, resource_verbs AS resourceVerbs, role_source AS roleSource, flag " +
            "FROM roles " +
            "WHERE role_name = #{roleName} and role_source = #{roleSource}")
    RoleInfoEntity findByRoleName(@Param("roleName") String roleName, @Param("roleSource") String roleSource);

    @Select("SELECT role_name AS roleName, role_id AS roleId, description, resource_type AS resourceType," +
            "resource_name AS resourceName, resource_verbs AS resourceVerbs, role_source AS roleSource, flag FROM roles")
    Page<RoleInfoEntity> findRoleList();

    @Update("UPDATE roles " +
            "SET description = #{description}, resource_type = #{resourceType}," +
            "resource_name = #{resourceName}, resource_verbs = #{resourceVerbs}, flag=#{flag} " +
            "WHERE role_name = #{roleName} and role_source = #{roleSource}")
    void update(RoleInfoEntity roleInfoEntity);

    @Delete("DELETE FROM roles WHERE role_name=#{roleName} and role_source = #{roleSource} ")
    void delete(@Param("roleName") String roleName, @Param("roleSource") String roleSource);
}
