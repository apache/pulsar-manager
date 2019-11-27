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
import org.apache.ibatis.annotations.*;
import org.apache.pulsar.manager.entity.UserInfoEntity;

@Mapper
public interface UsersMapper {

    @Insert("INSERT INTO users (access_token, name, description, email, phone_number" +
            ", location, company, expire)" +
            "VALUES (#{accessToken}, #{name}, #{description}, #{email}, #{phoneNumber}" +
            ", #{location}, #{company}, #{expire})")
    @Options(useGeneratedKeys=true, keyProperty="userId", keyColumn="user_id")
    Long save(UserInfoEntity userInfoEntity);

    @Select("SELECT access_token AS accessToken, user_id AS userId, name, description, email," +
            "phone_number AS phoneNumber, location, company, expire " +
            "FROM users " +
            "WHERE name = #{name}")
    UserInfoEntity findByUserName(String name);

    @Select("SELECT access_token AS accessToken, user_id AS userId, name, description, email," +
            "phone_number AS phoneNumber, location, company, expire " +
            "FROM users")
    Page<UserInfoEntity> findUsersList();

    @Update("UPDATE users " +
            "SET access_token = #{accessToken}, description = #{description}, email = #{email}," +
            "phone_number = #{phoneNumber}, location = #{location}, company = #{company}, expire=#{expire} " +
            "WHERE name = #{name}")
    void update(UserInfoEntity userInfoEntity);

    @Delete("DELETE FROM users WHERE name=#{name}")
    void delete(String name);
}
