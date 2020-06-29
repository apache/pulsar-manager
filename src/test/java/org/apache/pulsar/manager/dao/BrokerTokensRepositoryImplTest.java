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
package org.apache.pulsar.manager.dao;

import com.github.pagehelper.Page;
import org.apache.pulsar.manager.PulsarManagerApplication;
import org.apache.pulsar.manager.entity.BrokerTokenEntity;
import org.apache.pulsar.manager.entity.BrokerTokensRepository;
import org.apache.pulsar.manager.profiles.HerdDBTestProfile;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = {
                PulsarManagerApplication.class,
                HerdDBTestProfile.class
        }
)
@ActiveProfiles("test")
public class BrokerTokensRepositoryImplTest {

    @Autowired
    private BrokerTokensRepository brokerTokensRepository;

    @Test
    public void brokerTokenTest() {
        BrokerTokenEntity brokerTokenEntity = new BrokerTokenEntity();
        brokerTokenEntity.setRole("test");
        brokerTokenEntity.setDescription("This role for test");
        brokerTokenEntity.setToken("xxxxxxxxxxxxx");
        brokerTokensRepository.save(brokerTokenEntity);
        Page<BrokerTokenEntity> brokerTokenEntityPage = brokerTokensRepository.getBrokerTokensList(1, 1);
        brokerTokenEntityPage.count(true);
        brokerTokenEntityPage.getResult().forEach((result) -> {
            Assert.assertEquals(brokerTokenEntity.getRole(), result.getRole());
            Assert.assertEquals(brokerTokenEntity.getDescription(), result.getDescription());
        });

        brokerTokenEntity.setDescription("This role for update test");
        brokerTokenEntity.setToken("tokentestupdate");
        brokerTokensRepository.update(brokerTokenEntity);
        Optional<BrokerTokenEntity> optionalBrokerTokenEntity = brokerTokensRepository.findTokenByRole(brokerTokenEntity.getRole());
        BrokerTokenEntity updatedBrokerTokenEntity = optionalBrokerTokenEntity.get();
        Assert.assertEquals(brokerTokenEntity.getRole(), updatedBrokerTokenEntity.getRole());
        Assert.assertEquals(brokerTokenEntity.getDescription(), updatedBrokerTokenEntity.getDescription());

        brokerTokensRepository.remove(brokerTokenEntity.getRole());
        Assert.assertFalse(brokerTokensRepository.findTokenByRole(brokerTokenEntity.getRole()).isPresent());
    }
}
