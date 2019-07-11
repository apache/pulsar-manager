package com.manager.pulsar.dao;


import com.github.pagehelper.Page;
import com.manager.pulsar.entity.BundlesEntity;
import com.manager.pulsar.entity.BundlesRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Bundles crud test.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MyBatisBundlesRespositoryTest {

    @Autowired
    private BundlesRepository bundlesRepository;

    private void initBundleEntity(BundlesEntity bundlesEntity) {
        bundlesEntity.setBroker("test-broker");
        bundlesEntity.setTenant("public");
        bundlesEntity.setNamespace("default");
        bundlesEntity.setBundle("0x80000000_0x90000000");
        bundlesEntity.setMsgRateIn(0.0);
        bundlesEntity.setMsgRateOut(0.0);
        bundlesEntity.setMsgThroughputIn(0.0);
        bundlesEntity.setMsgThroughputOut(0.0);
        bundlesEntity.setConsumerCount(1);
        bundlesEntity.setProducerCount(1);
        bundlesEntity.setTopics(1);
        bundlesEntity.setCacheSize(0);
    }

    private void checkResult(Page<BundlesEntity> bundlesEntityPage) {
        long total = bundlesEntityPage.getTotal();
        Assert.assertEquals(total, 1);
        bundlesEntityPage.getResult().forEach((result) -> {
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

    private void checkDeleteResult(Page<BundlesEntity> bundlesEntityPage) {
        long total = bundlesEntityPage.getTotal();
        Assert.assertEquals(total, 0);
    }

    @Test
    public void getBundlesList() {
        BundlesEntity bundlesEntity = new BundlesEntity();
        initBundleEntity(bundlesEntity);
        bundlesRepository.save(bundlesEntity);
        Page<BundlesEntity> bundlesEntityPage = bundlesRepository.getBundlesList(1, 2);
        bundlesEntityPage.count(true);
        checkResult(bundlesEntityPage);
        bundlesEntityPage.getResult().forEach((result) -> {
            bundlesRepository.remove(result.getBroker(), result.getTenant(), result.getNamespace(), result.getBundle());
        });
        Page<BundlesEntity> deleteBundle = bundlesRepository.getBundlesList(1, 2);
        deleteBundle.count(true);
        checkDeleteResult(deleteBundle);
    }

    @Test
    public void getByBrokerOrTenantOrNamespaceOrbundle() {
        BundlesEntity bundlesEntity = new BundlesEntity();
        initBundleEntity(bundlesEntity);
        bundlesRepository.save(bundlesEntity);
        String broker = "test-broker";
        Page<BundlesEntity> bundlesEntityPageByBroker = bundlesRepository
                .findByBrokerOrTenantOrNamespaceOrBundle(1, 2, broker);
        bundlesEntityPageByBroker.count(true);
        checkResult(bundlesEntityPageByBroker);

        String tenant = "public";
        Page<BundlesEntity> bundlesEntityPageByTenant = bundlesRepository
                .findByBrokerOrTenantOrNamespaceOrBundle(1, 2, tenant);
        bundlesEntityPageByTenant.count(true);
        checkResult(bundlesEntityPageByTenant);

        String namespace = "default";
        Page<BundlesEntity> bundlesEntityPageByNamespace = bundlesRepository
                .findByBrokerOrTenantOrNamespaceOrBundle(1, 2, namespace);
        bundlesEntityPageByNamespace.count(true);
        checkResult(bundlesEntityPageByNamespace);

        String bundle = "0x80000000_0x90000000";
        Page<BundlesEntity> bundlesEntityPageByBundle = bundlesRepository
                .findByBrokerOrTenantOrNamespaceOrBundle(1,2, bundle);
        bundlesEntityPageByBundle.count(true);
        checkResult(bundlesEntityPageByBundle);

        bundlesEntityPageByBundle.getResult().forEach((result) -> {
            bundlesRepository.remove(result.getBroker(), result.getTenant(), result.getNamespace(), result.getBundle());
        });

        Page<BundlesEntity> deleteBundle = bundlesRepository.getBundlesList(1, 2);
        deleteBundle.count(true);
        checkDeleteResult(deleteBundle);
    }
}
