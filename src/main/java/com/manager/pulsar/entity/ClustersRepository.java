package com.manager.pulsar.entity;

import com.github.pagehelper.Page;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClustersRepository {

    void save(ClusterEntity clustersEntity);

    Optional<ClusterEntity> findById(long clusterId);

    Optional<ClusterEntity> findByCluster(String cluster);

    Page<ClusterEntity> getClustersList(Integer pageNum, Integer pageSize);

    void remove(String cluster);

    void update(ClusterEntity clustersEntity);
}
