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
import org.apache.pulsar.manager.entity.NamespaceEntity;
import org.apache.pulsar.manager.entity.NamespacesRepository;
import org.apache.pulsar.manager.entity.TenantEntity;
import org.apache.pulsar.manager.entity.TenantsRepository;
import org.apache.pulsar.manager.profiles.HerdDBTestProfile;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
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
public class NamespacesRepositoryImplTest {

    @Autowired
    private TenantsRepository tenantsRepository;

    @Autowired
    private NamespacesRepository namespacesRepository;

    public void initNamespaceEntity(NamespaceEntity namespacesEntity) {
        namespacesEntity.setTenant("test-namespace-public");
        namespacesEntity.setNamespace("test-namespace-default");
    }

    public void checkResult(Page<NamespaceEntity> namespacesEntityPage) {
        long total = namespacesEntityPage.getTotal();
        Assert.assertEquals(1, total);
        namespacesEntityPage.getResult().forEach((result) -> {
            Assert.assertEquals("test-namespace-public", result.getTenant());
            Assert.assertEquals("test-namespace-default", result.getNamespace());
        });
    }

    public void checkDeleteResult(Page<NamespaceEntity> namespacesEntityPage) {
        long total = namespacesEntityPage.getTotal();
        Assert.assertEquals(0, total);
    }

    @Before
    public void setup() {
        prepareTenant();
    }

    @After
    public void clear() {
        clearTenant();
    }

    public void prepareTenant() {
        TenantEntity tenantsEntity = new TenantEntity(
                1, "test-namespace-public",
                "testrole", "testCluster", "test-environment");
        tenantsRepository.save(tenantsEntity);
    }

    public void clearTenant() {
        tenantsRepository.remove("test-namespace-public");
    }

    @Test
    public void getNamespacesListTest() {
        NamespaceEntity namespacesEntity = new NamespaceEntity();
        initNamespaceEntity(namespacesEntity);
        namespacesRepository.save(namespacesEntity);
        Page<NamespaceEntity> namespacesEntityPage = namespacesRepository.getNamespacesList(1, 2);
        namespacesEntityPage.count(true);
        checkResult(namespacesEntityPage);
        namespacesEntityPage.getResult().forEach((result) -> {
            namespacesRepository.remove(result.getTenant(), result.getNamespace());
        });
        Page<NamespaceEntity> deleteNamespace = namespacesRepository.getNamespacesList(1, 2);
        deleteNamespace.count(true);
        checkDeleteResult(deleteNamespace);
    }

    @Test
    public void getNamespaceByTenantOrNamespaceTest() {
        NamespaceEntity namespacesEntity = new NamespaceEntity();
        initNamespaceEntity(namespacesEntity);
        namespacesRepository.save(namespacesEntity);
        String tenant = "test-namespace-public";
        Page<NamespaceEntity> namespacesEntityPageByTenant = namespacesRepository.
                findByTenantOrNamespace(1, 2, tenant);
        namespacesEntityPageByTenant.count(true);
        checkResult(namespacesEntityPageByTenant);
        String namespace = "test-namespace-default";
        Page<NamespaceEntity> namespacesEntityPageByNamespace = namespacesRepository.
                findByTenantOrNamespace(1, 2, namespace);
        namespacesEntityPageByNamespace.count(true);
        checkResult(namespacesEntityPageByNamespace);
        namespacesEntityPageByNamespace.getResult().forEach((result) -> {
            namespacesRepository.remove(result.getTenant(), result.getNamespace());
        });
        Page<NamespaceEntity> deleteNamespace = namespacesRepository.getNamespacesList(1, 2);
        deleteNamespace.count(true);
        checkDeleteResult(deleteNamespace);
    }

    @Test
    public void findByTenantTest() {
        NamespaceEntity namespacesEntity = new NamespaceEntity();
        initNamespaceEntity(namespacesEntity);
        namespacesRepository.save(namespacesEntity);
        Page<NamespaceEntity> namespacesEntityPage = namespacesRepository.findByTenant(
                1, 2, namespacesEntity.getTenant());
        namespacesEntityPage.count(true);
        checkResult(namespacesEntityPage);
        namespacesEntityPage.getResult().forEach((result) -> {
            namespacesRepository.remove(result.getTenant(), result.getNamespace());
        });
    }

    @Test
    public void findByMultiIdTest() {
        NamespaceEntity namespacesEntity = new NamespaceEntity();
        initNamespaceEntity(namespacesEntity);
        long namespaceId = namespacesRepository.save(namespacesEntity);
        List<Long> idList = new ArrayList<>();
        idList.add(namespaceId);
        Page<NamespaceEntity> namespacesEntityPage = namespacesRepository.findByMultiId(
                1, 2, idList);
        namespacesEntityPage.count(true);
        checkResult(namespacesEntityPage);
        namespacesEntityPage.getResult().forEach((result) -> {
            namespacesRepository.remove(result.getTenant(), result.getNamespace());
        });
    }

    @Test
    public void findByTenantNamespaceTest() {
        NamespaceEntity namespacesEntity = new NamespaceEntity();
        initNamespaceEntity(namespacesEntity);
        long namespaceId = namespacesRepository.save(namespacesEntity);
        List<Long> idList = new ArrayList<>();
        idList.add(namespaceId);
        Optional<NamespaceEntity> namespacesEntityOptional = namespacesRepository.findByTenantNamespace(
                namespacesEntity.getTenant(), namespacesEntity.getNamespace());
        Assert.assertEquals("test-namespace-public", namespacesEntityOptional.get().getTenant());
        Assert.assertEquals("test-namespace-default", namespacesEntityOptional.get().getNamespace());
        namespacesRepository.remove(namespacesEntity.getTenant(), namespacesEntity.getNamespace());
    }

    @Test
    public void findByNamespaceIdTest() {
        NamespaceEntity namespacesEntity = new NamespaceEntity();
        initNamespaceEntity(namespacesEntity);
        long namespaceId = namespacesRepository.save(namespacesEntity);
        Optional<NamespaceEntity> namespacesEntityOptional = namespacesRepository.findByNamespaceId(namespaceId);
        Assert.assertEquals("test-namespace-public", namespacesEntityOptional.get().getTenant());
        Assert.assertEquals("test-namespace-default", namespacesEntityOptional.get().getNamespace());
        namespacesRepository.remove(namespacesEntity.getTenant(), namespacesEntity.getNamespace());
    }

    @Test
    public void findByNamespaceTest() {
        NamespaceEntity namespacesEntity = new NamespaceEntity();
        initNamespaceEntity(namespacesEntity);
        namespacesRepository.save(namespacesEntity);
        Page<NamespaceEntity> namespacesEntityPage = namespacesRepository.findByNamespace(
                1, 2, namespacesEntity.getNamespace());
        namespacesEntityPage.count(true);
        checkResult(namespacesEntityPage);
        namespacesEntityPage.getResult().forEach((result) -> {
            namespacesRepository.remove(result.getTenant(), result.getNamespace());
        });
    }

}