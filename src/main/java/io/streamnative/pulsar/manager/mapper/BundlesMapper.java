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
package io.streamnative.pulsar.manager.mapper;

import com.github.pagehelper.Page;
import io.streamnative.pulsar.manager.entity.BundleEntity;
import org.apache.ibatis.annotations.*;

/**
 * Insert, delete, udpate and query for Bundle.
 */
@Mapper
public interface BundlesMapper {

    @Insert("INSERT INTO bundles(broker,tenant,namespace,bundle) VALUES(#{broker},#{tenant},#{namespace},#{bundle})")
    void insert(BundleEntity bundlesEntity);

    @Select("SELECT broker,tenant,namespace,bundle FROM bundles WHERE broker=#{btnb} or " +
            "tenant=#{btnb} or namespace=#{btnb} or bundle=#{btnb}")
    Page<BundleEntity> findByBrokerOrTenantOrNamespaceOrBundle(String btnb);

    @Select("SELECT broker,tenant,namespace,bundle FROM bundles WHERE bundle=#{bundle}")
    Page<BundleEntity> findByBundle(String bundle);

    @Select("SELECT broker,tenant,namespace,bundle FROM bundles")
    Page<BundleEntity> getBundlesList();

    @Delete("DELETE FROM bundles WHERE broker=#{broker} and tenant=#{tenant} " +
            "and namespace=#{namespace} and bundle=#{bundle}")
    void delete(@Param("broker") String broker, @Param("tenant") String tenant,
                @Param("namespace") String namespace, @Param("bundle") String bundle);

}
