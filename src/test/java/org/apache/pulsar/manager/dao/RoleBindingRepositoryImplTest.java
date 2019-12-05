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
            Assert.assertEquals(r.getRoleBindingId(), 1);
            Assert.assertEquals(r.getName(), "test-role-binding");
            Assert.assertEquals(r.getRoleId(), 1);
            Assert.assertEquals(r.getDescription(), "this is description");
        });

        roleBindingEntity.setName("update-role-binding");
        roleBindingEntity.setDescription("this is update description");
        roleBindingRepository.update(roleBindingEntity);
        Page<RoleBindingEntity> updateRoleBindingEntities = roleBindingRepository.findByUserId(
                1, 2, 2);
        updateRoleBindingEntities.getResult().forEach((r) -> {
            Assert.assertEquals(r.getRoleBindingId(), 1);
            Assert.assertEquals(r.getName(), "update-role-binding");
            Assert.assertEquals(r.getRoleId(), 1);
            Assert.assertEquals(r.getDescription(), "this is update description");
        });
    }

}
