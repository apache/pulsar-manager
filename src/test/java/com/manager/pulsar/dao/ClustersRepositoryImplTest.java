package com.manager.pulsar.dao;

import com.github.pagehelper.Page;
import com.manager.pulsar.entity.ClustersEntity;
import com.manager.pulsar.entity.ClustersRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ClustersRepositoryImplTest {

    @Autowired
    private ClustersRepository clustersRepository;

    private void initClustersEntity(ClustersEntity clustersEntity) {
        clustersEntity.setClusterId(1);
        clustersEntity.setCluster("test-cluster");
        clustersEntity.setServiceUrl("http://localhost:8080");
        clustersEntity.setServiceUrlTls("https://localhost:443");
        clustersEntity.setBrokerServiceUrl("pulsar://localhost:6650");
        clustersEntity.setBrokerServiceUrlTls("pulsar+ssl://localhost:6650");
    }

    private void checkResult(Page<ClustersEntity> clustersEntityPage) {
        long total = clustersEntityPage.getTotal();
        Assert.assertEquals(total, 1);
        clustersEntityPage.getResult().forEach((result) -> {
            Assert.assertEquals(result.getClusterId(), 1);
            Assert.assertEquals(result.getCluster(), "test-cluster");
            Assert.assertEquals(result.getServiceUrl(), "http://localhost:8080");
            Assert.assertEquals(result.getServiceUrlTls(), "https://localhost:443");
            Assert.assertEquals(result.getBrokerServiceUrl(), "pulsar://localhost:6650");
            Assert.assertEquals(result.getBrokerServiceUrlTls(), "pulsar+ssl://localhost:6650");
        });
    }

    private void checkDeleteResult(Page<ClustersEntity> clustersEntityPage) {
        long total = clustersEntityPage.getTotal();
        Assert.assertEquals(total, 0);
    }

    @Test
    public void testGetClustersList() {
        ClustersEntity clustersEntity = new ClustersEntity();
        initClustersEntity(clustersEntity);
        clustersRepository.save(clustersEntity);
        Page<ClustersEntity> clustersEntities = clustersRepository.getClustersList(1, 2);
        clustersEntities.count(true);
        checkResult(clustersEntities);
        clustersEntities.getResult().forEach((result) -> {
            clustersRepository.remove(result.getCluster());
        });
        Page<ClustersEntity> deleteCluster = clustersRepository.getClustersList(1, 2);
        deleteCluster.count(true);
        checkDeleteResult(deleteCluster);
    }

    @Test
    public void getCluster() {
        ClustersEntity clustersEntity = new ClustersEntity();
        initClustersEntity(clustersEntity);
        clustersRepository.save(clustersEntity);
        String cluster = "test-cluster";
        Optional<ClustersEntity> clusterEntity = clustersRepository.findByCluster(cluster);
        Page<ClustersEntity> clustersEntities = new Page();
        clustersEntities.add(clusterEntity.get());
        clustersEntities.setTotal(1);
        checkResult(clustersEntities);
        clustersEntities.getResult().forEach((result) -> {
            clustersRepository.remove(result.getCluster());
        });
        Page<ClustersEntity> deleteCluster = clustersRepository.getClustersList(1, 2);
        deleteCluster.count(true);
        checkDeleteResult(deleteCluster);
    }
}
