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
package org.apache.pulsar.manager.service;

import org.apache.pulsar.manager.PulsarManagerApplication;
import org.apache.pulsar.manager.entity.NamespaceEntity;
import org.apache.pulsar.manager.entity.NamespacesRepository;
import org.apache.pulsar.manager.entity.RoleBindingEntity;
import org.apache.pulsar.manager.entity.RoleBindingRepository;
import org.apache.pulsar.manager.entity.RoleInfoEntity;
import org.apache.pulsar.manager.entity.RolesRepository;
import org.apache.pulsar.manager.entity.TenantEntity;
import org.apache.pulsar.manager.entity.TenantsRepository;
import org.apache.pulsar.manager.entity.UserInfoEntity;
import org.apache.pulsar.manager.entity.UsersRepository;
import org.apache.pulsar.manager.profiles.HerdDBTestProfile;
import org.apache.pulsar.manager.utils.HttpUtil;
import org.apache.pulsar.manager.utils.ResourceType;
import org.apache.pulsar.manager.utils.ResourceVerbs;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PowerMockIgnore( {"javax.*", "sun.*", "com.sun.*", "org.xml.*", "org.w3c.*"})
@PrepareForTest(HttpUtil.class)
@SpringBootTest(
        classes = {
                PulsarManagerApplication.class,
                HerdDBTestProfile.class
        }
)
@ActiveProfiles("test")
public class RolesServiceImplTest {

    @Autowired
    private RolesService rolesService;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private TenantsRepository tenantsRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private RoleBindingRepository roleBindingRepository;

    @Autowired
    private NamespacesRepository namespacesRepository;

    @Test
    public void validateRoleInfoEntityTest() {
        RoleInfoEntity roleInfoEntity = new RoleInfoEntity();

        Map<String, String> roleNameIsEmpty = rolesService.validateRoleInfoEntity(roleInfoEntity);
        Assert.assertEquals("Role name cannot be empty", roleNameIsEmpty.get("error"));

        roleInfoEntity.setRoleName("------");

        Map<String, String> resourceNameIsEmpty = rolesService.validateRoleInfoEntity(roleInfoEntity);
        Assert.assertEquals("Resource name cannot be empty", resourceNameIsEmpty.get("error"));

        roleInfoEntity.setResourceName("===========");

        Map<String, String> roleNameIsIllegal = rolesService.validateRoleInfoEntity(roleInfoEntity);
        Assert.assertEquals("Role name is illegal", roleNameIsIllegal.get("error"));

        roleInfoEntity.setRoleName("testRoleName");

        Map<String, String> resourceNameIsIllegal = rolesService.validateRoleInfoEntity(roleInfoEntity);
        Assert.assertEquals("Resource Name is illegal", resourceNameIsIllegal.get("error"));

        roleInfoEntity.setResourceName("testResourceName");

        roleInfoEntity.setResourceType("test-resourceType");
        Map<String, String> resourceTypeIsIllegal = rolesService.validateRoleInfoEntity(roleInfoEntity);
        Assert.assertEquals("Resource type is illegal", resourceTypeIsIllegal.get("error"));

        roleInfoEntity.setResourceId(10);
        roleInfoEntity.setResourceType(ResourceType.TENANTS.name());
        Map<String, String> resourceNoExist = rolesService.validateRoleInfoEntity(roleInfoEntity);
        Assert.assertEquals("Tenant no exist, please check", resourceNoExist.get("error"));

        TenantEntity tenantEntity = new TenantEntity();
        tenantEntity.setTenant("test-tenant");
        tenantEntity.setAdminRoles("test-admin-roles");
        tenantEntity.setAllowedClusters("test-allowed-clusters");
        long tenantId = tenantsRepository.save(tenantEntity);
        roleInfoEntity.setResourceId(tenantId);

        roleInfoEntity.setResourceId(20);
        roleInfoEntity.setResourceType(ResourceType.NAMESPACES.name());
        Map<String, String> namespaceNoExist = rolesService.validateRoleInfoEntity(roleInfoEntity);
        Assert.assertEquals("Namespace no exist, please check", namespaceNoExist.get("error"));

        NamespaceEntity namespaceEntity = new NamespaceEntity();
        namespaceEntity.setTenant("test-tenant");
        namespaceEntity.setNamespace("test-namespace");
        long namespaceId = namespacesRepository.save(namespaceEntity);
        roleInfoEntity.setResourceId(namespaceId);

        roleInfoEntity.setResourceVerbs("xxxx");
        Map<String, String> stringMapVerbs = rolesService.validateRoleInfoEntity(roleInfoEntity);
        Assert.assertTrue(stringMapVerbs.get("error").startsWith("Verb"));

        roleInfoEntity.setResourceType(ResourceType.TOPICS.name());
        roleInfoEntity.setResourceVerbs(ResourceVerbs.ADMIN.name());
        Map<String, String> stringMapTopics = rolesService.validateRoleInfoEntity(roleInfoEntity);
        Assert.assertEquals("admin should not be excluded for resources of type topic",
                stringMapTopics.get("error"));

        roleInfoEntity.setResourceType(ResourceType.TENANTS.name());
        roleInfoEntity.setResourceVerbs(ResourceVerbs.PRODUCE.name() + "," +  ResourceVerbs.CONSUME.name());
        Map<String, String> stringMapTenants = rolesService.validateRoleInfoEntity(roleInfoEntity);
        Assert.assertEquals("Type TENANTS include not supported verbs", stringMapTenants.get("error"));

        roleInfoEntity.setResourceType(ResourceType.ALL.name());
        roleInfoEntity.setResourceVerbs(ResourceVerbs.ADMIN.name());
        Map<String, String> stringMapAll = rolesService.validateRoleInfoEntity(roleInfoEntity);
        Assert.assertEquals("Role validate success", stringMapAll.get("message"));
    }

    @Test
    public void validateCurrentTenantTest() {
        UserInfoEntity userInfoEntity = new UserInfoEntity();
        userInfoEntity.setName("test-user");
        userInfoEntity.setAccessToken("test-access-token");
        long userId = usersRepository.save(userInfoEntity);

        Map<String, String> currentTenantValidateUser = rolesService.validateCurrentTenant(
                "test-error-access-token", "test-tenant");
        Assert.assertEquals(currentTenantValidateUser.get("error"), "User no exist.");

        TenantEntity tenantEntity = new TenantEntity();
        tenantEntity.setTenant("test-tenant");
        tenantEntity.setAdminRoles("test-admin-roles");
        tenantEntity.setAllowedClusters("test-allowed-clusters");
        long tenantId = tenantsRepository.save(tenantEntity);
        RoleInfoEntity roleInfoEntity = new RoleInfoEntity();
        roleInfoEntity.setRoleName("test-role");
        roleInfoEntity.setRoleSource("test-tenant");
        roleInfoEntity.setResourceId(tenantId);
        roleInfoEntity.setFlag(1);
        roleInfoEntity.setResourceName("test-tenant-resource");
        roleInfoEntity.setResourceType(ResourceType.TENANTS.name());
        roleInfoEntity.setResourceVerbs(ResourceVerbs.ADMIN.name());
        long roleId = rolesRepository.save(roleInfoEntity);

        RoleBindingEntity roleBindingEntity = new RoleBindingEntity();
        roleBindingEntity.setDescription("This is role binding description");
        roleBindingEntity.setUserId(userId);
        roleBindingEntity.setRoleId(roleId);
        roleBindingEntity.setName("test-role-binding");
        roleBindingRepository.save(roleBindingEntity);

        Map<String, String> currentTenantValidateErrorTenant = rolesService.validateCurrentTenant(
                "test-access-token", "test-error-tenant");
        Assert.assertEquals(currentTenantValidateErrorTenant.get("error"), "This user no include this tenant");

        Map<String, String> currentTenantValidateSuccess = rolesService.validateCurrentTenant(
                "test-access-token", "test-tenant");
        Assert.assertEquals(currentTenantValidateSuccess.get("message"), "Validate tenant success");
    }
}
