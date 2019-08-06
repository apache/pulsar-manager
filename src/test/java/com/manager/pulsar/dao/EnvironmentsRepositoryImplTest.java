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
import com.manager.pulsar.entity.EnvironmentEntity;
import com.manager.pulsar.entity.EnvironmentsRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
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
            Assert.assertEquals(result.getName(), "test-environment");
            Assert.assertEquals(result.getBroker(), "http://localhost:8080");
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
        Assert.assertEquals(environmentEntityGet.getName(), "test-environment");
        Assert.assertEquals(environmentEntityGet.getBroker(), "https://localhost:8080");

        environmentEntity.setBroker("https://localhost:8081");
        environmentsRepository.update(environmentEntity);
        Optional<EnvironmentEntity> environmentEntityOptionalUpdate = environmentsRepository
                .findByName("test-environment");
        EnvironmentEntity environmentEntityUpdate = environmentEntityOptionalUpdate.get();
        Assert.assertEquals(environmentEntityUpdate.getName(), "test-environment");
        Assert.assertEquals(environmentEntityUpdate.getBroker(), "https://localhost:8081");

        environmentsRepository.remove(environmentEntityUpdate.getName());
    }


}
