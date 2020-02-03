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
import org.apache.pulsar.manager.entity.UserInfoEntity;

import java.util.List;

@Mapper
public interface UsersMapper {

    @Insert("INSERT INTO users (access_token, name, description, email, phone_number" +
            ", location, company, expire, password)" +
            "VALUES (#{accessToken}, #{name}, #{description}, #{email}, #{phoneNumber}" +
            ", #{location}, #{company}, #{expire}, #{password})")
    @Options(useGeneratedKeys=true, keyProperty="userId", keyColumn="user_id")
    long save(UserInfoEntity userInfoEntity);

    @Select("SELECT access_token AS accessToken, user_id AS userId, name, description, email," +
            "phone_number AS phoneNumber, location, company, expire, password " +
            "FROM users " +
            "WHERE name = #{name}")
    UserInfoEntity findByUserName(String name);

    @Select("SELECT access_token AS accessToken, user_id AS userId, name, description, email," +
            "phone_number AS phoneNumber, location, company, expire, password " +
            "FROM users " +
            "WHERE user_id = #{userId}")
    UserInfoEntity findByUserId(long userId);

    @Select("SELECT access_token AS accessToken, user_id AS userId, name, description, email," +
            "phone_number AS phoneNumber, location, company, expire " +
            "FROM users " +
            "WHERE access_token = #{accessToken}")
    UserInfoEntity findByAccessToken(String accessToken);

    @Select("SELECT access_token AS accessToken, user_id AS userId, name, description, email," +
            "phone_number AS phoneNumber, location, company, expire " +
            "FROM users")
    Page<UserInfoEntity> findUsersList();


    @Select({"<script>",
            "SELECT access_token AS accessToken, user_id AS userId, name, description, email," +
                    "phone_number AS phoneNumber, location, company, expire " +
                    "FROM users ",
            "WHERE user_id IN <foreach collection='userIdList' item='userId' open='(' separator=',' close=')'> #{userId} </foreach>" +
            "</script>"})
    List<UserInfoEntity> findUsersListByMultiUserId(@Param("userIdList") List<Long> userIdList);


    @Update("UPDATE users " +
            "SET access_token = #{accessToken}, description = #{description}, email = #{email}," +
            "phone_number = #{phoneNumber}, location = #{location}, company = #{company}, expire=#{expire}," +
            "password=#{password} WHERE name = #{name}")
    void update(UserInfoEntity userInfoEntity);

    @Delete("DELETE FROM users WHERE name=#{name}")
    void delete(String name);
}
