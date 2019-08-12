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
import io.streamnative.pulsar.manager.entity.BundleEntity;
import io.streamnative.pulsar.manager.entity.BundlesRepository;
import io.streamnative.pulsar.manager.profiles.SqliteDBTestProfile;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Bundles crud test.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
    classes = {
        PulsarManagerApplication.class,
        SqliteDBTestProfile.class
    }
)
@ActiveProfiles("test")
public class BundlesRespositoryImplTest {

    @Autowired
    private BundlesRepository bundlesRepository;

    private void initBundleEntity(BundleEntity bundlesEntity) {
        bundlesEntity.setBroker("test-broker");
        bundlesEntity.setTenant("public");
        bundlesEntity.setNamespace("default");
        bundlesEntity.setBundle("0x80000000_0x90000000");
    }

    private void checkResult(Page<BundleEntity> bundlesEntityPage) {
        long total = bundlesEntityPage.getTotal();
        Assert.assertEquals(total, 1);
        bundlesEntityPage.getResult().forEach((result) -> {
            Assert.assertEquals(result.getBroker(), "test-broker");
            Assert.assertEquals(result.getTenant(), "public");
            Assert.assertEquals(result.getNamespace(), "default");
            Assert.assertEquals(result.getBundle(), "0x80000000_0x90000000");
        });
    }

    private void checkDeleteResult(Page<BundleEntity> bundlesEntityPage) {
        long total = bundlesEntityPage.getTotal();
        Assert.assertEquals(total, 0);
    }

    @Test
    public void getBundlesList() {
        BundleEntity bundlesEntity = new BundleEntity();
        initBundleEntity(bundlesEntity);
        bundlesRepository.save(bundlesEntity);
        Page<BundleEntity> bundlesEntityPage = bundlesRepository.getBundlesList(1, 2);
        bundlesEntityPage.count(true);
        checkResult(bundlesEntityPage);
        bundlesEntityPage.getResult().forEach((result) -> {
            bundlesRepository.remove(result.getBroker(), result.getTenant(), result.getNamespace(), result.getBundle());
        });
        Page<BundleEntity> deleteBundle = bundlesRepository.getBundlesList(1, 2);
        deleteBundle.count(true);
        checkDeleteResult(deleteBundle);
    }

    @Test
    public void getByBrokerOrTenantOrNamespaceOrbundle() {
        BundleEntity bundlesEntity = new BundleEntity();
        initBundleEntity(bundlesEntity);
        bundlesRepository.save(bundlesEntity);
        String broker = "test-broker";
        Page<BundleEntity> bundlesEntityPageByBroker = bundlesRepository
                .findByBrokerOrTenantOrNamespaceOrBundle(1, 2, broker);
        bundlesEntityPageByBroker.count(true);
        checkResult(bundlesEntityPageByBroker);

        String tenant = "public";
        Page<BundleEntity> bundlesEntityPageByTenant = bundlesRepository
                .findByBrokerOrTenantOrNamespaceOrBundle(1, 2, tenant);
        bundlesEntityPageByTenant.count(true);
        checkResult(bundlesEntityPageByTenant);

        String namespace = "default";
        Page<BundleEntity> bundlesEntityPageByNamespace = bundlesRepository
                .findByBrokerOrTenantOrNamespaceOrBundle(1, 2, namespace);
        bundlesEntityPageByNamespace.count(true);
        checkResult(bundlesEntityPageByNamespace);

        String bundle = "0x80000000_0x90000000";
        Page<BundleEntity> bundlesEntityPageByBundle = bundlesRepository
                .findByBrokerOrTenantOrNamespaceOrBundle(1,2, bundle);
        bundlesEntityPageByBundle.count(true);
        checkResult(bundlesEntityPageByBundle);

        bundlesEntityPageByBundle.getResult().forEach((result) -> {
            bundlesRepository.remove(result.getBroker(), result.getTenant(), result.getNamespace(), result.getBundle());
        });

        Page<BundleEntity> deleteBundle = bundlesRepository.getBundlesList(1, 2);
        deleteBundle.count(true);
        checkDeleteResult(deleteBundle);
    }
}
