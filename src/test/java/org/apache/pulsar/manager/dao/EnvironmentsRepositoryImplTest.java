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
import org.apache.pulsar.manager.entity.EnvironmentEntity;
import org.apache.pulsar.manager.entity.EnvironmentsRepository;
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
public class EnvironmentsRepositoryImplTest {

    @Autowired
    private EnvironmentsRepository environmentsRepository;

    @Test
    public void getEnvironmentsList() {
        EnvironmentEntity environmentEntity = new EnvironmentEntity();
        environmentEntity.setName("test-environment");
        environmentEntity.setBroker("http://localhost:8080");
        environmentsRepository.save(environmentEntity);
        Page<EnvironmentEntity> environmentEntityPage = environmentsRepository.getEnvironmentsList(1, 1);
        environmentEntityPage.count(true);
        environmentEntityPage.getResult().forEach((result) -> {
            Assert.assertEquals("test-environment", result.getName());
            Assert.assertEquals("http://localhost:8080", result.getBroker());
            environmentsRepository.remove(result.getName());
        });
    }

    @Test
    public void getAndUpdateEnvironmentByName() {
        EnvironmentEntity environmentEntity = new EnvironmentEntity();
        environmentEntity.setName("test-environment");
        environmentEntity.setBroker("https://localhost:8080");
        environmentsRepository.save(environmentEntity);
        Optional<EnvironmentEntity> environmentEntityOptionalGet = environmentsRepository
                .findByBroker("https://localhost:8080");
        EnvironmentEntity environmentEntityGet = environmentEntityOptionalGet.get();
        Assert.assertEquals("test-environment", environmentEntityGet.getName());
        Assert.assertEquals("https://localhost:8080", environmentEntityGet.getBroker());

        environmentEntity.setBroker("https://localhost:8081");
        environmentsRepository.update(environmentEntity);
        Optional<EnvironmentEntity> environmentEntityOptionalUpdate = environmentsRepository
                .findByName("test-environment");
        EnvironmentEntity environmentEntityUpdate = environmentEntityOptionalUpdate.get();
        Assert.assertEquals("test-environment", environmentEntityUpdate.getName());
        Assert.assertEquals("https://localhost:8081", environmentEntityUpdate.getBroker());

        environmentsRepository.remove(environmentEntityUpdate.getName());
    }


}
