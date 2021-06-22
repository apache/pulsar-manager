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
import org.apache.pulsar.manager.entity.EnvironmentEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface EnvironmentsMapper {

    @Insert("INSERT INTO environments(name,broker,bookie) VALUES(#{name},#{broker},#{bookie})")
    void insert(EnvironmentEntity environmentEntity);

    @Select("SELECT name,broker,bookie FROM environments where broker=#{broker}")
    EnvironmentEntity findByBroker(String broker);

    @Select("SELECT name,broker,bookie FROM environments where name=#{name}")
    EnvironmentEntity findByName(String name);

    @Select("SELECT name,broker,bookie FROM environments")
    Page<EnvironmentEntity> findEnvironmentsList();

    @Select({"<script>",
            "SELECT name,broker,bookie FROM environments",
            "WHERE name IN <foreach collection='nameList' item='name' open='(' separator=',' close=')'> #{name} </foreach>" +
                    "</script>"})
    Page<EnvironmentEntity> findEnvironmentsListByMultiName(@Param("nameList") List<String> nameList);

    @Select("SELECT name,broker,bookie FROM environments")
    List<EnvironmentEntity> getAllEnvironments();

    @Update("UPDATE environments set broker=#{broker},bookie=#{bookie} where name=#{name}")
    void update(EnvironmentEntity environmentEntity);

    @Delete("DELETE FROM environments WHERE name=#{name}")
    void delete(String name);

    @Delete("DELETE FROM environments WHERE broker=#{broker}")
    void deleteByBroker(String broker);
}
