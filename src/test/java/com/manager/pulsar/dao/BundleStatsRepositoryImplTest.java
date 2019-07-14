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
import com.manager.pulsar.entity.BundleStatsEntity;
import com.manager.pulsar.entity.BundleStatsRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Bundle Stats crud test.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class BundleStatsRepositoryImplTest {

    @Autowired
    private BundleStatsRepository bundleStatsRepository;

    private void initBundleStatsEntity(BundleStatsEntity bundleStatsEntity) {
        bundleStatsEntity.setBroker("test-broker");
        bundleStatsEntity.setTenant("public");
        bundleStatsEntity.setNamespace("default");
        bundleStatsEntity.setBundle("0x80000000_0x90000000");
        bundleStatsEntity.setMsgRateIn(0.0);
        bundleStatsEntity.setMsgRateOut(0.0);
        bundleStatsEntity.setMsgThroughputIn(0.0);
        bundleStatsEntity.setMsgThroughputOut(0.0);
        bundleStatsEntity.setConsumerCount(1);
        bundleStatsEntity.setProducerCount(1);
        bundleStatsEntity.setTopics(1);
        bundleStatsEntity.setCacheSize(0);
    }

    private void checkResult(Page<BundleStatsEntity> bundleStatsEntityPage) {
        long total = bundleStatsEntityPage.getTotal();
        Assert.assertEquals(total, 1);
        bundleStatsEntityPage.getResult().forEach((result) -> {
            Assert.assertEquals(result.getBroker(), "test-broker");
            Assert.assertEquals(result.getTenant(), "public");
            Assert.assertEquals(result.getNamespace(), "default");
            Assert.assertEquals(result.getBundle(), "0x80000000_0x90000000");
            Assert.assertEquals(result.getMsgRateIn(), 0.0, 1);
            Assert.assertEquals(result.getMsgRateOut(), 0.0, 1);
            Assert.assertEquals(result.getMsgThroughputIn(), 0.0, 1);
            Assert.assertEquals(result.getMsgThroughputOut(), 0.0, 1);
            Assert.assertEquals(result.getConsumerCount(), 1);
            Assert.assertEquals(result.getProducerCount(), 1);
            Assert.assertEquals(result.getTopics(), 1);
            Assert.assertEquals(result.getCacheSize(), 0);
        });
    }

    private void checkDeleteResult(Page<BundleStatsEntity> bundleStatsEntityPage) {
        long total = bundleStatsEntityPage.getTotal();
        Assert.assertEquals(total, 0);
    }

    @Test
    public void getBundleStatsList() {
        BundleStatsEntity bundleStatsEntity = new BundleStatsEntity();
        initBundleStatsEntity(bundleStatsEntity);
        bundleStatsRepository.save(bundleStatsEntity);
        Page<BundleStatsEntity> bundlesEntityPage = bundleStatsRepository.getBundleStatsList(1, 2);
        bundlesEntityPage.count(true);
        checkResult(bundlesEntityPage);
        bundlesEntityPage.getResult().forEach((result) -> {
            bundleStatsRepository.remove(
                    result.getBroker(), result.getTenant(), result.getNamespace(), result.getBundle());
        });
        Page<BundleStatsEntity> deleteBundleStats = bundleStatsRepository.getBundleStatsList(1, 2);
        deleteBundleStats.count(true);
        checkDeleteResult(deleteBundleStats);
    }

    @Test
    public void getByBrokerOrTenantOrNamespaceOrbundle() {
        BundleStatsEntity bundleStatsEntity = new BundleStatsEntity();
        initBundleStatsEntity(bundleStatsEntity);
        bundleStatsRepository.save(bundleStatsEntity);
        String broker = "test-broker";
        Page<BundleStatsEntity> bundleStatsEntityPageByBroker = bundleStatsRepository
                .findByBrokerOrTenantOrNamespaceOrBundle(1, 2, broker);
        bundleStatsEntityPageByBroker.count(true);
        checkResult(bundleStatsEntityPageByBroker);

        String tenant = "public";
        Page<BundleStatsEntity> bundleStatsEntityPageByTenant = bundleStatsRepository
                .findByBrokerOrTenantOrNamespaceOrBundle(1, 2, tenant);
        bundleStatsEntityPageByTenant.count(true);
        checkResult(bundleStatsEntityPageByTenant);

        String namespace = "default";
        Page<BundleStatsEntity> bundleStatsEntityPageByNamespace = bundleStatsRepository
                .findByBrokerOrTenantOrNamespaceOrBundle(1, 2, namespace);
        bundleStatsEntityPageByNamespace.count(true);
        checkResult(bundleStatsEntityPageByNamespace);

        String bundle = "0x80000000_0x90000000";
        Page<BundleStatsEntity> bundleStatsEntityPageByBundle = bundleStatsRepository
                .findByBrokerOrTenantOrNamespaceOrBundle(1,2, bundle);
        bundleStatsEntityPageByBundle.count(true);
        checkResult(bundleStatsEntityPageByBundle);

        bundleStatsEntityPageByBundle.getResult().forEach((result) -> {
            bundleStatsRepository.remove(result.getBroker(), result.getTenant(), result.getNamespace(), result.getBundle());
        });

        Page<BundleStatsEntity> deleteBundle = bundleStatsRepository.getBundleStatsList(1, 2);
        deleteBundle.count(true);
        checkDeleteResult(deleteBundle);
    }
}
