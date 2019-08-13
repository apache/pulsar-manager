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
package io.streamnative.pulsar.manager.entity;

import com.github.pagehelper.Page;
import org.springframework.stereotype.Repository;

/**
 * Interface of bundles
 */
@Repository
public interface BundlesRepository {

    void save(BundleEntity bundlesEntity);

    Page<BundleEntity> findByBrokerOrTenantOrNamespaceOrBundle(Integer pageNum, Integer pageSize, String btnt);

    Page<BundleEntity> findByBundle(Integer pageNum, Integer pageSize, String bundle);

    Page<BundleEntity> getBundlesList(Integer pageNum, Integer pageSize);

    void remove(String broker, String tenant, String namespace, String bundle);

}
