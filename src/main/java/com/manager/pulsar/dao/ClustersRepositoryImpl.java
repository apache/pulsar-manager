package com.manager.pulsar.dao;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.manager.pulsar.entity.ClusterEntity;
import com.manager.pulsar.entity.ClustersRepository;
import com.manager.pulsar.mapper.ClustersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * ClustersRepositoryImpl implements ClustersRepository for operation crud of bundle.
 */
@Repository
public class ClustersRepositoryImpl implements ClustersRepository {

    private final ClustersMapper clustersMapper;

    @Autowired
    public ClustersRepositoryImpl(ClustersMapper clustersMapper) {
        this.clustersMapper = clustersMapper;
    }

    @Override
    public Optional<ClusterEntity> findById(long clusterId) {
        return Optional.ofNullable(clustersMapper.findById(clusterId));
    }

    @Override
    public Optional<ClusterEntity> findByCluster(String cluster) {
        return Optional.ofNullable(clustersMapper.findByCluster(cluster));
    }

    @Override
    public Page<ClusterEntity> getClustersList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<ClusterEntity> clustersEntities = clustersMapper.getClustersList();
        return clustersEntities;
    }

    @Override
    public void save(ClusterEntity clustersEntity) {
        clustersMapper.insert(clustersEntity);
    }


    @Override
    public void remove(String cluster) {
        clustersMapper.delete(cluster);
    }


    @Override
    public void update(ClusterEntity clustersEntity) {
        clustersMapper.update(clustersEntity);
    }
}
