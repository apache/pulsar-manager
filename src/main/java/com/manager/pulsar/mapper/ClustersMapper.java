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

import com.github.pagehelper.Page;
import com.manager.pulsar.entity.ClusterEntity;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ClustersMapper {

    @Insert("INSERT INTO clusters(clusterId,cluster,serviceUrl,serviceUrlTls,brokerServiceUrl,brokerServiceUrlTls)" +
            "VALUES(#{clusterId},#{cluster},#{serviceUrl},#{serviceUrlTls},#{brokerServiceUrl},#{brokerServiceUrlTls})")
    void insert(ClusterEntity clustersEntity);

    @Select("SELECT clusterId,cluster,serviceUrl,serviceUrlTls,brokerServiceUrl,brokerServiceUrlTls " +
            "FROM clusters WHERE clusterId = #{clusterId}")
    ClusterEntity findById(long clusterId);

    @Select("SELECT clusterId,cluster,serviceUrl,serviceUrlTls,brokerServiceUrl,brokerServiceUrlTls " +
            "FROM clusters WHERE cluster = #{cluster}")
    ClusterEntity findByCluster(String cluster);

    @Select("SELECT clusterId,cluster,serviceUrl,serviceUrlTls,brokerServiceUrl,brokerServiceUrlTls " +
            "FROM clusters")
    Page<ClusterEntity> getClustersList();


    @Delete("DELETE FROM clusters WHERE clusterId = #{clusterId}")
    void deleteByClusterId(Integer clusterId);

    @Update("UPDATE clusters set serviceUrl=#{serviceUrl},serviceUrlTls=#{serviceUrlTls}," +
            "brokerServiceUrl=#{brokerServiceUrl},brokerServiceUrlTls=#{brokerServiceUrlTls} FROM clusters " +
            "where cluster=#{cluster")
    void update(ClusterEntity clustersEntity);

    @Delete("DELETE FROM clusters WHERE cluster=#{cluster}")
    void delete(String cluster);
}
