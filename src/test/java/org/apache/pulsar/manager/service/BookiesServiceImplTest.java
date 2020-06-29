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
import org.apache.pulsar.manager.PulsarManagerApplication;
import org.apache.pulsar.manager.profiles.HerdDBTestProfile;
import org.apache.pulsar.manager.utils.HttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PowerMockIgnore( {"javax.*", "sun.*", "com.sun.*", "org.xml.*", "org.w3c.*"})
@PrepareForTest(HttpUtil.class)
@TestPropertySource(locations= "classpath:test-bookie.properties")
@SpringBootTest(
    classes = {
        PulsarManagerApplication.class,
        HerdDBTestProfile.class
    }
)
@ActiveProfiles("test")
public class BookiesServiceImplTest {

    @Autowired
    private BookiesService bookiesService;

    @Value("${backend.jwt.token}")
    private static String pulsarJwtToken;

    @Test
    public void bookiesServiceTest() {
        PowerMockito.mockStatic(HttpUtil.class);
        Map<String, String> header = Maps.newHashMap();
        header.put("Content-Type", "application/json");
        if (StringUtils.isNotBlank(pulsarJwtToken)) {
            header.put("Authorization", String.format("Bearer %s", pulsarJwtToken));
        }
        PowerMockito.when(HttpUtil.doGet("http://localhost:8050/api/v1/bookie/list_bookies?type=rw&print_hostnames=true", header))
                .thenReturn("{\"192.168.2.116:3181\" : \"192.168.2.116\"}");
        PowerMockito.when(HttpUtil.doGet("http://localhost:8080/admin/v2/brokers/standalone", header))
                .thenReturn("{ }");
        PowerMockito.when(HttpUtil.doGet("http://localhost:8050/api/v1/bookie/list_bookie_info", header))
                .thenReturn("{\"192.168.2.116:3181\" : \": {Free: 48920571904(48.92GB), Total: 250790436864(250.79GB)}," +
                        "\",\"ClusterInfo: \" : \"{Free: 48920571904(48.92GB), Total: 250790436864(250.79GB)}\" }");
        Map<String, Object> result = bookiesService.getBookiesList(1, 1, "standalone");
        Assert.assertEquals(1, result.get("total"));
        Assert.assertEquals("[{storage=[48920571904, 250790436864], bookie=192.168.2.116:3181, status=rw}]", result.get("data").toString());
        Assert.assertEquals(1, result.get("pageSize"));
    }
}
