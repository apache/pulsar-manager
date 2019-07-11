package com.manager.pulsar.dao;

import com.github.pagehelper.Page;
import com.manager.pulsar.entity.BrokersEntity;
import com.manager.pulsar.entity.BrokersRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

/**
 * Brokers crud test.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MyBatisBrokersRepositoryTest {

    @Autowired
    private BrokersRepository brokersRepository;

    private final String bundleStats = "{\"public/default/0x80000000_0x90000000\":{\"msgRateIn\":0.0," +
            "\"msgThroughputIn\":0.0\",\"msgRateOut\":0.0,\"msgThroughputOut\":0.0,\"consumerCount\":1," +
            "\"producerCount\":0,\"topics\":1,\"cacheSize\":0},\"public/functions/0x40000000_0x80000000\":" +
            "{\"msgRateIn\":0.0,\"msgThroughputIn\":0.0,\"msgRateOut\":0.0,\"msgThroughputOut\":0.0," +
            "\"consumerCount\":3,\"producerCount\":2,\"topics\":3,\"cacheSize\":0}," +
            "\"public/default/0x20000000_0x30000000\":\"{\"msgRateIn\":0.0,\"msgThroughputIn\":0.0," +
            "\"msgRateOut\":0.0,\"msgThroughputOut\":0.0,\"consumerCount\":1,\"producerCount\":1," +
            "\"topics\":1,\"cacheSize\":0}";

    private void initBrokerEntity(BrokersEntity brokersEntity) {
        brokersEntity.setBroker("test-broker");
        brokersEntity.setWebServiceUrl("http://tengdeMBP:8080");
        brokersEntity.setWebServiceUrlTls("https://tengdeMBP:8080");
        brokersEntity.setPulsarServiceUrl("pulsar://tengdeMBP:6650");
        brokersEntity.setPulsarServiceUrlTls("pulsar+ssl://tengdeMBP:6650");
        brokersEntity.setPersistentTopicsEnabled(true);
        brokersEntity.setNonPersistentTopicsEnabled(false);
        brokersEntity.setCpuUsage(148.69);
        brokersEntity.setCpuLimit(800.0);
        brokersEntity.setMemoryUsage(59.375);
        brokersEntity.setMemoryLimit(2048.0);
        brokersEntity.setDirectMemoryUsage(256.0);
        brokersEntity.setDirectMemoryLimit(4096.0);
        brokersEntity.setBandwidthInUsage(256.0);
        brokersEntity.setBandwidthInLimit(-1.0);
        brokersEntity.setBandwidthOutUsage(-1.0);
        brokersEntity.setBandwidthOutLimit(-1.0);
        brokersEntity.setMsgThroughputIn(0.0);
        brokersEntity.setMsgThroughputOut(0.0);
        brokersEntity.setMsgRateIn(0.0);
        brokersEntity.setMsgRateOut(0.0);
        brokersEntity.setLastUpdate(1560758470609L);
        brokersEntity.setLastStats(bundleStats);
        brokersEntity.setBundleStats(bundleStats);
        brokersEntity.setNumTopics(5);
        brokersEntity.setNumBundles(3);
        brokersEntity.setNumProducers(5);
        brokersEntity.setNumConsumers(3);
        brokersEntity.setBundles("[\"public/default/0x80000000_0x90000000\"," +
                "\"public/functions/0x40000000_0x80000000\",\"public/default/0x20000000_0x30000000\"]");
        brokersEntity.setLastBundleGains("[]");
        brokersEntity.setLastBundleLosses("[]");
        brokersEntity.setBrokerVersionString("2.4.0-SNAPSHOT");
        brokersEntity.setLoadReportType("LocalBrokerData");
        brokersEntity.setMaxResourceUsage(0.185);
    }

    private void checkResult(Page<BrokersEntity> brokersEntityPage) {
        long total = brokersEntityPage.getTotal();
        Assert.assertEquals(total, 1);
        brokersEntityPage.getResult().forEach((result) -> {
            Assert.assertEquals(result.getBroker(), "test-broker");
            Assert.assertEquals(result.getWebServiceUrl(), "http://tengdeMBP:8080");
            Assert.assertEquals(result.getWebServiceUrlTls(), "https://tengdeMBP:8080");
            Assert.assertEquals(result.getPulsarServiceUrl(), "pulsar://tengdeMBP:6650");
            Assert.assertEquals(result.getPulsarServiceUrlTls(), "pulsar+ssl://tengdeMBP:6650");
            Assert.assertTrue(result.isPersistentTopicsEnabled());
            Assert.assertFalse(result.isNonPersistentTopicsEnabled());
            Assert.assertEquals(result.getCpuUsage(), 148.69, 2);
            Assert.assertEquals(result.getCpuLimit(), 800.1, 1);
            Assert.assertEquals(result.getMemoryUsage(), 59.375, 3);
            Assert.assertEquals(result.getMemoryLimit(), 2048.0, 1);
            Assert.assertEquals(result.getDirectMemoryUsage(), 256.0, 1);
            Assert.assertEquals(result.getDirectMemoryLimit(), 4096.0, 1);
            Assert.assertEquals(result.getBandwidthInLimit(), -1.0, 1);
            Assert.assertEquals(result.getBandwidthInUsage(), 256.0, 1);
            Assert.assertEquals(result.getBandwidthOutLimit(), -1.0, 1);
            Assert.assertEquals(result.getBandwidthOutUsage(), -1.0, 1);
            Assert.assertEquals(result.getMsgThroughputIn(), 0.0, 1);
            Assert.assertEquals(result.getMsgThroughputOut(), 0.0, 1);
            Assert.assertEquals(result.getMsgRateIn(), 0.0, 1);
            Assert.assertEquals(result.getMsgRateOut(), 0.0, 1);
            Assert.assertEquals(result.getLastUpdate(), 1560758470609L);
            Assert.assertEquals(result.getLastStats(), bundleStats);
            Assert.assertEquals(result.getBundleStats(), bundleStats);
            Assert.assertEquals(result.getNumTopics(), 5);
            Assert.assertEquals(result.getNumBundles(), 3);
            Assert.assertEquals(result.getNumProducers(), 5);
            Assert.assertEquals(result.getNumConsumers(), 3);
            Assert.assertEquals(result.getBundles(), "[\"public/default/0x80000000_0x90000000\"," +
                    "\"public/functions/0x40000000_0x80000000\",\"public/default/0x20000000_0x30000000\"]");
            Assert.assertEquals(result.getLastBundleGains(), "[]");
            Assert.assertEquals(result.getLastBundleLosses(), "[]");
            Assert.assertEquals(result.getBrokerVersionString(), "2.4.0-SNAPSHOT");
            Assert.assertEquals(result.getLoadReportType(), "LocalBrokerData");
            Assert.assertEquals(result.getMaxResourceUsage(), 0.185, 3);
        });
    }

    private void checkDeleteResult(Page<BrokersEntity> brokersEntityPage) {
        long total = brokersEntityPage.getTotal();
        Assert.assertEquals(total, 0);
    }

    @Test
    public void getBrokersList() {
        BrokersEntity brokersEntity = new BrokersEntity();
        initBrokerEntity(brokersEntity);
        brokersRepository.save(brokersEntity);
        Page<BrokersEntity> brokersEntityPage = brokersRepository.getBrokersList(1, 2);
        brokersEntityPage.count(true);
        checkResult(brokersEntityPage);
        brokersEntityPage.getResult().forEach((result) -> {
            brokersRepository.remove(result.getBroker());
        });
        Page<BrokersEntity> deleteBroker = brokersRepository.getBrokersList(1, 2);
        deleteBroker.count(true);
        checkDeleteResult(deleteBroker);
    }

    @Test
    public void getBroker() {
        BrokersEntity brokersEntity = new BrokersEntity();
        initBrokerEntity(brokersEntity);
        brokersRepository.save(brokersEntity);
        String broker = "test-broker";
        Optional<BrokersEntity> brokerEntity = brokersRepository.findByBroker(broker);
        Page<BrokersEntity> brokersEntityPage = new Page<BrokersEntity>();
        brokersEntityPage.add(brokerEntity.get());
        brokersEntityPage.setTotal(1);
        checkResult(brokersEntityPage);
        brokersRepository.remove("test-broker");
        Page<BrokersEntity> deleteBroker = brokersRepository.getBrokersList(1, 2);
        deleteBroker.count(true);
        checkDeleteResult(deleteBroker);
    }
}
