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

import java.util.HashMap;
import java.util.Map;

import org.apache.pulsar.manager.PulsarManagerApplication;
import org.apache.pulsar.manager.profiles.HerdDBTestProfile;
import org.apache.pulsar.manager.service.impl.PulsarAdminServiceImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(
    classes = {
        PulsarManagerApplication.class,
        HerdDBTestProfile.class
    }
)
@ActiveProfiles("test")
public class PulsarAdminServiceImplTest {

    @Autowired
    private PulsarAdminService pulsarAdminService;

    @After
    public void teardown() {
        ((PulsarAdminServiceImpl) pulsarAdminService).destroy();
        ReflectionTestUtils.setField(pulsarAdminService, "pulsarAdmins", new HashMap<>());
    }

    @Test
    public void getPulsarAdminTest() {
        String serviceUrl = pulsarAdminService.getPulsarAdmin("http://localhost:8080").getServiceUrl();
        Assert.assertEquals("http://localhost:8080", serviceUrl);
    }

    @Test
    public void getAuthHeaderTest() {
        ReflectionTestUtils.setField(pulsarAdminService, "authPlugin", "org.apache.pulsar.client.impl.auth.AuthenticationToken");
        ReflectionTestUtils.setField(pulsarAdminService, "authParams", "test");
        Map<String, String> authHeader = pulsarAdminService.getAuthHeader("http://localhost:8080");
        Assert.assertEquals("Bearer test", authHeader.get("Authorization"));
    }
}
