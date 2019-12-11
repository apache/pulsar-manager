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
import org.apache.pulsar.manager.entity.EnvironmentsRepository;
import org.apache.pulsar.manager.entity.RoleInfoEntity;
import org.apache.pulsar.manager.entity.RolesRepository;
import org.apache.pulsar.manager.entity.TenantsRepository;
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

    @Test
    public void validateRoleInfoEntityTest() {
        RoleInfoEntity roleInfoEntity = new RoleInfoEntity();

        roleInfoEntity.setResourceVerbs("xxxx");
        Map<String, String> stringMapVerbs = rolesService.validateRoleInfoEntity(roleInfoEntity);
        Assert.assertTrue(stringMapVerbs.get("error").startsWith("Verb"));

        roleInfoEntity.setResourceType(ResourceType.TOPICS.name());
        roleInfoEntity.setResourceVerbs(ResourceVerbs.ADMIN.name());
        Map<String, String> stringMapTopics = rolesService.validateRoleInfoEntity(roleInfoEntity);
        Assert.assertEquals(stringMapTopics.get("error"),
                "admin should not be excluded for resources of type topic");

        roleInfoEntity.setResourceType(ResourceType.TENANTS.name());
        roleInfoEntity.setResourceVerbs(ResourceVerbs.PRODUCE.name() + "," +  ResourceVerbs.CONSUME.name());
        Map<String, String> stringMapTenants = rolesService.validateRoleInfoEntity(roleInfoEntity);
        Assert.assertEquals(stringMapTenants.get("error"), "Type TENANTS include not supported verbs");

        roleInfoEntity.setResourceType(ResourceType.ALL.name());
        roleInfoEntity.setResourceVerbs(ResourceVerbs.ADMIN.name());
        Map<String, String> stringMapAll = rolesService.validateRoleInfoEntity(roleInfoEntity);
        Assert.assertEquals(stringMapAll.get("message"), "Role validate success");
    }
}
