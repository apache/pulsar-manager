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
import com.manager.pulsar.entity.TenantsEntity;
import com.manager.pulsar.entity.TenantsRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MyBatisTenantsRepositoryTest {

    @Autowired
    private TenantsRepository tenantsRepository;

    @Test
    public void getTenantsListTest() {
        for (int i = 0; i < 10; i++) {
            TenantsEntity tenantsEntity = new TenantsEntity(
                    i + 1, "test" + (i + 1), "testrole" + (i + 1), "testCluster" + (i + 1));
            tenantsRepository.save(tenantsEntity);
        }
        Page<TenantsEntity> tenantsEntities = tenantsRepository.getTenantsList(1, 10);
        tenantsEntities.count(true);
        long total = tenantsEntities.getTotal();
        Assert.assertEquals(total, 10);
        tenantsEntities.getResult().forEach((result) -> {
            long index = result.getTenantId();
            Assert.assertEquals(result.getTenant(), "test" + index);
            Assert.assertEquals(result.getAdminRoles(), "testrole" + index);
            Assert.assertEquals(result.getAllowedClusters(), "testCluster" + index);
        });
        tenantsEntities.getResult().forEach((result) -> {
            tenantsRepository.remove(result);
        });
    }
}
