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
import org.apache.pulsar.manager.entity.RoleInfoEntity;
import org.apache.pulsar.manager.entity.RolesRepository;
import org.apache.pulsar.manager.profiles.HerdDBTestProfile;
import org.apache.pulsar.manager.profiles.SqliteDBTestProfile;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = {
                PulsarManagerApplication.class,
                HerdDBTestProfile.class
        }
)
@ActiveProfiles("test")
public class RolesRepositoryImplTest {

    @Autowired
    private RolesRepository rolesRepository;

    private void initRole(RoleInfoEntity roleInfoEntity) {
        roleInfoEntity.setRoleName("test-role-name");
        roleInfoEntity.setRoleSource("admin");
        roleInfoEntity.setResourceType("tenants");
        roleInfoEntity.setResourceName("tenants");
        roleInfoEntity.setResourceVerbs("admin");
        roleInfoEntity.setDescription("This is tenants permissions");
        roleInfoEntity.setFlag(0);
    }

    private void validateRole(RoleInfoEntity role) {
        Assert.assertEquals(role.getRoleName(), "test-role-name");
        Assert.assertEquals(role.getRoleSource(), "admin");
        Assert.assertEquals(role.getResourceType(), "tenants");
        Assert.assertEquals(role.getResourceName(), "tenants");
        Assert.assertEquals(role.getResourceVerbs(), "admin");
        Assert.assertEquals(role.getDescription(), "This is tenants permissions");
        Assert.assertEquals(role.getFlag(), 0);
    }

    @Test
    public void getRolesListTest() {
        RoleInfoEntity roleInfoEntity = new RoleInfoEntity();
        initRole(roleInfoEntity);

        rolesRepository.save(roleInfoEntity);

        Page<RoleInfoEntity> rolesList = rolesRepository.findRolesList(1, 10);
        rolesList.count(true);
        rolesList.getResult().forEach((role) -> {
            validateRole(role);
            rolesRepository.delete(role.getRoleName(), role.getRoleSource());
        });
    }

    @Test
    public void getRoleInfoTest() {
        RoleInfoEntity roleInfoEntity = new RoleInfoEntity();
        initRole(roleInfoEntity);

        rolesRepository.save(roleInfoEntity);

        Optional<RoleInfoEntity> getRoleInfo = rolesRepository.findByRoleName(
                roleInfoEntity.getRoleName(), roleInfoEntity.getRoleSource());
        RoleInfoEntity getRoleInfoEntity = getRoleInfo.get();
        validateRole(getRoleInfoEntity);

        roleInfoEntity.setResourceType("clusters");
        roleInfoEntity.setResourceVerbs("admin,produce,consume");
        roleInfoEntity.setDescription("This is update role");
        roleInfoEntity.setFlag(1);

        rolesRepository.update(roleInfoEntity);
        Optional<RoleInfoEntity> updateRoleInfo = rolesRepository.findByRoleName(
                roleInfoEntity.getRoleName(), roleInfoEntity.getRoleSource());
        RoleInfoEntity updateRoleInfoEntity = updateRoleInfo.get();
        Assert.assertEquals(updateRoleInfoEntity.getResourceType(), "clusters");
        Assert.assertEquals(updateRoleInfoEntity.getResourceVerbs(), "admin,produce,consume");
        Assert.assertEquals(updateRoleInfoEntity.getDescription(), "This is update role");
        Assert.assertEquals(updateRoleInfoEntity.getFlag(), 1);

        rolesRepository.delete(roleInfoEntity.getRoleName(), roleInfoEntity.getRoleSource());
        Optional<RoleInfoEntity> deleteRoleInfo = rolesRepository.findByRoleName(
                roleInfoEntity.getRoleName(), roleInfoEntity.getRoleSource());
        Assert.assertFalse(deleteRoleInfo.isPresent());
    }
}
