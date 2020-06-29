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

import java.util.List;
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
public class RoleBindingServiceImplTest {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private RoleBindingService roleBindingService;

    @Autowired
    private TenantsRepository tenantsRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private RoleBindingRepository roleBindingRepository;

    @Test
    public void validateCurrentUserTest() {
        UserInfoEntity userInfoEntity = new UserInfoEntity();
        userInfoEntity.setName("test-user");
        userInfoEntity.setAccessToken("test-access-token");
        long userId = usersRepository.save(userInfoEntity);

        RoleBindingEntity roleBindingEntity = new RoleBindingEntity();

        Map<String, String> validateErrorUser = roleBindingService.validateCurrentUser(
                "test-error-access-token", roleBindingEntity);
        Assert.assertEquals("User no exist.", validateErrorUser.get("error"));

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
        roleBindingEntity.setUserId(userId);
        roleBindingEntity.setRoleId(roleId);
        roleBindingEntity.setName("test-role-binding");
        roleBindingEntity.setDescription("test-role-binding-description");
        roleBindingRepository.save(roleBindingEntity);

        roleBindingEntity.setRoleId(10);
        Map<String, String> validateIllegalUser = roleBindingService.validateCurrentUser(
                "test-access-token", roleBindingEntity);
        Assert.assertEquals("This operation is illegal for this user", validateIllegalUser.get("error"));

        roleBindingEntity.setRoleId(roleId);
        Map<String, String> validateSuccessUser = roleBindingService.validateCurrentUser(
                "test-access-token", roleBindingEntity);
        Assert.assertEquals("Validate current user success", validateSuccessUser.get("message"));

        roleBindingRepository.delete(roleId, userId);
        rolesRepository.delete("test-role", "test-tenant");
        tenantsRepository.remove("test-tenant");
        usersRepository.delete("test-user");
    }

    @Test
    public void validateCreateRoleBinding() {
        UserInfoEntity userInfoEntity = new UserInfoEntity();
        userInfoEntity.setName("test-user");
        userInfoEntity.setAccessToken("test-access-token");
        long userId = usersRepository.save(userInfoEntity);

        RoleBindingEntity roleBindingEntity = new RoleBindingEntity();

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
        roleBindingEntity.setUserId(userId);
        roleBindingEntity.setRoleId(roleId);
        roleBindingEntity.setName("test-role-binding");
        roleBindingEntity.setDescription("test-role-binding-description");
        roleBindingRepository.save(roleBindingEntity);

        Map<String, Object> validateErrorUser = roleBindingService.validateCreateRoleBinding(
                "test-error-access-token", "test-error-tenant",
                "test-role-name", "test-user-name");
        Assert.assertEquals("The user is not exist", validateErrorUser.get("error"));

        Map<String, Object> validateErrorRoleName = roleBindingService.validateCreateRoleBinding(
                "test-access-token", "test-tenant",
                "test-error-role", "test-user");
        Assert.assertEquals("This role is no exist", validateErrorRoleName.get("error"));

        RoleInfoEntity testRoleInfoEntity = new RoleInfoEntity();
        testRoleInfoEntity.setRoleName("test-no-binding-role");
        testRoleInfoEntity.setRoleSource("test-tenant");
        testRoleInfoEntity.setResourceId(tenantId);
        testRoleInfoEntity.setFlag(1);
        testRoleInfoEntity.setResourceName("test-no-binding-tenant-resource");
        testRoleInfoEntity.setResourceType(ResourceType.TENANTS.name());
        testRoleInfoEntity.setResourceVerbs(ResourceVerbs.ADMIN.name());
        rolesRepository.save(testRoleInfoEntity);

        TenantEntity testNoBindingTenantEntity = new TenantEntity();
        testNoBindingTenantEntity.setTenant("test-no-binding-tenant");
        testNoBindingTenantEntity.setAdminRoles("test-admin-roles");
        testNoBindingTenantEntity.setAllowedClusters("test-allowed-clusters");
        long testNoBindingTenantId = tenantsRepository.save(tenantEntity);
        RoleInfoEntity testNoBindingRoleInfoEntity = new RoleInfoEntity();
        testNoBindingRoleInfoEntity.setRoleName("test-no-binding-role");
        testNoBindingRoleInfoEntity.setRoleSource("test-no-binding-tenant");
        testNoBindingRoleInfoEntity.setResourceId(testNoBindingTenantId);
        testNoBindingRoleInfoEntity.setFlag(1);
        testNoBindingRoleInfoEntity.setResourceName("test-no-binding-tenant-resource");
        testNoBindingRoleInfoEntity.setResourceType(ResourceType.TENANTS.name());
        testNoBindingRoleInfoEntity.setResourceVerbs(ResourceVerbs.ADMIN.name());
        rolesRepository.save(testNoBindingRoleInfoEntity);

        Map<String, Object> validateBindingRole = roleBindingService.validateCreateRoleBinding(
                "test-access-token", "test-tenant",
                "test-role", "test-user");
        Assert.assertEquals("Role binding already exist", validateBindingRole.get("error"));

        Map<String, Object> validateCreateRoleBinding = roleBindingService.validateCreateRoleBinding(
                "test-access-token", "test-tenant",
                "test-no-binding-role", "test-user");
        Assert.assertEquals("Validate create role success", validateCreateRoleBinding.get("message"));

        roleBindingRepository.delete(roleId, userId);
        rolesRepository.delete("test-role", "test-tenant");
        rolesRepository.delete("test-no-binding-role", "test-no-binding-tenant");
        rolesRepository.delete("test-no-binding-role", "test-tenant");
        tenantsRepository.remove("test-tenant");
        tenantsRepository.remove("test-no-binding-tenant");
        usersRepository.delete("test-user");
    }

    @Test
    public void getRoleBindingList() {
        UserInfoEntity userInfoEntity = new UserInfoEntity();
        userInfoEntity.setName("test-user-binding");
        userInfoEntity.setAccessToken("test-access-token-binding");
        long userId = usersRepository.save(userInfoEntity);

        RoleBindingEntity roleBindingEntity = new RoleBindingEntity();

        TenantEntity tenantEntity = new TenantEntity();
        tenantEntity.setTenant("test-tenant-binding");
        tenantEntity.setAdminRoles("test-admin-roles");
        tenantEntity.setAllowedClusters("test-allowed-clusters");
        long tenantId = tenantsRepository.save(tenantEntity);
        RoleInfoEntity roleInfoEntity = new RoleInfoEntity();
        roleInfoEntity.setRoleName("test-role-binding");
        roleInfoEntity.setRoleSource("test-tenant-binding");
        roleInfoEntity.setResourceId(tenantId);
        roleInfoEntity.setFlag(1);
        roleInfoEntity.setResourceName("test-tenant-resource");
        roleInfoEntity.setResourceType(ResourceType.TENANTS.name());
        roleInfoEntity.setResourceVerbs(ResourceVerbs.ADMIN.name());
        long roleId = rolesRepository.save(roleInfoEntity);
        roleBindingEntity.setUserId(userId);
        roleBindingEntity.setRoleId(roleId);
        roleBindingEntity.setName("test-role-binding");
        roleBindingEntity.setDescription("test-role-binding-description");
        roleBindingRepository.save(roleBindingEntity);

        List<Map<String, Object>> roleBindingMap = roleBindingService.getRoleBindingList(
                "test-access-token-binding", "test-tenant-binding");
        for (Map<String, Object> stringObjectMap : roleBindingMap) {
            Assert.assertEquals("test-role-binding", stringObjectMap.get("name"));
            Assert.assertEquals(userId, stringObjectMap.get("userId"));
            Assert.assertEquals("test-user-binding", stringObjectMap.get("userName"));
            Assert.assertEquals(roleId, stringObjectMap.get("roleId"));
            Assert.assertEquals("test-role-binding", stringObjectMap.get("roleName"));
            Assert.assertEquals("test-role-binding-description", stringObjectMap.get("description"));
        }

        roleBindingRepository.delete(roleId, userId);
        rolesRepository.delete("test-role-binding", "test-tenant-binding");
        tenantsRepository.remove("test-tenant-binding");
        usersRepository.delete("test-user-binding");
    }
}
