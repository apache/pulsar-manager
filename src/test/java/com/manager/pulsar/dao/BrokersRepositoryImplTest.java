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
import com.manager.pulsar.PulsarManagerApplication;
import com.manager.pulsar.entity.BrokerEntity;
import com.manager.pulsar.entity.BrokersRepository;
import com.manager.pulsar.profiles.SqliteDBTestProfile;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Brokers crud test.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
    classes = {
        PulsarManagerApplication.class,
        SqliteDBTestProfile.class
    }
)
@ActiveProfiles("test")
public class BrokersRepositoryImplTest {

    @Autowired
    private BrokersRepository brokersRepository;

    private void initBrokerEntity(BrokerEntity brokersEntity) {
        brokersEntity.setBrokerId(1);
        brokersEntity.setBroker("test-broker");
        brokersEntity.setWebServiceUrl("http://tengdeMBP:8080");
        brokersEntity.setWebServiceUrlTls("https://tengdeMBP:8080");
        brokersEntity.setPulsarServiceUrl("pulsar://tengdeMBP:6650");
        brokersEntity.setPulsarServiceUrlTls("pulsar+ssl://tengdeMBP:6650");
        brokersEntity.setPersistentTopicsEnabled(true);
        brokersEntity.setNonPersistentTopicsEnabled(false);
        brokersEntity.setBrokerVersionString("2.4.0-SNAPSHOT");
        brokersEntity.setLoadReportType("LocalBrokerData");
        brokersEntity.setMaxResourceUsage(0.185);
    }

    private void checkResult(Page<BrokerEntity> brokersEntityPage) {
        long total = brokersEntityPage.getTotal();
        Assert.assertEquals(total, 1);
        brokersEntityPage.getResult().forEach((result) -> {
            Assert.assertEquals(result.getBrokerId(), 1);
            Assert.assertEquals(result.getBroker(), "test-broker");
            Assert.assertEquals(result.getWebServiceUrl(), "http://tengdeMBP:8080");
            Assert.assertEquals(result.getWebServiceUrlTls(), "https://tengdeMBP:8080");
            Assert.assertEquals(result.getPulsarServiceUrl(), "pulsar://tengdeMBP:6650");
            Assert.assertEquals(result.getPulsarServiceUrlTls(), "pulsar+ssl://tengdeMBP:6650");
            Assert.assertTrue(result.isPersistentTopicsEnabled());
            Assert.assertFalse(result.isNonPersistentTopicsEnabled());
            Assert.assertEquals(result.getBrokerVersionString(), "2.4.0-SNAPSHOT");
            Assert.assertEquals(result.getLoadReportType(), "LocalBrokerData");
            Assert.assertEquals(result.getMaxResourceUsage(), 0.185, 3);
        });
    }

    private void checkDeleteResult(Page<BrokerEntity> brokersEntityPage) {
        long total = brokersEntityPage.getTotal();
        Assert.assertEquals(total, 0);
    }

    @Test
    public void getBrokersList() {
        BrokerEntity brokersEntity = new BrokerEntity();
        initBrokerEntity(brokersEntity);
        brokersRepository.save(brokersEntity);
        Page<BrokerEntity> brokersEntityPage = brokersRepository.getBrokersList(1, 2);
        brokersEntityPage.count(true);
        checkResult(brokersEntityPage);
        brokersEntityPage.getResult().forEach((result) -> {
            brokersRepository.remove(result.getBroker());
        });
        Page<BrokerEntity> deleteBroker = brokersRepository.getBrokersList(1, 2);
        deleteBroker.count(true);
        checkDeleteResult(deleteBroker);
    }

    @Test
    public void getBroker() {
        BrokerEntity brokersEntity = new BrokerEntity();
        initBrokerEntity(brokersEntity);
        brokersRepository.save(brokersEntity);
        String broker = "test-broker";
        Optional<BrokerEntity> brokerEntity = brokersRepository.findByBroker(broker);
        Page<BrokerEntity> brokersEntityPage = new Page<BrokerEntity>();
        brokersEntityPage.add(brokerEntity.get());
        brokersEntityPage.setTotal(1);
        checkResult(brokersEntityPage);
        brokersRepository.remove("test-broker");
        Page<BrokerEntity> deleteBroker = brokersRepository.getBrokersList(1, 2);
        deleteBroker.count(true);
        checkDeleteResult(deleteBroker);
    }
}
