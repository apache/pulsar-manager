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
import com.manager.pulsar.entity.BundleEntity;
import com.manager.pulsar.entity.BundlesRepository;
import com.manager.pulsar.mapper.BundlesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
    public Page<BundleEntity> findByBundle(Integer pageNum, Integer pageSize, String bundle) {
        PageHelper.startPage(pageNum, pageSize);
        return bundlesMapper.findByBundle(bundle);
    }


    @Override
    public Page<BundleEntity> findByBrokerOrTenantOrNamespaceOrBundle(Integer pageNum, Integer pageSize, String btnb) {
        PageHelper.startPage(pageNum, pageSize);
        Page<BundleEntity> bundlesEntities = bundlesMapper.findByBrokerOrTenantOrNamespaceOrBundle(btnb);
        return bundlesEntities;
    }

    @Override
    public Page<BundleEntity> getBundlesList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<BundleEntity> bundlesEntities = bundlesMapper.getBundlesList();
        return bundlesEntities;
    }

    @Override
    public void remove(String broker, String tenant, String namespace, String bundle) {
        bundlesMapper.delete(broker, tenant, namespace, bundle);
    }

    @Override
    public void save(BundleEntity bundlesEntity) {
        bundlesMapper.insert(bundlesEntity);
    }
}
