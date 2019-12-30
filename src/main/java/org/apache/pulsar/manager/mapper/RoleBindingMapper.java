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
import org.apache.pulsar.manager.entity.RoleBindingEntity;

import java.util.List;
import java.util.Optional;

@Mapper
public interface RoleBindingMapper {

    @Insert("INSERT INTO role_binding (name, description, user_id, role_id) " +
            "VALUES (#{name}, #{description}, #{userId}, #{roleId})")
    @Options(useGeneratedKeys=true, keyProperty="roleBindingId", keyColumn="role_binding_id")
    long insert(RoleBindingEntity roleBindingEntity);

    @Select("SELECT name, role_binding_id as roleBindingId, description, user_id as userId, role_id as roleId " +
            "FROM role_binding WHERE user_id = #{userId} and role_id=#{roleId}")
    RoleBindingEntity findByUserIdAndRoleId(@Param("userId") long userId, @Param("roleId") long roleId);

    @Select("SELECT name, role_binding_id as roleBindingId, description, user_id as userId, role_id as roleId " +
            "FROM role_binding WHERE user_id = #{userId}")
    Page<RoleBindingEntity> findByUserId(long userId);

    @Select({"<script>",
            "SELECT name, role_binding_id as roleBindingId, description, user_id as userId, role_id as roleId " +
                    "FROM role_binding",
            "WHERE role_id IN <foreach collection='roleIdList' item='roleId' open='(' separator=',' close=')'> #{roleId} </foreach>" +
                    "</script>"})
    List<RoleBindingEntity> findByMultiRoleId(@Param("roleIdList") List<Long> roleIdList);


    @Select("SELECT name, role_binding_id as roleBindingId, description, user_id as userId, role_id as roleId " +
            "FROM role_binding WHERE user_id = #{userId}")
    List<RoleBindingEntity> findAllByUserId(long userId);

    @Select("SELECT name, role_binding_id as roleBindingId, description, user_id as userId, role_id as roleId " +
            "FROM role_binding")
    Page<RoleBindingEntity> findRoleBindinglist();

    @Update("UPDATE role_binding " +
            "SET description = #{description}, name = #{name}, role_id=#{roleId}, user_id=#{userId} " +
            "Where role_binding_id=#{roleBindingId}")
    void update(RoleBindingEntity roleBindingEntity);

    @Delete("DELETE FROM role_binding WHERE role_id = #{roleId} and user_id = #{userId}")
    void delete(@Param("roleId") long roleId, @Param("userId") long userId);
}
