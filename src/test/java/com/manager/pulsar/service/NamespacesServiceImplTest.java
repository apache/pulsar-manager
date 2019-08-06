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
package com.manager.pulsar.service;

import com.google.common.collect.Maps;
import com.manager.pulsar.utils.HttpUtil;
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
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PowerMockIgnore( {"javax.management.*", "javax.net.ssl.*"})
@PrepareForTest(HttpUtil.class)
@SpringBootTest
public class NamespacesServiceImplTest {

    @Autowired
    private NamespacesService namespacesService;

    @Test
    public void namespaceServiceImplTest() {
        PowerMockito.mockStatic(HttpUtil.class);
        Map<String, String> header = Maps.newHashMap();
        header.put("Content-Type", "application/json");
        PowerMockito.when(HttpUtil.doGet("http://localhost:8080/admin/v2/namespaces/public", header))
                .thenReturn("[\"public/default\"]");

        PowerMockito.when(HttpUtil.doGet("http://localhost:8080/admin/v2/persistent/public/default", header))
                .thenReturn("[\"persistent://public/default/test789\"]");
        PowerMockito.when(HttpUtil.doGet(
                "http://localhost:8080/admin/v2/persistent/public/default/partitioned", header))
                .thenReturn("[]");
        Map<String, Object> result = namespacesService.getNamespaceList(1, 1, "public", "http://localhost:8080");
        Assert.assertEquals(result.get("total"), 1);
        Assert.assertFalse((Boolean) result.get("isPage"));
        Assert.assertEquals(result.get("data").toString(), "[{topics=1, namespace=default}]");
    }
}
