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
package io.streamnative.pulsar.manager.dao;

import com.github.pagehelper.Page;
import io.streamnative.pulsar.manager.PulsarManagerApplication;
import io.streamnative.pulsar.manager.entity.ClusterEntity;
import io.streamnative.pulsar.manager.entity.ClustersRepository;
import io.streamnative.pulsar.manager.profiles.SqliteDBTestProfile;
import org.apache.pulsar.shade.com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.LinkedHashSet;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest(
    classes = {
        PulsarManagerApplication.class,
        SqliteDBTestProfile.class
    }
)
@ActiveProfiles("test")
public class ClustersRepositoryImplTest {

    @Autowired
    private ClustersRepository clustersRepository;

    private LinkedHashSet<String> clusterNames = new LinkedHashSet<String>() {{
        add("standalone");
        add("pulsar-cluster-1");
    }};

    private void initClustersEntity(ClusterEntity clustersEntity) {
        Gson gson = new Gson();
        clustersEntity.setClusterId(1);
        clustersEntity.setCluster("test-cluster");
        clustersEntity.setServiceUrl("http://localhost:8080");
        clustersEntity.setServiceUrlTls("https://localhost:443");
        clustersEntity.setBrokerServiceUrl("pulsar://localhost:6650");
        clustersEntity.setBrokerServiceUrlTls("pulsar+ssl://localhost:6650");
        clustersEntity.setPeerClusterNames(gson.toJson(clusterNames));
    }

    private void checkResult(Page<ClusterEntity> clustersEntityPage) {
        long total = clustersEntityPage.getTotal();
        Assert.assertEquals(total, 1);
        Gson gson = new Gson();
        clustersEntityPage.getResult().forEach((result) -> {
            Assert.assertEquals(result.getClusterId(), 1);
            Assert.assertEquals(result.getCluster(), "test-cluster");
            Assert.assertEquals(result.getServiceUrl(), "http://localhost:8080");
            Assert.assertEquals(result.getServiceUrlTls(), "https://localhost:443");
            Assert.assertEquals(result.getBrokerServiceUrl(), "pulsar://localhost:6650");
            Assert.assertEquals(result.getBrokerServiceUrlTls(), "pulsar+ssl://localhost:6650");
            Assert.assertEquals(result.getPeerClusterNames(), gson.toJson(clusterNames));
        });
    }

    private void checkDeleteResult(Page<ClusterEntity> clustersEntityPage) {
        long total = clustersEntityPage.getTotal();
        Assert.assertEquals(total, 0);
    }

    @Test
    public void testGetClustersList() {
        ClusterEntity clustersEntity = new ClusterEntity();
        initClustersEntity(clustersEntity);
        clustersRepository.save(clustersEntity);
        Page<ClusterEntity> clustersEntities = clustersRepository.getClustersList(1, 2);
        clustersEntities.count(true);
        checkResult(clustersEntities);
        clustersEntities.getResult().forEach((result) -> {
            clustersRepository.remove(result.getCluster());
        });
        Page<ClusterEntity> deleteCluster = clustersRepository.getClustersList(1, 2);
        deleteCluster.count(true);
        checkDeleteResult(deleteCluster);
    }

    @Test
    public void getCluster() {
        ClusterEntity clustersEntity = new ClusterEntity();
        initClustersEntity(clustersEntity);
        clustersRepository.save(clustersEntity);
        String cluster = "test-cluster";
        Optional<ClusterEntity> clusterEntity = clustersRepository.findByCluster(cluster);
        Page<ClusterEntity> clustersEntities = new Page();
        clustersEntities.add(clusterEntity.get());
        clustersEntities.setTotal(1);
        checkResult(clustersEntities);
        clustersEntities.getResult().forEach((result) -> {
            clustersRepository.remove(result.getCluster());
        });
        Page<ClusterEntity> deleteCluster = clustersRepository.getClustersList(1, 2);
        deleteCluster.count(true);
        checkDeleteResult(deleteCluster);
    }
}
