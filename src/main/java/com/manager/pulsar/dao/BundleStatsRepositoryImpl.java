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
package com.manager.pulsar.dao;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.manager.pulsar.entity.BundleStatsEntity;
import com.manager.pulsar.entity.BundleStatsRepository;
import com.manager.pulsar.mapper.BundleStatsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * BundleStatsRepositoryImpl implements BundleStatsRepository for operation crud of bundle.
 */
@Repository
public class BundleStatsRepositoryImpl implements BundleStatsRepository {

    private final BundleStatsMapper bundleStatsMapper;

    @Autowired
    public BundleStatsRepositoryImpl(BundleStatsMapper bundleStatsMapper) {
        this.bundleStatsMapper = bundleStatsMapper;
    }

    @Override
    public Page<BundleStatsEntity> findByBundle(Integer pageNum, Integer pageSize, String bundle) {
        PageHelper.startPage(pageNum, pageSize);
        return bundleStatsMapper.findByBundle(bundle);
    }

    @Override
    public Optional<BundleStatsEntity> findByBrokerTenantNamespaceBundle(
            String broker, String tenant, String namespace, String bundle) {
        return Optional.ofNullable(bundleStatsMapper.findByBrokerTenantNamespaceBundle(broker, tenant, namespace, bundle));
    }

    @Override
    public Page<BundleStatsEntity> findByBrokerOrTenantOrNamespaceOrBundle(Integer pageNum, Integer pageSize, String btnb) {
        PageHelper.startPage(pageNum, pageSize);
        Page<BundleStatsEntity> bundleStatsEntities = bundleStatsMapper.findByBrokerOrTenantOrNamespaceOrBundle(btnb);
        return bundleStatsEntities;
    }

    @Override
    public Page<BundleStatsEntity> getBundleStatsList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<BundleStatsEntity> bundleStatsEntities = bundleStatsMapper.getBundlesList();
        return bundleStatsEntities;
    }

    @Override
    public void remove(String broker, String tenant, String namespace, String bundle) {
        bundleStatsMapper.delete(broker, tenant, namespace, bundle);
    }

    @Override
    public void update(BundleStatsEntity bundleStatsEntity) {
        bundleStatsMapper.update(bundleStatsEntity);
    }

    @Override
    public void save(BundleStatsEntity bundleStatsEntity) {
        bundleStatsMapper.insert(bundleStatsEntity);
    }
}
