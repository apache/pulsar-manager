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
import org.apache.pulsar.manager.entity.TenantEntity;
import org.apache.pulsar.manager.entity.TenantsRepository;
import org.apache.pulsar.manager.profiles.HerdDBTestProfile;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = {
                PulsarManagerApplication.class,
                HerdDBTestProfile.class
        }
)
@ActiveProfiles("test")
public class TenantsRepositoryImplTest {

    @Autowired
    private TenantsRepository tenantsRepository;

    @Test
    public void getTenantsListTest() {
        for (int i = 0; i < 10; i++) {
            TenantEntity tenantEntity = new TenantEntity();
            tenantEntity.setTenant("test" + i);
            tenantEntity.setAdminRoles("test" + i);
            tenantEntity.setAllowedClusters("test-cluster");
            tenantEntity.setEnvironmentName("test-environment");
            tenantsRepository.save(tenantEntity);
        }
        Page<TenantEntity> tenantsEntities = tenantsRepository.getTenantsList(1, 10);
        tenantsEntities.count(true);
        long total = tenantsEntities.getTotal();
        Assert.assertEquals(10, total);
        List<TenantEntity> tenantsEntityList = tenantsEntities.getResult();
        for (int i = 0; i < total; i ++) {
            TenantEntity tenantEntity = tenantsEntityList.get(i);
            Assert.assertEquals(tenantEntity.getTenant(), tenantEntity.getAdminRoles());
            Assert.assertEquals("test-cluster", tenantEntity.getAllowedClusters());
            Assert.assertEquals("test-environment", tenantEntity.getEnvironmentName());
        }
        tenantsEntities.getResult().forEach((result) -> {
            tenantsRepository.remove(result.getTenant());
        });
    }

    @Test
    public void findByMultiIdTest() {
        List<Long> idList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            TenantEntity tenantEntity = new TenantEntity();
            tenantEntity.setTenant("test" + i);
            tenantEntity.setAdminRoles("test" + i);
            tenantEntity.setAllowedClusters("test-cluster");
            tenantEntity.setEnvironmentName("test-environment");
            Long tenantId = tenantsRepository.save(tenantEntity);
            idList.add(tenantId);
        }
        Page<TenantEntity> tenantEntityPage = tenantsRepository.findByMultiId(1, 10, idList);
        tenantEntityPage.count(true);
        long total = tenantEntityPage.getTotal();
        Assert.assertEquals(total, 10);
        List<TenantEntity> tenantsEntityList = tenantEntityPage.getResult();
        for (int i = 0; i < total; i ++) {
            TenantEntity tenantEntity = tenantsEntityList.get(i);
            Assert.assertEquals(tenantEntity.getTenant(), tenantEntity.getAdminRoles());
            Assert.assertEquals("test-cluster", tenantEntity.getAllowedClusters());
            Assert.assertEquals("test-environment", tenantEntity.getEnvironmentName());
        }
        tenantEntityPage.getResult().forEach((result) -> {
            tenantsRepository.remove(result.getTenant());
        });
    }

    @Test
    public void findByNameTest() {
        TenantEntity tenantEntity = new TenantEntity();
        tenantEntity.setTenant("test");
        tenantEntity.setAdminRoles("test-role");
        tenantEntity.setAllowedClusters("test-cluster");
        tenantEntity.setEnvironmentName("test-environment");
        tenantsRepository.save(tenantEntity);
        Optional<TenantEntity> result = tenantsRepository.findByName("test");
        TenantEntity getTenantEntity = result.get();
        Assert.assertEquals("test", getTenantEntity.getTenant());
        Assert.assertEquals("test-role", getTenantEntity.getAdminRoles());
        Assert.assertEquals("test-cluster", getTenantEntity.getAllowedClusters());
        Assert.assertEquals("test-environment", getTenantEntity.getEnvironmentName());
        tenantsRepository.remove("test");
    }

    @Test
    public void findByTenantId() {
        TenantEntity tenantEntity = new TenantEntity();
        tenantEntity.setTenant("test");
        tenantEntity.setAdminRoles("test-role");
        tenantEntity.setAllowedClusters("test-cluster");
        tenantEntity.setEnvironmentName("test-environment");
        long tenantId = tenantsRepository.save(tenantEntity);
        Optional<TenantEntity> result = tenantsRepository.findByTenantId(tenantId);
        TenantEntity getTenantEntity = result.get();
        Assert.assertEquals("test", getTenantEntity.getTenant());
        Assert.assertEquals("test-role", getTenantEntity.getAdminRoles());
        Assert.assertEquals("test-cluster", getTenantEntity.getAllowedClusters());
        Assert.assertEquals("test-environment", getTenantEntity.getEnvironmentName());
    }
}