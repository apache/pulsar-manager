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

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import org.apache.pulsar.client.admin.Namespaces;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.client.admin.Tenants;
import org.apache.pulsar.common.policies.data.TenantInfo;
import org.apache.pulsar.manager.PulsarManagerApplication;
import org.apache.pulsar.manager.profiles.HerdDBTestProfile;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


@RunWith(SpringRunner.class)
@SpringBootTest(
    classes = {
        PulsarManagerApplication.class,
        HerdDBTestProfile.class
    }
)
@ActiveProfiles("test")
public class TenantsServiceImplTest {

    @Autowired
    private TenantsService tenantsService;

    @MockBean
    private PulsarAdminService pulsarAdminService;

    @Mock
    private Tenants tenants;

    @Mock
    private Namespaces namespaces;

    @Test
    public void tenantsServiceImplTest() throws PulsarAdminException {
        Mockito.when(pulsarAdminService.tenants("http://localhost:8080")).thenReturn(tenants);
        Mockito.when(tenants.getTenants()).thenReturn(Arrays.asList("public"));
        TenantInfo tenantInfo = new TenantInfo(Sets.newHashSet("admin"), Sets.newHashSet("standalone"));
        Mockito.when(tenants.getTenantInfo("public")).thenReturn(tenantInfo);
        Mockito.when(pulsarAdminService.namespaces("http://localhost:8080")).thenReturn(namespaces);
        Mockito.when(namespaces.getNamespaces("public")).thenReturn(Arrays.asList("public/default"));

        Map<String, Object> objectMap = tenantsService.getTenantsList(1, 2, "http://localhost:8080");
        Assert.assertEquals(1, objectMap.get("total"));
        Assert.assertEquals(1, objectMap.get("pageSize"));
        Assert.assertEquals(1, objectMap.get("pageNum"));
        List<Map<String, Object>> tenantsArray = new ArrayList<>();
        Map<String, Object> tenantMap = Maps.newHashMap();
        tenantMap.put("adminRoles", "admin");
        tenantMap.put("allowedClusters", "standalone");
        tenantMap.put("tenant", "public");
        tenantMap.put("namespaces", 1);
        tenantsArray.add(tenantMap);
        Assert.assertEquals(tenantsArray, objectMap.get("data"));
    }

    @Test
    public void createTenantTest() throws PulsarAdminException {
        String tenant = "test";
        String role = "test-role";
        String cluster = "test-cluster";
        Mockito.when(pulsarAdminService.tenants("http://localhost:8080")).thenReturn(tenants);
        TenantInfo tenantInfo = new TenantInfo(Sets.newHashSet(role), Sets.newHashSet(cluster));
        Mockito.doNothing().when(tenants).createTenant(tenant, tenantInfo);
        Map<String, String> createTenantResult =  tenantsService.createTenant(
                tenant, role, cluster, "http://localhost:8080");
        Assert.assertEquals("Create tenant success", createTenantResult.get("message"));
    }
}
