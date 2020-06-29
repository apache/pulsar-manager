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
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.pulsar.manager.PulsarManagerApplication;
import org.apache.pulsar.manager.entity.UserInfoEntity;
import org.apache.pulsar.manager.entity.UsersRepository;
import org.apache.pulsar.manager.profiles.HerdDBTestProfile;
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
public class UsersRepositoryImplTest {

    @Autowired
    private UsersRepository usersRepository;

    private void initUser(UserInfoEntity userInfoEntity) {
        userInfoEntity.setAccessToken("test-access-token");
        userInfoEntity.setEmail("test@apache.org");
        userInfoEntity.setLocation("bj");
        userInfoEntity.setDescription("test-description");
        userInfoEntity.setName("test-user");
        userInfoEntity.setExpire(157900045678l);
        userInfoEntity.setPhoneNumber("1356789023456");
        userInfoEntity.setPassword(DigestUtils.sha256Hex("hello-world"));
    }

    private void validateUser(UserInfoEntity user, boolean list) {
        Assert.assertEquals("test-user", user.getName());
        Assert.assertEquals(157900045678l, user.getExpire());
        Assert.assertEquals("1356789023456", user.getPhoneNumber());
        Assert.assertEquals("test-description", user.getDescription());
        Assert.assertEquals("bj", user.getLocation());
        Assert.assertEquals("test@apache.org", user.getEmail());
        Assert.assertEquals("test-access-token", user.getAccessToken());
        if (!list) {
            Assert.assertEquals(DigestUtils.sha256Hex("hello-world"), user.getPassword());
        }
    }

    @Test
    public void getUsersListTest() {
        UserInfoEntity userInfoEntity = new UserInfoEntity();
        initUser(userInfoEntity);

        usersRepository.save(userInfoEntity);

        Page<UserInfoEntity> userInfoEntities = usersRepository.findUsersList(1, 10);
        userInfoEntities.count(true);
        userInfoEntities.getResult().forEach((user) -> {
            validateUser(user, true);
            usersRepository.delete(user.getName());
        });
    }

    @Test
    public void getAndUpdateUserTest() {
        UserInfoEntity userInfoEntity = new UserInfoEntity();
        initUser(userInfoEntity);
        usersRepository.save(userInfoEntity);
        Optional<UserInfoEntity> userInfoEntityOptional = usersRepository.findByUserName(userInfoEntity.getName());
        UserInfoEntity getUserInfoEntity = userInfoEntityOptional.get();
        validateUser(getUserInfoEntity, false);
        userInfoEntity.setPhoneNumber("1356789023456");
        userInfoEntity.setEmail("test2@apache.org");
        usersRepository.update(userInfoEntity);

        userInfoEntityOptional = usersRepository.findByUserName(userInfoEntity.getName());
        UserInfoEntity updateUserInfoEntity = userInfoEntityOptional.get();
        Assert.assertEquals("1356789023456", updateUserInfoEntity.getPhoneNumber());
        Assert.assertEquals("test2@apache.org", updateUserInfoEntity.getEmail());

        usersRepository.delete(updateUserInfoEntity.getName());
        userInfoEntityOptional = usersRepository.findByUserName(userInfoEntity.getName());
        Assert.assertFalse(userInfoEntityOptional.isPresent());
    }

    @Test
    public void findUserByAccessTokenTest() {
        UserInfoEntity userInfoEntity = new UserInfoEntity();
        initUser(userInfoEntity);
        usersRepository.save(userInfoEntity);
        Optional<UserInfoEntity> userInfoEntityOptional = usersRepository.findByAccessToken(
                userInfoEntity.getAccessToken());
        UserInfoEntity getUserInfoEntity = userInfoEntityOptional.get();
        validateUser(getUserInfoEntity, true);
        usersRepository.delete(userInfoEntity.getName());
    }
}
