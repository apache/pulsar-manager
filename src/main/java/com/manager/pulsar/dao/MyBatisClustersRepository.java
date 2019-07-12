package com.manager.pulsar.dao;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.manager.pulsar.entity.ClustersEntity;
import com.manager.pulsar.entity.ClustersRepository;
import com.manager.pulsar.mapper.ClustersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MyBatisClustersRepository implements ClustersRepository {

    private final ClustersMapper clustersMapper;

    @Autowired
    public MyBatisClustersRepository(ClustersMapper clustersMapper) {
        this.clustersMapper = clustersMapper;
    }

    @Override
    public Optional<ClustersEntity> findById(int clusterId) {
        return Optional.ofNullable(clustersMapper.findById(clusterId));
    }

    @Override
    public Optional<ClustersEntity> findByCluster(String cluster) {
        return Optional.ofNullable(clustersMapper.findByCluster(cluster));
    }

    @Override
    public Page<ClustersEntity> getClustersList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<ClustersEntity> clustersEntities = clustersMapper.getClustersList();
        return clustersEntities;
    }

    @Override
    public int save(ClustersEntity clustersEntity) {
        clustersMapper.insert(clustersEntity);
        return clustersEntity.getClusterId();
    }


    @Override
    public void remove(String cluster) {
        clustersMapper.delete(cluster);
    }


    @Override
    public void update(ClustersEntity clustersEntity) {
        clustersMapper.update(clustersEntity);
    }
}
