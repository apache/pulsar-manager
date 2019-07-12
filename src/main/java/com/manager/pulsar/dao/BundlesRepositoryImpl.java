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
import com.manager.pulsar.entity.BundlesEntity;
import com.manager.pulsar.entity.BundlesRepository;
import com.manager.pulsar.mapper.BundlesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * BundlesRepositoryImpl implements BundlesRepository for operation crud of bundle.
 */
@Repository
public class BundlesRepositoryImpl implements BundlesRepository {

    private final BundlesMapper bundlesMapper;

    @Autowired
    public BundlesRepositoryImpl(BundlesMapper bundlesMapper) {
        this.bundlesMapper = bundlesMapper;
    }

    @Override
    public Page<BundlesEntity> findByBundle(Integer pageNum, Integer pageSize, String bundle) {
        PageHelper.startPage(pageNum, pageSize);
        return bundlesMapper.findByBundle(bundle);
    }

    @Override
    public Optional<BundlesEntity> findByBrokerTenantNamespaceBundle(
            String broker, String tenant, String namespace, String bundle) {
        return Optional.ofNullable(bundlesMapper.findByBrokerTenantNamespaceBundle(broker, tenant, namespace, bundle));
    }

    @Override
    public Page<BundlesEntity> findByBrokerOrTenantOrNamespaceOrBundle(Integer pageNum, Integer pageSize, String btnb) {
        PageHelper.startPage(pageNum, pageSize);
        Page<BundlesEntity> bundlesEntities = bundlesMapper.findByBrokerOrTenantOrNamespaceOrBundle(btnb);
        return bundlesEntities;
    }

    @Override
    public Page<BundlesEntity> getBundlesList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<BundlesEntity> bundlesEntities = bundlesMapper.getBundlesList();
        return bundlesEntities;
    }

    @Override
    public void remove(String broker, String tenant, String namespace, String bundle) {
        bundlesMapper.delete(broker, tenant, namespace, bundle);
    }

    @Override
    public void update(BundlesEntity bundlesEntity) {
        bundlesMapper.update(bundlesEntity);
    }

    @Override
    public void save(BundlesEntity bundlesEntity) {
        bundlesMapper.insert(bundlesEntity);
    }
}
