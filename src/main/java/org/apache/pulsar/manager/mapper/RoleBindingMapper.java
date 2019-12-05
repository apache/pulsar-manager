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

@Mapper
public interface RoleBindingMapper {

    @Insert("INSERT INTO role_binding (name, description, user_id, role_id) " +
            "VALUES (#{name}, #{description}, #{userId}, #{roleId})")
    @Options(useGeneratedKeys=true, keyProperty="roleBindingId", keyColumn="role_binding_id")
    long insert(RoleBindingEntity roleBindingEntity);

    @Select("SELECT name, role_binding_id as roleBindingId, description, user_id as userId, role_id as roleId " +
            "FROM role_binding WHERE user_id = #{userId}")
    Page<RoleBindingEntity> findByUserId(long userId);

    @Update("UPDATE role_binding " +
            "SET description = #{description}, name = #{name} Where role_id = #{roleId} and user_id = #{userId}")
    void update(RoleBindingEntity roleBindingEntity);

    @Delete("DELETE FROM role_binding WHERE role_id = #{roleId} and user_id = #{userId}")
    void delete(@Param("roleId") long roleId, @Param("userId") long userId);
}
