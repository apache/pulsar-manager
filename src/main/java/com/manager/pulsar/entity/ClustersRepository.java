package com.manager.pulsar.entity;

import com.github.pagehelper.Page;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClustersRepository {

    void save(ClustersEntity clustersEntity);

    Optional<ClustersEntity> findById(long clusterId);

    Optional<ClustersEntity> findByCluster(String cluster);

    Page<ClustersEntity> getClustersList(Integer pageNum, Integer pageSize);

    void remove(String cluster);

    void update(ClustersEntity clustersEntity);
}
