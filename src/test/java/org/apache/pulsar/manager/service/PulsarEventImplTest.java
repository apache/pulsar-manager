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
import org.apache.pulsar.manager.profiles.HerdDBTestProfile;
import org.junit.Assert;
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
    private NamespacesRepository namespacesRepository;

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
