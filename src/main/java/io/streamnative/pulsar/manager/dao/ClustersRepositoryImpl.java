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
import com.github.pagehelper.PageHelper;
import io.streamnative.pulsar.manager.entity.ClusterEntity;
import io.streamnative.pulsar.manager.entity.ClustersRepository;
import io.streamnative.pulsar.manager.mapper.ClustersMapper;
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
