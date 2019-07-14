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
import com.manager.pulsar.entity.BrokerStatsEntity;
import com.manager.pulsar.entity.BrokerStatsRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

/**
 * Broker Stats crud test.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class BrokerStatsRepositoryImplTest {

    @Autowired
    private BrokerStatsRepository brokerStatsRepository;

    private final String bundleStats = "{\"public/default/0x80000000_0x90000000\":{\"msgRateIn\":0.0," +
            "\"msgThroughputIn\":0.0,\"msgRateOut\":0.0,\"msgThroughputOut\":0.0,\"consumerCount\":1," +
            "\"producerCount\":0,\"topics\":1,\"cacheSize\":0},\"public/functions/0x40000000_0x80000000\":" +
            "{\"msgRateIn\":0.0,\"msgThroughputIn\":0.0,\"msgRateOut\":0.0,\"msgThroughputOut\":0.0," +
            "\"consumerCount\":3,\"producerCount\":2,\"topics\":3,\"cacheSize\":0}," +
            "\"public/default/0x20000000_0x30000000\":\"{\"msgRateIn\":0.0,\"msgThroughputIn\":0.0," +
            "\"msgRateOut\":0.0,\"msgThroughputOut\":0.0,\"consumerCount\":1,\"producerCount\":1," +
            "\"topics\":1,\"cacheSize\":0}}";

    private void initBrokerStatsEntity(BrokerStatsEntity brokerStatsEntity) {
        brokerStatsEntity.setBroker("test-broker");
        brokerStatsEntity.setCpuUsage(148.69);
        brokerStatsEntity.setCpuLimit(800.0);
        brokerStatsEntity.setMemoryUsage(59.375);
        brokerStatsEntity.setMemoryLimit(2048.0);
        brokerStatsEntity.setDirectMemoryUsage(256.0);
        brokerStatsEntity.setDirectMemoryLimit(4096.0);
        brokerStatsEntity.setBandwidthInUsage(256.0);
        brokerStatsEntity.setBandwidthInLimit(-1.0);
        brokerStatsEntity.setBandwidthOutUsage(-1.0);
        brokerStatsEntity.setBandwidthOutLimit(-1.0);
        brokerStatsEntity.setMsgThroughputIn(0.0);
        brokerStatsEntity.setMsgThroughputOut(0.0);
        brokerStatsEntity.setMsgRateIn(0.0);
        brokerStatsEntity.setMsgRateOut(0.0);
        brokerStatsEntity.setLastUpdate(1560758470609L);
        brokerStatsEntity.setLastStats(bundleStats);
        brokerStatsEntity.setBundleStats(bundleStats);
        brokerStatsEntity.setNumTopics(5);
        brokerStatsEntity.setNumBundles(3);
        brokerStatsEntity.setNumProducers(5);
        brokerStatsEntity.setNumConsumers(3);
        brokerStatsEntity.setBundles("[\"public/default/0x80000000_0x90000000\"," +
                "\"public/functions/0x40000000_0x80000000\",\"public/default/0x20000000_0x30000000\"]");
        brokerStatsEntity.setLastBundleGains("[]");
        brokerStatsEntity.setLastBundleLosses("[]");
    }

    private void checkResult(Page<BrokerStatsEntity> brokerStatsEntity) {
        long total = brokerStatsEntity.getTotal();
        Assert.assertEquals(total, 1);
        brokerStatsEntity.getResult().forEach((result) -> {
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
        });
    }

    private void checkDeleteResult(Page<BrokerStatsEntity> brokerStatsEntity) {
        long total = brokerStatsEntity.getTotal();
        Assert.assertEquals(total, 0);
    }

    @Test
    public void getBrokerStatsList() {
        BrokerStatsEntity brokerStatsEntity = new BrokerStatsEntity();
        initBrokerStatsEntity(brokerStatsEntity);
        brokerStatsRepository.save(brokerStatsEntity);
        Page<BrokerStatsEntity> brokerStatsEntityPage = brokerStatsRepository.getBrokerStatsList(1, 2);
        brokerStatsEntityPage.count(true);
        checkResult(brokerStatsEntityPage);
        brokerStatsEntityPage.getResult().forEach((result) -> {
            brokerStatsRepository.remove(result.getBroker());
        });
        Page<BrokerStatsEntity> deleteBrokerStats = brokerStatsRepository.getBrokerStatsList(1, 2);
        deleteBrokerStats.count(true);
        checkDeleteResult(deleteBrokerStats);
    }

    @Test
    public void getBroker() {
        BrokerStatsEntity brokerStatsEntity = new BrokerStatsEntity();
        initBrokerStatsEntity(brokerStatsEntity);
        brokerStatsRepository.save(brokerStatsEntity);
        String broker = "test-broker";
        Optional<BrokerStatsEntity> optionalBrokerStatsEntity = brokerStatsRepository.findByBroker(broker);
        Page<BrokerStatsEntity> brokerStatsEntityPage = new Page<BrokerStatsEntity>();
        brokerStatsEntityPage.add(optionalBrokerStatsEntity.get());
        brokerStatsEntityPage.setTotal(1);
        checkResult(brokerStatsEntityPage);
        brokerStatsRepository.remove("test-broker");
        Page<BrokerStatsEntity> deleteBroker = brokerStatsRepository.getBrokerStatsList(1, 2);
        deleteBroker.count(true);
        checkDeleteResult(deleteBroker);
    }
}
