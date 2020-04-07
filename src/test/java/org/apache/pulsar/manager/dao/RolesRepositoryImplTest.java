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
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
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
        roleInfoEntity.setRoleSource("test-tenant");
        roleInfoEntity.setResourceType("tenants");
        roleInfoEntity.setResourceName("tenants");
        roleInfoEntity.setResourceVerbs("admin");
        roleInfoEntity.setResourceId(2);
        roleInfoEntity.setDescription("This is tenants permissions");
        roleInfoEntity.setFlag(0);
    }

    private void validateRole(RoleInfoEntity role) {
        Assert.assertEquals("test-role-name", role.getRoleName());
        Assert.assertEquals("test-tenant", role.getRoleSource());
        Assert.assertEquals("tenants", role.getResourceType());
        Assert.assertEquals("tenants", role.getResourceName());
        Assert.assertEquals("admin", role.getResourceVerbs());
        Assert.assertEquals(2, role.getResourceId());
        Assert.assertEquals("This is tenants permissions", role.getDescription());
        Assert.assertEquals(0, role.getFlag());
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
    public void findRolesListByRoleSourceTest() {
        RoleInfoEntity roleInfoEntity = new RoleInfoEntity();
        initRole(roleInfoEntity);

        rolesRepository.save(roleInfoEntity);

        List<RoleInfoEntity> rolesList = rolesRepository.findRolesListByRoleSource("test-tenant");
        rolesList.forEach((role) -> {
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
        Assert.assertEquals("clusters", updateRoleInfoEntity.getResourceType());
        Assert.assertEquals("admin,produce,consume", updateRoleInfoEntity.getResourceVerbs());
        Assert.assertEquals("This is update role", updateRoleInfoEntity.getDescription());
        Assert.assertEquals(1, updateRoleInfoEntity.getFlag());

        rolesRepository.delete(roleInfoEntity.getRoleName(), roleInfoEntity.getRoleSource());
        Optional<RoleInfoEntity> deleteRoleInfo = rolesRepository.findByRoleName(
                roleInfoEntity.getRoleName(), roleInfoEntity.getRoleSource());
        Assert.assertFalse(deleteRoleInfo.isPresent());
    }

    @Test
    public void findMultiIdTest() {
        RoleInfoEntity roleInfoEntity = new RoleInfoEntity();
        initRole(roleInfoEntity);

        long roleId = rolesRepository.save(roleInfoEntity);
        List<Long> roleIdList = new ArrayList<>();
        roleIdList.add(roleId);
        roleIdList.add(roleInfoEntity.getRoleId());
        Page<RoleInfoEntity> roleInfoEntities = rolesRepository.findRolesMultiId(1, 2, roleIdList);
        roleInfoEntities.count(true);
        for (RoleInfoEntity infoEntity : roleInfoEntities.getResult()) {
            validateRole(infoEntity);
            rolesRepository.delete(infoEntity.getRoleName(), infoEntity.getRoleSource());
        }
    }
}
