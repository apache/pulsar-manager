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
import org.apache.pulsar.manager.utils.ResourceType;
import org.apache.pulsar.manager.utils.ResourceVerbs;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.Optional;


@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PowerMockIgnore( {"javax.*", "sun.*", "com.sun.*", "org.xml.*", "org.w3c.*"})
@SpringBootTest(
        classes = {
                PulsarManagerApplication.class,
                HerdDBTestProfile.class
        }
)
@ActiveProfiles("test")
public class PulsarEventImplTest {

    @Autowired
    private PulsarEvent pulsarEvent;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private RoleBindingRepository roleBindingRepository;

    @Autowired
    private TenantsRepository tenantsRepository;

    @Autowired
    private NamespacesRepository namespacesRepository;

    private long superUserId, superRoleId, adminUserId, adminRoleId;

    @Before
    public void setup() {
        // prepare super user
        UserInfoEntity superUserInfoEntity = new UserInfoEntity();
        superUserInfoEntity.setName("superuser");
        superUserInfoEntity.setAccessToken("super-access-token");
        superUserId = usersRepository.save(superUserInfoEntity);
        TenantEntity superTenantEntity = new TenantEntity();
        superTenantEntity.setTenant("superTenant");
        superTenantEntity.setAdminRoles("super-admin-roles");
        superTenantEntity.setAllowedClusters("super-allowed-clusters");
        long superTenantId = tenantsRepository.save(superTenantEntity);
        RoleInfoEntity superRoleInfoEntity = new RoleInfoEntity();
        superRoleInfoEntity.setRoleName("super-role");
        superRoleInfoEntity.setRoleSource("superTenant");
        superRoleInfoEntity.setResourceId(superTenantId);
        superRoleInfoEntity.setFlag(0);
        superRoleInfoEntity.setResourceName("super-tenant-resource");
        superRoleInfoEntity.setResourceType(ResourceType.TENANTS.name());
        superRoleInfoEntity.setResourceVerbs(ResourceVerbs.ADMIN.name());
        superRoleId = rolesRepository.save(superRoleInfoEntity);
        RoleBindingEntity superRoleBindingEntity = new RoleBindingEntity();
        superRoleBindingEntity.setDescription("This is role binding description");
        superRoleBindingEntity.setUserId(superUserId);
        superRoleBindingEntity.setRoleId(superRoleId);
        superRoleBindingEntity.setName("super-role-binding");
        roleBindingRepository.save(superRoleBindingEntity);

        // prepare admin user
        UserInfoEntity adminUserInfoEntity = new UserInfoEntity();
        adminUserInfoEntity.setName("admin");
        adminUserInfoEntity.setAccessToken("admin-access-token");
        adminUserId = usersRepository.save(adminUserInfoEntity);
        TenantEntity adminTenantEntity = new TenantEntity();
        adminTenantEntity.setTenant("adminTenant");
        adminTenantEntity.setAdminRoles("super-admin-roles");
        adminTenantEntity.setAllowedClusters("super-allowed-clusters");
        long adminTenantId = tenantsRepository.save(adminTenantEntity);
        RoleInfoEntity adminRoleInfoEntity = new RoleInfoEntity();
        adminRoleInfoEntity.setRoleName("admin-role");
        adminRoleInfoEntity.setRoleSource("adminTenant");
        adminRoleInfoEntity.setResourceId(adminTenantId);
        adminRoleInfoEntity.setFlag(1);
        adminRoleInfoEntity.setResourceName("admin-tenant-resource");
        adminRoleInfoEntity.setResourceType(ResourceType.TENANTS.name());
        adminRoleInfoEntity.setResourceVerbs(ResourceVerbs.ADMIN.name());
        adminRoleId = rolesRepository.save(adminRoleInfoEntity);
        RoleBindingEntity adminRoleBindingEntity = new RoleBindingEntity();
        adminRoleBindingEntity.setDescription("This is role binding description");
        adminRoleBindingEntity.setUserId(adminUserId);
        adminRoleBindingEntity.setRoleId(adminRoleId);
        adminRoleBindingEntity.setName("admin-role-binding");
        roleBindingRepository.save(adminRoleBindingEntity);
    }

    @After
    public void clear() {
        usersRepository.delete("superuser");
        usersRepository.delete("admin");
        tenantsRepository.remove("superTenant");
        tenantsRepository.remove("adminTenant");
        rolesRepository.delete("super-role", "superTenant");
        rolesRepository.delete("admin-role", "adminTenant");
        roleBindingRepository.delete(superRoleId, adminUserId);
        roleBindingRepository.delete(adminRoleId, adminUserId);
    }

    @Test
    public void validateRoutePermissionTest() {
        Assert.assertTrue(pulsarEvent.validateRoutePermission("/admin/v2/clusters", "super-access-token"));
        Assert.assertTrue(pulsarEvent.validateRoutePermission("/admin/v2/brokers", "super-access-token"));
        Assert.assertTrue(
                pulsarEvent.validateRoutePermission("/admin/v2/broker-stats", "super-access-token"));
        Assert.assertTrue(
                pulsarEvent.validateRoutePermission("/admin/v2/resource-quotas", "super-access-token"));

        Assert.assertFalse(pulsarEvent.validateRoutePermission("/admin/v2/clusters", "admin-access-token"));
        Assert.assertFalse(pulsarEvent.validateRoutePermission("/admin/v2/brokers", "admin-access-token"));
        Assert.assertFalse(
                pulsarEvent.validateRoutePermission("/admin/v2/broker-stats", "admin-access-token"));
        Assert.assertFalse(
                pulsarEvent.validateRoutePermission("/admin/v2/resource-quotas", "admin-access-token"));

        Assert.assertTrue(pulsarEvent.validateRoutePermission("/admin/v2/tenants", "admin-access-token"));
    }

    @Test
    public void validateTenantPermission() {
        Map<String, String> result;
        result = pulsarEvent.validateTenantPermission("/admin/v2/tenants/superTenant", "super-access-token");
        Assert.assertEquals("Validate tenant success", result.get("message"));
        result = pulsarEvent.validateTenantPermission("/admin/v2/tenants/adminTenant", "super-access-token");
        Assert.assertEquals("This user no include this tenant", result.get("error"));

        result = pulsarEvent.validateTenantPermission(
                "/pulsar-manager/admin/v2/schemas/adminTenant/default/test-topic", "super-access-token");
        Assert.assertEquals("This resource no need validate", result.get("message"));

        result = pulsarEvent.validateTenantPermission(
                "/pulsar-manager/admin/v2/namespaces/adminTenant/default", "admin-access-token");
        Assert.assertEquals("Validate tenant success", result.get("message"));
    }

    @Test
    public void parsePulsarEvent() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("PUT");
        pulsarEvent.parsePulsarEvent("/admin/v2/namespaces/adminTenant/default/test-topic", request);
        Optional<NamespaceEntity> namespaceEntity = namespacesRepository.findByTenantNamespace(
                "adminTenant", "default");
        Assert.assertTrue(namespaceEntity.isPresent());
    }
}
