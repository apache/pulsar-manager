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
