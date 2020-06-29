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
package org.apache.pulsar.manager.dao;

import com.github.pagehelper.Page;
import org.apache.pulsar.manager.PulsarManagerApplication;
import org.apache.pulsar.manager.entity.RoleBindingEntity;
import org.apache.pulsar.manager.entity.RoleBindingRepository;
import org.apache.pulsar.manager.profiles.HerdDBTestProfile;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = {
                PulsarManagerApplication.class,
                HerdDBTestProfile.class
        }
)
@ActiveProfiles("test")
public class RoleBindingRepositoryImplTest {

    @Autowired
    private RoleBindingRepository roleBindingRepository;

    @Test
    public void roleBindingTest() {
        RoleBindingEntity roleBindingEntity = new RoleBindingEntity();
        roleBindingEntity.setDescription("this is description");
        roleBindingEntity.setName("test-role-binding");
        roleBindingEntity.setRoleId(1);
        roleBindingEntity.setUserId(2);

        roleBindingRepository.save(roleBindingEntity);

        Page<RoleBindingEntity> roleBindingEntities = roleBindingRepository.findByUserId(
                1, 2, 2);
        roleBindingEntities.getResult().forEach((r) -> {
            Assert.assertEquals(1, r.getRoleBindingId());
            Assert.assertEquals("test-role-binding", r.getName());
            Assert.assertEquals(1, r.getRoleId());
            Assert.assertEquals("this is description", r.getDescription());
        });

        roleBindingEntity.setName("update-role-binding");
        roleBindingEntity.setDescription("this is update description");
        roleBindingRepository.update(roleBindingEntity);
        Page<RoleBindingEntity> updateRoleBindingEntities = roleBindingRepository.findByUserId(
                1, 2, 2);
        updateRoleBindingEntities.getResult().forEach((r) -> {
            Assert.assertEquals(1, r.getRoleBindingId());
            Assert.assertEquals("update-role-binding", r.getName());
            Assert.assertEquals(1, r.getRoleId());
            Assert.assertEquals("this is update description", r.getDescription());
        });
    }

}
