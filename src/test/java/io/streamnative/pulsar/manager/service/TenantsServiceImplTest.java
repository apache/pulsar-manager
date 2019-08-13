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
package io.streamnative.pulsar.manager.service;

import com.google.common.collect.Maps;
import io.streamnative.pulsar.manager.PulsarManagerApplication;
import io.streamnative.pulsar.manager.profiles.SqliteDBTestProfile;
import io.streamnative.pulsar.manager.utils.HttpUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PowerMockIgnore( {"javax.management.*", "javax.net.ssl.*"})
@PrepareForTest(HttpUtil.class)
@SpringBootTest(
    classes = {
        PulsarManagerApplication.class,
        SqliteDBTestProfile.class
    }
)
@ActiveProfiles("test")
public class TenantsServiceImplTest {

    @Autowired
    private TenantsService tenantsService;

    @Test
    public void tenantsServiceImplTest() {
        PowerMockito.mockStatic(HttpUtil.class);
        Map<String, String> header = Maps.newHashMap();
        header.put("Content-Type", "application/json");
        PowerMockito.when(HttpUtil.doGet("http://localhost:8080/admin/v2/tenants", header)).thenReturn("[\"public\"]");
        PowerMockito.when(HttpUtil.doGet("http://localhost:8080/admin/v2/tenants/public", header))
                .thenReturn("{\"adminRoles\": [\"admin\"], \"allowedClusters\": [\"standalone\"]}");
        PowerMockito.when(HttpUtil.doGet("http://localhost:8080/admin/v2/namespaces/public", header))
                .thenReturn("[\"public/default\"]");
        Map<String, Object> objectMap = tenantsService.getTenantsList(1, 2, "http://localhost:8080");
        Assert.assertEquals(objectMap.get("total"), 1);
        Assert.assertEquals(objectMap.get("pageSize"), 1);
        Assert.assertEquals(objectMap.get("pageNum"), 1);
        List<Map<String, Object>> tenantsArray = new ArrayList<>();
        Map<String, Object> tenantMap = Maps.newHashMap();
        tenantMap.put("adminRoles", "admin");
        tenantMap.put("allowedClusters", "standalone");
        tenantMap.put("tenant", "public");
        tenantMap.put("namespaces", 1);
        tenantsArray.add(tenantMap);
        Assert.assertEquals(objectMap.get("data"), tenantsArray);
    }
}
